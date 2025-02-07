package com.tcube.api.model;

import java.util.ArrayList;

public class CustomEmployeeDetails2 {

	private String id;

	private String firstname;

	private String lastname;

	private String email;

	private Long org_id;

	private String designation;

	private Long designation_id;

	private String role;

	private Long role_id;

	private String branch;

	private Long branch_id;

	private String access_to;

	private String reporting_manager;

	private String reporting_manager_id;

	private Boolean is_deleted = Boolean.FALSE;

	private Boolean is_activated = Boolean.FALSE;

	private Boolean is_designation_deleted = Boolean.FALSE;

	private Boolean is_role_deleted = Boolean.FALSE;
	
	private Boolean is_branch_deleted = Boolean.FALSE;

//	private Boolean isReportingManagerAvail = Boolean.TRUE;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getReporting_manager() {
		return reporting_manager;
	}

	public void setReporting_manager(String reporting_manager) {
		this.reporting_manager = reporting_manager;
	}

	public String getReporting_manager_id() {
		return reporting_manager_id;
	}

	public void setReporting_manager_id(String reporting_manager_id) {
		this.reporting_manager_id = reporting_manager_id;
	}

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	public Boolean getIs_designation_deleted() {
		return is_designation_deleted;
	}

	public void setIs_designation_deleted(Boolean is_designation_deleted) {
		this.is_designation_deleted = is_designation_deleted;
	}

	public Boolean getIs_role_deleted() {
		return is_role_deleted;
	}

	public void setIs_role_deleted(Boolean is_role_deleted) {
		this.is_role_deleted = is_role_deleted;
	}

	public Long getDesignation_id() {
		return designation_id;
	}

	public void setDesignation_id(Long designation_id) {
		this.designation_id = designation_id;
	}

	public Long getRole_id() {
		return role_id;
	}

	public void setRole_id(Long role_id) {
		this.role_id = role_id;
	}

	public String getAccess_to() {
		return access_to;
	}

	public void setAccess_to(String access_to) {
		this.access_to = access_to;
	}

	public Boolean getIs_activated() {
		return is_activated;
	}

	public void setIs_activated(Boolean is_activated) {
		this.is_activated = is_activated;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public Boolean getIs_branch_deleted() {
		return is_branch_deleted;
	}

	public void setIs_branch_deleted(Boolean is_branch_deleted) {
		this.is_branch_deleted = is_branch_deleted;
	}

//	public Boolean getIsReportingManagerAvail() {
//		return isReportingManagerAvail;
//	}
//	
//	public void setIsReportingManagerAvail(Boolean isReportingManagerAvail) {
//		this.isReportingManagerAvail = isReportingManagerAvail;
//	}

}
