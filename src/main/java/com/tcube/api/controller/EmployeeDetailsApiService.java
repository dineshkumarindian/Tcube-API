package com.tcube.api.controller;

import java.text.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.tcube.api.model.ClientDetails;
import com.tcube.api.model.CustomEmployeeDetails;
import com.tcube.api.model.CustomEmployeeDetails2;
import com.tcube.api.model.DayPlannerDetails;
import com.tcube.api.model.DesignationDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.JobAssigneeDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.LeaveTrackerDetails;
import com.tcube.api.model.MailConfigDetails;
import com.tcube.api.model.ManageBranchDetails;
import com.tcube.api.model.ManageIntegration;
import com.tcube.api.model.NotificationsDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.ProjectDetails;
import com.tcube.api.model.ProjectResourceDetails;
import com.tcube.api.model.RoleDetails;
import com.tcube.api.service.DayPlannerDetailsService;
import com.tcube.api.service.DesignationDetailsService;
import com.tcube.api.service.EmailService;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.JobDetailsService;
import com.tcube.api.service.LeaveTrackerDetailsService;
import com.tcube.api.service.MailConfigDetailsService;
import com.tcube.api.service.ManageBranchDetailsService;
import com.tcube.api.service.ManageIntegrationService;
import com.tcube.api.service.NotificationDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.ProjectDetailsService;
import com.tcube.api.service.RoleDetailsService;
import com.tcube.api.utils.EmailSender;
import com.tcube.api.utils.EmailTemplateMapperUtil;
import com.tcube.api.utils.EmployeeIdGenenrator;
import com.tcube.api.utils.EncryptorUtil;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MailSender;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy.Definition.Undefined;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/EmployeeDetails" })
public class EmployeeDetailsApiService {
	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(EmployeeDetailsApiService.class);
	@Autowired
	EmployeeDetailsService employeeDetailsService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@Autowired
	RoleDetailsService roleDetailsService;

	@Autowired
	DesignationDetailsService designationDetailsService;

	@Autowired
	ProjectDetailsService projectDetailsService;

	@Autowired
	JobDetailsService jobDetailsService;

	@Autowired
	NotificationDetailsService notificationDetailsService;

	@Autowired
	LeaveTrackerDetailsService leaveTrackerDetailsService;

	@Autowired
	DayPlannerDetailsService dayPlannerDetailsService;

	@Autowired
	EmailService emailService;

	@Autowired
	MailConfigDetailsService mailConfigDetailsService;

	@Autowired
	ManageIntegrationService manageIntegrationService;
	
	@Autowired
	ManageBranchDetailsService manageBranchDetailsService;

