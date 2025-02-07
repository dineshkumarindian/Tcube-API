package com.tcube.api.model;

public class CustomProjectName {

	private Long id;
	
	private Long orgId;
	
	private String project_name;
	
	private Boolean is_deleted = Boolean.FALSE;
	
	private Boolean is_activated = Boolean.TRUE;
	
	private String project_status;
	
	private String status_comment;
	
	public String getProject_status() {
		return project_status;
	}

	public void setProject_status(String project_status) {
		this.project_status = project_status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
	
	public Boolean getIs_activated() {
		return is_activated;
	}

	public void setIs_activated(Boolean is_activated) {
		this.is_activated = is_activated;
	}
	
	public String getStatus_comment() {
		return status_comment;
	}

	public void setStatus_comment(String status_comment) {
		this.status_comment = status_comment;
	}
	
}
