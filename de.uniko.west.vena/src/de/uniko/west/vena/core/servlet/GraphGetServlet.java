package de.uniko.west.vena.core.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.uniko.west.vena.core.DataConnector;
import de.uniko.west.vena.core.VenaSession;
import de.uniko.west.vena.core.mapping.GraphEntity;

/**
 * Servlet implementation class GraphServlet
 */
@WebServlet("/GraphServlet")
public class GraphGetServlet extends HttpServlet {
	Logger log = Logger.getLogger(GraphGetServlet.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GraphGetServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sid = request.getRequestedSessionId();
		VenaSession session = VenaSession.getSession(sid);
		DataConnector con = DataConnector.getInstance(session);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		ObjectMapper mapper = new ObjectMapper();
		
		String json = "";
		if(br!=null){
			json = br.readLine();
		}
		
		log.info("SessionID: " + sid);
		log.info("JSON obj: " + json);
		
		response.setContentType("json");
		response.setCharacterEncoding("UTF-8");	
		
		try {
			boolean isNew = mapper.readTree(json).get("isNew").asBoolean();
			String dsName = mapper.readTree(json).get("dsName").asText();
			
			String dsId = "";
			if (isNew){
				dsId = con.createDataset( dsName );
			}else{
				dsId = con.getDatasetId( dsName );
			}
			String probFolder = con.getProbFolderOfDataset(dsId);
			ArrayList<GraphEntity> resultNodes = con.getGraphNodesForProb(probFolder);

			log.info( "graph: "  + mapper.writeValueAsString(resultNodes) );
			mapper.writeValue(response.getWriter(), resultNodes);
		} catch (Exception e) {
			log.info("Error on importing graph", e);
			response.sendError(1, e.getMessage());
		}
		response.flushBuffer();
	}

	
}
