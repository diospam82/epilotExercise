package com.recruitment.epilot;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivity {
	
	private Long id;
	private String type;
	
	//The GitHub data bank only optionally saves time zone information. 
	//It is not clear from the documentation whether events will always get auto-created with GMT time-stamp
	//Browsing random examples, no instance of a time-zone for events could be found. Time zones are hence ignored and GMT is always assumed
	@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime created_at;
	
	//save all other junk in a hash map
	private Map<String, Object> payload = new HashMap<>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public LocalDateTime getCreatedAt() {
		return created_at;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.created_at = createdAt;
	}
	
	public Map<String, Object> getPayload() {
		return payload;
	}
	@JsonAnySetter
	public void setPayload(String key, Object value) {
		this.payload.put(key, value);;
	}
	
	@Override
	public String toString() {
		return "UserActivity [id=" + id + ", type=" + type + ", getCreatedAt()="
				+ getCreatedAt() + ", getPayload()=" + getPayload() + "]";
	}

	
	
}