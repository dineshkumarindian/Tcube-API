package com.tcube.api.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name ="access_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AccessDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "emp_id", referencedColumnName = "id")
	private EmployeeDetails employeeDetails;

	private Boolean dashboard = Boolean.TRUE;
	
	private Boolean project_jobs = Boolean.FALSE;
	
	private Boolean time_tracker = Boolean.FALSE;
	
	private Boolean attendance = Boolean.FALSE;
	
	private Boolean settings = Boolean.FALSE;
	
	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;

	
	private Boolean is_deleted = Boolean.FALSE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OrgDetails getOrgDetails() {
		return orgDetails;
	}

	public void setOrgDetails(OrgDetails orgDetails) {
		this.orgDetails = orgDetails;
	}

	public EmployeeDetails getEmployeeDetails() {
		return employeeDetails;
	}

	public void setEmployeeDetails(EmployeeDetails employeeDetails) {
		this.employeeDetails = employeeDetails;
	}

	public Boolean getDashboard() {
		return dashboard;
	}

	public void setDashboard(Boolean dashboard) {
		this.dashboard = dashboard;
	}

	public Boolean getProject_jobs() {
		return project_jobs;
	}

	public void setProject_jobs(Boolean project_jobs) {
		this.project_jobs = project_jobs;
	}

	public Boolean getTime_tracker() {
		return time_tracker;
	}

	public void setTime_tracker(Boolean time_tracker) {
		this.time_tracker = time_tracker;
	}

	public Boolean getAttendance() {
		return attendance;
	}

	public void setAttendance(Boolean attendance) {
		this.attendance = attendance;
	}

	public Boolean getSettings() {
		return settings;
	}

	public void setSettings(Boolean settings) {
		this.settings = settings;
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
	
	
}
