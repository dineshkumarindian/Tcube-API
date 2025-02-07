package com.tcube.api.model;

public class EmployeeAttendanceDatereport {
    
    private String id;
	
    private String firstname;
	
	private String lastname;
	
	private String email;

	private String role;
	
	private String designation;
	
	private String activehrs;

	private String lastaction;
	
	private boolean present;
	
	private String date;

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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getActivehrs() {
		return activehrs;
	}

	public void setActivehrs(String activehrs) {
		this.activehrs = activehrs;
	}

	public String getLastaction() {
		return lastaction;
	}

	public void setLastaction(String lastaction) {
		this.lastaction = lastaction;
	}

	public boolean isPresent() {
		return present;
	}

	public void setPresent(boolean present) {
		this.present = present;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	
}
