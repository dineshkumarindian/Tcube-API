package com.tcube.api.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name = "pricing_plan_details")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class PricingPlanDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "plan")
	private String plan;
	
	@Column(name = "plan_amount")
	private Double amount;
	
	@Column(name = "currency")
	private String currency;

	@Column(name = "days" , columnDefinition = "VARCHAR(255) DEFAULT '0'")
	private String days = "0" ;

	@Column(name = "userslimit" , columnDefinition = "VARCHAR(255) DEFAULT '0'")
	private String userslimit = "0" ;

	@Column(name = "category" , columnDefinition = "VARCHAR(255) DEFAULT 'Month'")
	private String category = "Month" ;
	
	@Column(name = "modules")
	private String modules;
	
	@Column(name = "description")
	private String desc;
	
	private Boolean is_deleted = Boolean.FALSE;
	
	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getUserslimit() {
		return userslimit;
	}

	public void setUserslimit(String userslimit) {
		this.userslimit = userslimit;
	}

	public String getModules() {
		return modules;
	}

	public void setModules(String modules) {
		this.modules = modules;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
