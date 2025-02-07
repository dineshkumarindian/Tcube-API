package com.tcube.api.model;

import java.util.Date;

public class DateEmployeeStatus {

	    private String id;
		
	    private String firstname;
		
		private String lastname;
		
		private String email;

		private String role;
		
		private String designation;
		
		private byte[] image;
		
		private boolean present;
		
		private Date timeOfAction;

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

		public byte[] getImage() {
			return image;
		}

		public void setImage(byte[] image) {
			this.image = image;
		}

		public boolean isPresent() {
			return present;
		}

		public void setPresent(boolean present) {
			this.present = present;
		}

		public Date getTimeOfAction() {
			return timeOfAction;
		}

		public void setTimeOfAction(Date timeOfAction) {
			this.timeOfAction = timeOfAction;
		}
		
}