	/**
	 * This service is to Create Employee details.
	 *
	 * @param request
	 * @param ucBuilder
	 * @return
	 */

	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createEmployeeDetails(@RequestParam(value = "data") String request,
										@RequestParam(value = "org_id", required = false) Long org_id,
										@RequestParam(value = "role_id", required = false) Long role_id,
										@RequestParam(value = "designation_id", required = false) Long designation_id,
										@RequestParam(value = "reporting_manager_id", required = false) String reporting_manager_id,
										@RequestParam(value = "branch_id", required = false) Long branch_id,
										final UriComponentsBuilder ucBuilder) {
		logger.info("EmployeeDetailsApiService(createEmployeeDetails) >> Entry");
		String response = "";

		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newdetails = new JSONObject(request);
			String loginUrl = newdetails.getString("login_str");
			newdetails.remove("login_str");
			String zone = newdetails.getString("timezone");
			newdetails.remove("timezone");
			Integer roleConut = 0;
			Integer branchCount = 0;

			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(org_id);
			final DesignationDetails designationDetails = designationDetailsService.getDesignationById(designation_id);
			final EmployeeDetails reporterDetails = employeeDetailsService
					.getAllEmployeeDetailsByID(reporting_manager_id);
			final RoleDetails roledetails = roleDetailsService.getRoleDetailsById(role_id);
			final ManageBranchDetails branchdetails = manageBranchDetailsService.getBranchById(branch_id);
			logger.debug("EmployeeDetailsApiService(createEmployeeDetails) >> Request");
			final EmployeeDetails details = MapperUtil.readAsObjectOf(EmployeeDetails.class, newdetails.toString());
			details.setRoleDetails(roledetails);
			details.setUser_login_type(roledetails.getRole());
			details.setOrgDetails(orgDetails);
			details.setDesignationDetails(designationDetails);
			details.setReporting_manager(reporting_manager_id);
			details.setReporter_name(reporterDetails.getFirstname());
			details.setBranchDetails(branchdetails);
//			details.setReporterDetails(reporterDetails);

			// for active and inactive users counts based on branch
						final List<EmployeeDetails> tempDetails = employeeDetailsService.getAllEmployeeReportsByOrgId(org_id);
						final List<EmployeeDetails> activeEmpDetails = new ArrayList();
						final List<EmployeeDetails> allEmpDetails = new ArrayList();
						
						for (EmployeeDetails i : tempDetails) {
							if (i.getIs_activated().equals(true) && (branchdetails == i.getBranchDetails())) {
								activeEmpDetails.add(i);
							}
						}
						for (EmployeeDetails i : tempDetails) {
							if (branchdetails == i.getBranchDetails() ) {
								allEmpDetails.add(i);
							}
						}
						if (activeEmpDetails.size() != 0) {
							branchCount = branchCount + activeEmpDetails.size();
						} else
							branchCount = 0;
						branchdetails.setActive_total_counts(branchCount + 1);
						branchdetails.setTotal_counts(allEmpDetails.size() + 1);
						ManageBranchDetails branchDetails = manageBranchDetailsService.updateBranchDetails(branchdetails);
			
			// for active and inactive users counts
//			final List<EmployeeDetails> tempDetails = employeeDetailsService.getAllEmployeeReportsByOrgId(org_id);
//			final List<EmployeeDetails> activeEmpDetails = new ArrayList();
//			final List<EmployeeDetails> allEmpDetails = new ArrayList();
//			for (EmployeeDetails i : tempDetails) {
//				if (i.getIs_activated().equals(true) && role_id == i.getRoleDetails().getId()) {
//					activeEmpDetails.add(i);
//				}
//			}
//			for (EmployeeDetails i : tempDetails) {
//				if (role_id == i.getRoleDetails().getId()) {
//					allEmpDetails.add(i);
//				}
//			}
//			if (activeEmpDetails.size() != 0) {
//				roleConut = roleConut + activeEmpDetails.size();
//			} else
//				roleConut = 0;
//			roledetails.setActive_total_counts(roleConut + 1);
//			roledetails.setTotal_counts(allEmpDetails.size() + 1);
//			RoleDetails RDetails = roleDetailsService.updateRoleDetails(roledetails);

			/* Emp Id generation */

			final List<EmployeeDetails> emp = employeeDetailsService.getAllEmployeeDetailsByOrgID(org_id);
//			EmployeeDetails emp1 = emp.get(emp.size()-1);
			EmployeeDetails emp1 = emp.stream().max((x, y) -> Long.compare(x.getSeq(), y.getSeq())).get();
			Long maxSeqId = emp1.getSeq();
//			Integer maxSeqId = emp.size();
			String idPrefix = EmployeeIdGenenrator.genarateId(orgDetails.getCompany_name(), details.getFirstname());
//			long maxSeqId = employeeDetailsService.getMaxSequenceId();
			if (maxSeqId >= 0) {
				long seqid = ++maxSeqId;
				details.setSeq(seqid);
				String idSuffix = String.format(details.getOrgDetails().getOrg_id() + "%02d", seqid);
				details.setId(idPrefix + idSuffix);
			}
			details.setIs_activated(true);
			details.setSkipped_leave(false);
			final EmployeeDetails details2 = employeeDetailsService.createEmployeeDetailsWithTimeZone(details, zone);
//			details2.setPassword(EncryptorUtil.decryptPropertyValue(details2.getPassword()));

			MailConfigDetails mailConfigDetails = mailConfigDetailsService.getMailConfigByOrgId(orgDetails.getOrg_id());

			ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(orgDetails.getOrg_id(), "mail",
					"manage-user");
			EmployeeDetails  employeeDetailsPassword  = new EmployeeDetails();
			employeeDetailsPassword.setPassword(EncryptorUtil.decryptPropertyValue(details2.getPassword()));
//			employeeDetails.setFirstname(details2.getFirstname());
//			employeeDetails.setLastname(details2.getLastname());
//			employeeDetails.set

			if (mailConfigDetails != null && manageIntegrate != null) {

				if (mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {

					String template = EmailTemplateMapperUtil.getActivateAccountMailTemplate(details2,employeeDetailsPassword.getPassword(),loginUrl);
					String subject = "T-CUBE | " + details2.getFirstname() + " " + details2.getLastname()
							+ " Your Account is Activated";
					EmailSender emailSender = new EmailSender();
					emailSender.sendEmail(mailConfigDetails,details2.getEmail(), subject, template, true);
//					emailService.sendEmail(details2.getEmail(), subject, template, true);
				}
			}
//			MailSender.sendEmail(details2.getEmail(), template, subject, "");
//			MailSender.mailToUser(details2);
			if (details2 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New Employee Details Created Successfully");
				response = new Gson().toJson(jsonObject);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to add Employee Details");
				response = new Gson().toJson(jsonObject);
			}

		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in EmployeeDetailsApiService(createEmployeeDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in create user due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to create user");
			}
//			jsonObject.put(RestConstants.DATA, "Failed to add Employee details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(createEmployeeDetails) >> Exit");
		return response;
	}


	@PostMapping(value = "/createBulkUserFromExcelFile", headers = "Accept=application/json")
	public String createUserFromExcelFile(@RequestBody String request,
										  @RequestParam(value = "org_id", required = false) Long org_id,
										  @RequestParam(value = "role_id", required = false) String role,
										  @RequestParam(value = "designation_id", required = false) String designation,
										  @RequestParam(value = "reporting_manager_id", required = false) String reporting_manager,
										  final UriComponentsBuilder ucBuilder) {
		logger.info("EmployeeDetailsApiService(createUserFromExcelFile) >> Entry");
		String response = "";

		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newdetails = new JSONObject(request);
			JSONArray detailsArr = newdetails.getJSONArray("detailsArray");
			newdetails.remove("detailsArray");
			final Long orgId = newdetails.getLong("org_id");
			newdetails.remove("org_id");
			String loginUrl = newdetails.getString("login_str");
			newdetails.remove("login_str");
			String zone = newdetails.getString("timezone");
			newdetails.remove("timezone");

			String empDesignation = null;
			String empRole = null;
			String empBranch = null;
			String empReport = null;
			String empFirstName = null;
			String empLastName = null;
			String empEmail = null;
			String empDOJ;
			String empPassword = null;
			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			Integer branchCount = 0;
			
			// Name validation
			String empFirstNameArray =null;
			String empLastNameArray = null;
			List<String> str1 = new ArrayList<>();
			List<String> str2 = new ArrayList<>();
			List<String> duplicateNames = new ArrayList<>();
			
			List<EmployeeDetails> empNameDetails = new ArrayList<>();
			 empNameDetails =  employeeDetailsService.getEmployeeDetailsByName(orgId);	
			 for (int q = 0; q < empNameDetails.size(); q++) {
					str1.add(empNameDetails.get(q).getFirstname().concat(empNameDetails.get(q).getLastname()));			
				}	
			for (int q = 0; q < detailsArr.length(); q++) {
				JSONObject objects = detailsArr.getJSONObject(q);
				empFirstNameArray = objects.getString("firstname");
				empLastNameArray =  objects.getString("lastname");
				 str2.add(empFirstNameArray.concat(empLastNameArray));
			}
				 for (int p = 0; p < str1.size(); p++) {
					 for (int m = 0; m < str2.size(); m++) {
					 if(str2.get(m).equals(str1.get(p))) {
						 duplicateNames.add(str2.get(m));
					 }
				 }
				 }
			   if (duplicateNames.size() != 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Duplicate Name Exists!");
				jsonObject.put("duplicateName", duplicateNames);
				response = new Gson().toJson(jsonObject);
				return response;
			}
			
			// Email validation
			List<String> duplicateEmails = new ArrayList<>();
			for (int q = 0; q < detailsArr.length(); q++) {
				JSONObject objects = detailsArr.getJSONObject(q);
				empEmail = objects.getString("email");
				final EmployeeDetails emailInfo = employeeDetailsService.getEmployeeDetailsByEmail(empEmail);
				if (emailInfo != null) {
					duplicateEmails.add(empEmail);
				}				
			}			
			if (duplicateEmails.size() != 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Duplicate Email Exists!");
				jsonObject.put("duplicateEmailIds", duplicateEmails);
				response = new Gson().toJson(jsonObject);
				return response;
			}
			// Ends

			for (int j = 0; j < detailsArr.length(); j++) {
				JSONObject objects = detailsArr.getJSONObject(j);
				empFirstName = objects.getString("firstname");
				empLastName = objects.getString("lastname");
				empEmail = objects.getString("email");
				empDOJ = objects.getString("date_of_joining");
				empPassword = objects.getString("password");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				empDesignation = objects.getString("designation");
				empRole = objects.getString("role");
				empBranch = objects.getString("branch");
				empReport = objects.getString("reporting_manager");

				Integer roleConut = 0;
				DesignationDetails designationDetails = designationDetailsService.getDesignationByName(empDesignation,
						orgId);
				if (designationDetails == null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Designation not found in the database! - " +empDesignation);
					response = new Gson().toJson(jsonObject);
					return response;
				}

				final EmployeeDetails reporterDetails = employeeDetailsService.getReportingManagerByName(empReport, orgId);
				if (reporterDetails == null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Reporting manager is not found in the database! - " +empReport);
					response = new Gson().toJson(jsonObject);
					return response;					
				}
				RoleDetails roledetails = roleDetailsService.getRoleByName(empRole, orgId);
				if (roledetails == null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Role not found in the database! - " +empRole);
					response = new Gson().toJson(jsonObject);
					return response;
				}
				ManageBranchDetails branchdetails = manageBranchDetailsService.getBranchByName(empBranch,orgId);
				if (branchdetails == null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Branch not found in the database! - " +empBranch);
					response = new Gson().toJson(jsonObject);
					return response;
				}
				logger.debug("EmployeeDetailsApiService(createUserFromExcelFile) >> Request ");
				int check = 0;
				final EmployeeDetails details = MapperUtil.readAsObjectOf(EmployeeDetails.class, newdetails.toString());
				logger.debug("EmployeeDetailsApiService(createUserFromExcelFile) >> Request Model Object :"
						+ new Gson().toJson(details));
				details.setRoleDetails(roledetails);
				details.setUser_login_type((roledetails).getRole());
				details.setBranchDetails(branchdetails);
				details.setOrgDetails(orgDetails);
				details.setDesignationDetails(designationDetails);
//			details.setReporterDetails(reporterDetails);
				details.setReporting_manager(reporterDetails.getId());
				details.setReporter_name(reporterDetails.getFirstname());
				details.setFirstname(empFirstName);
				details.setLastname(empLastName);
				details.setPassword(empPassword);
				details.setEmail(empEmail);
				// Extracting date from string
				try {
					Date date = formatter.parse(empDOJ);
					details.setDate_of_joining(date);
				} catch (ParseException e) {
					Sentry.captureException(e);
					e.printStackTrace();
				}

				/* Emp Id generation */

//				final List<EmployeeDetails> emp = employeeDetailsService.getAllEmployeeDetailsByOrgID(orgId);
//				EmployeeDetails emp1 = emp.get(emp.size() - 1);
//				Long maxSeqId = emp1.getSeq();
//				String idPrefix = EmployeeIdGenenrator.genarateId(orgDetails.getCompany_name(), details.getFirstname());
//				if (maxSeqId >= 0) {
//					long seqid = ++maxSeqId;
//					details.setSeq(seqid);
//					String idSuffix = String.format(details.getOrgDetails().getOrg_id() + "%02d", seqid);
//					details.setId(idPrefix + idSuffix);
//				}
				final List<EmployeeDetails> emp = employeeDetailsService.getAllEmployeeDetailsByOrgID(orgId);
				// EmployeeDetails emp1 = emp.get(emp.size()-1);
				EmployeeDetails emp1 = emp.stream()
						.max((x, y) -> Long.compare(x.getSeq(),y.getSeq()))
						.get();
				Long maxSeqId = emp1.getSeq() ;
				// Integer maxSeqId = emp.size();
				String idPrefix = EmployeeIdGenenrator.genarateId(orgDetails.getCompany_name(), details.getFirstname());
				// long maxSeqId = employeeDetailsService.getMaxSequenceId();
				if (maxSeqId >= 0) {
					long seqid = ++maxSeqId;
					details.setSeq(seqid);
					String idSuffix = String.format(details.getOrgDetails().getOrg_id() + "%02d", seqid);
					details.setId(idPrefix + idSuffix);
				}

				details.setIs_activated(true);
				
				// for active and inactive users counts based on branch
				final List<EmployeeDetails> tempDetails = employeeDetailsService.getAllEmployeeReportsByOrgId(org_id);
				final List<EmployeeDetails> activeEmpDetails = new ArrayList();
				final List<EmployeeDetails> allEmpDetails = new ArrayList();
				
				for (EmployeeDetails i : tempDetails) {
					if (i.getIs_activated().equals(true) && branchdetails == i.getBranchDetails()) {
						activeEmpDetails.add(i);
					}
				}
				for (EmployeeDetails i : tempDetails) {
					if (branchdetails == i.getBranchDetails() ) {
						allEmpDetails.add(i);
					}
				}
				if (activeEmpDetails.size() != 0) {
					branchCount = branchCount + activeEmpDetails.size();
				} else
					branchCount = 0;
				branchdetails.setActive_total_counts(branchCount + 1);
				branchdetails.setTotal_counts(allEmpDetails.size() + 1);
				ManageBranchDetails branchDetails = manageBranchDetailsService.updateBranchDetails(branchdetails);

				// for active and inactive users counts//						
//				final List<EmployeeDetails> tempDetails = employeeDetailsService.getAllEmployeeReportsByOrgId(orgId);
//				final List<EmployeeDetails> activeEmpDetails = new ArrayList();
//				final List<EmployeeDetails> allEmpDetails = new ArrayList();
//
//				for (EmployeeDetails empDetails : tempDetails) {
//					if (empDetails.getIs_activated().equals(true)
//							&& detailsArr.get(j) == empDetails.getRoleDetails().getId()) {
//						activeEmpDetails.add(empDetails);
//					}
//				}
//				for (EmployeeDetails empeRole : tempDetails) {
//					if (detailsArr.get(j) == empeRole.getRoleDetails().getId()) {
//						allEmpDetails.add(empeRole);
//					}
//				}
//
//				if (activeEmpDetails.size() != 0) {
//					roleConut = roleConut + activeEmpDetails.size();
//				} else
//					roleConut = 0;
//				roledetails.setActive_total_counts(roleConut + 1);
//				roledetails.setTotal_counts(allEmpDetails.size() + 1);
//				RoleDetails RDetails = roleDetailsService.updateRoleDetails(roledetails);
//									}
				final EmployeeDetails details2 = employeeDetailsService.createUserFromExcelFile(details, zone);
				if (details2 != null) {
					check += 1;
				}
//				details2.setPassword(EncryptorUtil.decryptPropertyValue(details2.getPassword()));
				MailConfigDetails mailConfigDetails = mailConfigDetailsService.getMailConfigByOrgId(orgId);

				ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(orgId, "mail",
						"manage-user");
				EmployeeDetails  employeeDetailsPassword  = new EmployeeDetails();
				employeeDetailsPassword.setPassword(EncryptorUtil.decryptPropertyValue(details2.getPassword()));

				if (mailConfigDetails != null && manageIntegrate != null) {

					if (mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {

//						details2.setPassword(EncryptorUtil.decryptPropertyValue(details2.getPassword()));
						String template = EmailTemplateMapperUtil.getActivateAccountMailTemplate(details2,employeeDetailsPassword.getPassword(),loginUrl);
						String subject = "T-CUBE | " + details2.getFirstname() + " " + details2.getLastname()
								+ " Your Account is Activated";
						EmailSender emailSender = new EmailSender();
						emailSender.sendEmail(mailConfigDetails,details2.getEmail(), subject, template, true);
//						emailService.sendEmail(details2.getEmail(), subject, template, true);
					}
				}
//			MailSender.sendEmail(details2.getEmail(), template, subject, "");
//			MailSender.mailToUser(details2);
				if (details2 != null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "Bulk New Employee Details Created Successfully");
					response = new Gson().toJson(jsonObject);
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Add bulk Employee Details getting failed");
					response = new Gson().toJson(jsonObject);
				}

			}
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in EmployeeDetailsApiService(createUserFromExcelFile) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in add bulk Employee details due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to add bulk Employee details");
			}
//			jsonObject.put(RestConstants.DATA, "Failed to add bulk Employee details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(createUserFromExcelFile) >> Exit");
		return response;
	}


	/**
	 * This service is to get all Employee Details
	 *
	 * @return
	 * @throws JSONException
	 */
	@GetMapping(value = "/getAllEmployeeDetails", headers = "Accept=application/json")
	public String getAllEmployeeDetails() {
		logger.info("EmployeeDetailsApiService(getAllEmployeeDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getAllEmployeeDetails)");
			final List<EmployeeDetails> details = employeeDetailsService.getAllEmployeeDetails();
			if (details != null && details.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting employee getall details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getAllEmployeeDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting Employee get all details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getAllEmployeeDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllEmployeeDetailsByEmail", headers = "Accept=application/json")
	public String getAllEmployeeDetailsByEmail() {
		logger.info("EmployeeDetailsApiService(getAllEmployeeDetailsByEmail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getAllEmployeeDetailsByEmail)");
			final List<EmployeeDetails> details = employeeDetailsService.getAllEmployeeDetailsByEmail();
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to extract all employee details by email");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getAllEmployeeDetailsByEmail) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting all employee details by email");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getAllEmployeeDetailsByEmail) >> Exit");
		return response;
	}

