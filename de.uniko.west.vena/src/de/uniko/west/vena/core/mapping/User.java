/**
 * 
 */
package de.uniko.west.vena.core.mapping;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author f
 *
 */
public class User {
	
	@JsonProperty("name")
	private String name;
	@JsonProperty("password")
	private String password;
	
	@JsonSetter("name")
	public void setName(String name) {
		this.name = name;
	}
	@JsonGetter("name")
	public String getName() {
		return name;
	}
	@JsonSetter("password")
	public void setPassword(String password) {
		this.password = password;
	}
	@JsonGetter("password")
	public String getPassword() {
		return password;
	}
}
