/**
 * 
 */
package de.uniko.west.vena.core.mapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.uniko.west.vena.core.DataConnector;

/**
 * @author f
 *
 */
public class FamilyEntity extends GraphEntity {

	@JsonProperty("mother")
	@SparqlMapping(predicate = DataConnector.PRED_MOTHER)
	private String mother;
	
	@JsonProperty("father")
	@SparqlMapping(predicate = DataConnector.PRED_FATHER)
	private String father;
	
	@JsonProperty("children")
	@SparqlMapping(predicate = DataConnector.PRED_CHILDREN)
	private List<String> children;
	
	public FamilyEntity() {
		this.type = TYPE_FAMILY;
	}
	
//	@Override
//	public List<GraphEntity> getConnected() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
