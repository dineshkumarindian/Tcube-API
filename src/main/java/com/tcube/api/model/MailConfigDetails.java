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
@Table(name = "mail_configuration")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class MailConfigDetails {

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
	 * smtp host
	 */
	@Column(name = "host")
	private String host;

	/**
	 * smtp port
	 */
	@Column(name = "port")
	private Integer port;

	/**
	 * User name
	 */
	@Column(name = "username")
	private String username;

	/**
	 * password
	 */
	@Column(name = "password")
	private String password;

	/**
	 * sender
	 */
	@Column(name = "sender")
	private String sender;

	/**
	 * enable or disable mail
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
	 * getters and setters
	 * 
	 */

	public String getHost() {
		return host;
	}

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

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

}
