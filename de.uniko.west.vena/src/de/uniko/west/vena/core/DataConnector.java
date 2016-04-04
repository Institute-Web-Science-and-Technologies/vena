/**
 * 
 */
package de.uniko.west.vena.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RemoteRepositoryManager;

import com.google.common.util.concurrent.Service.State;

import de.uniko.west.vena.core.mapping.FamilyEntity;
import de.uniko.west.vena.core.mapping.GraphEntity;
import de.uniko.west.vena.core.mapping.PersonEntity;
import de.uniko.west.vena.core.mapping.SparqlMapping;
import de.uniko.west.vena.core.mapping.User;
import info.aduna.iteration.Iterations;

/**
 * @author f
 *
 */
public class DataConnector {
	private static Logger log = Logger.getLogger(DataConnector.class);

	private static final String SERVER_URL = "http://localhost:8088/openrdf-sesame/";
	private static final String REPO_NAME = "venav2";
	
	private static final String VENA_URI = "http://west.uni-koblenz.de/vena";
	private static final String VENA_URI_SEP = VENA_URI + "/";
	
	private static final String VENA_PROB = VENA_URI_SEP+"prob/";
	private static final String VENA_USER = VENA_URI_SEP+"user/";
	
	private static final String VENA_META_DATA = "http://west.uni-koblenz.de/vena/data";
	
	
	private static final String TY_USR = VENA_URI_SEP+"types/user";
	private static final String TY_ENT = VENA_URI_SEP+"types/graphEntity";
	
	private static final String PRED_PWD = VENA_URI_SEP+"preds/hasPassword";
	
	private static final String PRED_HAS_EGO = VENA_URI_SEP+"preds/hasEgo";
	
	public static final String PRED_NAME 			= VENA_URI_SEP+"preds/hasName";
	public static final String PRED_NODE_TYPE 		= VENA_URI_SEP+"preds/isNodeType";
	public static final String PRED_POSX 			= VENA_URI_SEP+"preds/hasPosX";
	public static final String PRED_POSY 			= VENA_URI_SEP+"preds/hasPosY";
	public static final String PRED_INFO 			= VENA_URI_SEP+"preds/hasInfo";
	public static final String PRED_GENDER 			= VENA_URI_SEP+"preds/hasGender";
	public static final String PRED_TOP_FAMILIES 	= VENA_URI_SEP+"preds/hasTopFamily";
	public static final String PRED_BOT_FAMILIES 	= VENA_URI_SEP+"preds/hasBotFamily";
	public static final String PRED_FRIENDS 		= VENA_URI_SEP+"preds/hasFriends";
	public static final String PRED_MOTHER 			= VENA_URI_SEP+"preds/hasMother";;		
	public static final String PRED_FATHER 			= VENA_URI_SEP+"preds/hasFather";;		
	public static final String PRED_CHILDREN		= VENA_URI_SEP+"preds/hasChildren";;		
	
	private static final String KW_INFO_EGO = "ego";

	private static Map<VenaSession, DataConnector> connectors = new HashMap<VenaSession, DataConnector>();
	
	private RepositoryConnection con = null;
	private ValueFactory fac = null;
	
	// NOTE:
	// the user (= creator of data, logged in user of the system) resp. his/her uri is used as context of his data
	// the proband (= ego of data, center of graph) resp. his/her uri is used as uri-prefix for the graph data
	// therefore mixing of for-all/for-one requests depending on user OR proband is possible
	
	private URI userContext = null;
	
	public static DataConnector getInstance(VenaSession session){
		if (!connectors.containsKey(session)){
			try {
				connectors.put( session, new DataConnector() );
			} catch (RepositoryException | RepositoryConfigException e) {
				e.printStackTrace();
			}
		}
		return connectors.get(session);
	}
	