	@GetMapping(value = "/getSkippedEmployeeLeaveDetails", headers = "Accept=application/json")
	public String getSkippedLeaveEmployeeDetails() {
		logger.info("EmployeeDetailsApiService(getSkippedLeaveEmployeeDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getSkippedLeaveEmployeeDetails)");
			final List<EmployeeDetails> details = employeeDetailsService.getAllSkippedLeaveEmployeeDetails();;
			if (details != null && details.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting employee getall details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getSkippedLeaveEmployeeDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting Employee get all details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getSkippedLeaveEmployeeDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllActiveEmployeeDetails", headers = "Accept=application/json")
	public String getAllActiveEmployeeDetails() {
		logger.info("EmployeeDetailsApiService(getAllActiveEmployeeDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getAllActiveEmployeeDetails)");
			final List<EmployeeDetails> details = employeeDetailsService.getAllActiveEmployeeDetails();
			if (details != null && details.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting employee getall details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getAllActiveEmployeeDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting Employee get all details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getAllActiveEmployeeDetails) >> Exit");
		return response;
	}

	/**
	 * This service is to get all active Employee Details
	 *
	 * @return
	 * @throws JSONException
	 */
	@GetMapping(value = "/getActiveEmployeeDetails", headers = "Accept=application/json")
	public String getactiveEmployeeDetails() {
		logger.info("EmployeeDetailsApiService(getactiveEmployeeDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getActiveEmployeeDetails)>> Request");
			final List<EmployeeDetails> details = employeeDetailsService.getAllEmployeeDetails();
			final List<EmployeeDetails> newDetails = new ArrayList<>();
			for (EmployeeDetails i : details) {
				if (i.getIs_deleted() == false) {
					newDetails.add(i);
				}
			}
			if (newDetails != null && newDetails.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting Active employee details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getactiveEmployeeDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting get active Employee details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getactiveEmployeeDetails) >> Exit");
		return response;
	}

	/**
	 * This service is to Get Employee details by id.
	 *
	 * @param request
	 * @param ucBuilder
	 * @return
	 */
	@GetMapping(value = "/getEmployeeById/{id}", headers = "Accept=application/json")
	public String getEmployeeByID(@PathVariable final String id, final UriComponentsBuilder ucBuilder) {
		logger.info("EmployeeDetailsApiService(getEmployeeByID) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getEmployeeByID)");
			final EmployeeDetails details = employeeDetailsService.getAllEmployeeDetailsByID(id);
			if (details.getProfile_image() != null) {
				details.setProfile_image(ImageProcessor.decompressBytes(details.getProfile_image()));
			}
//			if (details.getReporterDetails() != null) {
//				if (details.getReporterDetails().getProfile_image() != null) {
//					details.getReporterDetails().setProfile_image(
//							ImageProcessor.decompressBytes(details.getReporterDetails().getProfile_image()));
//				}
//			}

//        	details.setProfile_image(ImageProcessor.decompressBytes(dtails.getProfile_image()));
			if(details.getPassword() != null) {
				details.setPassword(EncryptorUtil.decryptPropertyValue(details.getPassword()));
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting employee details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getEmployeeByID) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  employee details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getEmployeeByID) >> Exit");
		return response;
	}

	/**
	 * This service is to Get Employee details by email.
	 *
	 * @param request
	 * @param ucBuilder
	 * @return
	 */
	@GetMapping(value = "/getEmployeeByEmail/{email}", headers = "Accept=application/json")
	public String getEmployeeByEmail(@PathVariable(value = "email") final String email) {
		logger.info("EmployeeDetailsApiService(getEmployeeByEmail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getEmployeeByEmail)>> Request");
			final EmployeeDetails details = employeeDetailsService.getEmployeeDetailsByEmail(email);
			details.setPassword(EncryptorUtil.decryptPropertyValue(details.getPassword()));
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting employee details by email");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getEmployeeByEmail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  employee details by email");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getEmployeeByEmail) >> Exit");
		return response;
	}

	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateEmployeDetail(@RequestBody final String detailsOfEmployee) {
		logger.info("EmployeeDetailsApiService(updateEmployeDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfEmployee);
			String empId = newJsonObject.getString("id");
			newJsonObject.remove("id");
			Long roleId = newJsonObject.getLong("role_id");
			newJsonObject.remove("role_id");
			Long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			Long designationId = newJsonObject.getLong("designation_id");
			newJsonObject.remove("designation_id");
			String reporting_manager_id = newJsonObject.getString("reporting_manager_id");
			newJsonObject.remove("reporting_manager_id");
			String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");
			Long branchId = newJsonObject.getLong("branch_id");
			newJsonObject.remove("branch_id");
			Integer roleConut = 0;
			 ManageBranchDetails oldBranchDetails = null;
			 
			final EmployeeDetails oldempDetails = employeeDetailsService.getAllEmployeeDetailsByID(empId);
			// get with old role id
			final RoleDetails oldRoleDetails = roleDetailsService.getRoleDetailsById(oldempDetails.getRoleDetails().getId());

			final RoleDetails roleDetails = roleDetailsService.getRoleDetailsById(roleId);
			final DesignationDetails designationDetails = designationDetailsService.getDesignationById(designationId);
			
			//to get branch details by branch id
			EmployeeDetails reporterDetails = null;
			if(reporting_manager_id !="") {
				reporterDetails = employeeDetailsService
						.getAllEmployeeDetailsByID(reporting_manager_id);
			}
			// to get details by old branch id
						if(oldempDetails.getBranchDetails() != null) {
							 oldBranchDetails = manageBranchDetailsService.getBranchById(oldempDetails.getBranchDetails().getId());
						}else {			
						 logger.debug("EmployeeDetailsApiService(updateEmployeDetail) >> OldBranch is empty ");
						}
			final ManageBranchDetails branchdetails = manageBranchDetailsService.getBranchById(branchId);
			logger.debug("EmployeeDetailsApiService(updateEmployeDetail) >> Request ");
			final EmployeeDetails newempDetails = MapperUtil.readAsObjectOf(EmployeeDetails.class,
					newJsonObject.toString());
			oldempDetails.setFirstname(newempDetails.getFirstname());
			oldempDetails.setLastname(newempDetails.getLastname());
			oldempDetails.setEmail(newempDetails.getEmail());
			oldempDetails.setPassword(newempDetails.getPassword());
			oldempDetails.setUser_login_type(roleDetails.getRole());
			oldempDetails.setRoleDetails(roleDetails);
			oldempDetails.setDesignationDetails(designationDetails);
			if(reporting_manager_id !="") {
				oldempDetails.setReporting_manager(reporterDetails.getId());
				oldempDetails.setReporter_name(reporterDetails.getFirstname());
			}
//			oldempDetails.setReporterDetails(reporterDetails);
			oldempDetails.setDate_of_joining(newempDetails.getDate_of_joining());
//			oldempDetails.setIsReportingManagerAvail(true);
//			oldempDetails.setRole(newempDetails.getRole());	
			oldempDetails.setBranchDetails(branchdetails);
			oldempDetails.setIs_details_updated(true);
			final EmployeeDetails details = employeeDetailsService.updateEmployeeDetailsWithZone(oldempDetails,zone);
			
			if( oldBranchDetails!= null && branchId != oldBranchDetails.getId()) {
				// Update active user with old branch name
				List<EmployeeDetails> activeEmpDetailsByOldBranch = employeeDetailsService.getActiveEmpByBranchAndOrgId(orgId, oldBranchDetails.getId());
				if(activeEmpDetailsByOldBranch.size() !=0 || (activeEmpDetailsByOldBranch.size() ==0 ) ) {
				oldBranchDetails.setActive_total_counts(activeEmpDetailsByOldBranch.size());
				final ManageBranchDetails updateActiveUsersByOldBranch = manageBranchDetailsService.updateBranchDetails(oldBranchDetails);
				}				
				
				// Update inactive user with old branch name
				List<EmployeeDetails> inactiveEmpDetailsByOldBranch = employeeDetailsService.getInactiveEmpByBranchAndOrgId(orgId, oldBranchDetails.getId());
				if(inactiveEmpDetailsByOldBranch.size() !=0 || (inactiveEmpDetailsByOldBranch.size() ==0 ) ) {
				oldBranchDetails.setInactive_total_counts(inactiveEmpDetailsByOldBranch.size());
				final ManageBranchDetails updateInactiveUsersByOldBranch = manageBranchDetailsService.updateBranchDetails(oldBranchDetails);
				}
				
				// Update active user with new branch name
				List<EmployeeDetails> activeEmpDetailsByNewBranch = employeeDetailsService.getActiveEmpByBranchAndOrgId(orgId, branchId);
				if(activeEmpDetailsByNewBranch.size() !=0 || (activeEmpDetailsByNewBranch.size() ==0 )) {
				branchdetails.setActive_total_counts(activeEmpDetailsByNewBranch.size());
				final ManageBranchDetails updateActiveUsersByNewBranch = manageBranchDetailsService.updateBranchDetails(branchdetails);
				}
				
				// Update inactive user with new branch name
				List<EmployeeDetails> inactiveEmpDetailsByNewBranch = employeeDetailsService.getInactiveEmpByBranchAndOrgId(orgId, branchId);
				if(inactiveEmpDetailsByNewBranch.size() !=0 || (inactiveEmpDetailsByNewBranch.size() ==0 )) {
				branchdetails.setInactive_total_counts(inactiveEmpDetailsByNewBranch.size());
				final ManageBranchDetails updateInactiveUsersByNewBranch = manageBranchDetailsService.updateBranchDetails(branchdetails);
				}					
			} else {
				// Update active user with new branch name
				List<EmployeeDetails> activeEmpDetailsByNewBranch = employeeDetailsService.getActiveEmpByBranchAndOrgId(orgId, branchId);
				if(activeEmpDetailsByNewBranch.size() !=0 || (activeEmpDetailsByNewBranch.size() ==0 )) {
				branchdetails.setActive_total_counts(activeEmpDetailsByNewBranch.size());
				final ManageBranchDetails updateActiveUsersByNewBranch = manageBranchDetailsService.updateBranchDetails(branchdetails);
				}
				
				// Update inactive user with new branch name
				List<EmployeeDetails> inactiveEmpDetailsByNewBranch = employeeDetailsService.getInactiveEmpByBranchAndOrgId(orgId, branchId);
				if(inactiveEmpDetailsByNewBranch.size() !=0 || (inactiveEmpDetailsByNewBranch.size() ==0 )) {
				branchdetails.setInactive_total_counts(inactiveEmpDetailsByNewBranch.size());
				final ManageBranchDetails updateInactiveUsersByNewBranch = manageBranchDetailsService.updateBranchDetails(branchdetails);
				}
			}
			
//			if(roleId != oldRoleDetails.getId()) {
//				// Update active total count with old role
//				List<EmployeeDetails> activeRoleDetailsWithOldRole = employeeDetailsService.getActiveEmpByRoleAndOrgId(orgId,oldRoleDetails.getId());
//				if(activeRoleDetailsWithOldRole != null) {
//					oldRoleDetails.setActive_total_counts(activeRoleDetailsWithOldRole.size());
//					final RoleDetails updateActiveEmpRoleDetailsWithOldRole = roleDetailsService.updateRoleDetails(oldRoleDetails);
//				}
//
//				// Update inactive total count with old role
//				List<EmployeeDetails> inactiveRoleDetailsWithOldRole = employeeDetailsService.getInactiveEmpByRoleAndOrgId(orgId,oldRoleDetails.getId());
//				if(inactiveRoleDetailsWithOldRole != null) {
//					oldRoleDetails.setInactive_total_counts(inactiveRoleDetailsWithOldRole.size());
//					final RoleDetails updateInactiveEmpRoleDetailsWithOldRole = roleDetailsService.updateRoleDetails(oldRoleDetails);
//				}
//				// Update active total count with new role
//				List<EmployeeDetails> activeRoleDetailsWithNewRole = employeeDetailsService.getActiveEmpByRoleAndOrgId(orgId,roleId);
//				if(activeRoleDetailsWithNewRole != null) {
//					roleDetails.setActive_total_counts(activeRoleDetailsWithNewRole.size());
//					final RoleDetails updateActiveEmpRoleDetailsWithNewRole = roleDetailsService.updateRoleDetails(roleDetails);
//				}
//				// Update inactive total count with new role
//				List<EmployeeDetails> inactiveRoleDetailsWithNewRole = employeeDetailsService.getInactiveEmpByRoleAndOrgId(orgId,oldRoleDetails.getId());
//				if(inactiveRoleDetailsWithNewRole != null) {
//					roleDetails.setInactive_total_counts(inactiveRoleDetailsWithNewRole.size());
//					final RoleDetails updateInactiveEmpRoleDetailsWithNewRole = roleDetailsService.updateRoleDetails(roleDetails);
//				}
//			}

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Employee details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating Employee details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("EmployeeDetailsApiService(updateEmployeDetail) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating Employee details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(updateEmployeDetail) >> Exit");
		return response;
	}

	@PutMapping(value = "/updateskippedTime", headers = "Accept=application/json")
	public String updateSkippedTimeEmployeDetail(@RequestBody final String detailsOfEmployee) {
		logger.info("EmployeeDetailsApiService(updateSkippedTimeEmployeDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfEmployee);

			String empId = newJsonObject.getString("id");
			newJsonObject.remove("id");
			Boolean isSkipped = newJsonObject.getBoolean("is_skipped");
			newJsonObject.remove("is_skipped");
			String zone  = newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");
			String skipped_time = newJsonObject.getString("skipped_time");
			newJsonObject.remove("skipped_time");
			final EmployeeDetails oldempDetails = employeeDetailsService.getAllEmployeeDetailsByID(empId);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			 Use india time zone to format the date in
//			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(skipped_time);  
//			df.setTimeZone(TimeZone.getTimeZone(zone));
//			oldempDetails.setSkipped_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
//			oldempDetails.setIs_skipped(isSkipped);
			final EmployeeDetails details = employeeDetailsService.updateskippedTimeEmployeeDetails(oldempDetails,zone,skipped_time,isSkipped);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Employee details updateskippedTime  successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updateskippedTime Employee details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("EmployeeDetailsApiService(updateSkippedTimeEmployeDetail) >> Response ");
		}catch(Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updateskippedTime Employee details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(updateSkippedTimeEmployeDetail) >> Exit");
		return response;
	}

	//
	@PutMapping(value = "/updateSkippedLeave", headers = "Accept=application/json")
	public String updateSkippedLeaveEmpDetail(@RequestBody String detailsOfEmployee) {
		logger.info("EmployeeDetailsApiService(updateSkippedLeaveEmpDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfEmployee);
			String empId = newJsonObject.getString("id");
			newJsonObject.remove("id");
			final EmployeeDetails oldempDetails = employeeDetailsService.getAllEmployeeDetailsByID(empId);
			final EmployeeDetails empDetails = employeeDetailsService.updateSkippedLeaveById(oldempDetails);
			if (empDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Employee details updateSkippedLeave  successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updateSkippedLeave Employee details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("EmployeeDetailsApiService(updateSkippedLeaveEmpDetail) >> Response");

		}catch(Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updateskippedTime Employee details");
			response = new Gson().toJson(jsonObject);

		}
		logger.info("EmployeeDetailsApiService(updateSkippedLeaveEmpDetail) >> Exit");
		return response;
	}

//	@PutMapping(value = "/updateSkippedLeave", headers = "Accept=application/json")
//	public String updateSkippedLeaveEmpDetail() {
//		logger.info("EmployeeDetailsApiService(updateSkippedLeave) >> Entry");
//		String response = "";
//		final JSONObject jsonObject = new JSONObject();
//		try {
//			final List<EmployeeDetails> oldempDetails = employeeDetailsService.getAllSkippedLeaveEmployeeDetails();
//			final List<EmployeeDetails> newDetails = new ArrayList<>();
//			for (EmployeeDetails i : oldempDetails) {
//				JSONObject	jsonObject1 = new JSONObject(i);
//				Long org_id = jsonObject1.getLong("org_id");
////				EmployeeDetails updateDetails = employeeDetailsService
////				final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(org_id);
//				
//				
//			}
//			
//		} catch(Exception e) {
//			
//		}
//		return response;
//		}

	@PutMapping(value = "/updatePersonal", headers = "Accept=application/json")
	public String updatePersonalEmpDetail(@RequestBody String detailsOfEmployee) {
		logger.info("EmployeeDetailsApiService(updatePersonalEmpDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfEmployee);
			String empId = newJsonObject.getString("id");
			newJsonObject.remove("id");
			String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");

			final EmployeeDetails oldempDetails = employeeDetailsService.getAllEmployeeDetailsByID(empId);
			logger.debug("EmployeeDetailsApiService(updatePersonalEmpDetail) >> Request ");
			final EmployeeDetails newempDetails = MapperUtil.readAsObjectOf(EmployeeDetails.class,
					newJsonObject.toString());
			oldempDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldempDetails.getPassword()));
			oldempDetails.setDate_of_birth(newempDetails.getDate_of_birth());
			oldempDetails.setMarital_status(newempDetails.getMarital_status());
			oldempDetails.setBlood_group(newempDetails.getBlood_group());
			oldempDetails.setGender(newempDetails.getGender());

			oldempDetails.setPersonal_mobile_number(newempDetails.getPersonal_mobile_number());
			oldempDetails.setPersonal_email(newempDetails.getPersonal_email());
			oldempDetails.setPresent_address(newempDetails.getPresent_address());
			oldempDetails.setPermanent_address(newempDetails.getPermanent_address());

			final EmployeeDetails details = employeeDetailsService.updateEmployeeDetailsWithZone(oldempDetails,zone);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Employee details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating Employee details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("EmployeeDetailsApiService(updatePersonalEmpDetail) >> Response ");
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating Employee details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(updatePersonalEmpDetail) >> Exit");
		return response;
	}

	@PutMapping(value = "/updateProfileImage", headers = "Accept=application/json")
	public String updateProfileImage(@RequestParam(value = "data") String detailsOfEmployee,
									 @RequestParam(value = "employee_photo", required = false) MultipartFile employee_photo) {
		logger.info("EmployeeDetailsApiService(updateProfileImage) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfEmployee);
			String empId = newJsonObject.getString("id");
			newJsonObject.remove("id");

			if (employee_photo != null) {
				newJsonObject.put("profile_image", ImageProcessor.compressBytes(employee_photo.getBytes()));

				// Update the image in notification details
//				final NotificationsDetails data = notificationDetailsService.updateNotifierPersonalDetails(empId,
//						ImageProcessor.compressBytes(employee_photo.getBytes()));

				// While user update the profilr image --> update the image in leave tracker
				// details for that user
				// final LeaveTrackerDetails leaveDetails = leaveTrackerDetailsService.updateEmpDetails(empId,
				// 		ImageProcessor.compressBytes(employee_photo.getBytes()));

				// DayPlannerDetails dayPlannerDetails = dayPlannerDetailsService.updateEmpImageDetails(empId, ImageProcessor.compressBytes(employee_photo.getBytes()));

			}

			final EmployeeDetails oldempDetails = employeeDetailsService.getAllEmployeeDetailsByID(empId);
			logger.debug("EmployeeDetailsApiService(updateProfileImage) >> Request :");
			final EmployeeDetails newempDetails = MapperUtil.readAsObjectOf(EmployeeDetails.class,
					newJsonObject.toString());
			oldempDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldempDetails.getPassword()));
			oldempDetails.setProfile_image(ImageProcessor.compressBytes(employee_photo.getBytes()));
			final EmployeeDetails details = employeeDetailsService.updateEmployeeDetails(oldempDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Profile image updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating profile image");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("EmployeeDetailsApiService(updateProfileImage) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating profile image");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(updateProfileImage) >> Exit");
		return response;
	}

	@PutMapping(value = "/activateEmployee", headers = "Accept=application/json")
	public String activateEmployee(@RequestBody final String details1) {
		logger.info("EmployeeDetailsApiService(activateEmployee) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details1);
			String id = newJsonObject.getString("id");
			newJsonObject.remove("id");
//			JSONArray empIds = newJsonObject.getJSONArray(id);
			String comment = newJsonObject.getString("comments");
			newJsonObject.remove("comments");
			String loginUrl = newJsonObject.getString("login_str");
			newJsonObject.remove("login_str");
			final EmployeeDetails oldAdminDetails = employeeDetailsService.getAllEmployeeDetailsByID(id);
			int leaveDetails = leaveTrackerDetailsService.getLeaveRequestByEmpIdByStatusPending(id,"activate");
			String oldEmployeeEmail = oldAdminDetails.getEmail();
			String orgCompanyDetails = oldAdminDetails.getOrgDetails().getCompany_name();
			oldAdminDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldAdminDetails.getPassword()));
			oldAdminDetails.setIs_activated(true);
			if (comment == "") {
				oldAdminDetails.setComments("-");
			} else {
				oldAdminDetails.setComments(comment);
			}
			final EmployeeDetails details = employeeDetailsService.updateEmployeeDetails(oldAdminDetails);
			int empDetailsInProjectResource = projectDetailsService.enableUserInProject(id);
			int empDetailsInJobAssignee = jobDetailsService.enableAssigneeInJob(id);
			// add active users count
			Long role_id = oldAdminDetails.getRoleDetails().getId();
			Long org_id = oldAdminDetails.getOrgDetails().getOrg_id();
			Long branch_id = oldAdminDetails.getBranchDetails().getId();
			final RoleDetails roledetails = roleDetailsService.getRoleDetailsById(role_id);
			final ManageBranchDetails branchDetails = manageBranchDetailsService.getBranchById(branch_id);
			
			// Update active and inactive users based on branch
			List<EmployeeDetails> activeEmpDetailsByBranch = employeeDetailsService.getActiveEmpByBranchAndOrgId(org_id, branch_id);
			branchDetails.setActive_total_counts(activeEmpDetailsByBranch.size());
			final ManageBranchDetails updateActiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);
						
			List<EmployeeDetails> inactiveEmpDetailsByBranch = employeeDetailsService.getInactiveEmpByBranchAndOrgId(org_id, branch_id);
			branchDetails.setInactive_total_counts(inactiveEmpDetailsByBranch.size());
			final ManageBranchDetails updateInactiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);
			
			// Update active and inactive user count based on role after activate
