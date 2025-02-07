package com.tcube.api.model;

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
@Table(name = "job_assignee_details")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class JobAssigneeDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "emp_id", referencedColumnName = "id")
	private EmployeeDetails employeeDetails;
	
	@Column(name = "rate_per_hour")
	private Long rate_per_hour;
	
	@Column(name = "logged_hours")
	private String logged_hours = "0";
	
	@Column(name = "assignee_cost")
	private long assignee_cost = 0;
	
	@Column(name = "assignee_hours")
	private Double assignee_hours = 0.0;
	
	@Column(name = "status")
	private String status = "Active";
	
	@Column(name = "reference_jobid")
	private Long ref_jobid;
	
	private Boolean is_deleted = Boolean.FALSE;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EmployeeDetails getEmployeeDetails() {
		return employeeDetails;
	}

	public void setEmployeeDetails(EmployeeDetails employeeDetails) {
		this.employeeDetails = employeeDetails;
	}

	public Long getRate_per_hour() {
		return rate_per_hour;
	}

	public void setRate_per_hour(Long rate_per_hour) {
		this.rate_per_hour = rate_per_hour;
	}

	public String getLogged_hours() {
		return logged_hours;
	}

	public void setLogged_hours(String logged_hours) {
		this.logged_hours = logged_hours;
	}

	public long getAssignee_cost() {
		return assignee_cost;
	}

	public void setAssignee_cost(long assignee_cost) {
		this.assignee_cost = assignee_cost;
	}

	public Double getAssignee_hours() {
		return assignee_hours;
	}

	public void setAssignee_hours(Double assignee_hours) {
		this.assignee_hours = assignee_hours;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getRef_jobid() {
		return ref_jobid;
	}

	public void setRef_jobid(Long ref_jobid) {
		this.ref_jobid = ref_jobid;
	}
	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
}