	private DataConnector() throws RepositoryException, RepositoryConfigException {
	
		RemoteRepositoryManager manager = new RemoteRepositoryManager(SERVER_URL);
		manager.initialize();
		Repository repo = manager.getRepository(REPO_NAME);
		con = repo.getConnection();
		
		fac = new ValueFactoryImpl();
	}
	
	public String checkUser(String user, String pwd){
		
		String response = "unknown status... error in connection?";
		
		URI usrUri = fac.createURI(VENA_USER +user);
		Literal pwdLit = fac.createLiteral(pwd);
		
		Statement stmUsr = fac.createStatement(usrUri, RDF.TYPE, fac.createURI( TY_USR ) );
		Statement stmPwd = fac.createStatement(usrUri, fac.createURI(PRED_PWD), pwdLit );

		userContext = usrUri;
		
		try {
			if (con.hasStatement(stmUsr, false, userContext)){
				// user exists
				if (con.hasStatement(stmPwd, false, userContext)){
					// pwd correct
					
					response = "user approved";
					
					return response;
				}else{					
					response = "wrong password";
					userContext = null;
					throw new VenaException(response);
				}
				
			}else{
				//create user
				log.info("adding user: " + user + " - " + pwd);
				createUser(user, stmUsr,stmPwd);
				
				response =  "user created";
				return response;
			}
			
		} catch (RepositoryException e) {
			
			e.printStackTrace();
			response = e.getMessage();
		}
		throw new VenaException(response);
	}
	
	private void createUser(String name, Statement stmUsr, Statement stmPwd) throws RepositoryException{
		
		con.add(stmUsr, userContext);
		con.add(stmPwd, userContext);
		
		
//		String userFolder = stmUsr.getSubject() + "/";
//		String identUri = userFolder + createUUID();
		
////		make link from user to identityEntity as graphroot
//		Statement stmIdt = fac.createStatement(stmUsr.getSubject(),
//				fac.createURI(PRED_IDENT), fac.createURI( identUri ));
//		con.add(stmIdt, context);
//		
////		create and store users Entity as placeholder
//		GraphEntity ident = new GraphEntity( identUri, name, GraphEntity.TYPE_PERSON, -1,-1, KW_INFO_EGO);
//		storeEntity(ident);
				
	}

	//putting draw coordinates into uuid makes things alot easier
	private String createUUID(){
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}
	

	
	public GraphEntity getEntity(String id) throws RepositoryException, IllegalArgumentException, IllegalAccessException{
		URI entityUri = fac.createURI( id );
		
		String type = con.getStatements(
				entityUri,
				fac.createURI( PRED_NODE_TYPE ),
				null,
				false, userContext).next().getObject().stringValue();

		GraphEntity entity = null;
		if (type.equals(GraphEntity.TYPE_PERSON)){
			entity = new PersonEntity();
		}
		if (type.equals(GraphEntity.TYPE_FAMILY)){
			entity = new FamilyEntity();
		}
		
		entity.setID(id);
		
		
		Map<Field,String> fields = getAnnotatetedFields(entity);
		
		for (Field field : fields.keySet()) {
			field.setAccessible(true);
			
			if (field.getType().equals(String.class)){
				RepositoryResult<Statement> stm= con.getStatements(
								entityUri,
								fac.createURI( fields.get(field) ),
								null,
								false, userContext);
				if (stm.hasNext()){
					field.set(entity, stm.next().getObject().stringValue());
				}
				
			}
			if (field.getType().equals(List.class)){
				RepositoryResult<Statement> repres = con.getStatements(
								entityUri,
								fac.createURI( fields.get(field) ),
								null,
								false, userContext);
				List<String> fieldVal = new ArrayList<String>(); 
				while (repres.hasNext()) {
					fieldVal.add( repres.next().getObject().stringValue() );
				}
				field.set(entity, fieldVal);
			}
		}
				
		return entity;
	}



