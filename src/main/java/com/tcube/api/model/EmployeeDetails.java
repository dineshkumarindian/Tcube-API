package com.tcube.api.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Admin
 *
 */
@Entity
@Table(name ="employee_details")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class EmployeeDetails {
	@Id
	@Column(name = "id")
    private String id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "org_id", referencedColumnName = "org_id")
	private OrgDetails orgDetails;
	
	
	@Column(name = "firstname")
    private String firstname;
	
	@Column(name = "lastname")
	private String lastname;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
		
	private String oldPassword;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "role_id", referencedColumnName = "id")
	private RoleDetails roleDetails;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "designation_id", referencedColumnName = "id")
	private DesignationDetails designationDetails;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "branch_id", referencedColumnName = "id")
	private ManageBranchDetails branchDetails;
	
//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "reporting_manager", referencedColumnName = "id")
//	private EmployeeDetails reporterDetails;
	
	@Column(name = "reporting_manager")
	private String reporting_manager;
	
	@Column(name = "reporting_manager_name")
	private String reporter_name;
	
	@Column(name = "date_of_joining")
	private Date date_of_joining;
//	@Column(name = "role")
//	private String role;
	
	@Column(name = "user_login_type")
	private String user_login_type;
	
	
//	@Column(name = "profile_image")
	@Lob
	@Column(name = "profile_image")
//	@Column(length = 999999)
	private byte[] profile_image;

	
	@Column(name = "date_of_birth")
	private Date date_of_birth;
	
	@Column(name = "marital_status")
	private String marital_status;
	
	@Column(name = "blood_group")
	private String blood_group;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "work_mobile_number")
	private String work_mobile_number;
	
	@Column(name = "personal_mobile_number")
	private String personal_mobile_number;
	
	
	@Column(name = "personal_email")
	private String personal_email;
	
	@Column(name = "present_address")
	private String present_address;
	
	@Column(name = "permanent_address")
	private String permanent_address;
	
	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;
	
	@Column(name = "seq")
	private Long seq;
	
	@Column(name = "isForgotPwdEnabled")
	private Boolean isForgotPwdEnabled = Boolean.FALSE;
	
	@Column(name ="is_skipped")
	private Boolean is_skipped = Boolean.FALSE;
	
	@Column(name ="skipped_time")
	private Date skipped_time;
	
	@Column(name="skipped_leave")
	private Boolean skipped_leave = Boolean.FALSE;
	
	@Column(name="new_release")
	private Boolean new_release = Boolean.FALSE;
	
	@Column(name="mail_otp")
	private String mail_otp;
	
	@Column(name ="is_details_updated")
	private Boolean is_details_updated = Boolean.FALSE;
	
