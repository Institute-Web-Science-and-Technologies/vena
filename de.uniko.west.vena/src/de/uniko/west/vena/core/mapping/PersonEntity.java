/**
 * 
 */
package de.uniko.west.vena.core.mapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import de.uniko.west.vena.core.DataConnector;

/**
 * @author f
 *
 */
public class PersonEntity extends GraphEntity {
	public static final String GENDER_MALE 		= "männlich";
	public static final String GENDER_FEMALE 	= "weiblich";
	public static final String GENDER_UNDEFINED = "unbekannt";

	@JsonProperty("info")
	@SparqlMapping(predicate = DataConnector.PRED_INFO)
	private String info;
	
	@JsonProperty("gender")
	@SparqlMapping(predicate = DataConnector.PRED_GENDER)
	private String gender;
	
	@JsonProperty("topFamilies")
	@SparqlMapping(predicate = DataConnector.PRED_TOP_FAMILIES)
	private List<String> topFamilies;
	
	@JsonProperty("botFamilies")
	@SparqlMapping(predicate = DataConnector.PRED_BOT_FAMILIES)
	private List<String> botFamilies;
	
	@JsonProperty("friends")
	@SparqlMapping(predicate = DataConnector.PRED_FRIENDS)
	private List<String> friends;

	public PersonEntity() {
		this.type = TYPE_PERSON;
	}
	
	@JsonGetter("info")
	public String getInfo() {
		return info;
	}
	
	@JsonSetter("info")
	public void setInfo(String info) {
		this.info = info;
	}

//	@Override
//	public List<GraphEntity> getConnected() {
//		//needed?
//		// TODO Auto-generated method stub
//		return null;
//	}
	
}