//			List<EmployeeDetails> activeEmpDetailsByRoleId = employeeDetailsService.getActiveEmpByRoleAndOrgId(org_id, role_id);
////				if(activeEmpDetailsByRoleId != null) {
//			roledetails.setActive_total_counts(activeEmpDetailsByRoleId.size());
//			final RoleDetails updateActiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);
////					}else {
////						final RoleDetails updateActiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);	
////					}
//			List<EmployeeDetails> inactiveEmpDetailsByRoleId = employeeDetailsService.getInactiveEmpByRoleAndOrgId(org_id, role_id);
//			roledetails.setInactive_total_counts(inactiveEmpDetailsByRoleId.size());
//			final RoleDetails updateInactiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);


//			final List<EmployeeDetails> tempDetails = employeeDetailsService.getAllEmployeeReportsByOrgId(org_id);
//			final List<EmployeeDetails> activeEmpDetails = new ArrayList();
//			final List<EmployeeDetails> inactiveEmpDetails = new ArrayList();
//			for (EmployeeDetails i : tempDetails) {
//				if (i.getIs_activated().equals(true) && role_id == i.getRoleDetails().getId()) {
//					activeEmpDetails.add(i);
//				} else if (i.getIs_activated().equals(false) && role_id == i.getRoleDetails().getId()) {
//					inactiveEmpDetails.add(i);
//				}
//			}
//			Integer inactiveCount = 0;
//			if (roledetails.getInactive_total_counts().equals(null) && roledetails.getInactive_total_counts() != 0) {
//				inactiveCount = 0;
//				roledetails.setInactive_total_counts(inactiveCount);
//			} else {
//				inactiveCount = roledetails.getInactive_total_counts();
//				roledetails.setInactive_total_counts(inactiveCount - 1);
//			}
//			roledetails.setActive_total_counts(activeEmpDetails.size());
//			roledetails.setInactive_total_counts(inactiveEmpDetails.size());
//			RoleDetails RDetails = roleDetailsService.updateRoleDetails(roledetails);
			MailConfigDetails mailConfigDetails = mailConfigDetailsService
					.getMailConfigByOrgId(oldAdminDetails.getOrgDetails().getOrg_id());

			ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(oldAdminDetails.getOrgDetails().getOrg_id(), "mail",
					"manage-user");
			final int details2 = employeeDetailsService.activate(id);
			if (details2 >= 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Employee activated successfully");

				if(mailConfigDetails != null && manageIntegrate != null) {

					if(mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
						String template = EmailTemplateMapperUtil.getEmployeeActivateAccountMailTemplate(details, comment,
								loginUrl,orgCompanyDetails);
						String subject = "T-CUBE | " + " Your Account is Activated ";
						EmailSender emailSender = new EmailSender();
						emailSender.sendEmail(mailConfigDetails,oldEmployeeEmail, subject, template, true);
//				emailService.sendEmail(oldEmployeeEmail,subject,template, true);
					}
				}
//				MailSender.sendEmail(oldEmployeeEmail, template, subject, "");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in activate employee");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in activate employee status due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to activate employee");
			}
