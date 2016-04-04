package de.uniko.west.vena.core.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
import de.uniko.west.vena.core.mapping.User;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger(LoginServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	}

//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		
//		String id = request.getRequestedSessionId();
//		VenaSession session = VenaSession.getSession(id);
//		
//		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
//		ObjectMapper mapper = new ObjectMapper();
//		
//		String json = "";
//		if(br!=null){
//			json = br.readLine();
//		}
//		
//		log.info("SessionID: " + id);
//		log.info("JSON obj for: " + json);
//
//		User user = mapper.readValue(json, User.class);
//
//		log.info("got " + user.getName() + " - " + user.getPassword() + " from website");
//		response.setContentType("json");
//		response.setCharacterEncoding("UTF-8");
//		
//		try {
//			//get user throws exception if unsuccessful
//			DataConnector.getInstance(session).checkUser(user.getName(), user.getPassword());
//			GraphEntity node = DataConnector.getInstance(session).getEntityForUser(user.getName());
//			
//			log.info("responding graphnode Id: " + node.getID());
//			mapper.writeValue(response.getWriter(), node);
//			String test = mapper.writeValueAsString(node);
//			log.info(test);
//		}catch(Exception e){
//			log.info("error on login", e);
//			response.sendError(1, e.getMessage());
//		}
//		
//		response.flushBuffer();		
//	}	
	
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String id = request.getRequestedSessionId();
		VenaSession session = VenaSession.getSession(id);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		ObjectMapper mapper = new ObjectMapper();
		
		String json = "";
		if(br!=null){
			json = br.readLine();
		}
		
		log.info("SessionID: " + id);
//		log.info("JSON obj for: " + json);

		User user = mapper.readValue(json, User.class);
		session.setUser(user);

		log.info("got " + user.getName() +  " from website");
		response.setContentType("json");
		response.setCharacterEncoding("UTF-8");
		
		try {
			//get user throws exception if unsuccessful
			DataConnector.getInstance(session).checkUser(user.getName(), user.getPassword());
			
			
			//possible all/for context switch
			List<String> datasets = DataConnector.getInstance(session).getDatasets(user);
			log.info("found "+ datasets.size() +" datasets... sending to frontend");
			String test = mapper.writeValueAsString(datasets);
			log.info(test);
			
			mapper.writeValue(response.getWriter(), datasets);
			
//			GraphEntity node = DataConnector.getInstance(session).getEntityForUser(user.getName());
			
//			log.info("responding graphnode Id: " + node.getID());
//			mapper.writeValue(response.getWriter(), node);
//			String test = mapper.writeValueAsString(node);
//			log.info(test);
		}catch(Exception e){
			log.info("error on login", e);
			response.sendError(1, e.getMessage());
		}
		
		response.flushBuffer();		
	}

}
