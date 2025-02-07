package com.tcube.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

@Entity
@Table(name = "manage_branch_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ManageBranchDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "org_id")
	private Long orgid;
	
	@Column(name = "branch_name")
	private String branch;
	
	private Boolean is_deleted = Boolean.FALSE;
	
	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "modified_time")
	private Date modifiedTime;
	
	@Column(name = "active_total_counts")
	private Integer active_total_counts;
	
	@Column(name = "inactive_total_counts")
	private Integer inactive_total_counts;
	
	@Column(name = "total_counts")
	private Integer total_counts;

	public Integer getActive_total_counts() {
		return active_total_counts;
	}

	public void setActive_total_counts(Integer active_total_counts) {
		this.active_total_counts = active_total_counts;
	}

	public Integer getInactive_total_counts() {
		return inactive_total_counts;
	}

	public void setInactive_total_counts(Integer inactive_total_counts) {
		this.inactive_total_counts = inactive_total_counts;
	}

	public Integer getTotal_counts() {
		return total_counts;
	}

	public void setTotal_counts(Integer total_counts) {
		this.total_counts = total_counts;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrgid() {
		return orgid;
	}

	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
}
