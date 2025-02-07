package com.tcube.api.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name ="role_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class RoleDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	@Column(name = "role")
    private String role;
	
	@Column(name = "access_to")
	private String access_to;
	
	@Column(name = "active_total_counts")
	private Integer active_total_counts;
	
	@Column(name = "inactive_total_counts")
	private Integer inactive_total_counts;
	
	@Column(name = "total_counts")
	private Integer total_counts;
	
	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;
	
	private Boolean is_deleted = Boolean.FALSE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OrgDetails getOrgDetails() {
		return orgDetails;
	}

	public void setOrgDetails(OrgDetails orgDetails) {
		this.orgDetails = orgDetails;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

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

	public Date getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}

	public Date getModified_time() {
		return modified_time;
	}

	public void setModified_time(Date modified_time) {
		this.modified_time = modified_time;
	}

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	public String getAccess_to() {
		return access_to;
	}

	public void setAccess_to(String access_to) {
		this.access_to = access_to;
	}
	
	
}
