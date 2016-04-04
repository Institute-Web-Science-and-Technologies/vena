/**
 * 
 */
package de.uniko.west.vena.core;

import java.util.HashMap;

import de.uniko.west.vena.core.mapping.User;

/**
 * @author f
 *
 */
public class VenaSession {

	public static HashMap<String, VenaSession> clientSessions = new HashMap<String, VenaSession>();
	
	public static VenaSession getSession(String sessionID){
		if (!clientSessions.keySet().contains(sessionID)){
			clientSessions.put(sessionID, new VenaSession());
		}
		return clientSessions.get(sessionID);
	}
	
	private User user = null;
	
	private VenaSession() {
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	
	
}
