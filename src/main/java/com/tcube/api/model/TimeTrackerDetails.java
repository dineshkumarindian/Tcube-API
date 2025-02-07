package com.tcube.api.model;

import java.math.BigDecimal;
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
@Table(name = "time_tracker_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TimeTrackerDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "emp_id")
	private String emp_id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "client_id", referencedColumnName = "id")
	private ClientDetails clientDetails;

	@Column(name = "task")
	private String task;

	@Column(name = "project")
	private String project;

	@Column(name = "job")
	private String job;

	@Column(name = "bill")
	private String bill;

	/*
	 * @Column(name = "start_time") private Date start_time;
	 * 
	 * @Column(name = "end_time") private Date end_time;
	 * 
	 * @Column(name = "task_duration") private Long task_duration;
	 * 
	 * @Column(name = "total_time") private Long total_time;
	 */

	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;

	@Column(name = "date_of_request")
	private String date_of_request;

	@Column(name = "time_interval")
	private String time_interval;

	private Boolean is_deleted = Boolean.FALSE;

	private Boolean is_active = Boolean.FALSE;

	@Column(name = "task_duration")
	private String task_duration;

	@Column(name = "task_duration_ms")
	private BigDecimal task_duration_ms;

	@Column(name = "approval_status")
	private String approval_status;

	@Column(name = "approval_comments")
	private String approval_comments;

	@Column(name = "reporter")
	private String reporter;

	@Column(name = "reporter_name")
	private String reporter_name;

	@Column(name = "timesheet_id")
	private Long timesheet_id;

	public String getTime_interval() {
		return time_interval;
	}

	public void setTime_interval(String time_interval) {
		this.time_interval = time_interval;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getBill() {
		return bill;
	}

	public void setBill(String bill) {
		this.bill = bill;
	}

	/*
	 * public Date getStart_time() { return start_time; }
	 * 
	 * public void setStart_time(Date start_time) { this.start_time = start_time; }
	 * 
	 * public Date getEnd_time() { return end_time; }
	 * 
	 * public void setEnd_time(Date end_time) { this.end_time = end_time; }
	 * 
	 * public Long getTask_duration() { return task_duration; }
	 * 
	 * public void setTask_duration(Long task_duration) { this.task_duration =
	 * task_duration; }
	 * 
	 * public Long getTotal_time() { return total_time; }
	 * 
	 * public void setTotal_time(Long total_time) { this.total_time = total_time; }
	 */
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

	public String getDate_of_request() {
		return date_of_request;
	}

	public void setDate_of_request(String date_of_request) {
		this.date_of_request = date_of_request;
	}

	public OrgDetails getOrgDetails() {
		return orgDetails;
	}

	public void setOrgDetails(OrgDetails orgDetails) {
		this.orgDetails = orgDetails;
	}

	public Boolean getIs_active() {
		return is_active;
	}

	public void setIs_active(Boolean is_active) {
		this.is_active = is_active;
	}

	public String getTask_duration() {
		return task_duration;
	}

	public void setTask_duration(String task_duration) {
		this.task_duration = task_duration;
	}

	public BigDecimal getTask_duration_ms() {
		return task_duration_ms;
	}

	public void setTask_duration_ms(BigDecimal task_duration_ms) {
		this.task_duration_ms = task_duration_ms;
	}

	public ClientDetails getClientDetails() {
		return clientDetails;
	}

	public void setClientDetails(ClientDetails clientDetails) {
		this.clientDetails = clientDetails;
	}

	public String getApproval_status() {
		return approval_status;
	}

	public void setApproval_status(String approval_status) {
		this.approval_status = approval_status;
	}

	public String getApproval_comments() {
		return approval_comments;
	}

	public void setApproval_comments(String approval_comments) {
		this.approval_comments = approval_comments;
	}

	public String getReporter() {
		return reporter;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	public String getReporter_name() {
		return reporter_name;
	}

	public void setReporter_name(String reporter_name) {
		this.reporter_name = reporter_name;
	}

	public Long getTimesheet_id() {
		return timesheet_id;
	}

	public void setTimesheet_id(Long timesheet_id) {
		this.timesheet_id = timesheet_id;
	}

}
