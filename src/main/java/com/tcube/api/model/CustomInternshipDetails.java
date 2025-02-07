package com.tcube.api.model;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;

public class CustomInternshipDetails {
	
	private Long id;
	
	private Long orgId;
	
	private String name;
	
	private Date doj;
	
	private String address;
	
	private String program_title;
	
	private String PdfFileLink;
	
	private String today_Date;
	
	
	private Date modified_time;
	
	private Date created_time;
	
	public Date getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}


	
	public String getTodayDate() {
		return today_Date;
	}

	public void setTodayDate(String today_Date) {
		this.today_Date = today_Date;
	}

	public String getPdfFileLink() {
		return PdfFileLink;
	}

	public void setPdfFileLink(String pdfFileLink) {
		PdfFileLink = pdfFileLink;
	}

	private byte[] internPdfFormat;
	
	private Boolean is_deleted = Boolean.FALSE;
	
	
	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	
	
	public String getToday_Date() {
		return today_Date;
	}

	public void setToday_Date(String today_Date) {
		this.today_Date = today_Date;
	}

	public Date getModified_time() {
		return modified_time;
	}

	public void setModified_time(Date modified_time) {
		this.modified_time = modified_time;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDoj() {
		return doj;
	}

	public void setDoj(Date doj) {
		this.doj = doj;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProgram_title() {
		return program_title;
	}

	public void setProgram_title(String program_title) {
		this.program_title = program_title;
	}

	public byte[] getInternPdfFormat() {
		return internPdfFormat;
	}

	public void setInternPdfFormat(byte[] internPdfFormat) {
		this.internPdfFormat = internPdfFormat;
	}

	

}
