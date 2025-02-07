package com.tcube.api.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "manage_integrations")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ManageIntegration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * org id
	 */

	@Column(name = "org_id")
	private Long org_id;

	/**
	 * module
	 */
	@Column(name = "module")
	private String module;

	/**
	 * app
	 */
	@Column(name = "app")
	private String app;

	/**
	 * enable or disable
	 */

	@Column(name = "is_active")

	private boolean isActive = true;

	/**
	 * Soft delete
	 */

	@Column(name = "is_delete")
	private boolean isDelete;

	/**
	 * Refers the created_time
	 */

	@Column(name = "created_time")
	private Date createdTime;

	/**
	 * Refers the modified_time
	 */

	@Column(name = "modified_time")
	private Date modifiedTime;

	/**
	 * Getters and Setters
	 */

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public boolean getisActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean getisDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
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
