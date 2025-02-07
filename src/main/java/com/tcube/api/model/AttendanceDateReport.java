package com.tcube.api.model;

public class AttendanceDateReport {

	private String date;

	private String activeHours;

	private String firstIn;

	private String lastout;

	private String outForLunch;

	private String outForBreak;
	
	private String inactiveHours;

	private String overtime;

	private String deviation;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getActiveHours() {
		return activeHours;
	}

	public void setActiveHours(String activeHours) {
		this.activeHours = activeHours;
	}

	public String getFirstIn() {
		return firstIn;
	}

	public void setFirstIn(String firstIn) {
		this.firstIn = firstIn;
	}

	public String getLastout() {
		return lastout;
	}

	public void setLastout(String lastout) {
		this.lastout = lastout;
	}

	public String getOutForLunch() {
		return outForLunch;
	}

	public void setOutForLunch(String outForLunch) {
		this.outForLunch = outForLunch;
	}

	public String getOutForBreak() {
		return outForBreak;
	}

	public void setOutForBreak(String outForBreak) {
		this.outForBreak = outForBreak;
	}
	
	

	public String getInactiveHours() {
		return inactiveHours;
	}

	public void setInactiveHours(String inactiveHours) {
		this.inactiveHours = inactiveHours;
	}

	public String getOvertime() {
		return overtime;
	}

	public void setOvertime(String overtime) {
		this.overtime = overtime;
	}

	public String getDeviation() {
		return deviation;
	}

	public void setDeviation(String deviation) {
		this.deviation = deviation;
	}

}
