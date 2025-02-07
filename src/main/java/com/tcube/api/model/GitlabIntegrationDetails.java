package com.tcube.api.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "git_integration_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class GitlabIntegrationDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "org_id")
	private Long org_id;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "access_token")
	private String access_token;
	
	@Column(name = "base_url")
	private String url;
	
	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;
	
	private Boolean is_deleted = Boolean.FALSE;
	
	private Boolean git_integration = Boolean.FALSE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}

	public Date getModified_time() {
		return modified_time;
	}

	public void setModified_time(Date modified_time) {
		this.modified_time = modified_time;
	}

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	public Boolean getGit_integration() {
		return git_integration;
	}

	public void setGit_integration(Boolean git_integration) {
		this.git_integration = git_integration;
	}
	
	
}
