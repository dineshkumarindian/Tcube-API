package com.tcube.api.model;

import java.util.Date;


public class LeaveTrackerReport {
	
    private Long id;
    
	private String emp_id;
	
	private String emp_name;

	private String reporter;

	private String reporter_name;

	private Double total_days;
	
	private String leaveComments;	
	
	private String leave_type;
	
	private Date start_date;
	
	private Date end_date;

	private String reason_for_leave;

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

	public String getLeaveComments() {
		return leaveComments;
	}

	public void setLeaveComments(String leaveComments) {
		this.leaveComments = leaveComments;
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

	public String getReason_for_leave() {
		return reason_for_leave;
	}

	public void setReason_for_leave(String reason_for_leave) {
		this.reason_for_leave = reason_for_leave;
	}
	
	

}
