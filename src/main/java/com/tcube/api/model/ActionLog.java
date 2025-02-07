package com.tcube.api.model;

import java.util.Date;

public class ActionLog {

	private Date timeOfAction;

	private String action;

	private String actionType;

	private byte[] image;

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

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
	
	
}
