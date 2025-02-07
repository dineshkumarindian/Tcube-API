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
@Table(name = "timesheet_approval_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TimesheetApprovalDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "timesheet_name")
	private String timesheet_name;
	
	@Column(name = "emp_id")
	private String emp_id;
	
	@Column(name = "emp_name")
	private String emp_name;
	
	@Column(name = "emp_designation")
	private String emp_designation;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;

	@Column(name = "date_of_request")
	private String date_of_request;

	@Column(name = "total_time")
	private String total_time;

	@Column(name = "total_time_ms")
	private BigDecimal total_time_ms;
	
	@Column(name = "billable_total_time")
	private String billable_total_time;

//	@Column(name = "billable_total_time")
//	private BigDecimal billable_total_time;
	
	@Column(name = "non_billable_total_time")
	private String non_billable_total_time;

//	@Column(name = "total_time_ms")
//	private BigDecimal total_time_ms;

	@Column(name = "approval_status")
	private String approval_status;

	@Column(name = "approval_comments")
	private String approval_comments;

	@Column(name = "reporter")
	private String reporter;

	@Column(name = "reporter_name")
	private String reporter_name;

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

	public String getTimesheet_name() {
		return timesheet_name;
	}

	public void setTimesheet_name(String timesheet_name) {
		this.timesheet_name = timesheet_name;
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

	public String getEmp_designation() {
		return emp_designation;
	}

	public void setEmp_designation(String emp_designation) {
		this.emp_designation = emp_designation;
	}

	public OrgDetails getOrgDetails() {
		return orgDetails;
	}

	public void setOrgDetails(OrgDetails orgDetails) {
		this.orgDetails = orgDetails;
	}

	public String getDate_of_request() {
		return date_of_request;
	}

	public void setDate_of_request(String date_of_request) {
		this.date_of_request = date_of_request;
	}

	public String getTotal_time() {
		return total_time;
	}

	public void setTotal_time(String total_time) {
		this.total_time = total_time;
	}

	public BigDecimal getTotal_time_ms() {
		return total_time_ms;
	}

	public void setTotal_time_ms(BigDecimal total_time_ms) {
		this.total_time_ms = total_time_ms;
	}

	public String getBillable_total_time() {
		return billable_total_time;
	}

	public void setBillable_total_time(String billable_total_time) {
		this.billable_total_time = billable_total_time;
	}

	public String getNon_billable_total_time() {
		return non_billable_total_time;
	}

	public void setNon_billable_total_time(String non_billable_total_time) {
		this.non_billable_total_time = non_billable_total_time;
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