//			jsonObject.put(RestConstants.DATA, "Error in activate employee");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(activateEmployee) >> Exit");
		return response;
	}

	@PutMapping(value = "/bulkActivateEmployeeDetails", headers = "Accept=application/json")
	public String bulkActivateEmployee(@RequestBody final String request) {
		logger.info("EmployeeDetailsApiService(bulkActivateEmployee) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			String action = newJsonObject.getString("action");
			newJsonObject.remove("action");
			String comment = newJsonObject.getString("comments");
			newJsonObject.remove("comments");
			String loginUrl = newJsonObject.getString("login_str");
			newJsonObject.remove("login_str");
//			final int details = employeeDetailsService.bulkActivate(deleteIds, action);
			for (Object id : deleteIds) {
				String stringToConvert = String.valueOf(id);
//			     Long convertedLongId = Long.parseLong(stringToConvert);
				final EmployeeDetails oldAdminDetails = employeeDetailsService
						.getAllEmployeeDetailsByID(stringToConvert);
				String oldEmployeeEmail = oldAdminDetails.getEmail();
				String orgCompanyDetails = oldAdminDetails.getOrgDetails().getCompany_name();
				oldAdminDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldAdminDetails.getPassword()));
				oldAdminDetails.setIs_activated(true);
				if (comment == "") {
					oldAdminDetails.setComments("-");
				} else {
					oldAdminDetails.setComments(comment);
				}
				final EmployeeDetails details1 = employeeDetailsService.updateEmployeeDetails(oldAdminDetails);

				int leaveDetails = leaveTrackerDetailsService.bulkGetLeaveRequestByEmpIdByStatusPending(deleteIds, "activate");
				// Enable the user in project
				int empDetailsInProjectResource = projectDetailsService.enableBulkUsersInProject(deleteIds);
				// Enable assignee in job
				int empDetailsInJobAssignee = jobDetailsService.enableBulkAssigneeInJob(deleteIds);
				Long role_id = oldAdminDetails.getRoleDetails().getId();
				Long org_id = oldAdminDetails.getOrgDetails().getOrg_id();
				final RoleDetails roledetails = roleDetailsService.getRoleDetailsById(role_id);
				Long branch_id = oldAdminDetails.getBranchDetails().getId();
				final ManageBranchDetails branchDetails = manageBranchDetailsService.getBranchById(branch_id);
				
				// Update active and inactive users based on branch
				List<EmployeeDetails> activeEmpDetailsByBranch = employeeDetailsService.getActiveEmpByBranchAndOrgId(org_id, branch_id);
				branchDetails.setActive_total_counts(activeEmpDetailsByBranch.size());
				final ManageBranchDetails updateActiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);
				
				List<EmployeeDetails> inactiveEmpDetailsByBranch = employeeDetailsService.getInactiveEmpByBranchAndOrgId(org_id, branch_id);
				branchDetails.setInactive_total_counts(inactiveEmpDetailsByBranch.size());
				final ManageBranchDetails updateInactiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);
				
				
				// Update active and inactive total count based on role
//				List<EmployeeDetails> activeEmpDetailsByRoleId = employeeDetailsService.getActiveEmpByRoleAndOrgId(org_id, role_id);
////				if(activeEmpDetailsByRoleId != null) {
//				roledetails.setActive_total_counts(activeEmpDetailsByRoleId.size());
//				final RoleDetails updateActiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);
////					}else {
////						final RoleDetails updateActiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);	
////					}
//				List<EmployeeDetails> inactiveEmpDetailsByRoleId = employeeDetailsService.getInactiveEmpByRoleAndOrgId(org_id, role_id);
////				if(inactiveEmpDetailsByRoleId != null) {
//				roledetails.setInactive_total_counts(inactiveEmpDetailsByRoleId.size());
//				final RoleDetails updateInactiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);
//				}else {
//					final RoleDetails updateActiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);	
//				}

//				final List<EmployeeDetails> tempDetails = employeeDetailsService.getAllEmployeeReportsByOrgId(org_id);
//				final List<EmployeeDetails> activeEmpDetails = new ArrayList();
//				final List<EmployeeDetails> inactiveEmpDetails = new ArrayList();
//				for (EmployeeDetails i : tempDetails) {
//					if (i.getIs_activated().equals(true) && role_id == i.getRoleDetails().getId()) {
//						activeEmpDetails.add(i);
//					} else if (i.getIs_activated().equals(false) && role_id == i.getRoleDetails().getId()) {
//						inactiveEmpDetails.add(i);
//					}
//				}
//				roledetails.setActive_total_counts(activeEmpDetails.size());
//				roledetails.setInactive_total_counts(inactiveEmpDetails.size());
//				RoleDetails RDetails = roleDetailsService.updateRoleDetails(roledetails);
				MailConfigDetails mailConfigDetails = mailConfigDetailsService
						.getMailConfigByOrgId(oldAdminDetails.getOrgDetails().getOrg_id());

				ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(oldAdminDetails.getOrgDetails().getOrg_id(), "mail",
						"manage-user");
				final int details = employeeDetailsService.bulkActivate(deleteIds, action);
				if (details == deleteIds.length()) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "Employee activated successfully");

					if(mailConfigDetails != null && manageIntegrate != null) {
						if (mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
//					if(mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == false) {
							String template = EmailTemplateMapperUtil.getEmployeeActivateAccountMailTemplate(details1, comment,
									loginUrl,orgCompanyDetails);
							String subject = "T-CUBE | " + " Your Account is Activated ";
							EmailSender emailSender = new EmailSender();
							emailSender.sendEmail(mailConfigDetails,oldEmployeeEmail, subject, template, true);
//					emailService.sendEmail(oldEmployeeEmail,subject,template, true);
						}
					}
//					MailSender.sendEmail(oldEmployeeEmail, template, subject, "");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Error in activate employee");
				}
				response = new Gson().toJson(jsonObject);
			}
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in bulk activate employee due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to bulk activate employee");
			}
//			jsonObject.put(RestConstants.DATA, "Error in bulk delete employee");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(bulkactiveEmployee) >> Exit");
		return response;
	}

	@PutMapping(value = "/deactivateEmployee", headers = "Accept=application/json")
	public String deactivateEmployee(@RequestBody final String details1) {
		logger.info("EmployeeDetailsApiService(deactivateEmployee) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details1);
			String id = newJsonObject.getString("id");
			newJsonObject.remove("id");
			String comment = newJsonObject.getString("comments");
			newJsonObject.remove("comments");
			Long orgId =  newJsonObject.getLong("org_id");
			newJsonObject.remove("orgId");
			String url = newJsonObject.getString("url");
			newJsonObject.remove("url");
			final EmployeeDetails oldAdminDetails = employeeDetailsService.getAllEmployeeDetailsByID(id);
			String oldEmployeeEmail = oldAdminDetails.getEmail();
			String orgCompanyName = oldAdminDetails.getOrgDetails().getCompany_name();
			oldAdminDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldAdminDetails.getPassword()));
			oldAdminDetails.setIs_activated(false);
			if (comment == "") {
				oldAdminDetails.setComments("-");
			} else {
				oldAdminDetails.setComments(comment);
			}
			final EmployeeDetails details = employeeDetailsService.updateEmployeeDetails(oldAdminDetails);

			int LeaveDetails = leaveTrackerDetailsService.getLeaveRequestByEmpIdByStatusPending(id,"deactivate");

