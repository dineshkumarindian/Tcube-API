package com.tcube.api.model;

import java.util.Date;



public class ActionCards {
	
	private Long id;

	private byte[] action_image;

	private String action_type;

	private String action;

	private String current_section;


	private String next_section;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	
}