	public void deleteEntity(GraphEntity entity, boolean asSubject, boolean asObject) throws RepositoryException {
		
		//TODO may delete too much ... may be reworked later
		
		List<Statement> statements = new ArrayList<Statement>();
		if (asSubject){
			statements.addAll( Iterations.asList( con.getStatements(
					fac.createURI( entity.getID() ),
					null,
					null,
					false,
					userContext) ) );
		}
		
		if (asObject){
			statements.addAll( Iterations.asList( con.getStatements(
					null,
					null,
					fac.createURI( entity.getID() ),
					false,
					userContext) ) );
		}
		con.remove(statements, userContext);

	}
	
	public void storeEntity(GraphEntity entity) throws RepositoryException, IllegalArgumentException, IllegalAccessException{
		con.add(fac.createStatement(
				fac.createURI( entity.getID() ),
				RDF.TYPE,
				fac.createURI( TY_ENT ),
				userContext));
		Map<Field, String> annotatedFields = getAnnotatetedFields(entity);
		
		for (Field field : annotatedFields.keySet()) {
			field.setAccessible(true);
			List<Literal> ll = fieldToLiteralList(field, entity);
			for (Literal literal : ll) {
				con.add(fac.createStatement(
						fac.createURI( entity.getID() ),
						fac.createURI( annotatedFields.get(field) ),
						literal,
						userContext));
			}
		}
	}
	
	private List<Literal> fieldToLiteralList(Field field, GraphEntity e) throws IllegalArgumentException, IllegalAccessException{
		List <Literal> ll = new ArrayList<Literal>();
		
		if (field.getType().equals(String.class)){
			String val = (String) field.get(e);
			if (val!=null){
				ll.add( fac.createLiteral( val ) ) ;	
			}
		}
		if (field.getType().equals(List.class)){
			List<?> fl = (List<?>) field.get(e);
			if (fl != null){
				for (Object fieldElem : fl) {
					ll.add( fac.createLiteral( (String)fieldElem ));
				}	
			}
			
		}
		return ll;
	}

	
//	public static void main(String[] args) {
//		GraphEntity e = new PersonEntity();
//		
//		List<Field> fields = new ArrayList<Field>();
//		fields.addAll( Arrays.asList( e.getClass().getDeclaredFields() ) );
//		fields.addAll( Arrays.asList( e.getClass().getSuperclass().getDeclaredFields() ) );
//		
//		System.out.println("Testing " + fields.size() + " fields");
//		for (Field field : fields) {
//			SparqlMapping anno = field.getAnnotation(SparqlMapping.class);
//			System.out.print( field.getName() );
//			if (anno!=null){
//				System.out.println("  -> " + anno.predicate() );
//				System.out.println(field.getType());
//			}else{
//				System.out.println();
//			}
//			
//			
//			
//		}
//		  
//	}
//	
	private Map<Field,String> getAnnotatetedFields(GraphEntity entity){
		Map<Field, String> result = new HashMap<Field, String>();
		
		List<Field> fields = new ArrayList<Field>();
		fields.addAll( Arrays.asList( entity.getClass().getDeclaredFields() ) );
		fields.addAll( Arrays.asList( entity.getClass().getSuperclass().getDeclaredFields() ) );
		
		for (Field field : fields) {
			SparqlMapping anno = field.getAnnotation(SparqlMapping.class);
			if (anno!=null){
				result.put(field, anno.predicate() );
			}
		}
		return result;
	}
	
	public ArrayList<GraphEntity> getGraphNodesForProb(String probFolder) throws RepositoryException, IllegalArgumentException, IllegalAccessException {
		log.info("probFolder: "+ probFolder);
		ArrayList<GraphEntity> result = new ArrayList<>();
		
		List<Statement> tmpStms = Iterations.asList( con.getStatements(
													null,
													RDF.TYPE,
													fac.createURI( TY_ENT ),
													false,
													userContext) );
		
		List<Statement> entityStms = filterProbOnly(tmpStms, probFolder);
		log.info("statements after filter: "+ entityStms.size());
		
		for (Iterator<Statement> it = entityStms.iterator(); it.hasNext();) {
			String id = it.next().getSubject().stringValue();
			GraphEntity ent = getEntity( id );
			result.add(ent);
		}
		log.info("result size: " + result.size());
		return result;
	}
	
