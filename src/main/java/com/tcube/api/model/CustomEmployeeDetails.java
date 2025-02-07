package com.tcube.api.model;

public class CustomEmployeeDetails {

    private String id;
	
	private OrgDetails org_id;
	
    private String firstname;
	
	private String lastname;
	
	private String email;
	
	private byte[] profile_image;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OrgDetails getOrg_id() {
		return org_id;
	}

	public void setOrg_id(OrgDetails org_id) {
		this.org_id = org_id;
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

	public byte[] getProfile_image() {
		return profile_image;
	}

	public void setProfile_image(byte[] profile_image) {
		this.profile_image = profile_image;
	}
	
	
	
}
