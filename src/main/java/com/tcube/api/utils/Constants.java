package com.tcube.api.utils;

/**
 * Constants for Email template
 *
 */
public class Constants{

	
	/*config property related strings*/
	public static final String CONFIG_FILE_LOCATION = "config.properties";
	public static final String REST_API_USERNAME = "rest_api_username";
	public static final String REST_API_PASSWORD = "rest_api_password";
	
	/*email property related strings*/
	public static final String SMTP_HOST = "smtp_host";
	public static final String SMTP_PORT = "smtp_port";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "email_password";
	public static final String LOGO_IMAGE_URL_UAT ="logo_image_url_UAT";
	public static final String LOGO_IMAGE_URL_PROD ="logo_image_url_PROD";
	/*email template property related strings*/
	public static final String LOGIN_URL="login_url";
	public static final String APPROVALS_URL="approvals_url";
//	public static final String APPLYLEAVE_URL="leave-tracker";
	public static final String PROJECTS_URL="projects_url";
	public static final String TIMESHEETS_URL="timesheets_url";
	public static final String JOBS_URL="jobs_url";
	public static final String ACTIVATE_ACCOUNT_EMAIL_TEMPLATE = "activate_account_email_template";
	public static final String Trial_ACTIVATE_ACCOUNT_EMAIL_TEMPLATE = "trail_account_email_template";

//	public static final String REGISTER_ACCOUNT_EMAIL_TEMPLATE = "register_account_email_template";
	public static final String FORGET_PASSWORD_EMAIL_TEMPLATE = "forget_password_email_template";
	public static final String HOURS_REACHED_EMAIL_TEMPLATE = "hours_reached_email_template";		/* Mail template for --> once the job logged hours reached estimated hours  */
	public static final String TIMELOGS_SUBMITTED_EMAIL_TEMPLATE = "timelogs_submitted_email_template" ; 		/* Mail template for --> send mail to approver while submit the tasks */
	public static final String TIMELOGS_APPROVED_OR_REJECTED_EMAIL_TEMPLATE = "timelogs_approved_or_rejected_email_template" ;		/* Mail template for --> send mail to user while approver approve or reject user request */
	public static final String NEW_PROJECT_USERS_EMAIL_TEMPLATE = "new_project_users_email_teamplate" ;   /* Mail template for --> send mail notification to assigned users while creating new project */
	public static final String ADD_USERS_TO_PROJECT_EMAIL_TEMPLATE = "add_users_to_project_email_teamplate" ;   /* Mail template for --> send mail notification to add new user to project */
	public static final String NEW_JOB_ASSIGNEES_EMAIL_TEMPLATE = "new_job_assignees_email_template" ;   /* Mail template for --> send mail notification to assigned users while creating new job */
	public static final String ORGANIZATION_ACTIVATE_ACCOUNT_EMAIL_TEMPLATE = "organization_activate_account_email_template";   /* Mail template for --> send mail notification to organization while activating their account */
	public static final String ORGANIZATION_DEACTIVATE_ACCOUNT_EMAIL_TEMPLATE = "organization_deactivate_account_email_template";   /* Mail template for --> send mail notification to organization while activating their account */
	public static final String ORG_REGISTRATION_EMAIL_TEMPLATE = "org_registration_email_template"; /* Mail template for --> send mail notification to organization for after registration recent work(09/08/2022) */
	public static final String APPROVED_ORG_DETAILS_EMAIL_TEMPLATE = "approved_org_details_email_template";
	public static final String REJECTED_ORG_DETAILS_EMAIL_TEMPLATE = "rejected_org_details_email_template";
//	public static final String CLIENT_ACTIVATE_ACCOUNT_EMAIL_TEMPLATE = "client_activate_account_email_template";   /* Mail template for --> send mail notification to client while activating their account */
//	public static final String CLIENT_DEACTIVATE_ACCOUNT_EMAIL_TEMPLATE = "client_deactivate_account_email_template";   /* Mail template for --> send mail notification to client while deactivating their account */
	public static final String EMPLOYEE_ACTIVATE_ACCOUNT_EMAIL_TEMPLATE = "employee_activate_account_email_template";   /* Mail template for --> send mail notification to employee while activating their account */
	public static final String EMPLOYEE_DEACTIVATE_ACCOUNT_EMAIL_TEMPLATE = "employee_deactivate_account_email_template";   /* Mail template for --> send mail notification to employee while deactivating their account */
	public static final String UPGRADE_ORG_PRICING_PLAN_TEMPLATE = "upgrade_org_pricing_plan_template"; /*mail template for the upgrade the plan details*/

	public static final String LEAVE_REQUEST_REJECT_TEMPLATE = "leave_request_reject_template"; /*the reject the apply leave add mail */
	
	public static final String LEAVE_REQUEST_APPROVE_TEMPLATE = "leave_request_approve_template";
	
	public static final String LEAVE_APPLY_LEAVE_TEMPLATE ="leave_apply_leave_template";

	public static final String TrialDays ="TrialDays";

	public static final String TrialUserLimit ="TrialUserLimit";

}