	private List<Statement> filterProbOnly(List<Statement> statements, String probFolder){
		List<Statement> result = new ArrayList<>();
		log.info("filter: " + probFolder + " , listsize: " + statements.size());
		for (Iterator<Statement> it = statements.iterator(); it.hasNext();) {
			Statement statement = it.next();
			log.info(statement.getSubject().stringValue());
			if (statement.getSubject().stringValue().startsWith(probFolder)){
				result.add(statement);
			}
		}
		return result;
	}

	public List<String> getDatasets(User user) throws RepositoryException {
		List<String> result = new ArrayList<String>();
		URI context = userContext; // null or userContext later
		
		RepositoryResult<Statement> repRes = con.getStatements(
				null,
				fac.createURI( PRED_HAS_EGO ),
				null,
				false, context);
		
		URI datasetUri;
		while (repRes.hasNext()) {
			datasetUri = fac.createURI( repRes.next().getSubject().stringValue() );
			String dsName = con.getStatements(
					datasetUri,
					fac.createURI( PRED_NAME ),
					null,
					false, context).next().getObject().stringValue();
			result.add(dsName);
		}
		return result;
	}
	
	
	public String getProbFolderOfDataset(String dsId) throws RepositoryException{
		String egoId = getEgoOfDataset( dsId );
		String folder = egoId.substring( 0, egoId.lastIndexOf("/") + 1 );
		
		return folder;
	}
	
	
	public String getEgoOfDataset(String dsId) throws RepositoryException{
		URI context = userContext; // null or userContext later
		log.info("getting probfolder with dsId:" + dsId);
		String egoId = con.getStatements(
				fac.createURI( dsId ),
				fac.createURI( PRED_HAS_EGO ),
				null,
				false, context).next().getObject().stringValue();
		
		return egoId;
		
	}
	
//	public GraphEntity getEntityForUser(String userName) throws RepositoryException {
//	URI usrUri = fac.createURI(VENA_PROB +userName);
//	
////	get link from user to identityEntity as graphroot
//	RepositoryResult<Statement> idResult = con.getStatements(usrUri,
//											fac.createURI(PRED_IDENT),
//											null,
//											false, context);
//	
//	String identURIString =  idResult.next().getObject().stringValue();
//	return getEntity(identURIString);
//}
	
	
	// returns dataset id
	public String createDataset(String dsName) throws RepositoryException, IllegalArgumentException, IllegalAccessException {
		URI dsURI = fac.createURI(VENA_META_DATA + "/" + dsName);
		
		con.add(fac.createStatement(
					dsURI,
					fac.createURI( PRED_NAME ),
					fac.createLiteral( dsName ) ),
				userContext);
		
		String probName = dsName.split("#")[0].trim();
		String probId = VENA_PROB + probName + "/" + createUUID();
		
		PersonEntity ego = new PersonEntity();// probId, probName, GraphEntity.TYPE_PERSON, -1,-1, KW_INFO_EGO);
		ego.setID(probId);
		ego.setName(probName);
		ego.setPosX("-1");
		ego.setPosY("-1");
		ego.setInfo(KW_INFO_EGO);
		
		storeEntity(ego);
		
		con.add(fac.createStatement(
					dsURI,
					fac.createURI( PRED_HAS_EGO ),
					fac.createURI(probId) ),
				userContext);
		
		return dsURI.toString();
	}
	
	public String getDatasetId(String dsName) throws RepositoryException {
		URI context = userContext; // null or userContext later
		String datasetUri = con.getStatements(
				null,
				fac.createURI( PRED_NAME ),
				fac.createLiteral( dsName ),
				false, context).next().getSubject().stringValue();
		
		return datasetUri;
	}
	
}
