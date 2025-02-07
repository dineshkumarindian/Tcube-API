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
@Table(name="Experience_letter")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class ExperienceLetterDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="id")
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	@Column(name="employee_name")
	private String employeeName;
	
	@Column(name="id_no")
	private String idNo;
	
	@Column(name="designation")
	private String designation;
	
	@Column(name="joining_Date")
	private String joiningDate;
	
	@Column(name="logoImage")
	private byte[] logoImage;
	
	@Column(name="signImage")
	private byte[] signImage;
	
	public byte[] getLogoImage() {
		return logoImage;
	}
	@Column(name="date_OfJoining")
	private String dateOfJoining;
	
	@Column(name="completing_Date")
	private Date completingDate;
	
	@Column(name="company_name")
	private String companyName;
	
	@Column(name="company_place")
	private String companyPlace;
	
	@Column(name="issue_Date")
	private Date issueDate;
	
	@Column(name="company_Logo")
	private String companyLogo;
	
	@Column(name="sign_role")
	private String signRole;
	
	@Column(name="sign")
	private String sign;
	
	@Column(name="name_sign")
	private String nameOfSign;
	
	@Column(name="description")
	private String description;
	
	@Column(name="created_time")
	private Date created_time;
	
	@Column(name="modified_time")
	private Date modified_time;
	
	@Column(name="sign_filename")
	private String signFileName;
	
	@Column(name="company_site")
	private String companySite;
	
	@Column(name="company_address")
	private String companyAddress;
	
	@Column(name="experience_pdf_format")
	private byte[] experiencePdfFormat;
	
	public String getCompanySite() {
		return companySite;
	}

	public byte[] getExperiencePdfFormat() {
		return experiencePdfFormat;
	}

	public void setExperiencePdfFormat(byte[] experiencePdfFormat) {
		this.experiencePdfFormat = experiencePdfFormat;
	}

	public void setCompanySite(String companySite) {
		this.companySite = companySite;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
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

	
	
	public String getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(String dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	
	
	@Column(name="logo_filename")
	private String logoFileName;
	
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

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(String joiningDate) {
		this.joiningDate = joiningDate;
	}

	public Date getCompletingDate() {
		return completingDate;
	}

	public void setCompletingDate(Date completingDate) {
		this.completingDate = completingDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyPlace() {
		return companyPlace;
	}

	public void setCompanyPlace(String companyPlace) {
		this.companyPlace = companyPlace;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	public String getSignRole() {
		return signRole;
	}

	public void setSignRole(String signRole) {
		this.signRole = signRole;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getNameOfSign() {
		return nameOfSign;
	}

	public void setNameOfSign(String nameOfSign) {
		this.nameOfSign = nameOfSign;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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


}
