package com.tcube.api.model;

import java.util.Date;

import java.util.List;
import javax.persistence.Column;

public class CustomReleaseNotes {
	
	private Long id;
	
	 private Date created_time;
	 
	 private Date modified_time;
	 
	 private String releaseNotesTitle;
	 
	 private String keyNote;
	 
	 private String productName;
	 
	 private Date dor;
	 
	 private String version;
	 
	 private String whatsNew;
	 
	 private String improvement;
	 
	 private String bugFixes;
	 
	 private String general;
	 
	 private String comingsoon;
	 
	 private Boolean is_deleted = Boolean.FALSE;
	 
	 public Boolean getIs_deleted() {
			return is_deleted;
		}

		public void setIs_deleted(Boolean is_deleted) {
			this.is_deleted = is_deleted;
		}
	
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

	public String getWhatsNew() {
		return whatsNew;
	}

	public void setWhatsNew(String whatsNew) {
		this.whatsNew = whatsNew;
	}

	public String getImprovement() {
		return improvement;
	}

	public void setImprovement(String improvement) {
		this.improvement = improvement;
	}

	public String getBugFixes() {
		return bugFixes;
	}

	public void setBugFixes(String bugFixes) {
		this.bugFixes = bugFixes;
	}

	public String getGeneral() {
		return general;
	}

	public void setGeneral(String general) {
		this.general = general;
	}

	public String getComingsoon() {
		return comingsoon;
	}

	public void setComingsoon(String comingsoon) {
		this.comingsoon = comingsoon;
	}

	

}