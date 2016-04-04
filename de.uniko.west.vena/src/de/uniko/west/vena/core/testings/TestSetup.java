package de.uniko.west.vena.core.testings;

import org.apache.catalina.Context;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RemoteRepositoryManager;

/**
 * @author f
 *
 */

public class TestSetup {
	private static final String SERVER_URL = "http://localhost:8088/openrdf-sesame/";
	private static final String REPO_NAME = "vena";
	
	public static void main(String[] args) {
		System.out.println("Start");
		try {
			setup();
		} catch (RepositoryException | RepositoryConfigException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	
	public static void setup() throws RepositoryException, RepositoryConfigException {
		RemoteRepositoryManager manager = new RemoteRepositoryManager(SERVER_URL);
		manager.initialize();
		Repository repo = manager.getRepository(REPO_NAME);
		RepositoryConnection con = repo.getConnection();
		
		ValueFactory fac = ValueFactoryImpl.getInstance();
		
		URI bob = fac.createURI("http://example.org/bob");
		Literal bobName = fac.createLiteral("BOB");
		
		Statement bobst = fac.createStatement(bob, RDF.TYPE, FOAF.PERSON);
		
		con.add(bobst);
				
		
	}

}
