package com.tcube.api.model;

public class AttendanceCurrentStatus {
	
	private String email;
	
	private String actionType;
	
	private String activeHours;
	
	private String action;
	
	private String next_section;
	
	private String dateOfRequest;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getActiveHours() {
		return activeHours;
	}

	public void setActiveHours(String activeHours) {
		this.activeHours = activeHours;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getNext_section() {
		return next_section;
	}

	public void setNext_section(String next_section) {
		this.next_section = next_section;
	}

	public String getDateOfRequest() {
		return dateOfRequest;
	}

	public void setDateOfRequest(String dateOfRequest) {
		this.dateOfRequest = dateOfRequest;
	}
	
	

}
