package com.tcube.api.model;

import java.math.BigDecimal;
import java.sql.Time;
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
@Table(name = "reminder_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ReminderDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;

	@Column(name = "emp_id")
	private String emp_id;
	
	@Column(name = "module_name")
	private String module_name;

	@Column(name = "key_primary")
	private String key_primary;

	@Column(name = "key_secondary")
	private String key_secondary;

	@Column(name = "reminder_type")
	private String reminder_type;
	
	@Column(name = "reminder_date")
	private Date reminder_date;
	
	@Column(name = "reminder_time_ms")
	private BigDecimal reminder_time_ms;

	@Column(name = "reminder_time_str")
	private String reminder_time_str;
	
	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;
	
	@Column(name ="active_users")
	private String active_users;

	private Boolean is_active = Boolean.FALSE;

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

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public String getKey_primary() {
		return key_primary;
	}

	public void setKey_primary(String key_primary) {
		this.key_primary = key_primary;
	}

	public String getKey_secondary() {
		return key_secondary;
	}

	public void setKey_secondary(String key_secondary) {
		this.key_secondary = key_secondary;
	}

	public String getReminder_type() {
		return reminder_type;
	}

	public void setReminder_type(String reminder_type) {
		this.reminder_type = reminder_type;
	}

	public Date getReminder_date() {
		return reminder_date;
	}

	public void setReminder_date(Date reminder_date) {
		this.reminder_date = reminder_date;
	}

	public BigDecimal getReminder_time_ms() {
		return reminder_time_ms;
	}

	public void setReminder_time_ms(BigDecimal reminder_time_ms) {
		this.reminder_time_ms = reminder_time_ms;
	}

	public String getReminder_time_str() {
		return reminder_time_str;
	}

	public void setReminder_time_str(String reminder_time_str) {
		this.reminder_time_str = reminder_time_str;
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

	public Boolean getIs_active() {
		return is_active;
	}

	public void setIs_active(Boolean is_active) {
		this.is_active = is_active;
	}

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
	
	public String getActive_users() {
		return active_users;
	}

	public void setActive_users(String active_users) {
		this.active_users = active_users;
	}
	
	
}
