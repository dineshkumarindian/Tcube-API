package com.tcube.api;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tcube.api.utils.Constants;

/**
 * application specific properties configuration class instance
 *
 */
public class PropertiesConfig {
	private static final Logger LOGGER = (Logger) LogManager.getLogger(PropertiesConfig.class);
	private static PropertiesConfig singleton;
	private final Properties properties = new Properties();

	/**
	 * this is a static method to get the instance of the 'properties'
	 * 
	 * @return
	 * @throws Exception
	 */
	public static PropertiesConfig getInstance() throws Exception {
		if (singleton == null) {
			load();
		}
		return singleton;
	}

	/**
	 * this is a static method to load the properties file from classpath
	 * 
	 * @return
	 * @throws Exception
	 */
	private static synchronized void load() throws Exception {
		LOGGER.info("PropertiesConfig(load) >> Entry");
		try {
			InputStream stream = null;
			stream = PropertiesConfig.class.getClassLoader().getResourceAsStream(Constants.CONFIG_FILE_LOCATION);
			singleton = new PropertiesConfig();
			singleton.properties.load(stream);
		} catch (final Exception exception) {
			LOGGER.error("Error in PropertiesConfig(load),Exception Details {}", exception);
		}
		LOGGER.info("PropertiesConfig(load) >> Exit");
	}


	/**
	 * This will read and return the mail config call info string from properties file
	 * 
	 * @return string
	 */

	public String getsmtpHost() {
		return properties.getProperty(Constants.SMTP_HOST);
	}

	public String getsmtpPort() {
		return properties.getProperty(Constants.SMTP_PORT);
	}

	public String getUsername() {
		return properties.getProperty(Constants.USERNAME);
	}

	public String getPassword() {
		return properties.getProperty(Constants.PASSWORD);
	}
	
	
	/**
	 * This will read and return the mail template info string from properties file
	 * 
	 * @return string
	 */
	
	public String getLoginUrl() {
		return properties.getProperty(Constants.LOGIN_URL);
	}
	
	public String getApprovalsUrl() {
		return properties.getProperty(Constants.APPROVALS_URL);
	}
//	public String getApplyLeaveUrl() {
//		return properties.getProperty(Constants.APPLYLEAVE_URL);
//	}
	
	public String getTimesheetsUrl() {
		return properties.getProperty(Constants.TIMESHEETS_URL);
	}
	
	public String getProjectsUrl() {
		return properties.getProperty(Constants.PROJECTS_URL);
	}
	
	public String getJobsUrl() {
		return properties.getProperty(Constants.JOBS_URL);
	}
	
	public String getAcctivteAccountMailTemplate() {
		return properties.getProperty(Constants.ACTIVATE_ACCOUNT_EMAIL_TEMPLATE);
	}
	
//	public String getRegisterAccountMailTemplate() {
//		return properties.getProperty(Constants.REGISTER_ACCOUNT_EMAIL_TEMPLATE);
//	}
	
	public String getForgetPasswordMailTemplate() {
		return properties.getProperty(Constants.FORGET_PASSWORD_EMAIL_TEMPLATE);
	}
	
	public String getHoursReachedMailTemplate() {
		return properties.getProperty(Constants.HOURS_REACHED_EMAIL_TEMPLATE);
	}
	
	public String getTimelogsSubmittedMailTemplate() {
		return properties.getProperty(Constants.TIMELOGS_SUBMITTED_EMAIL_TEMPLATE);
	}

	public String getApprovedOrRejectedTimelogsMailTemplate() {
		return properties.getProperty(Constants.TIMELOGS_APPROVED_OR_REJECTED_EMAIL_TEMPLATE);
	}

	
	public String getNewProjectUsersEmailTemplate() {
		return properties.getProperty(Constants.NEW_PROJECT_USERS_EMAIL_TEMPLATE);
	}
	
	public String getAddUsersToProjectMailTemplate() {
		return properties.getProperty(Constants.ADD_USERS_TO_PROJECT_EMAIL_TEMPLATE);
	}
	
