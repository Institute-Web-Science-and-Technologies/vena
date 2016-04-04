package de.uniko.west.vena.core.mapping;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.uniko.west.vena.core.DataConnector;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")  
@JsonSubTypes({  
	    @Type(value = PersonEntity.class, name = "person"),  
	    @Type(value = FamilyEntity.class, name = "family") })
public abstract class GraphEntity {
	public static final String TYPE_PERSON = "person";
	public static final String TYPE_FAMILY = "family";
	
	@JsonProperty("id")
	protected String id;

	@JsonProperty("posX")
	@SparqlMapping(predicate = DataConnector.PRED_POSX)
	protected String posX;

	@JsonProperty("posY")
	@SparqlMapping(predicate = DataConnector.PRED_POSY)
	protected String posY;

	@JsonProperty("type")
	@SparqlMapping(predicate = DataConnector.PRED_NODE_TYPE)
	protected String type;
	
	@JsonProperty("name")
	@SparqlMapping(predicate = DataConnector.PRED_NAME)
	protected String name;	
	
	
	@JsonGetter("name")
	public String getName() {
		return name;
	}
	@JsonSetter("name")
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonGetter("posX")
	public String getPosX() {
		return posX;
	}
	
	@JsonSetter("posX")
	public void setPosX(String posX) {
		this.posX = posX;
	}

	@JsonGetter("posY")
	public String getPosY() {
		return posY;
	}
	@JsonSetter("posY")
	public void setPosY(String posY) {
		this.posY = posY;
	}
	
	@JsonGetter("type")
	public String getType() {
		return type;
	}
	@JsonSetter("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonGetter("id")
	public String getID() {
		return id;
	}
	
	@JsonSetter("id")
	public void setID(String id) {
		this.id = id;
	}
	
//	public abstract List<GraphEntity> getConnected();
	
}
