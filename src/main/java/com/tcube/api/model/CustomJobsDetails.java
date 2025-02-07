package com.tcube.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

public class CustomJobsDetails {

	private Long id;
	
	private Long orgId;

	private Long project_id;
	
	private String project_name;
	
	private String job_name;
	
	private  List<JSONObject>   jobAssigneeDetails = new ArrayList<JSONObject>();
	
	private Date start_date;
	
	private Date end_date;
	
	private Long hours;
	
	private Long rate_per_hour;

	private String bill;

	private String description;
	
	private Boolean is_deleted = Boolean.FALSE;
	
	private Boolean is_activated = Boolean.FALSE;
	
	private Boolean is_activated_project = Boolean.FALSE;
	
	private String logged_hours = "0";

	private long job_cost = 0;
	
	private int count_active = 0;

	private String job_status;
	
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

	public Long getProject_id() {
		return project_id;
	}

	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getJob_name() {
		return job_name;
	}

	public void setJob_name(String job_name) {
		this.job_name = job_name;
	}


	public List<JSONObject> getJobAssigneeDetails() {
		return jobAssigneeDetails;
	}

	public void setJobAssigneeDetails(List<JSONObject> jobAssigneeDetails) {
		this.jobAssigneeDetails = jobAssigneeDetails;
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

	public Long getHours() {
		return hours;
	}

	public void setHours(Long hours) {
		this.hours = hours;
	}

	public Long getRate_per_hour() {
		return rate_per_hour;
	}

	public void setRate_per_hour(Long rate_per_hour) {
		this.rate_per_hour = rate_per_hour;
	}

	public String getBill() {
		return bill;
	}

	public void setBill(String bill) {
		this.bill = bill;
	}

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	public String getLogged_hours() {
		return logged_hours;
	}

	public void setLogged_hours(String logged_hours) {
		this.logged_hours = logged_hours;
	}

	public long getJob_cost() {
		return job_cost;
	}

	public void setJob_cost(long job_cost) {
		this.job_cost = job_cost;
	}

	public int getCount_active() {
		return count_active;
	}

	public void setCount_active(int count_active) {
		this.count_active = count_active;
	}
	
	public Boolean getIs_activated() {
		return is_activated;
	}

	public void setIs_activated(Boolean is_activated) {
		this.is_activated = is_activated;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setJob_status(String job_status) {
		this.job_status = job_status;
		
	}

	public Boolean getIs_activated_project() {
		return is_activated_project;
	}

	public void setIs_activated_project(Boolean is_activated_project) {
		this.is_activated_project = is_activated_project;
	}
	
	
	
	
}
