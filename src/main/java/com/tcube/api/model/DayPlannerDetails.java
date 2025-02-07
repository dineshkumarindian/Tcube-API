package com.tcube.api.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name ="day_planner_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class DayPlannerDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
//	@Column(name = "org_id")
//	private Long org_id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	@Column(name = "emp_id")
	private String emp_id;
	
	@Column(name = "emp_name")
	private String emp_name;
	
//	@Lob
//	@Column(name = "emp_image")
////	@Column(length = 999999)
//	private byte[] emp_image;
	
//	@Column(name = "creater_id")
//	private String creater_id;
//	
//	@Column(name = "creater_name")
//	private String creater_name;
	
	@Column(name = "day_task")
	private String day_task;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "date")
	private String date;
	
	@Column(name = "project_id")
	private Long project_id;
	
	@Column(name = "project_name")
	private String project_name;
	
	@Column(name = "reminder_date_time")
	private Date reminder_date_time;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "comments")
	private String comments;
	
	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;
	
	private Boolean is_reminder = Boolean.FALSE;
	
	private Boolean is_submitted = Boolean.FALSE ;
	
	private Boolean is_updated = Boolean.FALSE ;
	
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

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getEmp_name() {
		return emp_name;
	}

	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}

//	public byte[] getEmp_image() {
//		return emp_image;
//	}
//
//	public void setEmp_image(byte[] emp_image) {
//		this.emp_image = emp_image;
//	}

	public String getDay_task() {
		return day_task;
	}

	public void setDay_task(String day_task) {
		this.day_task = day_task;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Date getReminder_date_time() {
		return reminder_date_time;
	}

	public void setReminder_date_time(Date reminder_date_time) {
		this.reminder_date_time = reminder_date_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

//	public String getCreater_id() {
//		return creater_id;
//	}
//
//	public void setCreater_id(String creater_id) {
//		this.creater_id = creater_id;
//	}
//
//	public String getCreater_name() {
//		return creater_name;
//	}
//
//	public void setCreater_name(String creater_name) {
//		this.creater_name = creater_name;
//	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public Boolean getIs_reminder() {
		return is_reminder;
	}

	public void setIs_reminder(Boolean is_reminder) {
		this.is_reminder = is_reminder;
	}

	public Boolean getIs_updated() {
		return is_updated;
	}

	public void setIs_updated(Boolean is_updated) {
		this.is_updated = is_updated;
	}

	public Boolean getIs_submitted() {
		return is_submitted;
	}

	public void setIs_submitted(Boolean is_submitted) {
		this.is_submitted = is_submitted;
	}

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
	
}
