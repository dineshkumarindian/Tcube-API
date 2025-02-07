package com.tcube.api.model;

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
@Table(name = "attendance_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AttendanceDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "email")
	private String email;

	/**
	 * Refers to date of the request
	 */
	
	@Column(name = "date_of_Request")
	private String dateOfRequest;

	
	/**
	 * Refers to time of the action
	 */
	
	@Column(name = "time_of_action")
	private Date timeOfAction;

	/**
	 * Refers to action
	 */
	
	@Column(name = "action")
	private String action;

	/**
	 * Refers to action type
	 * 
	 */
	@Column(name = "action_type")
	private String actionType;
	
	/**
	 * Refers to next action section 
	 * 
	 */
	@Column(name = "next_action_section")
	private String next_action_section;


	/*
	 * To track the last active action
	 */
	
	@Column(name = "is_active")
	private boolean isActive;
	
	/*
	 * To Store Active time 
	 */
	
	@Column(name = "active_hours")
	private String activeHours;

	/**
	 * Refers the created_time
	 */
	@Column(name = "created_time")
	private Date createdTime;

	/**
	 * Refers the modified_time
	 */
	@Column(name = "modified_time")
	private Date modifiedTime;

	/*
	 * Soft delete 
	 */
	
	@Column(name = "is_delete")
	private boolean isDelete;

	/*
	 * To Refers the Org
	 */
	
//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "client_id", referencedColumnName = "id")
//	private ClientDetails clientDetails;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	// getters and setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDateOfRequest() {
		return dateOfRequest;
	}

	public void setDateOfRequest(String dateOfRequest) {
		this.dateOfRequest = dateOfRequest;
	}

	public Date getTimeOfAction() {
		return timeOfAction;
	}

	public void setTimeOfAction(Date timeOfAction) {
		this.timeOfAction = timeOfAction;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getActiveHours() {
		return activeHours;
	}

	public void setActiveHours(String activeHours) {
		this.activeHours = activeHours;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public OrgDetails getOrgDetails() {
		return orgDetails;
	}

	public void setOrgDetails(OrgDetails orgDetails) {
		this.orgDetails = orgDetails;
	}

	public String getNext_action_section() {
		return next_action_section;
	}

	public void setNext_action_section(String next_action_section) {
		this.next_action_section = next_action_section;
	}

	
	
	

}
