package com.tcube.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

public class CustomDetailsForProjectWithReferenceId {
	
	private Long id;
	
	private Long orgId;
	
	private String project_name;
	
	private  List<JSONObject> resourceDetails= new ArrayList<JSONObject>();	
	
	private Boolean is_deleted = Boolean.FALSE;
	
	private Boolean is_activated = Boolean.TRUE;
	
	private String project_status;
	
	private String clientName;
	
	private long clientId;
	
	private Long project_cost;
	
	private String description;
	
	private Date start_date;
	
	private Date end_date;
	
	private Integer total_jobs;
	
	private String status_comment;
	
	private String logged_hours;
	
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
	public  List<JSONObject> getResourceDetails() {
		return resourceDetails;
	}

	public void setResourceDetails( List<JSONObject> resourceDetails) {
		this.resourceDetails = resourceDetails;
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
	
	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public Long getProject_cost() {
		return project_cost;
	}

	public void setProject_cost(Long project_cost) {
		this.project_cost = project_cost;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
	public Integer getTotal_jobs() {
		return total_jobs;
	}

	public void setTotal_jobs(Integer total_jobs) {
		this.total_jobs = total_jobs;
	}
	
	public String getStatus_comment() {
		return status_comment;
	}

	public void setStatus_comment(String status_comment) {
		this.status_comment = status_comment;
	}

	public String getLogged_hours() {
		return logged_hours;
	}

	public void setLogged_hours(String logged_hours) {
		this.logged_hours = logged_hours;
	}
	
	
}
