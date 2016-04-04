package de.uniko.west.vena.core.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.uniko.west.vena.core.DataConnector;
import de.uniko.west.vena.core.VenaSession;
import de.uniko.west.vena.core.mapping.GraphEntity;

/**
 * Servlet implementation class GraphUpdateServlet
 */
@WebServlet("/GraphUpdateServlet")
public class GraphUpdateServlet extends HttpServlet {
	Logger log = Logger.getLogger(GraphUpdateServlet.class);
	private static final long serialVersionUID = 1L;
    
	private static final String ACTION_ADD = "add";
	private static final String ACTION_MOD = "modify";
	private static final String ACTION_DEL = "delete";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GraphUpdateServlet() {
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
		// "add" "modify" "delete"
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		
		String sid = request.getRequestedSessionId();
		VenaSession session = VenaSession.getSession(sid);
		DataConnector con = DataConnector.getInstance(session);
		
		String json = "";
		if(br!=null){
			json = br.readLine();
		}
		
		try {
		
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(json);
			String action = root.get("action").asText();
			log.info("action: " + action);
			
			if (action.equals( ACTION_ADD )){
				log.info("doing ADD with: " + root.get("nodes"));
				GraphEntity[] entities = mapper.readValue(root.get("nodes").toString(), GraphEntity[].class);
				for (int i = 0; i < entities.length; i++) {
					con.storeEntity(entities[i]);
				}
				
				mapper.writeValue(response.getWriter(), entities);
			}
			if (action.equals( ACTION_MOD )){
				GraphEntity oldEntity = mapper.readValue(root.get("oldNode").toString(), GraphEntity.class);
				GraphEntity newEntity = mapper.readValue(root.get("newNode").toString(), GraphEntity.class);
				con.deleteEntity(oldEntity, true, false);
				con.storeEntity(newEntity);
				mapper.writeValue(response.getWriter(), newEntity);
			}
			
			if (action.equals( ACTION_DEL )){
				GraphEntity[] entities = mapper.readValue(root.get("nodes").toString(), GraphEntity[].class);
				for (int i = 0; i < entities.length; i++) {
					con.deleteEntity(entities[i], true, true);	
				}
				mapper.writeValue(response.getWriter(), entities);
			}
			log.info("successful action: graph." + action );
		} catch (Exception e) {
			log.info("Error on update", e);
			response.sendError(1, e.getMessage());
		}
		response.flushBuffer();
	}

}
