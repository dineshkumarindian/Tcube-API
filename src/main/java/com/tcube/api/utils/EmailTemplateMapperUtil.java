package com.tcube.api.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tcube.api.PropertiesConfig;
import com.tcube.api.model.ClientDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.LeaveTrackerDetails;
import com.tcube.api.model.ManageLeaveTypes;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.ProjectDetails;

public class EmailTemplateMapperUtil {
	static PropertiesConfig config = null;
	private static Logger logger = (Logger) LogManager.getLogger(EmailTemplateMapperUtil.class);

	public static String getActivateAccountMailTemplate(EmployeeDetails details, String employeePassword,
			String loginUrl) throws Exception {
		logger.info("EmailTemplateMapperUtil(getActivateAccountMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getAcctivteAccountMailTemplate();
		String loginurl = config.getLoginUrl();
		String firstname = details.getFirstname();
		String lastname = details.getLastname();
		String email = details.getEmail();
		String password = employeePassword;
		String company_name = details.getOrgDetails().getCompany_name();
		String[] arrOfStr = loginUrl.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[LAST_NAME]", lastname);
		template = template.replace("[EMAIL]", email);
		template = template.replace("[PASSWORD]", password);
		template = template.replace("[LOGIN]", loginUrl);
		template = template.replace("[ORG_NAME]", company_name);
		logger.info("EmailTemplateMapperUtil(getActivateAccountMailTemplate)>> Exit");
		return template;
	}

//	public static String getRegisterAccountMailTemplate(EmployeeDetails details) throws Exception {
//		logger.info("EmailTemplateMapperUtil(getRegisterAccountMailTemplate)>> Entry");
//		config = PropertiesConfig.getInstance();
//		String template = config.getRegisterAccountMailTemplate();
//		String loginurl = config.getLoginUrl();
//		String firstname = details.getFirstname();
//		String lastname = details.getLastname();
//		String email = details.getEmail();
//		String password = details.getPassword();
//		template = template.replace("[FIRST_NAME]", firstname);
//		template = template.replace("[LAST_NAME]", lastname);
//		template = template.replace("[EMAIL]", email);
//		template = template.replace("[PASSWORD]", password);
//		template = template.replace("[LOGIN]", loginurl);
//		logger.info("EmailTemplateMapperUtil(getRegisterAccountMailTemplate)>> Exit");
//		return template;
//	}

	public static String getForgetPasswordMailTemplate(EmployeeDetails details, String loginUrl) throws Exception {
		logger.info("EmailTemplateMapperUtil(getForgetPasswordMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getForgetPasswordMailTemplate();
		String loginurl = config.getLoginUrl();
		String firstname = details.getFirstname();
		String company_name = details.getOrgDetails().getCompany_name();
		String password = details.getMail_otp();
		String[] arrOfStr = loginUrl.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[PASSWORD]", EncryptorUtil.decryptPropertyValue(password));
		template = template.replace("[LOGIN]", loginUrl);
		template = template.replace("[ORG_NAME]", company_name);
		logger.info("EmailTemplateMapperUtil(getForgetPasswordMailTemplate)>> Exit");
		return template;
	}

	public static String getHoursReachedMailTemplate(EmployeeDetails details, JobDetails jobDetails, String loginUrl)
			throws Exception {
		logger.info("EmailTemplateMapperUtil(getHoursReachedMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getHoursReachedMailTemplate();
		String loginurl = config.getLoginUrl();
		String firstname = details.getFirstname();
		String company_name = details.getOrgDetails().getCompany_name();
		String jobname = jobDetails.getJob_name();
		String hours = jobDetails.getHours().toString();
		String[] arrOfStr = loginUrl.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[JOB_NAME]", jobname);
		template = template.replace("[ESTIMATED_HOURS]", hours);
		template = template.replace("[LOGIN]", loginUrl);
		template = template.replace("[ORG_NAME]", company_name);
		logger.info("EmailTemplateMapperUtil(getHoursReachedMailTemplate)>> Exit");
		return template;
	}

	public static String getTimelogsSubmittedMailTemplate(EmployeeDetails empdetails, EmployeeDetails approverDetails,
			String totalHours, String approvals_url) throws Exception {
		logger.info("EmailTemplateMapperUtil(getTimelogsSubmittedMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getTimelogsSubmittedMailTemplate();
		String approvalsurl = config.getApprovalsUrl();
		String[] arrOfStr = approvals_url.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		String firstname = approverDetails.getFirstname();
		String company_name = empdetails.getOrgDetails().getCompany_name();
		String id = empdetails.getId();
		String submittedBYFirstName = empdetails.getFirstname();
		template = template.replace("[SUBMITTED_BY_ID]", id);
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[SUBMITTED_BY_FIRST_NAME]", submittedBYFirstName);
		template = template.replace("[TOTAL_HOURS]", totalHours);
		template = template.replace("[APPROVALS]", approvals_url);
		template = template.replace("[ORG_NAME]", company_name);
		
//		template = template.replace("[approval_url]",approvals_url);
		logger.info("EmailTemplateMapperUtil(getTimelogsSubmittedMailTemplate)>> Exit");
		return template;
	}

	public static String getTimesheetsApprovedOrRejectedMailTemplate(EmployeeDetails empdetails,
			EmployeeDetails approverDetails, String totalHours, String status, String comment, String timesheets_url)
			throws Exception {
		logger.info("EmailTemplateMapperUtil(getTimesheetsApprovedMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getApprovedOrRejectedTimelogsMailTemplate();
		String timesheetssurl = config.getTimesheetsUrl();
		String firstname = empdetails.getFirstname();
		String company_name = empdetails.getOrgDetails().getCompany_name();
		String id = approverDetails.getId();
		String approvedBYFirstName = approverDetails.getFirstname();
		String[] arrOfStr = timesheets_url.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		template = template.replace("[APPROVED_BY_ID]", id);
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[APPROVED_BY_FIRST_NAME]", approvedBYFirstName);
		template = template.replace("[TOTAL_HOURS]", totalHours);
		template = template.replace("[APPROVEL_COMMENT]", comment);
		template = template.replace("[STATUS_OF_TIMESHEET]", status);
		template = template.replace("[TIMESHEETS]", timesheets_url);
		template = template.replace("[ORG_NAME]", company_name);
		logger.info("EmailTemplateMapperUtil(getTimesheetsApprovedMailTemplate)>> Exit");
		return template;
	}

	public static String getNewProjectUsersMailTemplate(EmployeeDetails empdetails, String s_date, String e_date,
			String pt_name, String projects_url) throws Exception {
		logger.info("EmailTemplateMapperUtil(getNewProjectUsersMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getNewProjectUsersEmailTemplate();
		String projectsurl = config.getProjectsUrl();
		String firstname = empdetails.getFirstname();
		String company_name = empdetails.getOrgDetails().getCompany_name();
		String[] arrOfStr = projects_url.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[PROJECT_NAME]", pt_name);
		template = template.replace("[START_DATE]", s_date);
		template = template.replace("[END_DATE]", e_date);
//		template = template.replace("[PROJECT_COST]", pt_cost);
		template = template.replace("[PROJECTS]", projects_url);
		template = template.replace("[ORG_NAME]", company_name);
		logger.info("EmailTemplateMapperUtil(getNewProjectUsersMailTemplate)>> Exit");
		return template;
	}

	public static String getAddUsersToProjectMailTemplate(EmployeeDetails empdetails, String s_date, String e_date,
			String pt_name, String projects_url) throws Exception {
		logger.info("EmailTemplateMapperUtil(getNewProjectUsersMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getAddUsersToProjectMailTemplate();
		String projectsurl = config.getProjectsUrl();
		String firstname = empdetails.getFirstname();
		String company_name = empdetails.getOrgDetails().getCompany_name();
		String[] arrOfStr = projects_url.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[PROJECT_NAME]", pt_name);
		template = template.replace("[START_DATE]", s_date);
		template = template.replace("[END_DATE]", e_date);
//		template = template.replace("[PROJECT_COST]", pt_cost);
		template = template.replace("[PROJECTS]", projects_url);
		template = template.replace("[ORG_NAME]", company_name);
		logger.info("EmailTemplateMapperUtil(getNewProjectUsersMailTemplate)>> Exit");
		return template;
	}

	public static String getNewJobAssigneesMailTemplate(EmployeeDetails empdetails, String s_date, String e_date,
			String jb_name, String hours, String jobs_url) throws Exception {
		logger.info("EmailTemplateMapperUtil(getNewJobAssigneesMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getNewJobAssigneesMailTemplate();
		String jobsurl = config.getJobsUrl();
		String firstname = empdetails.getFirstname();
		String company_name = empdetails.getOrgDetails().getCompany_name();
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[JOB_NAME]", jb_name);
		template = template.replace("[START_DATE]", s_date);
		template = template.replace("[END_DATE]", e_date);
		template = template.replace("[ESTIMATED_HOURS]", hours);
		template = template.replace("[JOBS]", jobs_url);
		template = template.replace("[ORG_NAME]", company_name);
		String[] arrOfStr = jobs_url.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getNewJobAssigneesMailTemplate)>> Exit");
		return template;
	}

	public static String getOrganizationActivateAccountMailTemplate(OrgDetails details, String comment, String loginUrl)
			throws Exception {
		logger.info("EmailTemplateMapperUtil(getOrganizationActivateAccountMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getOrganizationActivteAccountMailTemplate();
		String loginurl = config.getLoginUrl();
		String firstname = details.getFirstname();
		String orgname = details.getCompany_name();
//		String lastname = details.getLastname();
		String email = details.getEmail();
		String password = details.getPassword();
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[ORG_NAME]", orgname);
		template = template.replace("[EMAIL]", email);
		template = template.replace("[PASSWORD]", EncryptorUtil.decryptPropertyValue(password));
		template = template.replace("[ACTIVATE_COMMENT]", comment);
		template = template.replace("[LOGIN]", loginUrl);
		String[] arrOfStr = loginUrl.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getOrganizationActivateAccountMailTemplate)>> Exit");
		return template;
	}

	public static String getOrganizationDeactivateAccountMailTemplate(OrgDetails checkEmp, String comment, String url)
			throws Exception {
		logger.info("EmailTemplateMapperUtil(getOrganizationDeactivateAccountMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getOrganizationDeactivteAccountMailTemplate();
		String firstname = checkEmp.getFirstname();
		String orgname = checkEmp.getCompany_name();
		String email = checkEmp.getEmail();
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[ORG_NAME]", orgname);
		template = template.replace("[EMAIL]", email);
		template = template.replace("[DEACTIVATE_COMMENT]", comment);
		if(url=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getOrganizationDeactivateAccountMailTemplate)>> Exit");
		return template;
	}

	public static String getOrgRegistrationMailTemplate(EmployeeDetails details, String url) throws Exception {
		logger.info("EmailTemplateMapperUtil(getOrgRegistrationMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getOrgRegistrationMailTemplate();
		String firstname = details.getFirstname();
		String lastname = details.getLastname();
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[LAST_NAME]", lastname);
		if(url=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getOrgRegistrationMailTemplate)>> Exit");
		return template;
	}

	public static String getApprovedOrgMailTemplate(OrgDetails details, String comment, String loginUrl)
			throws Exception {
		logger.info("EmailTemplateMapperUtil(getApprovedOrgMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getOrgApprovedMailTemplate();
		String loginurl = config.getLoginUrl();
		String firstname = details.getFirstname();
		String lastname = details.getLastname();
		String email = details.getEmail();
		String password = details.getPassword();
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[LAST_NAME]", lastname);
		template = template.replace("[EMAIL]", email);
		System.out.println(password);
		System.out.println(EncryptorUtil.decryptPropertyValue(password));
		template = template.replace("[PASSWORD]", EncryptorUtil.decryptPropertyValue(password));
		template = template.replace("[COMMENT]", comment);
		template = template.replace("[LOGIN]", loginUrl);
		String[] arrOfStr = loginUrl.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getApprovedOrgMailTemplate)>> Exit");
		return template;
	}

	public static String getTrialApprovedOrgMailTemplate(OrgDetails details, String comment, String loginUrl)
			throws Exception {
		logger.info("EmailTemplateMapperUtil(getTrialApprovedOrgMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getTrialOrgApprovedMailTemplate();
		String TrialDays = config.getTrialDays();
		String TrialUserLimit = config.getTrialUserLimit();
		String firstname = details.getFirstname();
		String lastname = details.getLastname();
		String email = details.getEmail();
		String password = details.getPassword();
		String orgname = details.getCompany_name();
		template = template.replace("[ORG_NAME]", orgname);
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[LAST_NAME]", lastname);
		template = template.replace("[EMAIL]", email);
		template = template.replace("[PASSWORD]", EncryptorUtil.decryptPropertyValue(password));
		template = template.replace("[COMMENT]", comment);
		template = template.replace("[LOGIN]", loginUrl);
		template = template.replace("[TrialDays]", TrialDays);
		template = template.replace("[TrialUserLimit]", TrialUserLimit);
		String[] arrOfStr = loginUrl.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getApprovedOrgMailTemplate)>> Exit");
		return template;
	}

	public static String getRejectedOrgMailTemplate(OrgDetails details, String comment, String loginUrl) throws Exception {
		logger.info("EmailTemplateMapperUtil(getRejectedOrgMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getOrgRejectedMailTemplate();
		String orgname = details.getCompany_name();
		String email = details.getEmail();
		template = template.replace("[ORG_NAME]", orgname);
		template = template.replace("[EMAIL]", email);
		template = template.replace("[COMMENT]", comment);
		String[] arrOfStr = loginUrl.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getRejectedOrgMailTemplate)>> Exit");
		return template;
	}

//	public static String getClientActivateAccountMailTemplate(ClientDetails details,String comment) throws Exception {
//		logger.info("EmailTemplateMapperUtil(getClientActivateAccountMailTemplate)>> Entry");
//		config = PropertiesConfig.getInstance();
//		String template = config.getClientActivteAccountMailTemplate();
//		String loginurl = config.getLoginUrl();
//		String firstname = details.getFirstname();
//		String clientname = details.getClient_name();
//		String email = details.getEmail();
//		template = template.replace("[FIRST_NAME]", firstname);
//		template = template.replace("[CLIENT_NAME]", clientname);
//		template = template.replace("[EMAIL]", email);
//		template = template.replace("[ACTIVATE_COMMENT]",comment );
//		template = template.replace("[LOGIN]", loginurl);
//		logger.info("EmailTemplateMapperUtil(getClientActivateAccountMailTemplate)>> Exit");
//		return template;
//	}
//	public static String getClientDeactivateAccountMailTemplate(ClientDetails details,String comment) throws Exception {
//		logger.info("EmailTemplateMapperUtil(getClientDeactivateAccountMailTemplate)>> Entry");
//		config = PropertiesConfig.getInstance();
//		String template = config.getClientDeactivteAccountMailTemplate();
//		String firstname = details.getFirstname();
//		String clientname = details.getClient_name();
//		String email = details.getEmail();
//		template = template.replace("[FIRST_NAME]", firstname);
//		template = template.replace("[CLIENT_NAME]", clientname);
//		template = template.replace("[EMAIL]", email);
//		template = template.replace("[DEACTIVATE_COMMENT]",comment );
//		logger.info("EmailTemplateMapperUtil(getClientDeactivateAccountMailTemplate)>> Exit");
//		return template;
//	}
	public static String getEmployeeActivateAccountMailTemplate(EmployeeDetails details, String comment,
			String loginUrl, String orgCompanyDetails) throws Exception {
		logger.info("EmailTemplateMapperUtil(getEmployeeActivateAccountMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getEmployeeActivteAccountMailTemplate();
		String loginurl = config.getLoginUrl();
		String firstname = details.getFirstname();
		String employeename = details.getFirstname();
		String email = details.getEmail();
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[EMPLOYEE_NAME]", employeename);
		template = template.replace("[EMAIL]", email);
		template = template.replace("[ACTIVATE_COMMENT]", comment);
		template = template.replace("[LOGIN]", loginUrl);
		template = template.replace("[ORG_NAME]", orgCompanyDetails);
		String[] arrOfStr = loginUrl.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getEmployeeActivateAccountMailTemplate)>> Exit");
		return template;
	}

	public static String getEmployeeDeactivateAccountMailTemplate(EmployeeDetails details, String comment,
			String orgCompanyDetails, String url) throws Exception {
		logger.info("EmailTemplateMapperUtil(getEmployeeDeactivateAccountMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getEmployeeDeactivteAccountMailTemplate();
		String firstname = details.getFirstname();
		String employeename = details.getFirstname();
		String email = details.getEmail();
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[EMPLOYEE_NAME]", employeename);
		template = template.replace("[EMAIL]", email);
		template = template.replace("[DEACTIVATE_COMMENT]", comment);
		template = template.replace("[ORG_NAME]", orgCompanyDetails);
		if(url=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getEmployeeDeactivateAccountMailTemplate)>> Exit");
		return template;
	}

	public static String getupgradeOrgPlanMailTemplate(OrgDetails details, String url) throws Exception {
		logger.info("EmailTemplateMapperUtil(getupgradeOrgPlanMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getUpgradePricingPlanMailTemplate();
		String firstname = details.getFirstname();
		String orgname = details.getCompany_name();
		String email = details.getEmail();
		String plan = details.getPlan();
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[ORG_NAME]", orgname);
		template = template.replace("[EMAIL]", email);
		template = template.replace("[PLAN]", plan);
		if(url=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getupgradeOrgPlanMailTemplate)>> Exit");
		return template;
	}

	// sending mail notification for reject leave request
	public static String getRejectLeaveRequestMailTemplate(EmployeeDetails eDetails, ManageLeaveTypes leaveType,
			LeaveTrackerDetails oldDetails, String url) throws Exception {
		logger.info("EmailTemplateMapperUtil(getRejectLeaveRequestMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getRejectLeaveRequestMailTemplate();
		String firstname = eDetails.getFirstname();
		String employeename = eDetails.getFirstname();
		String email = eDetails.getEmail();
		String CompanyName = eDetails.getOrgDetails().getCompany_name();
		String leaveType1 = leaveType.getLeave_type();
		String comment = eDetails.getComments();
		String totaldays = String.valueOf(oldDetails.getTotal_days());
		Date startDate = oldDetails.getStart_date();
		Date EndDate = oldDetails.getEnd_date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		String strDate = formatter.format(startDate);
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
		String endDate = formatter1.format(EndDate);
//		String status = oldDetails.getApproval_status();
		String approvalName = oldDetails.getReporter_name();

		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[EMPLOYEE_NAME]", employeename);
		template = template.replace("[EMAIL]", email);
		template = template.replace("[DEACTIVATE_COMMENT]", comment);
		template = template.replace("[LEAVETYPE]", leaveType1);
		template = template.replace("[STARTDATE]", strDate);
		template = template.replace("[ENDDATE]", endDate);
		template = template.replace("[ORG_NAME]", CompanyName);
		template = template.replace("[TOTALDAYS]", totaldays);
		template = template.replace("[APPROVEL_COMMENT]", comment);
		template = template.replace("[APPROVER_NAME]", approvalName);
		template = template.replace("[URL]", url);
		String[] arrOfStr = url.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getRejectLeaveRequestMailTemplate)>> Exit");
//		template = template.replace("[STATUS]",status);
		return template;
//		return null;

	}

	public static String getApproveLeaveRequestMailTemplate(EmployeeDetails eDetails, ManageLeaveTypes leaveType,
			LeaveTrackerDetails oldDetails, String url) throws Exception {
		logger.info("EmailTemplateMapperUtil(getRejectLeaveRequestMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getApproveLeaveRequestMailTemplate();
		String firstname = eDetails.getFirstname();
		String employeename = eDetails.getFirstname();
		String email = eDetails.getEmail();
		String CompanyName = eDetails.getOrgDetails().getCompany_name();
		String leaveType1 = leaveType.getLeave_type();
		String comment = eDetails.getComments();
		String totaldays = String.valueOf(oldDetails.getTotal_days());
		Date startDate = oldDetails.getStart_date();
		Date EndDate = oldDetails.getEnd_date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		String strDate = formatter.format(startDate);
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
		String endDate = formatter1.format(EndDate);
//		String status = oldDetails.getApproval_status();
		String approvalName = oldDetails.getReporter_name();

		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[EMPLOYEE_NAME]", employeename);
		template = template.replace("[EMAIL]", email);
		template = template.replace("[DEACTIVATE_COMMENT]", comment);
		template = template.replace("[LEAVETYPE]", leaveType1);
		template = template.replace("[STARTDATE]", strDate);
		template = template.replace("[ENDDATE]", endDate);
		template = template.replace("[ORG_NAME]", CompanyName);
		template = template.replace("[TOTALDAYS]", totaldays);
		template = template.replace("[APPROVEL_COMMENT]", comment);
		template = template.replace("[APPROVER_NAME]", approvalName);
		template = template.replace("[URL]", url);
		String[] arrOfStr = url.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		logger.info("EmailTemplateMapperUtil(getRejectLeaveRequestMailTemplate)>> Exit");
//		template = template.replace("[STATUS]",status);
		return template;
//		return null;

	}

	public static String getApplyLeaveMailTemplate(LeaveTrackerDetails details, String reasonForLeave,
			EmployeeDetails empdetails, String approverName, String leaveTrackerUrl) throws Exception {
		logger.info("EmailTemplateMapperUtil(getApplyLeaveMailTemplate)>> Entry");
		config = PropertiesConfig.getInstance();
		String template = config.getApplyLeaveMailTemplate();
		String leaveApprovalUrl = leaveTrackerUrl;
		String firstname = approverName;
		String company_name = empdetails.getOrgDetails().getCompany_name();
		String id = empdetails.getId();
		Date startDate = details.getStart_date();
		Date endDate = details.getEnd_date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		String strStartDate = formatter.format(startDate);
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
		String strEndDate = formatter1.format(endDate);
		String total_days = String.valueOf(details.getTotal_days());
		String reason_for_leave = reasonForLeave;
		String leaveType = details.getLeave_type().toUpperCase();
		String submittedBYFirstName = empdetails.getFirstname();
		template = template.replace("[SUBMITTED_BY_ID]", id);
		template = template.replace("[FIRST_NAME]", firstname);
		template = template.replace("[SUBMITTED_BY_FIRST_NAME]", submittedBYFirstName);
		template = template.replace("[APPROVALS]", leaveApprovalUrl);
		template = template.replace("[ORG_NAME]", company_name);
		template = template.replace("[STARTDATE]", strStartDate);
		template = template.replace("[ENDDATE]", strEndDate);
		template = template.replace("[TOTAL_DAYS]", total_days);
		template = template.replace("[LEAVETYPE]", leaveType);
		template = template.replace("[REASON_FOR_LEAVE]", reason_for_leave);
		String[] arrOfStr = leaveTrackerUrl.split("/", 4);
		if(arrOfStr[2]=="app.tcube.io"){
			String logoImageUrl = config.getLogoImageUrlPROD();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
		else {
			String logoImageUrl = config.getLogoImageUrlUAT();
			template = template.replace("[LOGO_IMAGE_URL]", logoImageUrl);
		}
//		template = template.replace("[approval_url]",approvals_url);
		logger.info("EmailTemplateMapperUtil(getTimelogsSubmittedMailTemplate)>> Exit");
		return template;
	}

}