//			Integer check = 0;
//			List<ProjectDetails> PDetails = projectDetailsService
//					.getProjectDetailsByOrgId(oldAdminDetails.getOrgDetails().getOrg_id());
//			List<JobDetails> JDetails = jobDetailsService
//					.getActiveJobDetailsByOrgId(oldAdminDetails.getOrgDetails().getOrg_id());
//			List<ProjectResourceDetails> ReDetails = new ArrayList<>();
//			List<JobAssigneeDetails> ADetails = new ArrayList<>();
//			for (ProjectDetails i : PDetails) {
//				ReDetails = i.getResourceDetails();
//				for (int j = 0; j < ReDetails.size(); j++) {
//					if (ReDetails.get(j).getEmployeeDetails().getId() == id) {
//						ReDetails.get(j).setStatus("Inactive");
//						ReDetails.remove(ReDetails.get(j));
//						j--;
//					}
//				}
//				i.setResourceDetails(ReDetails);
//				ProjectDetails projectDetails = projectDetailsService.updateProject(i);
//				if (projectDetails != null) {
//					check += 1;
//				}
//			}
//			for (JobDetails a : JDetails) {
//				ADetails = a.getJobAssigneeDetails();
//				for (int b = 0; b < ADetails.size(); b++) {
//					if (ADetails.get(b).getEmployeeDetails().getId() == id) {
//						ADetails.get(b).setStatus("Inactive");
//						ADetails.remove(ADetails.get(b));
//						b--;						
//						}
//				}
//				a.setJobAssigneeDetails(ADetails);
//				JobDetails jobDetails = jobDetailsService.updateJobDetail(a);
//			}
//			final RoleDetails roledetails = roleDetailsService.getRoleDetailsById(role_id);
//			final List<EmployeeDetails> tempDetails = employeeDetailsService.getAllEmployeeReportsByOrgId(org_id);
//			final List<EmployeeDetails> activeEmpDetails = new ArrayList();
//			final List<EmployeeDetails> inactiveEmpDetails = new ArrayList();
//			for (EmployeeDetails i : tempDetails) {
//				if (i.getIs_activated().equals(true) && role_id == i.getRoleDetails().getId()) {
//					activeEmpDetails.add(i);
//				} else if (i.getIs_activated().equals(false) && role_id == i.getRoleDetails().getId()) {
//					inactiveEmpDetails.add(i);
//				}
//			}
//			Integer inactiveCount = 0;
//			Integer activeCount = 0;
//			if (roledetails.getInactive_total_counts() == 0) {
//				inactiveCount = 0;
//			} else {
//				inactiveCount = roledetails.getInactive_total_counts();
//			}
//			if (roledetails.getActive_total_counts() == 0) {
//				activeCount = 0;
//			} else {
//				activeCount = activeEmpDetails.size();
//			}
//			roledetails.setActive_total_counts(activeCount);
////			roledetails.setActive_total_counts(activeEmpDetails.size());
//			roledetails.setInactive_total_counts(inactiveCount + 1);
//			RoleDetails RDetails = roleDetailsService.updateRoleDetails(roledetails);
			Long role_id = oldAdminDetails.getRoleDetails().getId();
			Long org_id = oldAdminDetails.getOrgDetails().getOrg_id();
			final int details2 = employeeDetailsService.deactivateEmp(id,orgId);
			final RoleDetails roledetails = roleDetailsService.getRoleDetailsById(role_id);
			
			Long branch_id = oldAdminDetails.getBranchDetails().getId();
			final ManageBranchDetails branchDetails = manageBranchDetailsService.getBranchById(branch_id);
			
			// Update active and inactive users based on branch
			List<EmployeeDetails> activeEmpDetailsByBranch = employeeDetailsService.getActiveEmpByBranchAndOrgId(org_id, branch_id);
			branchDetails.setActive_total_counts(activeEmpDetailsByBranch.size());
			final ManageBranchDetails updateActiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);
			
			List<EmployeeDetails> inactiveEmpDetailsByBranch = employeeDetailsService.getInactiveEmpByBranchAndOrgId(org_id, branch_id);
			branchDetails.setInactive_total_counts(inactiveEmpDetailsByBranch.size());
			final ManageBranchDetails updateInactiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);
			
			// Update active and inactive user count based on role after deactivate
//			List<EmployeeDetails> activeEmpDetailsByRoleId = employeeDetailsService.getActiveEmpByRoleAndOrgId(orgId, role_id);
////				if(activeEmpDetailsByRoleId != null) {
//			roledetails.setActive_total_counts(activeEmpDetailsByRoleId.size());
//			final RoleDetails updateActiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);
////					}else {
////						final RoleDetails updateActiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);	
////					}
//			List<EmployeeDetails> inactiveEmpDetailsByRoleId = employeeDetailsService.getInactiveEmpByRoleAndOrgId(orgId, role_id);
////				if(inactiveEmpDetailsByRoleId != null) {
//			roledetails.setInactive_total_counts(inactiveEmpDetailsByRoleId.size());
//			final RoleDetails updateInactiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);
//				}else {
//					final RoleDetails updateInactiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);	
//				}

			final int details3 = leaveTrackerDetailsService.updateReportingManagerAfterDeactivateUser(id,orgId);
			MailConfigDetails mailConfigDetails = mailConfigDetailsService
					.getMailConfigByOrgId(oldAdminDetails.getOrgDetails().getOrg_id());

			ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(oldAdminDetails.getOrgDetails().getOrg_id(), "mail",
					"manage-user");
//			if ((details2 != 0 || details2 == 0) && RDetails != null && (details3 != 0 || details3 == 0)) {
			if ((details2 >=0) && details!=null) {

				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Employee deactivated successfully");
				if(mailConfigDetails != null && manageIntegrate != null) {
					if(mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
						String template = EmailTemplateMapperUtil.getEmployeeDeactivateAccountMailTemplate(details, comment, orgCompanyName,url);
						String subject = "T-CUBE | " + " Your Account is Deactivated ";
//						emailService.sendEmail(oldEmployeeEmail,subject,template, true);
						EmailSender emailSender = new EmailSender();
						emailSender.sendEmail(mailConfigDetails,oldEmployeeEmail, subject, template, true);
//				MailSender.sendEmail(oldEmployeeEmail, template, subject, "");
					}
				}

			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deactivate employee");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in deactivate employee due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to deactivate employee");
			}
//			jsonObject.put(RestConstants.DATA, "Error in deactivate employee");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(deactivateEmployee) >> Exit");
		return response;
	}

	@PutMapping(value = "/bulkDeactivateEmployeeDetails", headers = "Accept=application/json")
	public String bulkDeactiveUserDetails(@RequestBody final String request) {
		logger.info("EmployeeDetailsApiService(bulkDeactiveUserDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			List<String> list = new ArrayList<String>();
			for(int i = 0; i < deleteIds.length(); i++){
				list.add(deleteIds.get(i).toString());
			}
			String action = newJsonObject.getString("action");
			newJsonObject.remove("action");
			String comment = newJsonObject.getString("comments");
			newJsonObject.remove("comments");
			final Long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			String url = newJsonObject.getString("url");
			newJsonObject.remove("url");
//			final int details = employeeDetailsService.bulkDeactiveEmp(deleteIds, action);
			for(int id=0;id<deleteIds.length();id++) {
				String stringId = deleteIds.get(id).toString();
				final EmployeeDetails oldDetails = employeeDetailsService.getAllEmployeeDetailsByID(stringId);
				String oldUserEmail = oldDetails.getEmail();
				String orgCompanyDetails = oldDetails.getOrgDetails().getCompany_name();
				oldDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldDetails.getPassword()));
				oldDetails.setIs_activated(false);
				if (comment == "") {
					oldDetails.setComments("-");
				} else {
					oldDetails.setComments(comment);
				}
				final EmployeeDetails details1 = employeeDetailsService.updateEmployeeDetails(oldDetails);
				int leaveDetails = leaveTrackerDetailsService.bulkGetLeaveRequestByEmpIdByStatusPending(deleteIds, "deactivate");

				Long role_id = oldDetails.getRoleDetails().getId();
				Long org_id = oldDetails.getOrgDetails().getOrg_id();				
				Long branch_id = oldDetails.getBranchDetails().getId();
				final ManageBranchDetails branchDetails = manageBranchDetailsService.getBranchById(branch_id);
				
				// Update active and inactive users based on branch
				List<EmployeeDetails> activeEmpDetailsByBranch = employeeDetailsService.getActiveEmpByBranchAndOrgId(org_id, branch_id);
				branchDetails.setActive_total_counts(activeEmpDetailsByBranch.size());
				final ManageBranchDetails updateActiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);
				
				List<EmployeeDetails> inactiveEmpDetailsByBranch = employeeDetailsService.getInactiveEmpByBranchAndOrgId(org_id, branch_id);
				branchDetails.setInactive_total_counts(inactiveEmpDetailsByBranch.size());
				final ManageBranchDetails updateInactiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);
				
//				final RoleDetails roledetails = roleDetailsService.getRoleDetailsById(role_id);
//				// Update active and inactive total count based on role
//				List<EmployeeDetails> activeEmpDetailsByRoleId = employeeDetailsService.getActiveEmpByRoleAndOrgId(orgId, role_id);
////				if(activeEmpDetailsByRoleId != null) {
//				roledetails.setActive_total_counts(activeEmpDetailsByRoleId.size());
//				final RoleDetails updateActiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);
////					}else {
////						final RoleDetails updateActiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);	
////					}
//				List<EmployeeDetails> inactiveEmpDetailsByRoleId = employeeDetailsService.getInactiveEmpByRoleAndOrgId(orgId, role_id);
////				if(inactiveEmpDetailsByRoleId != null) {
//				roledetails.setInactive_total_counts(inactiveEmpDetailsByRoleId.size());
//				final RoleDetails updateInactiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);
//				}else {
//					final RoleDetails updateInactiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);	
//				}
//				Integer check = 0;
//				List<ProjectDetails> PDetails = projectDetailsService
//						.getProjectDetailsByOrgId(oldDetails.getOrgDetails().getOrg_id());
//				List<JobDetails> JDetails = jobDetailsService
//						.getActiveJobDetailsByOrgId(oldDetails.getOrgDetails().getOrg_id());
//				List<ProjectResourceDetails> ReDetails = new ArrayList<>();
//				List<JobAssigneeDetails> ADetails = new ArrayList<>();
//				for (ProjectDetails i : PDetails) {
//					ReDetails = i.getResourceDetails();
//					List<ProjectResourceDetails> NewReDetails = new ArrayList<>();
//					boolean isprojectAvail= false;
//					for (int j = 0; j < ReDetails.size(); j++) {
//						if (!(list.contains(ReDetails.get(j).getEmployeeDetails().getId()))) {
////							ReDetails.remove(ReDetails.get(j));
////							j--;
//							NewReDetails.add(ReDetails.get(j));
//						}else {
////							ReDetails.remove(ReDetails.get(j));
//							ReDetails.get(j).setStatus("Inactive");
//							isprojectAvail= true;
//						}
//					}
//					if(isprojectAvail == false) {
//					i.setResourceDetails(NewReDetails);
//					ProjectDetails projectDetails = projectDetailsService.updateProject(i);
//					if (projectDetails != null) {
//						check += 1;
//					}
//					}
//				}
//				for (JobDetails a : JDetails) {
//					ADetails = a.getJobAssigneeDetails();
//					List<JobAssigneeDetails> NewADetails = new ArrayList<>();
//					boolean isjobAvail= false;
//					for (int b = 0; b < ADetails.size(); b++) {
//							if (!(list.contains(ADetails.get(b).getEmployeeDetails().getId()))) {
//								NewADetails.add(ADetails.get(b));
//							} else {
//								ADetails.get(b).setStatus("Inactive");
//								isjobAvail= true;
//							}
//					}
//					if(isjobAvail == false) {
//					a.setJobAssigneeDetails(ADetails);
//					JobDetails jobDetails = jobDetailsService.updateJobDetail(a);
//					}
//				}
//				final List<EmployeeDetails> tempDetails = employeeDetailsService.getAllEmployeeReportsByOrgId(org_id);
//				final List<EmployeeDetails> activeEmpDetails = new ArrayList();
//				final List<EmployeeDetails> inactiveEmpDetails = new ArrayList();
//				for (EmployeeDetails i : tempDetails) {
//					if (i.getIs_activated().equals(true) && role_id == i.getRoleDetails().getId()) {
//						activeEmpDetails.add(i);
//					} else if (i.getIs_activated().equals(false) && role_id == i.getRoleDetails().getId()) {
//						inactiveEmpDetails.add(i);
//					}
//				}
//				Integer inactiveCount = 0;
//				Integer activeCount = 0;
//				if (roledetails.getInactive_total_counts() == 0) {
//					inactiveCount = 0;
//				} else {
//					inactiveCount = roledetails.getInactive_total_counts();
//				}
//				if (roledetails.getActive_total_counts() == 0) {
//					activeCount = 0;
//				} else {
//					activeCount = activeEmpDetails.size();
//				}
//
//				roledetails.setActive_total_counts(activeCount);
////						roledetails.setActive_total_counts(activeEmpDetails.size());
//				roledetails.setInactive_total_counts(inactiveCount + 1);
//				RoleDetails RDetails = roleDetailsService.updateRoleDetails(roledetails);
				final int details = employeeDetailsService.bulkDeactiveEmp(deleteIds, action , orgId);
				final int details2 = leaveTrackerDetailsService.updateReporterAfterBulkDeactivateUser(deleteIds, orgId);
				MailConfigDetails mailConfigDetails = mailConfigDetailsService
						.getMailConfigByOrgId(orgId);

				ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(orgId, "mail",
						"manage-user");
				if (details == deleteIds.length()) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "Employee deactivated successfully");

					if(mailConfigDetails != null && manageIntegrate != null) {
						if(mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {

							String template = EmailTemplateMapperUtil.getEmployeeDeactivateAccountMailTemplate(details1,
									comment,orgCompanyDetails,url);
							String subject = "T-CUBE | " + " Your Account is Deactivated ";
//					emailService.sendEmail(oldUserEmail,subject,template, true);
							EmailSender emailSender = new EmailSender();
							emailSender.sendEmail(mailConfigDetails,oldUserEmail, subject, template, true);
						}
					}
//					MailSender.sendEmail(oldUserEmail, template, subject, "");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA, "Failed to bulk deactivate employee");
				}
				response = new Gson().toJson(jsonObject);
				logger.debug("EmployeeDetailsApiService(bulkDeactiveUserDetails)  >> Response :" + response);
			}
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in bulk deactivate employee due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to bulk deactivate employee");
			}
