package com.recruitment.epilot;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RepoPullRequest {

	private Long id;
	
	private Integer number;
	private String state;
    private String title;
    
    private Integer commits;
    private Integer additions;
    private Integer deletions;
    private Integer changed_files;
    
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime created_at;
    
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime updated_at;
    
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime closed_at; 
    
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime merged_at;
	
	//save all other junk in a hash map
	private Map<String, Object> payload = new HashMap<>();

	public RepoPullRequest() {
		// TODO Auto-generated constructor stub
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Map<String, Object> getPayload() {
		return payload;
	}

	@JsonAnySetter
	public void setPayload(String key, Object value) {
		this.payload.put(key, value);;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

	public LocalDateTime getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(LocalDateTime updated_at) {
		this.updated_at = updated_at;
	}

	public LocalDateTime getClosed_at() {
		//LocalDateTime class throws an exception if null
		if (this.closed_at != null) {
			return closed_at;
		}
		else
			return null;
	}

	public void setClosed_at(LocalDateTime closed_at) {
		this.closed_at = closed_at;
	}

	public LocalDateTime getMerged_at() {
		//LocalDateTime class throws an exception if null
		if (this.merged_at != null) {
			return merged_at;
		}
		else
			return null;
	}

	public void setMerged_at(LocalDateTime merged_at) {
		this.merged_at = merged_at;
	}

	public Integer getCommits() {
		return commits;
	}

	public void setCommits(Integer commits) {
		this.commits = commits;
	}

	public Integer getAdditions() {
		return additions;
	}

	public void setAdditions(Integer additions) {
		this.additions = additions;
	}

	public Integer getDeletions() {
		return deletions;
	}

	public void setDeletions(Integer deletions) {
		this.deletions = deletions;
	}

	public Integer getChanged_files() {
		return changed_files;
	}

	public void setChanged_files(Integer changed_files) {
		this.changed_files = changed_files;
	}
	
	

}
