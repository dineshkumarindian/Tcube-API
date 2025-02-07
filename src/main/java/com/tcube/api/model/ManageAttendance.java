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
@Table(name = "manage_attendance")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ManageAttendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	@Lob
	@Column(name = "action_image")
	private byte[] action_image;
	
	@Column(name = "action_type")
	private String action_type;
	
	@Column(name = "action")
	private String action;
	
	@Column(name = "current_section")
	private String current_section;
	
	
	@Column(name = "next_section")
	private String next_section;
	
	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;
	
	/*
	 * Soft delete 
	 */
	
	@Column(name = "is_delete")
	private boolean isDelete;

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

	public byte[] getAction_image() {
		return action_image;
	}

	public void setAction_image(byte[] action_image) {
		this.action_image = action_image;
	}

	public String getAction_type() {
		return action_type;
	}

	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCurrent_section() {
		return current_section;
	}

	public void setCurrent_section(String current_section) {
		this.current_section = current_section;
	}

	public String getNext_section() {
		return next_section;
	}

	public void setNext_section(String next_section) {
		this.next_section = next_section;
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

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	

}