//			jsonObject.put(RestConstants.DATA, "Error in bulk delete employee");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(bulkDeactiveUserDetails)  >> Exit");
		return response;

	}

	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteEmployeeDetail(@PathVariable(value = "id") String id) {
		logger.info("EmployeeDetailsApiService(deleteEmployeeDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final EmployeeDetails oldAdminDetails = employeeDetailsService.getAllEmployeeDetailsByID(id);
			oldAdminDetails.setIs_deleted(true);
			Integer check = 0;
			List<ProjectDetails> PDetails = projectDetailsService
					.getProjectDetailsByOrgId(oldAdminDetails.getOrgDetails().getOrg_id());
			List<JobDetails> JDetails = jobDetailsService
					.getActiveJobDetailsByOrgId(oldAdminDetails.getOrgDetails().getOrg_id());
			List<ProjectResourceDetails> RDetails = new ArrayList<>();
			List<JobAssigneeDetails> ADetails = new ArrayList<>();


//			//removoing the disabled user from project after delete from inactive user table
//			
//			for (ProjectDetails i : PDetails) {
//				RDetails = i.getResourceDetails();
//				for (int j = 0; j < RDetails.size(); j++) {
//					if (RDetails.get(j).getEmployeeDetails().getId() == id) {
//					RDetails.get(j).setIs_deleted(true);
//					}
//				}
//				i.setResourceDetails(RDetails);
//				ProjectDetails projectDetails = projectDetailsService.updateProject(i);
//				if (projectDetails != null) {
//					check += 1;
//				}
//			}
//			// removing the disabled user from jobs after delete from inactive user table 
//						for (JobDetails a : JDetails) {
//							ADetails = a.getJobAssigneeDetails();
//							for (int b = 0; b < ADetails.size(); b++) {
//								if (ADetails.get(b).getEmployeeDetails().getId() == id) {
//									ADetails.get(b).setIs_deleted(true);
//								}
//							}
//						a.setJobAssigneeDetails(ADetails);
//						JobDetails jobDetails = jobDetailsService.updateJobDetail(a);
//						}		
			int empDetailsInProjectResource = projectDetailsService.removeProjectUserByEmployeeId(id);
			int empDetailsInJobAssignee = jobDetailsService.removeJobAssigneeByEmployeeId(id);
			final EmployeeDetails details = employeeDetailsService.deleteEmployeeDetails(oldAdminDetails);
			Long role_id = oldAdminDetails.getRoleDetails().getId();
			Long org_id = oldAdminDetails.getOrgDetails().getOrg_id();
			final RoleDetails roledetails = roleDetailsService.getRoleDetailsById(role_id);
			Long branch_id = oldAdminDetails.getBranchDetails().getId();
			final ManageBranchDetails branchDetails = manageBranchDetailsService.getBranchById(branch_id);
			
			// Update active and inactive users based on branch
			List<EmployeeDetails> activeEmpDetailsByBranch = employeeDetailsService.getActiveEmpByBranchAndOrgId(org_id, branch_id);
			branchDetails.setActive_total_counts(activeEmpDetailsByBranch.size());
			final ManageBranchDetails updateActiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);
			
			List<EmployeeDetails> inactiveEmpDetailsByBranch = employeeDetailsService.getInactiveEmpByBranchAndOrgId(org_id, branch_id);
			branchDetails.setInactive_total_counts(inactiveEmpDetailsByBranch.size());
			final ManageBranchDetails updateInactiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);

//			List<EmployeeDetails> activeEmpDetailsByRoleId = employeeDetailsService.getActiveEmpByRoleAndOrgId(org_id, role_id);
//			roledetails.setActive_total_counts(activeEmpDetailsByRoleId.size());
//			final RoleDetails updateActiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);
//
//			List<EmployeeDetails> inactiveEmpDetailsByRoleId = employeeDetailsService.getInactiveEmpByRoleAndOrgId(org_id, role_id);
//			roledetails.setInactive_total_counts(inactiveEmpDetailsByRoleId.size());
//			final RoleDetails updateInactiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Employee details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting Employee details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting  Employee details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(deleteEmployeeDetail) >> Exit");
		return response;
	}

	//bulk delete for user
    @PutMapping(value = "/bulkUserdelete", headers = "Accept=application/json")
	public String bulkUserdelete(@RequestBody final String request) {
		logger.info("EmployeeDetailsApiService(bulkUserdelete) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			List<String> list = new ArrayList<String>();
			for(int i = 0; i < deleteIds.length(); i++){
				list.add(deleteIds.get(i).toString());
			}
			List<JobAssigneeDetails> ADetails = new ArrayList<>();
			for(int id=0;id<deleteIds.length();id++) {
				String stringId = deleteIds.get(id).toString();
				final EmployeeDetails oldAdminDetails = employeeDetailsService
						.getAllEmployeeDetailsByID(stringId);
			}
			int empDetailsInProjectResource = projectDetailsService. removeBulkProjectUserByEmployeeId(deleteIds);
			int empDetailsInJobAssignee = jobDetailsService.removeBulkJobAssigneeByEmployeeId(deleteIds);
			final int details = employeeDetailsService.bulkUserdelete(deleteIds);
			for(int id=0;id<deleteIds.length();id++) {
				String stringId = deleteIds.get(id).toString();
				final EmployeeDetails oldAdminDetails = employeeDetailsService
						.getAllEmployeeDetailsByID(stringId);
				Long role_id = oldAdminDetails.getRoleDetails().getId();
				Long org_id = oldAdminDetails.getOrgDetails().getOrg_id();
				final RoleDetails roledetails = roleDetailsService.getRoleDetailsById(role_id);
				
				Long branch_id = oldAdminDetails.getBranchDetails().getId();
				final ManageBranchDetails branchDetails = manageBranchDetailsService.getBranchById(branch_id);
				
				// Update active and inactive users based on branch
				List<EmployeeDetails> activeEmpDetailsByBranch = employeeDetailsService.getActiveEmpByBranchAndOrgId(org_id, branch_id);
				branchDetails.setActive_total_counts(activeEmpDetailsByBranch.size());
				final ManageBranchDetails updateActiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);
				
				List<EmployeeDetails> inactiveEmpDetailsByBranch = employeeDetailsService.getInactiveEmpByBranchAndOrgId(org_id, branch_id);
				branchDetails.setInactive_total_counts(inactiveEmpDetailsByBranch.size());
				final ManageBranchDetails updateInactiveUsersByBranchAfterActivate = manageBranchDetailsService.updateBranchDetails(branchDetails);
				
				
//				List<EmployeeDetails> activeEmpDetailsByRoleId = employeeDetailsService.getActiveEmpByRoleAndOrgId(org_id, role_id);
//				roledetails.setActive_total_counts(activeEmpDetailsByRoleId.size());
//				final RoleDetails updateActiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);
//				List<EmployeeDetails> inactiveEmpDetailsByRoleId = employeeDetailsService.getInactiveEmpByRoleAndOrgId(org_id, role_id);
//				roledetails.setInactive_total_counts(inactiveEmpDetailsByRoleId.size());
//				final RoleDetails updateInactiveEmpRoleDetailsWithRole = roleDetailsService.updateRoleDetails(roledetails);
			}
			if (details == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Bulk user deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to delete bulk user");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("EmployeeDetailsApiService(bulkUserdelete) >> Response ");
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in bulk delete user");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(bulkUserdelete) >> Exit");
		return response;
	}

	/**
	 * This service is to Get Employee details by id.
	 *
	 * @param request
	 * @param ucBuilder
	 * @return
	 */
	@GetMapping(value = "/getDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getDetailsByOrgID(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("EmployeeDetailsApiService(getDetailsByOrgID) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getDetailsByOrgID)");
			List<EmployeeDetails> details = employeeDetailsService.getEmployeeDetailsByOrgID(id);
			List<EmployeeDetails> newDetails = new ArrayList();

			for (EmployeeDetails i : details) {
				i.setPassword(EncryptorUtil.decryptPropertyValue(i.getPassword()));
				if (i.getProfile_image() != null) {
					i.setProfile_image(ImageProcessor.decompressBytes(i.getProfile_image()));
					newDetails.add(i);
				} else
					newDetails.add(i);
			}
			if (newDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get employee details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getDetailsByOrgID) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  employee details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getDetailsByOrgID) >> Exit");
		return response;
	}

	//get by org id
	// emp id , emp name , emp img and org details  only
	@GetMapping(value = "/getActiveEmpDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getActiveEmpDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("EmployeeDetailsApiService(getActiveEmpDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getActiveEmpDetailsByOrgId)>> Request");
			List<EmployeeDetails> details = employeeDetailsService.getActiveEmpDetailsByOrgId(id);
			List<CustomEmployeeDetails> newDetails = new ArrayList();

			for(EmployeeDetails i : details) {
				CustomEmployeeDetails empDetails = new CustomEmployeeDetails();
				empDetails.setId(i.getId());
				empDetails.setEmail(i.getEmail());
				empDetails.setFirstname(i.getFirstname());
				empDetails.setLastname(i.getLastname());
				if(i.getProfile_image() != null) {
					empDetails.setProfile_image(ImageProcessor.decompressBytes(i.getProfile_image()));
				}
				newDetails.add(empDetails);
			}

			if (newDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get employee details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getActiveEmpDetailsByOrgId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  employee details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getActiveEmpDetailsByOrgId) >> Exit");
		return response;
	}

	@GetMapping(value = "/getInactiveDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getInactiveDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("EmployeeDetailsApiService(getInactiveDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getInactiveDetailsByOrgId)>>Request");
			List<EmployeeDetails> details = employeeDetailsService.getInactiveEmployeeDetailsByOrgID(id);
			for(EmployeeDetails i : details) {
				i.setPassword(EncryptorUtil.decryptPropertyValue(i.getPassword()));
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get employee details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getInactiveDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  employee details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getInactiveDetailsByOrgId) >> Exit");
		return response;
	}

	/**
	 * This service is to update employee password by id.
	 *
	 * @param request
	 * @param ucBuilder
	 * @return
	 */
	@PutMapping(value = "/ChangePasswordEmployee", headers = "Accept=application/json")
	public String updatePassword(@RequestBody final String passwordDetails) {
		logger.info("EmployeeDetailsApiService(updatePassword) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(passwordDetails);
			Long orgId = newJsonObject.getLong("orgId");
			newJsonObject.remove("orgId");
			String empId = newJsonObject.getString("id");
			newJsonObject.remove("id");
			String oldPassword = newJsonObject.getString("oldPassword");
			final EmployeeDetails oldempDetails = employeeDetailsService.getAllEmployeeDetailsByID(empId);
			final OrgDetails oldOrgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("EmployeeDetailsApiService(updatePassword) >> Request :" + oldempDetails);
			final EmployeeDetails newempDetails = MapperUtil.readAsObjectOf(EmployeeDetails.class,
					newJsonObject.toString());
			EmployeeDetails details = null;
//			System.out.println("----1954"+oldPassword);
//			System.out.println("-------1955"+oldempDetails.getPassword());
			if (EncryptorUtil.decryptPropertyValue(oldempDetails.getPassword()).equals(oldPassword)) {
//				System.out.println("----1956"+oldempDetails.getPassword());
				oldempDetails.setPassword(newempDetails.getPassword());
//				System.out.println("----1958"+oldempDetails.getPassword());
				details = employeeDetailsService.updateEmployeePassword(oldempDetails);
//				oldOrgDetails.setPassword(newOrgDetails.getPassword());
//				details1 = orgDetailsService.updateOrgDetails(oldOrgDetails);
			}
//			System.out.println("----1962");
			if(empId.equals(oldOrgDetails.getEmp_id())) {
				if ((EncryptorUtil.decryptPropertyValue(oldOrgDetails.getPassword())).equals(oldPassword)) {
					final OrgDetails newOrgDetails = MapperUtil.readAsObjectOf(OrgDetails.class,
							newJsonObject.toString());
					OrgDetails details1 = new OrgDetails();
					oldOrgDetails.setPassword(EncryptorUtil.encryptPropertyValue(newOrgDetails.getPassword()));
					details1 = orgDetailsService.updateOrgDetails(oldOrgDetails);
				}
			}
			newJsonObject.remove("oldPassword");
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Password has been updated successfully, Try to login with a new password!");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Old password is incorrect");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating employee password");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(updatePassword) >> Exit");
		return response;
	}

	/*
	 * Get all employee details by org id except deleted data
	 */
	@GetMapping(value = "/getAllEmployeeReportsByOrgId/{id}", headers = "Accept=application/json")
	public String getAllEmployeeReportsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("EmployeeDetailsApiService(getAllEmployeeReportsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getAllEmployeeReportsByOrgId) >> Request");
			List<EmployeeDetails> details = employeeDetailsService.getAllEmployeeReportsByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get employee details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getAllEmployeeReportsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  employee details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getAllEmployeeReportsByOrgId) >> Exit");
		return response;
	}

	/**
	 * This service is to Get users list by orgId.
	 *
	 * @param request
	 * @param ucBuilder
	 * @return
	 */
	@GetMapping(value = "/getOrgUsers/{orgId}", headers = "Accept=application/json")
	public String getOrgUsers(@PathVariable(value = "orgId") final Long orgId) {
		logger.info("EmployeeDetailsApiService(getOrgUsers) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getOrgUsers) >> Request");
			final List<EmployeeDetails> details = employeeDetailsService.getOrgUsers(orgId);
			final List<EmployeeDetails> inactivatedetails = employeeDetailsService.getinactiveOrgUsers(orgId);
			List<JSONObject> outdata = new ArrayList<JSONObject>();
			JSONArray users = new JSONArray();
			JSONArray inactivatedetailsusers = new JSONArray();
			if (details != null) {
				for(int i =0 ; i < details.size(); i++) {
					JSONObject data = new JSONObject();
					data.put("name", details.get(i).getFirstname()+" "+details.get(i).getLastname());
					data.put("id" ,details.get(i).getId());
					data.put("mail" ,details.get(i).getEmail());
					users.put(data);
				}
				if (inactivatedetails!= null) {
					for(int i =0 ; i < inactivatedetails.size(); i++) {
						JSONObject data = new JSONObject();
						data.put("name", inactivatedetails.get(i).getFirstname()+" "+inactivatedetails.get(i).getLastname());
						data.put("id" ,inactivatedetails.get(i).getId());
						data.put("mail" ,inactivatedetails.get(i).getEmail());
						inactivatedetailsusers.put(data);
					}
				}

				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, users);
				jsonObject.put("inactivateusers",inactivatedetailsusers);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting  org users list by orgId");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getOrgUsers) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  org users list by orgId");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getOrgUsers) >> Exit");
		return response;
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void scheduleTodayLeaveAsync() {
		logger.info("(---------- scheduleTodayLeaveAsync >> Entry ----------");
		List<EmployeeDetails> details = employeeDetailsService.getAllActiveEmployeeDetails();
//	JSONArray EmpIdArr =new JSONArray();
//	if(details.size() >0) {
		logger.info("(---------- scheduleTodayLeaveAsync >> Entry request----------");
		String id_list = new String();
		for(EmployeeDetails i :details) {
//		EmpIdArr.put(i.getId());
			id_list += "'" + i.getId() + "'" + ",";
		}
		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length()-1);
