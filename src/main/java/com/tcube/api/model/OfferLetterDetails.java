package com.tcube.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Offer_letter")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class OfferLetterDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="id")
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	@Lob
	@Column(name = "annexure_details",length = 100000)
    private String annexure_details;
	
	@Column(name="name")
	private String name;
	
	@Column(name="dob")
	private String dob;
	
//	@Column(name="doj")
//	private Date doj;

	@Column(name="letterTitle")
	private String letterTitle;
	
	@Column(name="description",length = 5000)
	@Lob
	private String description;
	
//	@Column(name="designation")
//	private String designation;
//	
//	@Column(name="location")
//	private String location;
//	
//	@Column(name="salary")
//	private String salary;
	
	@Column(name="created_time")
	private Date created_time;
	
	@Column(name="modified_time")
	private Date modified_time;
	
	@Lob
	@Column(name="offerLetter_pdfFormat")
	private byte[] offerLetterPdfFormat;
		
	@Column(name="company_logo")
	@Lob
	private String companyLogo;
	
	@Column(name="companyName")
	private String companyName;
		
	@Column(name="companyLink")
	private String companyLink;
	
	@Column(name="companyAddress")
	private String companyAddress;
	
	@Column(name="signature")
	@Lob
	private String signature;
	
	@Column(name="signFileName")
	private String signFileName;
	
	@Column(name="logoFileName")
	private String logoFileName;
		
	@Column(name="signatureName")
	private String signatureName;
	
	@Column(name="signatureRole")
	private String signatureRole;
	
	public byte[] getOfferLetterPdfFormat() {
		return offerLetterPdfFormat;
	}

	public void setOfferLetterPdfFormat(byte[] offerLetterPdfFormat) {
		this.offerLetterPdfFormat = offerLetterPdfFormat;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

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

	public OrgDetails getOrgDetails() {
		return orgDetails;
	}

	public void setOrgDetails(OrgDetails orgDetails) {
		this.orgDetails = orgDetails;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

//	public Date getDoj() {
//		return doj;
//	}
//
//	public void setDoj(Date doj) {
//		this.doj = doj;
//	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyLink() {
		return companyLink;
	}

	public void setCompanyLink(String companyLink) {
		this.companyLink = companyLink;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

//	public String getDesignation() {
//		return designation;
//	}
//
//	public void setDesignation(String designation) {
//		this.designation = designation;
//	}
//
//	
//	public String getLocation() {
//		return location;
//	}
//
//	public void setLocation(String location) {
//		this.location = location;
//	}
//	
//	public String getSalary() {
//		return salary;
//	}
//
//	public void setSalary(String salary) {
//		this.salary = salary;
//	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSignFileName() {
		return signFileName;
	}

	public void setSignFileName(String signFileName) {
		this.signFileName = signFileName;
	}

	public String getLogoFileName() {
		return logoFileName;
	}

	public void setLogoFileName(String logoFileName) {
		this.logoFileName = logoFileName;
	}
	
	public String getLetterTitle() {
		return letterTitle;
	}

	public void setLetterTitle(String letterTitle) {
		this.letterTitle = letterTitle;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSignatureName() {
		return signatureName;
	}

	public void setSignatureName(String signatureName) {
		this.signatureName = signatureName;
	}
	
	public String getSignatureRole() {
		return signatureRole;
	}

	public void setSignatureRole(String signatureRole) {
		this.signatureRole = signatureRole;
	}
	
	public String getAnnexure_details() {
		return annexure_details;
	}

	public void setAnnexure_details(String annexure_details) {
		this.annexure_details = annexure_details;
	}
}
