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
@Table(name = "leave_tracker_details")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class LeaveTrackerDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	@Column(name = "emp_id")
	private String emp_id;
	
	@Column(name = "emp_name")
	private String emp_name;


	@Column(name = "reporter")
	private String reporter;

	@Column(name = "reporter_name")
	private String reporter_name;

	@Column(name="total_days")
	private Double total_days;
	
	@Column(name="leave_comments")
	private String leaveComments;
	
	
	public String getLeaveComments() {
		return leaveComments;
	}

	public void setLeaveComments(String leaveComments) {
		this.leaveComments = leaveComments;
	}

	@Column(name = "approval_status")
	private String approval_status;

	@Column(name = "approval_comments")
	private String approval_comments;
	
	@Column(name = "leave_type_id")
	private Long leave_type_id;
	
	@Column(name = "leave_type")
	private String leave_type;
	
	@Column(name = "start_date")
	private Date start_date;
	
	@Column(name = "end_date")
	private Date end_date;

	@Column(name = "half_full_day")
	private String half_full_day;
	
	@Lob
	@Column(name = "reason_for_leave")
	private String reason_for_leave;
	
	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;
	
	@Column(name ="start_date_str")
	private String start_date_str;
	
	@Column(name="end_date_str")
	private String end_date_str;
	
	@Column(name="is_notified_toslack")
	private Boolean is_notified_toslack;
	
	private Boolean slack_notify = Boolean.FALSE;
	
//	@Lob
//	@Column(name = "emp_img")
//	private byte[] emp_img;

	private Boolean is_deleted = Boolean.FALSE;
	
	private Boolean is_active = Boolean.TRUE;

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

	public Double getTotal_days() {
		return total_days;
	}

	public void setTotal_days(Double total_days) {
		this.total_days = total_days;
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

	public Long getLeave_type_id() {
		return leave_type_id;
	}

	public void setLeave_type_id(Long leave_type_id) {
		this.leave_type_id = leave_type_id;
	}

	public void setApproval_comments(String approval_comments) {
		this.approval_comments = approval_comments;
	}

	public String getLeave_type() {
		return leave_type;
	}

	public void setLeave_type(String leave_type) {
		this.leave_type = leave_type;
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

	public String getHalf_full_day() {
		return half_full_day;
	}

	public void setHalf_full_day(String half_full_day) {
		this.half_full_day = half_full_day;
	}

	public String getReason_for_leave() {
		return reason_for_leave;
	}

	public void setReason_for_leave(String reason_for_leave) {
		this.reason_for_leave = reason_for_leave;
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

	public Boolean getIs_active() {
		return is_active;
	}

	public void setIs_active(Boolean is_active) {
		this.is_active = is_active;
	}

//	public byte[] getEmp_img() {
//		return emp_img;
//	}
//
//	public void setEmp_img(byte[] emp_img) {
//		this.emp_img = emp_img;
//	}
	public String getStart_date_str() {
		return start_date_str;
	}

	public void setStart_date_str(String start_date_str) {
		this.start_date_str = start_date_str;
	}

	public String getEnd_date_str() {
		return end_date_str;
	}

	public void setEnd_date_str(String end_date_str) {
		this.end_date_str = end_date_str;
	}

	public Boolean getIs_notified_toslack() {
		return is_notified_toslack;
	}

	public void setIs_notified_toslack(Boolean is_notified_toslack) {
		this.is_notified_toslack = is_notified_toslack;
	}

	public Boolean getSlack_notify() {
		return slack_notify;
	}

	public void setSlack_notify(Boolean slack_notify) {
		this.slack_notify = slack_notify;
	}
	
	
	

//	public Object getEmp_img() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
	
	
}