//	System.out.println(id_sb);
		int returnData = employeeDetailsService.updateDailyOnceDisplayTodayLeave(id_sb);
		if(returnData>0) {
			logger.info("(---------- scheduleTodayLeaveAsync >> response----------");
		} else {
			logger.info("(---------- scheduleTodayLeaveAsync >> can't receive response ----------");
		}
		logger.info("---------- scheduleTodayLeaveAsync >> Exit ----------");

//	}
//	else {
//		logger.info("---------- scheduleTodayLeaveAsync >> Exit ----------");
//		
//	}
//	
	}

	@GetMapping(value = "/getCustomActiveEmpDetailsByOrgID/{id}", headers = "Accept=application/json")
	public String getCustomActiveEmpDetailsByOrgID(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("EmployeeDetailsApiService(getCustomActiveEmpDetailsByOrgID) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getCustomActiveEmpDetailsByOrgID)");
			List<CustomEmployeeDetails2> details = employeeDetailsService.getCustomActiveEmpDetailsByOrgID(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get employee details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getCustomActiveEmpDetailsByOrgID) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  employee details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getCustomActiveEmpDetailsByOrgID) >> Exit");
		return response;
	}

	@GetMapping(value = "/getCustomInactiveEmpDetailsByOrgID/{id}", headers = "Accept=application/json")
	public String getCustomInactiveEmpDetailsByOrgID(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("EmployeeDetailsApiService(getCustomInactiveEmpDetailsByOrgID) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getCustomInactiveEmpDetailsByOrgID)");
			List<CustomEmployeeDetails2> details = employeeDetailsService.getCustomInactiveEmpDetailsByOrgID(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get employee details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in EmployeeDetailsApiService(getCustomInactiveEmpDetailsByOrgID) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  employee details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getCustomInactiveEmpDetailsByOrgID) >> Exit");
		return response;
	}

	/**
	 * To update the reporting manager for the orgadmin and employees
	 * @param details
	 * @return
	 */
	@PutMapping(value = "/updatereportingmanager", headers = "Accept=application/json")
	public String updateReportingManager(@RequestBody final String details) {
		logger.info("EmployeeDetailsApiService(updateReportingManager) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			String empid = newJsonObject.getString("empid");
			newJsonObject.remove("empid");
			String reporting_manager = newJsonObject.getString("reporting_manager");
			newJsonObject.remove("reporting_manager");

			EmployeeDetails empdetails = employeeDetailsService.getAllEmployeeDetailsByID(empid);
			EmployeeDetails reportingManagerDetails = employeeDetailsService.getAllEmployeeDetailsByID(reporting_manager);

			//to update the reporting manager details
			empdetails.setPassword(EncryptorUtil.decryptPropertyValue(empdetails.getPassword()));
			empdetails.setReporter_name(reportingManagerDetails.getFirstname());
			empdetails.setReporting_manager(reportingManagerDetails.getId());
			empdetails.setIs_details_updated(true);
			EmployeeDetails updatedDetails = employeeDetailsService.updateEmployeeDetails(empdetails);
			if (updatedDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Reporting manager updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to update the reporting manager");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating reporting manager");
			response = new Gson().toJson(jsonObject);
		}
		return response;
	}
	
	@PutMapping(value = "/getNewReleaseByEmpId/{empid}", headers = "Accept=application/json")
	public Boolean getNewReleaseByEmpId(@PathVariable(value = "empid") String empid) {
		logger.info("EmployeeDetailsApiService(getNewReleaseByEmpId) >> Entry");
		Boolean response = null;
		final JSONObject jsonObject = new JSONObject();
		try {

			Boolean empReleaseUpdate = employeeDetailsService.getNewReleaseByEmpId(empid);
			if (empReleaseUpdate != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "getting new release by empid successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get new release by empid");
			}
			response = empReleaseUpdate;
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting new release by empid");
//			response = new Gson().toJson(jsonObject);
		}
		return response;
	}
	
	//Get the employee images by emp ids -->  ["id","id"]
	@PutMapping(value = "/getEmpImgsByIds", headers = "Accept=application/json")
	public String getEmpImagesByIds(@RequestBody final String empIdList, final UriComponentsBuilder ucBuilder) {
		logger.info("EmployeeDetailsApiService(getEmpImagesByIds) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("EmployeeDetailsApiService(getEmpImagesByIds)");
			final JSONObject newJsonObject = new JSONObject(empIdList);
			JSONArray empIds = newJsonObject.getJSONArray("emp_ids");
			newJsonObject.remove("emp_ids");
			final JSONObject details = employeeDetailsService.getEmpImagesByIds(empIds);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get images by emp ids");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in EmployeeDetailsApiService(getEmpImagesByIds) and Exception details >> "+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting images by emp ids");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("EmployeeDetailsApiService(getEmpImagesByIds) >> Exit");
		return response;
	}

	/**
	 * to check the is_details_updated column status to check the emp details is updated 
	 * if the response is true call the update method to set the column to false
	 * @param empid
	 * @return
	 */
	@GetMapping(value = "/checkdetailsupdatedstatus/{empid}", headers = "Accept=application/json")
	public Boolean checkIsDetailsUpdatedStatus(@PathVariable(value = "empid") String empid) {
		logger.info("EmployeeDetailsApiService(checkIsDetailsUpdatedStatus) >> Entry");
		Boolean response = null;
		final JSONObject jsonObject = new JSONObject();
		try {

			Boolean detailUpdated = employeeDetailsService.checkIsDetailsUpdatedColumnStatus(empid);
			if (detailUpdated.equals(true)) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Employee details recently updated");
			} else if(detailUpdated.equals(false)) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Employee details not updated recently");
			}
			response = detailUpdated;
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error while fetching the status of the isDetailsUpdated column");
			response = null;
		}
		logger.info("EmployeeDetailsApiService(checkIsDetailsUpdatedStatus) >> Exit");
		//to set the is_details_updated column to false after send response to client if column is in true
		if(response.equals(true)) {
		logger.info("EmployeeDetailsApiService(updateFalseInEmpDetailsUpdated - API service) >> Entry");
		employeeDetailsService.updateFalseInEmpDetailsUpdated(empid);
		logger.info("EmployeeDetailsApiService(updateFalseInEmpDetailsUpdated - API service) >> Exit");
		}
		return response;
	}
	
}
