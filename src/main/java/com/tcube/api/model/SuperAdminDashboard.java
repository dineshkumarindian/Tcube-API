package com.tcube.api.model;

public class SuperAdminDashboard {
	
	private Long org_id;
	
	private Boolean is_activated;
	
	private String status;
	
	private int activeCountInfo;
	
	private int inActiveCountInfo;
	
	private int rejectCountInfo;
	
	private int pendingCountInfo;

	private int totalCountInfo;
	
	public int getPendingCountInfo() {
		return pendingCountInfo;
	}

	public void setPendingCountInfo(int pendingCountInfo) {
		this.pendingCountInfo = pendingCountInfo;
	}
	
	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Boolean getIs_activated() {
		return is_activated;
	}

	public void setIs_activated(Boolean is_activated) {
		this.is_activated = is_activated;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getActiveCountInfo() {
		return activeCountInfo;
	}

	public void setActiveCountInfo(int activeCountInfo) {
		this.activeCountInfo = activeCountInfo;
	}

	public int getInActiveCountInfo() {
		return inActiveCountInfo;
	}

	public void setInActiveCountInfo(int inActiveCountInfo) {
		this.inActiveCountInfo = inActiveCountInfo;
	}

	public int getRejectCountInfo() {
		return rejectCountInfo;
	}

	public void setRejectCountInfo(int rejectCountInfo) {
		this.rejectCountInfo = rejectCountInfo;
	}

	public int getTotalCountInfo() {
		return totalCountInfo;
	}

	public void setTotalCountInfo(int totalCountInfo) {
		this.totalCountInfo = totalCountInfo;
	}

}