	public String getNewJobAssigneesMailTemplate() {
		return properties.getProperty(Constants.NEW_JOB_ASSIGNEES_EMAIL_TEMPLATE);
	}
	public String getOrganizationActivteAccountMailTemplate() {
		return properties.getProperty(Constants.ORGANIZATION_ACTIVATE_ACCOUNT_EMAIL_TEMPLATE);
	}

	public String getOrganizationDeactivteAccountMailTemplate() {
		// TODO Auto-generated method stub
		return properties.getProperty(Constants.ORGANIZATION_DEACTIVATE_ACCOUNT_EMAIL_TEMPLATE);
	}
	
	public String getOrgRegistrationMailTemplate() {
		return properties.getProperty(Constants.ORG_REGISTRATION_EMAIL_TEMPLATE);
	}

	public String getOrgApprovedMailTemplate() {
		return properties.getProperty(Constants.APPROVED_ORG_DETAILS_EMAIL_TEMPLATE);
	}

	public String getTrialOrgApprovedMailTemplate() {
		return properties.getProperty(Constants.Trial_ACTIVATE_ACCOUNT_EMAIL_TEMPLATE);
	}
	
	public String getOrgRejectedMailTemplate() {
		return properties.getProperty(Constants.REJECTED_ORG_DETAILS_EMAIL_TEMPLATE);
	}
//	public String getClientActivteAccountMailTemplate() {
//		return properties.getProperty(Constants.CLIENT_ACTIVATE_ACCOUNT_EMAIL_TEMPLATE);
//	}

//	public String getClientDeactivteAccountMailTemplate() {
//		// TODO Auto-generated method stub
//		return properties.getProperty(Constants.CLIENT_DEACTIVATE_ACCOUNT_EMAIL_TEMPLATE);
//	}
	public String getEmployeeActivteAccountMailTemplate() {
		return properties.getProperty(Constants.EMPLOYEE_ACTIVATE_ACCOUNT_EMAIL_TEMPLATE);
	}

	public String getEmployeeDeactivteAccountMailTemplate() {
		// TODO Auto-generated method stub
		return properties.getProperty(Constants.EMPLOYEE_DEACTIVATE_ACCOUNT_EMAIL_TEMPLATE);
	}
	
	public String getUpgradePricingPlanMailTemplate() {
		return properties.getProperty(Constants.UPGRADE_ORG_PRICING_PLAN_TEMPLATE);
	}

	public String getRejectLeaveRequestMailTemplate() {
		// TODO Auto-generated method stubleave_request_reject_template
		return properties.getProperty(Constants.LEAVE_REQUEST_REJECT_TEMPLATE);
	}
	
	public String getApproveLeaveRequestMailTemplate() {
		return properties.getProperty(Constants.LEAVE_REQUEST_APPROVE_TEMPLATE);
	}
	
	public String getApplyLeaveMailTemplate() {
		return properties.getProperty(Constants.LEAVE_APPLY_LEAVE_TEMPLATE);
	}
	public String getLogoImageUrlUAT() {
		return properties.getProperty(Constants.LOGO_IMAGE_URL_UAT);
	}
	
	public String getLogoImageUrlPROD() {
		// TODO Auto-generated method stub
		return properties.getProperty(Constants.LOGO_IMAGE_URL_PROD);
	}
	
	/**
	 * This will read and return the rest api user name string from properties file
	 * 
	 * @return string
	 */
	public String getRestApiUsername() {
		return properties.getProperty(Constants.REST_API_USERNAME);
	}

	/**
	 * This will read and return the rest api password string from properties file
	 * 
	 * @return string
	 */
	public String getRestApiPassword() {
		return properties.getProperty(Constants.REST_API_PASSWORD);
	}

	public String getTrialDays() {
		return properties.getProperty(Constants.TrialDays);
	}

	public String getTrialUserLimit() {
		return properties.getProperty(Constants.TrialUserLimit);
	}

}