//	@Column(name = "isReportingManagerAvail")
//	private Boolean isReportingManagerAvail = Boolean.TRUE;
//	
//	public Boolean getIsReportingManagerAvail() {
//		return isReportingManagerAvail;
//	}
//	
//	public void setIsReportingManagerAvail(Boolean isReportingManagerAvail) {
//		this.isReportingManagerAvail = isReportingManagerAvail;
//	}
	
	public String getMail_otp() {
		return mail_otp;
	}


	public void setMail_otp(String mail_otp) {
		this.mail_otp = mail_otp;
	}


	public Boolean getIs_skipped() {
		return is_skipped;
	}

	
	public void setIs_skipped(Boolean is_skipped) {
		this.is_skipped = is_skipped;
	}
	public Date getSkipped_time() {
		return skipped_time;
	}

	public Boolean getSkipped_leave() {
		return skipped_leave;
	}


	public void setSkipped_leave(Boolean skipped_leave) {
		this.skipped_leave = skipped_leave;
	}


	public void setSkipped_time(Date skipped_time) {
		this.skipped_time = skipped_time;
	}

	public Boolean getIsForgotPwdEnabled() {
		return isForgotPwdEnabled;
	}

	public void setIsForgotPwdEnabled(Boolean isForgotPwdEnabled) {
		this.isForgotPwdEnabled = isForgotPwdEnabled;
	}
	
	@Column(name ="comments")
	private String comments;

	private Boolean is_activated = Boolean.FALSE;
	
	private Boolean is_deleted = Boolean.FALSE;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public RoleDetails getRoleDetails() {
		return roleDetails;
	}

	public void setRoleDetails(RoleDetails roleDetails) {
		this.roleDetails = roleDetails;
	}

	public String getUser_login_type() {
		return user_login_type;
	}

	public void setUser_login_type(String user_login_type) {
		this.user_login_type = user_login_type;
	}

	public byte[] getProfile_image() {
		return profile_image;
	}

	public void setProfile_image(byte[] profile_image) {
		this.profile_image = profile_image;
	}

	public DesignationDetails getDesignationDetails() {
		return designationDetails;
	}

	public void setDesignationDetails(DesignationDetails designationDetails) {
		this.designationDetails = designationDetails;
	}

	public Date getDate_of_birth() {
		return date_of_birth;
	}

	public void setDate_of_birth(Date date_of_birth) {
		this.date_of_birth = date_of_birth;
	}

	public String getMarital_status() {
		return marital_status;
	}

	public void setMarital_status(String marital_status) {
		this.marital_status = marital_status;
	}

	public String getBlood_group() {
		return blood_group;
	}

	public void setBlood_group(String blood_group) {
		this.blood_group = blood_group;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getWork_mobile_number() {
		return work_mobile_number;
	}

	public void setWork_mobile_number(String work_mobile_number) {
		this.work_mobile_number = work_mobile_number;
	}

	public String getPersonal_mobile_number() {
		return personal_mobile_number;
	}

	public void setPersonal_mobile_number(String personal_mobile_number) {
		this.personal_mobile_number = personal_mobile_number;
	}

	public String getPersonal_email() {
		return personal_email;
	}

	public void setPersonal_email(String personal_email) {
		this.personal_email = personal_email;
	}

	public String getPresent_address() {
		return present_address;
	}

	public void setPresent_address(String present_address) {
		this.present_address = present_address;
	}

	public String getPermanent_address() {
		return permanent_address;
	}

	public void setPermanent_address(String permanent_address) {
		this.permanent_address = permanent_address;
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

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}
	

	public Boolean getIs_activated() {
		return is_activated;
	}

	public void setIs_activated(Boolean is_activated) {
		this.is_activated = is_activated;
	}

	public Boolean getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	public OrgDetails getOrgDetails() {
		return orgDetails;
	}

	public void setOrgDetails(OrgDetails orgDetails) {
		this.orgDetails = orgDetails;
	}

//	public EmployeeDetails getReporterDetails() {
//		return reporterDetails;
//	}
//
//	public void setReporterDetails(EmployeeDetails reporterDetails) {
//		this.reporterDetails = reporterDetails;
//	}

	
	public Date getDate_of_joining() {
		return date_of_joining;
	}

	public String getReporting_manager() {
		return reporting_manager;
	}


	public void setReporting_manager(String reporting_manager) {
		this.reporting_manager = reporting_manager;
	}


	public String getReporter_name() {
		return reporter_name;
	}


	public void setReporter_name(String reporter_name) {
		this.reporter_name = reporter_name;
	}


	public void setDate_of_joining(Date date_of_joining) {
		this.date_of_joining = date_of_joining;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}


	public Boolean getnew_release() {
		return new_release;
	}


	public void setnew_release(Boolean new_release) {
		this.new_release = new_release;
	}


	public ManageBranchDetails getBranchDetails() {
		return branchDetails;
	}


	public void setBranchDetails(ManageBranchDetails branchDetails) {
		this.branchDetails = branchDetails;
	}


	public Boolean getIs_details_updated() {
		return is_details_updated;
	}


	public void setIs_details_updated(Boolean is_details_updated) {
		this.is_details_updated = is_details_updated;
	}
}
