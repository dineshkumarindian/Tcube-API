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
@Table(name = "notification_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class NotificationsDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "org_id")
	private Long org_id;

	@Column(name = "message")
	private String message;

	@Column(name = "timesheet_id")
	private Long timesheet_id;

//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "to_notify_id", referencedColumnName = "id")
//	private EmployeeDetails toNotifyEmpDetails;

	// who need to see the notification
	@Column(name = "to_notify_id")
	private String to_notify_id;

	@Column(name = "to_notifier_name")
	private String to_notifier_name;

//	@Lob
//	@Column(name = "to_notifier_prfl_img")
//	private byte[] to_notifier_prfl_img;

	@Column(name = "notifier")
	private String notifier;

	@Column(name = "is_deleted")
	private Boolean is_deleted;

	@Column(name = "is_read")
	private Boolean is_read;

	@Column(name = "created_time")
	private Date created_time;
	
//	@Column(name = "timesheet_date")
//	private String timesheet_date;

	@Column(name = "modified_time")
	private Date modified_time;

	@Column(name = "module_name")
	private String module_name;

	@Column(name = "sub_module_name")
	private String sub_module_name;

	@Column(name = "date_of_request")
	private String date_of_request;

	@Column(name = "approval_status")
	private String approval_status;

	@Column(name = "approval_comments")
	private String approval_comments;
	
	@Column(name ="keyword")
	private String keyword;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

//	public EmployeeDetails getToNotifyEmpDetails() {
//		return toNotifyEmpDetails;
//	}
//
//	public void setToNotifyEmpDetails(EmployeeDetails toNotifyEmpDetails) {
//		this.toNotifyEmpDetails = toNotifyEmpDetails;
//	}

	public String getTo_notify_id() {
		return to_notify_id;
	}

	public void setTo_notify_id(String to_notify_id) {
		this.to_notify_id = to_notify_id;
	}

	public String getTo_notifier_name() {
		return to_notifier_name;
	}

	public void setTo_notifier_name(String to_notifier_name) {
		this.to_notifier_name = to_notifier_name;
	}

//	public byte[] getTo_notifier_prfl_img() {
//		return to_notifier_prfl_img;
//	}
//
//	public void setTo_notifier_prfl_img(byte[] to_notifier_prfl_img) {
//		this.to_notifier_prfl_img = to_notifier_prfl_img;
//	}

	public String getNotifier() {
		return notifier;
	}

	public void setNotifier(String notifier) {
		this.notifier = notifier;
	}

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	public Boolean getIs_read() {
		return is_read;
	}

	public void setIs_read(Boolean is_read) {
		this.is_read = is_read;
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
	
//	public String getTimesheet_date() {
//		return timesheet_date;
//	}
//
//	public void setTimesheet_date(String timesheet_date) {
//		this.timesheet_date = timesheet_date;
//	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public String getSub_module_name() {
		return sub_module_name;
	}

	public void setSub_module_name(String sub_module_name) {
		this.sub_module_name = sub_module_name;
	}

	public Long getTimesheet_id() {
		return timesheet_id;
	}

	public void setTimesheet_id(Long timesheet_id) {
		this.timesheet_id = timesheet_id;
	}

	public String getDate_of_request() {
		return date_of_request;
	}

	public void setDate_of_request(String date_of_request) {
		this.date_of_request = date_of_request;
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

	
	
	

}
