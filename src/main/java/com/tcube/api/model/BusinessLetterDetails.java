package com.tcube.api.model;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Internship_letter")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class BusinessLetterDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="id")
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	@Column(name="name")
	private String name;
	
	@Column(name="doj")
	private Date doj;
	
	@Column(name="address")
	private String address;
	
	@Column(name="program_title")
	private String program_title;
	
	@Column(name="description")
	@Lob
	private String description;
	 
	@Column(name="created_time")
	private Date created_time;
	
	public String getToday_Date() {
		return today_Date;
	}

	public void setToday_Date(String today_Date) {
		this.today_Date = today_Date;
	}

	@Column(name="modified_time")
	private Date modified_time;
	
	@Lob
	@Column(name="intern_pdfFormat")
	private byte[] internPdfFormat;
	

	@Column(name="company_logo")
	@Lob
	private String companyLogo;
	
	@Column(name="today_Date")
	private String today_Date;
	
	public byte[] getLogoImage() {
		return logoImage;
	}

	public void setLogoImage(byte[] logoImage) {
		this.logoImage = logoImage;
	}

	public byte[] getSignImage() {
		return signImage;
	}

	public void setSignImage(byte[] signImage) {
		this.signImage = signImage;
	}
	
	@Lob
	@Column(name="logoimage")
	private byte[] logoImage;
	
	@Lob
	@Column(name="signimage")
	private byte[] signImage;
	
	
	@Column(name="director_sign")
	@Lob
	private String directorSign;
	
	
	@Column(name="director_name")
	private String directorName;
	
	@Column(name="company_name")
	private String companyName;
	
	@Column(name="company_address")
	private String companyAddress;
	
	@Column(name="company_site")
	private String companySite;
	
	@Column(name="signature_role")
	private String signatureRole;
	
	@Column(name="sign_filename")
	private String signFileName;
	
	@Column(name="logo_filename")
	private String logoFileName;
	
	@Column(name="pdf_file_link")
	private String pdfFileLink;
	
	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

	public String getCompanyAddress() {
		return companyAddress;
	}

	public String getSignatureRole() {
		return signatureRole;
	}

	public String getPdfFileLink() {
		return pdfFileLink;
	}

	public void setPdfFileLink(String pdfFileLink) {
		this.pdfFileLink = pdfFileLink;
	}

	public void setSignatureRole(String signatureRole) {
		this.signatureRole = signatureRole;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCompanySite() {
		return companySite;
	}

	public void setCompanySite(String companySite) {
		this.companySite = companySite;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	private Boolean is_deleted = Boolean.FALSE;
	
	public String getDirectorSign() {
		return directorSign;
	}

	public void setDirectorSign(String directorSign) {
		this.directorSign = directorSign;
	}

	public String getDirectorName() {
		return directorName;
	}

	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}

	public OrgDetails getOrgDetails() {
		return orgDetails;
	}

	public void setOrgDetails(OrgDetails orgDetails) {
		this.orgDetails = orgDetails;
	}
	public byte[] getInternPdfFormat() {
		return internPdfFormat;
	}

	public void setInternPdfFormat(byte[] internPdfFormat) {
		this.internPdfFormat = internPdfFormat;
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

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
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

	public String getDescription() {
			return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
