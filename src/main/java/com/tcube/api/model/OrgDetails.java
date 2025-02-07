package com.tcube.api.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name = "org_details")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class OrgDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "org_id")
	private Long org_id;
	
	@Column(name = "emp_id")
	private String emp_id;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "firstname")
	private String firstname;
	
	@Column(name = "lastname")
	private String lastname;
	
	@Column(name = "company_name")
	private String company_name;
	
	@Column(name = "deactivated_ids")
	private String deactivated_ids;
	
	@Column(name = "date_of_joining")
	private Date date_of_joining;
	
	@Column(name = "created_time")
	private Date created_time;

	@Column(name = "modified_time")
	private Date modified_time;
	
	@Column(name ="comments")
	private String comments;
	
	private Boolean is_deleted = Boolean.FALSE;

	private Boolean is_activated = Boolean.FALSE;
	
	private Boolean is_leavetype_created = Boolean.FALSE;
	
	private Boolean first_time = Boolean.TRUE;
	
	
	@Column(name ="pricing_plan")
	private String plan;
	
	@Column(name ="status")
	private String status;
	
	@Column(name ="approve_reject_comments")
	private String apporrejcomments;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "plan_id", referencedColumnName = "id")
	private PricingPlanDetails pricingPlanDetails;
	
	private Boolean plan_upgrade = Boolean.FALSE;
	
	@Column(name = "plan_expiry_date")
	private Date expiry_date;
	
	private Boolean plan_renewal = Boolean.FALSE;
	
	@Column(name = "plan_approve_reject_time")
	private Date apprejtime;
	
	@Column(name = "isForgotPwdEnabled")
	private Boolean isForgotPwdEnabled = Boolean.FALSE;
	
	private String oldPassword;

	@Column(name = "description")
	private String desc;

	@Column(name = "userslimit" , columnDefinition = "VARCHAR(255) DEFAULT '0'")
	private String userslimit = "0" ;
	
	@Column(name="mail_otp")
	private String mail_otp;

	public String getMail_otp() {
		return mail_otp;
	}

	public void setMail_otp(String mail_otp) {
		this.mail_otp = mail_otp;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public Boolean getIsForgotPwdEnabled() {
		return isForgotPwdEnabled;
	}

	public void setIsForgotPwdEnabled(Boolean isForgotPwdEnabled) {
		this.isForgotPwdEnabled = isForgotPwdEnabled;
	}
	
	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}
	
	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
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

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public Date getDate_of_joining() {
		return date_of_joining;
	}

	public void setDate_of_joining(Date date_of_joining) {
		this.date_of_joining = date_of_joining;
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

	public Boolean getIs_activated() {
		return is_activated;
	}

	public void setIs_activated(Boolean is_activated) {
		this.is_activated = is_activated;
	}

	public String getDeactivated_ids() {
		return deactivated_ids;
	}

	public void setDeactivated_ids(String deactivated_ids) {
		this.deactivated_ids = deactivated_ids;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Boolean getIs_leavetype_created() {
		return is_leavetype_created;
	}

	public void setIs_leavetype_created(Boolean is_leavetype_created) {
		this.is_leavetype_created = is_leavetype_created;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApporrejcomments() {
		return apporrejcomments;
	}

	public void setApporrejcomments(String apporrejcomments) {
		this.apporrejcomments = apporrejcomments;
	}

	public Boolean getFirst_time() {
		return first_time;
	}

	public void setFirst_time(Boolean first_time) {
		this.first_time = first_time;
	}

	public PricingPlanDetails getPricingPlanDetails() {
		return pricingPlanDetails;
	}

	public void setPricingPlanDetails(PricingPlanDetails pricingPlanDetails) {
		this.pricingPlanDetails = pricingPlanDetails;
	}

	public Boolean getPlan_upgrade() {
		return plan_upgrade;
	}

	public void setPlan_upgrade(Boolean plan_upgrade) {
		this.plan_upgrade = plan_upgrade;
	}

	public Date getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(Date expiry_date) {
		this.expiry_date = expiry_date;
	}

	public Boolean getPlan_renewal() {
		return plan_renewal;
	}

	public void setPlan_renewal(Boolean plan_renewal) {
		this.plan_renewal = plan_renewal;
	}

    public Date getApprejtime() {
        return apprejtime;
    }

    public void setApprejtime(Date apprejtime) {
        this.apprejtime = apprejtime;
    }

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUserslimit() {
		return userslimit;
	}

	public void setUserslimit(String userslimit) {
		this.userslimit = userslimit;
	}
}
