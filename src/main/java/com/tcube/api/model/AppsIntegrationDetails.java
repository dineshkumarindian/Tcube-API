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
@Table(name ="apps_integration_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AppsIntegrationDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	@Column(name = "created_by")
	private String created_by;
	
	@Column(name = "app_name")
    private String app_name;
	
//	@Size(min = 3, max = 15)
	@Column(name = "url")
    private String url;
	
	@Column(name = "whatsapp_access_token")
    private String whatsapp_access_token;
	
	@Column(name = "numbers")
	private String numbers;
	
//	@Column(name = "mobile_number_2")
//	private String mobile_number_2;
	
	@Column(name = "module_name")
    private String module_name;
	
//	@Column(name="country_code_1")
//	private String country_code_1;
//	
//	@Column(name="country_code_2")
//	private String country_code_2;

//	public String getCountry_code_1() {
//		return country_code_1;
//	}
//
//	public void setCountry_code_1(String country_code_1) {
//		this.country_code_1 = country_code_1;
//	}
//
//	public String getCountry_code_2() {
//		return country_code_2;
//	}
//
//	public void setCountry_code_2(String country_code_2) {
//		this.country_code_2 = country_code_2;
//	}

	@Column(name = "reason")
    private String reason;
	
	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;
	
	private Boolean is_deleted = Boolean.FALSE;

	
	private Boolean is_paused = Boolean.FALSE;

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

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getWhatsapp_access_token() {
		return whatsapp_access_token;
	}

	public void setWhatsapp_access_token(String whatsapp_access_token) {
		this.whatsapp_access_token = whatsapp_access_token;
	}

//	public String getMobile_number_1() {
//		return mobile_number_1;
//	}
//
//	public void setMobile_number_1(String mobile_number_1) {
//		this.mobile_number_1 = mobile_number_1;
//	}
//
//	public String getMobile_number_2() {
//		return mobile_number_2;
//	}
//
//	public void setMobile_number_2(String mobile_number_2) {
//		this.mobile_number_2 = mobile_number_2;
//	}

	public Date getCreated_time() {
		return created_time;
	}

	public String getNumbers() {
		return numbers;
	}

	public void setNumbers(String numbers) {
		this.numbers = numbers;
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

	public Boolean getIs_paused() {
		return is_paused;
	}

	public void setIs_paused(Boolean is_paused) {
		this.is_paused = is_paused;
	}
	
	
}
