package com.tcube.api.model;

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
@Table(name = "project_resource_details")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class ProjectResourceDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "emp_id", referencedColumnName = "id")
	private EmployeeDetails employeeDetails;

	@Column(name = "designation")
	private String designation;

	@Column(name = "rate_per_hour")
	private Long rate_per_hour;
	
	@Column(name = "reference_projectid")
	private Long ref_projectid;
	
	@Column(name = "status")
	private String status = "Active";
	
	private Boolean is_deleted = Boolean.FALSE;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EmployeeDetails getEmployeeDetails() {
		return employeeDetails;
	}

	public void setEmployeeDetails(EmployeeDetails employeeDetails) {
		this.employeeDetails = employeeDetails;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Long getRate_per_hour() {
		return rate_per_hour;
	}

	public void setRate_per_hour(Long rate_per_hour) {
		this.rate_per_hour = rate_per_hour;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getRef_projectid() {
		return ref_projectid;
	}

	public void setRef_projectid(Long ref_projectid) {
		this.ref_projectid = ref_projectid;
	}
	
	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
}
