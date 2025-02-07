package com.tcube.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "job_details")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class JobDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "client_id", referencedColumnName = "id")
//	private ClientDetails clientDetails;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "project_id", referencedColumnName = "id")
//	private ProjectDetails projectDetails;
	
	@Column(name = "project_id")
	private Long project_id;
	
	@Column(name = "project_name")
	private String project_name;
	
	@Column(name = "job_name")
	private String job_name;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "job_id", referencedColumnName = "id")
	private List<JobAssigneeDetails>  jobAssigneeDetails= new ArrayList<JobAssigneeDetails>();
	
	@Column(name = "start_date")
	private Date start_date;
	
	@Column(name = "end_date")
	private Date end_date;
	
	@Column(name = "hours")
	private Long hours;
	
	@Column(name = "rate_per_hour")
	private Long rate_per_hour;
	
	@Column(name = "job_status")
	private String job_status;
	
	@Column(name = "status_comment")
	private String status_comment;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "bill")
	private String bill;

	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;

	private Boolean is_deleted = Boolean.FALSE;
	
	private Boolean is_activated = Boolean.TRUE;
	
	private Boolean is_activated_project = Boolean.TRUE;
	
	@Column(name = "mail_sended")
	private Boolean mail_sended = Boolean.FALSE;
	
	@Column(name = "logged_hours")
	private String logged_hours = "0";
	
	@Column(name = "job_cost")
	private long job_cost = 0;

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

//	public ProjectDetails getProjectDetails() {
//		return projectDetails;
//	}
//
//	public void setProjectDetails(ProjectDetails projectDetails) {
//		this.projectDetails = projectDetails;
//	}

	public String getJob_name() {
		return job_name;
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

	public void setJob_name(String job_name) {
		this.job_name = job_name;
	}


	public List<JobAssigneeDetails> getJobAssigneeDetails() {
		return jobAssigneeDetails;
	}

	public void setJobAssigneeDetails(List<JobAssigneeDetails> jobAssigneeDetails) {
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

	public String getJob_status() {
		return job_status;
	}

	public void setJob_status(String job_status) {
		this.job_status = job_status;
	}

	public String getStatus_comment() {
		return status_comment;
	}

	public void setStatus_comment(String status_comment) {
		this.status_comment = status_comment;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBill() {
		return bill;
	}

	public void setBill(String bill) {
		this.bill = bill;
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

	public Boolean getMail_sended() {
		return mail_sended;
	}

	public void setMail_sended(Boolean mail_sended) {
		this.mail_sended = mail_sended;
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

	public Boolean getIs_activated() {
		return is_activated;
	}

	public void setIs_activated(Boolean is_activated) {
		this.is_activated = is_activated;
	}

	public Boolean getIs_activated_project() {
		return is_activated_project;
	}

	public void setIs_activated_project(Boolean is_activated_project) {
		this.is_activated_project = is_activated_project;
	}
	
	
}

