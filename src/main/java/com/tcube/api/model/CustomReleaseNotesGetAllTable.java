package com.tcube.api.model;

import java.util.Date;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Lob;

public class CustomReleaseNotesGetAllTable {
	
	private Long id;
	
	 private Date created_time;
	 
	 private Date modified_time;
	 
	 private String releaseNotesTitle;
	 
	 private String keyNote;
	 
	 private String productName;
	 
	 private Date dor;
	 
	 private String version;	 
	 
	 private Boolean is_deleted = Boolean.FALSE;
	    
	 private Boolean is_publish = Boolean.FALSE;
	 
	 private Boolean is_republish = Boolean.FALSE;
	 
//	 private byte[] notes_pdfFormat;
//	 
//	 public byte[] getNotes_pdfFormat() {
//		return notes_pdfFormat;
//	}
//
//	public void setNotes_pdfFormat(byte[] notes_pdfFormat) {
//		this.notes_pdfFormat = notes_pdfFormat;
//	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getReleaseNotesTitle() {
		return releaseNotesTitle;
	}

	public void setReleaseNotesTitle(String releaseNotesTitle) {
		this.releaseNotesTitle = releaseNotesTitle;
	}

	public String getKeyNote() {
		return keyNote;
	}

	public void setKeyNote(String keyNote) {
		this.keyNote = keyNote;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Date getDor() {
		return dor;
	}

	public void setDor(Date dor) {
		this.dor = dor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	public Boolean getIs_publish() {
		return is_publish;
	}

	public void setIs_publish(Boolean is_publish) {
		this.is_publish = is_publish;
	}

	public Boolean getIs_republish() {
		return is_republish;
	}

	public void setIs_republish(Boolean is_republish) {
		this.is_republish = is_republish;
	}


	 
	
}