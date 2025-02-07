package com.tcube.api.model;

public class CustomAuthenticateDetails {
	
	private Long orgId;
	
	private String empId;
	
	private String orgName;
	
	private String email;
	
	private String firstName;
	
	private String role;
	
	private String status;

	private Boolean empRoleDeleted;
	
	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getEmpRoleDeleted() {
		return empRoleDeleted;
	}

	public void setEmpRoleDeleted(Boolean empRoleDeleted) {
		this.empRoleDeleted = empRoleDeleted;
	}
	
}
