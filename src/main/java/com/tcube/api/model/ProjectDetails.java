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
@Table(name = "project_details")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class ProjectDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "client_id", referencedColumnName = "id")
	private ClientDetails clientDetails;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "project_id", referencedColumnName = "id")
	private List<ProjectResourceDetails>  resourceDetails= new ArrayList<ProjectResourceDetails>();
	
	@Column(name = "project_name")
	private String project_name;
	
//	@Column(name = "client")
//	private String client;
	
	@Column(name = "project_cost")
	private Long project_cost;
	
	@Column(name = "total_jobs")
	private Integer total_jobs;
	
	@Column(name = "project_status")
	private String project_status;
	
	@Column(name = "status_comment")
	private String status_comment;
	
	@Column(name = "description")
	private String description;

	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;
	
	@Column(name = "logged_hours")
	private String logged_hours = "0";

	private Boolean is_deleted = Boolean.FALSE;
	
	private Boolean is_activated = Boolean.TRUE;
	
	@Column(name = "start_date")
	private Date start_date;
	
	@Column(name = "end_date")
	private Date end_date;

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
	
	public List<ProjectResourceDetails> getResourceDetails() {
		return resourceDetails;
	}

	public void setResourceDetails(List<ProjectResourceDetails> resourceDetails) {
		this.resourceDetails = resourceDetails;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public ClientDetails getClientDetails() {
		return clientDetails;
	}

	public void setClientDetails(ClientDetails clientDetails) {
		this.clientDetails = clientDetails;
	}

	public Long getProject_cost() {
		return project_cost;
	}

	public void setProject_cost(Long project_cost) {
		this.project_cost = project_cost;
	}

	public String getProject_status() {
		return project_status;
	}

	public void setProject_status(String project_status) {
		this.project_status = project_status;
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


	public Integer getTotal_jobs() {
		return total_jobs;
	}

	public void setTotal_jobs(Integer total_jobs) {
		this.total_jobs = total_jobs;
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

	public Boolean getIs_activated() {
		return is_activated;
	}

	public void setIs_activated(Boolean is_activated) {
		this.is_activated = is_activated;
	}

	public String getLogged_hours() {
		return logged_hours;
	}

	public void setLogged_hours(String logged_hours) {
		this.logged_hours = logged_hours;
	}
	
	
	
	
	
	
}
