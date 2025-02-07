package com.tcube.api.controller;

import java.math.BigInteger;


import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.tcube.api.model.ApprovedLeaveDetails;
import com.tcube.api.model.AppsIntegrationDetails;
import com.tcube.api.model.BusinessLetterDetails;
import com.tcube.api.model.DesignationDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.LeaveTrackerDetails;
import com.tcube.api.model.LeaveTrackerReport;
import com.tcube.api.model.MailConfigDetails;
import com.tcube.api.model.ManageIntegration;
import com.tcube.api.model.ManageLeaveTypes;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.PricingPlanDetails;
import com.tcube.api.model.ProjectDetails;
import com.tcube.api.model.ReminderDetails;
import com.tcube.api.service.ApprovedLeaveDetailsService;
import com.tcube.api.service.EmailService;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.LeaveTrackerDetailsService;
import com.tcube.api.service.ManageLeaveTypesService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.EmailSender;
import com.tcube.api.utils.EmailTemplateMapperUtil;
import com.tcube.api.utils.EncryptorUtil;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;
import com.tcube.api.service.PricingPlanService;
import com.tcube.api.service.AppsIntegrationDetailsService;
import com.tcube.api.utils.TodayLeaveUserListSendToSlack;
import com.tcube.api.service.AppsIntegrationDetailsService;
import com.tcube.api.service.ReminderDetailsService;
import com.tcube.api.service.MailConfigDetailsService;
import com.tcube.api.service.ManageIntegrationService;
import io.sentry.Sentry;
import com.tcube.api.utils.EmailSender;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/LeaveTrackerDetails" })
public class LeaveTrackerDetailsApiService {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(LeaveTrackerDetailsApiService.class);

	@Autowired
	LeaveTrackerDetailsService leaveTrackerDetailsService;
	
	@Autowired
	MailConfigDetailsService mailConfigDetailsService;
	
	@Autowired
	ManageIntegrationService manageIntegrationService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@Autowired
	EmployeeDetailsService employeeDetailsService;
	
	@Autowired
	ApprovedLeaveDetailsService approvedLeaveDetailsService;
	
	@Autowired
	ManageLeaveTypesService manageLeaveTypesService;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	PricingPlanService pricingplanService;
	
	@Autowired
	TodayLeaveUserListSendToSlack todayLeaveUserListSendToSlackService;
	
	@Autowired
	AppsIntegrationDetailsService appsIntegrationDetailsService;
	
	@Autowired
	ReminderDetailsService remainderDetailsService;
	
	
	
	// create api for timesheet
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createLeaveDetails(@RequestBody String details) {
		logger.info("LeaveTrackerDetailsApiService(createLeaveDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("LeaveTrackerDetailsApiService(createLeaveDetails) >> Request");
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			String empId = newJsonObject.getString("emp_id");
			String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");
			String leaveTrackerUrl = newJsonObject.getString("leave_tracker_url");
			newJsonObject.remove("leave_tracker_url");		
			final LeaveTrackerDetails leaveDetails = MapperUtil.readAsObjectOf(LeaveTrackerDetails.class,
					newJsonObject.toString());
//			System.out.println(leaveDetails.getTotal_days());
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			EmployeeDetails empDeatils = employeeDetailsService.getAllEmployeeDetailsByID(empId);
			EmployeeDetails repoDeatils = new EmployeeDetails();
			String oldEmployeeEmail;
			if (empDeatils.getRoleDetails().getRole().equals("OrgAdmin") && empDeatils.getReporting_manager() == null) {
				repoDeatils = empDeatils;
				oldEmployeeEmail = repoDeatils.getEmail();
			} else {
				repoDeatils = employeeDetailsService.getAllEmployeeDetailsByID(empDeatils.getReporting_manager());
				oldEmployeeEmail = repoDeatils.getEmail();
			}
			leaveDetails.setEmp_name(empDeatils.getFirstname());
			leaveDetails.setReporter(repoDeatils.getId());
			leaveDetails.setReporter_name(repoDeatils.getFirstname());
			leaveDetails.setOrgDetails(orgDetails);
			leaveDetails.setApproval_status("Pending");
			leaveDetails.setIs_deleted(false);
			leaveDetails.setIs_active(true);
//			leaveDetails.setEmp_img(empDeatils.getProfile_image());
			leaveDetails.setIs_notified_toslack(false);

//			EmployeeDetails eDetails = employeeDetailsService.getAllEmployeeDetailsByID(oldDetails.getEmp_id());
			String reasonForLeave="";
			final LeaveTrackerDetails newdetails = leaveTrackerDetailsService.createLeave(leaveDetails, zone);
			if(newdetails.getReason_for_leave() == "") {
				reasonForLeave = "-";
			} else {
				reasonForLeave = newdetails.getReason_for_leave();
			}
			
			MailConfigDetails mailConfigDetails = mailConfigDetailsService.getMailConfigByOrgId(orgDetails.getOrg_id());

			ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(orgDetails.getOrg_id(), "mail",
					"leave-tracker");
			if (mailConfigDetails != null && manageIntegrate != null) {
				
				if (mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
					String template = EmailTemplateMapperUtil.getApplyLeaveMailTemplate(newdetails,reasonForLeave,empDeatils,
							repoDeatils.getFirstname(), leaveTrackerUrl);
					String subject = "T-CUBE | " + empDeatils.getFirstname() + " Applied Leave ";
					EmailSender emailSender = new EmailSender();
					emailSender.sendEmail(mailConfigDetails,oldEmployeeEmail, subject, template, true);
//					emailService.sendEmail(oldEmployeeEmail, subject, template, true);
				}
			}

			if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Leave details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating leave details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("LeaveTrackerDetailsApiService(createLeaveDetails) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in TimesheetApprovalDetailsApiService(createLeaveDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in creating leave details due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to apply leave");
			}
//			jsonObject.put(RestConstants.DATA, "Error in creating leave details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("LeaveTrackerDetailsApiService(createLeaveDetails) >> Exit");
		return response;
	}
	
	// this api only update the leave status
	@PutMapping(value = "/updateStatus", headers = "Accept=application/json")
	public String updateleaveStatus(@RequestBody final String details) {
		logger.info("LeaveTrackerDetailsApiService(updateleaveStatus) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			String approveStatus = newJsonObject.getString("status");
			newJsonObject.remove("status");
			String comments = newJsonObject.getString("comments");
			newJsonObject.remove("comments");
			String url = newJsonObject.getString("url");
			newJsonObject.remove("url");
			LeaveTrackerDetails oldDetails = leaveTrackerDetailsService.getById(Id);

			logger.debug("LeaveTrackerDetailsApiService(updateleaveStatus) >> Request");
			oldDetails.setApproval_comments(comments);
			oldDetails.setApproval_status(approveStatus);
//			System.out.println(approveStatus);
			if (approveStatus.equals("Approved")) {
				ApprovedLeaveDetails approvedetails = new ApprovedLeaveDetails();

				EmployeeDetails eDetails = employeeDetailsService.getAllEmployeeDetailsByID(oldDetails.getEmp_id());
				OrgDetails oDetails = orgDetailsService.getOrgDetailsById(oldDetails.getOrgDetails().getOrg_id());
				ManageLeaveTypes ltDetails = manageLeaveTypesService.getById(oldDetails.getLeave_type_id());

				approvedetails.setEmp_id(oldDetails.getEmp_id());
				approvedetails.setEmp_firstname(eDetails.getFirstname());
				approvedetails.setTotal_days(oldDetails.getTotal_days());
				approvedetails.setOrgDetails(oDetails);
				approvedetails.setLeaveTypeDetails(ltDetails);
				approvedetails.setStart_date(ltDetails.getStart_date());
				approvedetails.setEnd_date(ltDetails.getEnd_date());
				String oldEmployeeEmail = eDetails.getEmail();
				
				if (comments == "") {
					eDetails.setComments("-");
				} else {
					eDetails.setComments(comments);
				}
				
				MailConfigDetails mailConfigDetails = mailConfigDetailsService
						.getMailConfigByOrgId(oDetails.getOrg_id());

				ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(oDetails.getOrg_id(), "mail", "leave-tracker");
				
				if(mailConfigDetails != null && manageIntegrate != null) {
					
				if (mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
					
					String template = EmailTemplateMapperUtil.getApproveLeaveRequestMailTemplate(eDetails, ltDetails, oldDetails, url);
					String subject = "T-CUBE | " + " Your Leave Request was approved ";
					EmailSender emailSender = new EmailSender();
					emailSender.sendEmail(mailConfigDetails,oldEmployeeEmail, subject, template, true);
//					emailService.sendEmail(oldEmployeeEmail, subject, template, true);

				}
				}
				ApprovedLeaveDetails approvedLeaveDetails = approvedLeaveDetailsService
						.createApprovedLeaveDetails(approvedetails);
			} else {
//				System.out.println("Rejected");
				EmployeeDetails eDetails = employeeDetailsService.getAllEmployeeDetailsByID(oldDetails.getEmp_id());
				OrgDetails oDetails = orgDetailsService.getOrgDetailsById(oldDetails.getOrgDetails().getOrg_id());
				ManageLeaveTypes ltDetails = manageLeaveTypesService.getById(oldDetails.getLeave_type_id());
//				final EmployeeDetails oldAdminDetails = employeeDetailsService.getAllEmployeeDetailsByID(id);
				String oldEmployeeEmail = eDetails.getEmail();
				String orgCompanyDetails = eDetails.getOrgDetails().getCompany_name();
//				eDetails.setPassword(EncryptorUtil.decryptPropertyValue(eDetails.getPassword()));
				eDetails.setIs_activated(true);
				if (comments == "") {
					eDetails.setComments("-");
				} else {
					eDetails.setComments(comments);
				}
//				System.out.println(eDetails.getFirstname()+""+eDetails.getOrgDetails().getCompany_name()+"\n"+ltDetails.getLeave_type()+""+eDetails.getComments()+
//						" "+oldDetails.getStart_date()+" "+oldDetails.getEnd_date()+" "+oldDetails.getTotal_days());
//				sending mail for reject leave request
				MailConfigDetails mailConfigDetails = mailConfigDetailsService
						.getMailConfigByOrgId(oDetails.getOrg_id());

				ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(oDetails.getOrg_id(), "mail", "leave-tracker");
				
				if(mailConfigDetails != null && manageIntegrate != null) {
					
				if (mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
					
					String template = EmailTemplateMapperUtil.getRejectLeaveRequestMailTemplate(eDetails, ltDetails,
							oldDetails, url);
					String subject = "T-CUBE | " + " Your Leave Request was Rejected ";
					EmailSender emailSender = new EmailSender();
					emailSender.sendEmail(mailConfigDetails,oldEmployeeEmail, subject, template, true);
//					emailService.sendEmail(oldEmployeeEmail, subject, template, true);

				}
				}
			}

			final LeaveTrackerDetails details1 = leaveTrackerDetailsService.updateLeaveStatus(oldDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Leave status updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating leave status");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("LeaveTrackerDetailsApiService(updateleaveStatus) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in DesignationDetailsApiService(updateleaveStatus) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in update leave status due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to update leave status");
			}
//			jsonObject.put(RestConstants.DATA, "Error in updatiing leave status");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("LeaveTrackerDetailsApiService(updateleaveStatus) >> Exit");
		return response;
	}
	
	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteLeaveDetail(@PathVariable(value = "id") Long id) {
		logger.info("LeaveTrackerDetailsApiService(deleteLeaveDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final LeaveTrackerDetails newDetails = leaveTrackerDetailsService.getById(id);
			newDetails.setIs_deleted(true);
			final LeaveTrackerDetails details = leaveTrackerDetailsService.updateLeave(newDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Leave details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting leave details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in LeaveTrackerDetailsApiService(deleteLeaveDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting leave details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("LeaveTrackerDetailsApiService(deleteLeaveDetail) >> Exit");
		return response;
	}
	
	@PutMapping(value = "/deleteLeaveComments", headers = "Accept=application/json")
	public String deleteLeaveComments(@RequestBody String details) {
		logger.info("LeaveTrackerDetailsApiService(deleteLeaveComments) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject leaveTypeDetails = new JSONObject(details);
			long id = leaveTypeDetails.getLong("id");
			leaveTypeDetails.remove("id");
			String comments = leaveTypeDetails.getString("leaveComments");
			leaveTypeDetails.remove("leaveComments");
			String approvalStatus= leaveTypeDetails.getString("approval_status");
			final LeaveTrackerDetails newDetails = leaveTrackerDetailsService.getById(id);
			if(approvalStatus.equals("cancelLeave")) {
			newDetails.setIs_deleted(true);
			newDetails.setApproval_status("Cancelled");
			newDetails.setApproval_comments(comments);
			} else if(approvalStatus.equals("approveLeave")) {
//				final LeaveTrackerDetails newDetails = leaveTrackerDetailsService.getById(id);
			newDetails.setIs_deleted(true);
			newDetails.setApproval_status("Cancelled");
			newDetails.setApproval_comments(comments);
			}
			final LeaveTrackerDetails details1 = leaveTrackerDetailsService.updateLeave(newDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, " delete Leave type comments added successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting leave type adding comments details");
			}
			response = new Gson().toJson(jsonObject);
			
		} catch(Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in (deleteLeaveComments) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting leave type details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("LeaveTrackerDetailsApiService(deleteLeaveComments) >> exit");
		return response;
		
	}
	// get api for get leave by id
	@GetMapping(value = "/getLeaveById/{id}", headers = "Accept=application/json")
	@ResponseBody
	public String getLeaveById(@PathVariable("id") final Long id) {
		logger.info("LeaveTrackerDetailsApiService(getLeaveById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			logger.debug("LeaveTrackerDetailsApiService(getLeaveById) >> Request ");
			final LeaveTrackerDetails details = leaveTrackerDetailsService.getById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given id.");
			}
			logger.debug("LeaveTrackerDetailsApiService(getLeaveById) >> Request");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in LeaveTrackerDetailsApiService(getLeaveById) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting leave details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("LeaveTrackerDetailsApiService(getLeaveById) >> Exit");
		return response;
	}
	
	// get api for get leave by emp id
	@GetMapping(value="/getActiveLeaveByEmpId/{id}",headers="Accept=application/json")
    public String getActiveLeaveByEmpId(@PathVariable final String id, final UriComponentsBuilder ucBuilder){
		logger.info("LeaveTrackerDetailsApiService(getActiveLeaveByEmpId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("LeaveTrackerDetailsApiService(getActiveLeaveByEmpId)" );
        	 List<LeaveTrackerDetails> details = leaveTrackerDetailsService.getActiveLeaveByEmpId(id);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get leave details by emp Id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error("Exception occured in LeaveTrackerDetailsApiService(getActiveLeaveByEmpId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  leave details by emp Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("LeaveTrackerDetailsApiService(getActiveLeaveByEmpId) >> Exit");
		return response;
    }
	
	// this api only get details by emp id and start date and end date and ORG ID
	@PutMapping(value = "/getLeaveByEmpIdAndYear", headers = "Accept=application/json")
	public String getLeavebyEmpIdAndYearAndOrgId(@RequestBody final String details) {
		logger.info("LeaveTrackerDetailsApiService(getLeavebyEmpIdAndYearAndOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();	
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			final LeaveTrackerDetails newDetails = MapperUtil.readAsObjectOf(LeaveTrackerDetails.class,
					newJsonObject.toString());
			OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
			newDetails.setOrgDetails(org);
//			newDetails.setIs_deleted(false);
			List<LeaveTrackerDetails> leaveDetails = leaveTrackerDetailsService.getLeaveByEmpIdAndYear(newDetails);
			//Image Decompress
//			for(LeaveTrackerDetails i: leaveDetails) {
//				if(i.getEmp_img() != null) {
//					i.setEmp_img(ImageProcessor.decompressBytes(i.getEmp_img()));
//				}
//			}
			if (leaveDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(leaveDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in getting leave details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("LeaveTrackerDetailsApiService(getLeavebyEmpIdAndYearAndOrgId) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in DesignationDetailsApiService(getLeavebyEmpIdAndYearAndOrgId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting leave details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("LeaveTrackerDetailsApiService(getLeavebyEmpIdAndYearAndOrgId) >> Exit");
		return response;
	}
	
	@PutMapping(value = "/getLeaveByEmpIdAndYearPendingAndApprovedLeave", headers = "Accept=application/json")
	public String getLeaveByEmpIdAndYearforappliedAndApprovedLeave(@RequestBody final String details) {
		logger.info("LeaveTrackerDetailsApiService(getLeaveByEmpIdAndYearforappliedAndApprovedLeave) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();	
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			final LeaveTrackerDetails newDetails = MapperUtil.readAsObjectOf(LeaveTrackerDetails.class,
					newJsonObject.toString());
			OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
			newDetails.setOrgDetails(org);
//			newDetails.setIs_deleted(false);
			List<LeaveTrackerDetails> leaveDetails = leaveTrackerDetailsService.getLeaveByEmpIdAndYearforappliedAndApprovedLeave(newDetails);
			//Image Decompress
//			for(LeaveTrackerDetails i: leaveDetails) {
//				if(i.getEmp_img() != null) {
//					i.setEmp_img(ImageProcessor.decompressBytes(i.getEmp_img()));
//				}
//			}
			if (leaveDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(leaveDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in getting leave details");
			}
			response = new Gson().toJson(jsonObject);
			logger.info("LeaveTrackerDetailsApiService(getLeaveByEmpIdAndYearforappliedAndApprovedLeave) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in LeaveTrackerDetailsApiService(getLeaveByEmpIdAndYearforappliedAndApprovedLeave) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting leave details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("LeaveTrackerDetailsApiService(getLeaveByEmpIdAndYearforappliedAndApprovedLeave) >> Exit");
		return response;
	}
	
	@PutMapping(value = "/getAllLeave", headers = "Accept=application/json")
	public String getCancelLeavebyEmpId(@RequestBody final String details,final UriComponentsBuilder ucBuilder) {
		logger.info("getCancelLeavebyEmpId(getCancelLeavebyEmpId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
				final JSONObject newJsonObject = new JSONObject(details);
				long orgId = newJsonObject.getLong("org_id");
				newJsonObject.remove("org_id");
				final LeaveTrackerDetails newDetails = MapperUtil.readAsObjectOf(LeaveTrackerDetails.class,
						newJsonObject.toString());
				OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
				newDetails.setOrgDetails(org);
				List<LeaveTrackerDetails> leaveDetails = leaveTrackerDetailsService.getAllCancelLeaveType(newDetails);	
			if (leaveDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, leaveDetails);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in getting leave details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("LeaveTrackerDetailsApiService(getCancelLeavebyEmpId) >> Response ");
		}catch(Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in DesignationDetailsApiService(getCancelLeavebyEmpId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting leave details");
		}
		logger.info("getCancelLeavebyEmpId(getCancelLeavebyEmpId) >> exit");
		return response;	
	}
	
	// this api only get details by reporter id and start date and end date and ORG ID
	@PutMapping(value = "/getLeaveByReporterIdAndYear", headers = "Accept=application/json")
	public String getLeavebyReporterIdAndYearAndOrgId(@RequestBody final String details) {
		logger.info("LeaveTrackerDetailsApiService(getLeavebyReporterIdAndYearAndOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();	
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
//			String reporter = newJsonObject.getString("reporter");
//			newJsonObject.remove("reporter");
//			String startDate = newJsonObject.getString("start_date");
//			newJsonObject.remove("start_date");
//			String endDate = newJsonObject.getString("end_date");
//			newJsonObject.remove("end_date");
//			int pageSize = newJsonObject.getInt("page_size");
//			newJsonObject.remove("page_size");
//			int pageIndex =newJsonObject.getInt("indexPos");
//			newJsonObject.remove("indexPos");
//			final JSONObject newJsonObject1 = new JSONObject();
//			newJsonObject1.put("reporter", reporter);
//			newJsonObject1.put("org_id", orgId);
//			newJsonObject1.put("start_date",startDate);
//			newJsonObject1.put("end_date", endDate);
//			System.out.println(orgId+"\n"+reporter+"\n"+startDate+"\n"+endDate+"\n"+pageSize+"\n"+pageIndex);
//			System.out.println(newJsonObject1);
			final LeaveTrackerDetails newDetails = MapperUtil.readAsObjectOf(LeaveTrackerDetails.class,
					newJsonObject.toString());
			OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
			newDetails.setOrgDetails(org);
			newDetails.setIs_deleted(true);
			List<LeaveTrackerDetails> leaveDetails = leaveTrackerDetailsService.getLeaveByReporterIdAndYear(newDetails);
			//Image Decompress
//			for(LeaveTrackerDetails i: leaveDetails) {
//				if(i.getEmp_img() != null) {
//					i.setEmp_img(ImageProcessor.decompressBytes(i.getEmp_img()));
//				}
//			}
			System.out.println(leaveDetails.size());
			if (leaveDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA,new Gson().toJson(leaveDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in getting leave details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("LeaveTrackerDetailsApiService(getLeavebyReporterIdAndYearAndOrgId) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in DesignationDetailsApiService(getLeavebyReporterIdAndYearAndOrgId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting leave details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("LeaveTrackerDetailsApiService(getLeavebyReporterIdAndYearAndOrgId) >> Exit");
		return response;
	}
	
	// this api only get details by start date and ORG ID
		@PutMapping(value = "/getLeavesByOrgId", headers = "Accept=application/json")
		public String getTodayLeavesbyOrgId(@RequestBody final String details) {
			logger.info("LeaveTrackerDetailsApiService(getTodayLeavesbyOrgId) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();	
			try {
				final JSONObject newJsonObject = new JSONObject(details);
				long orgId = newJsonObject.getLong("org_id");
				newJsonObject.remove("org_id");
				String zone = newJsonObject.getString("timezone");
				newJsonObject.remove("timezone");
				final LeaveTrackerDetails newDetails = MapperUtil.readAsObjectOf(LeaveTrackerDetails.class,
						newJsonObject.toString());
				OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
				newDetails.setOrgDetails(org);
				List<LeaveTrackerDetails> leaveDetails = leaveTrackerDetailsService.getTodayLeavesbyOrgId(newDetails,zone);
				final List<LeaveTrackerDetails> updatedleaveDetails =  new ArrayList<LeaveTrackerDetails>();
				for(int i=0;i<leaveDetails.size();i++) {
					LeaveTrackerDetails data = new LeaveTrackerDetails();
					data = leaveDetails.get(i);
					updatedleaveDetails.add(data);
//					if(data.getEmp_img() != null) {
//						data.setEmp_img(ImageProcessor.decompressBytes(leaveDetails.get(i).getEmp_img()));
//						updatedleaveDetails.add(data);
//					}else updatedleaveDetails.add(data);
				}
				if (leaveDetails != null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, new Gson().toJson(leaveDetails));
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Error in getting leave details");
				}
				response = new Gson().toJson(jsonObject);
				logger.debug("LeaveTrackerDetailsApiService(getTodayLeavesbyOrgId) >> Response");
			} catch (Exception e) {
				Sentry.captureException(e);
				e.printStackTrace();
				logger.error("Exception occured in DesignationDetailsApiService(getTodayLeavesbyOrgId) and Exception details >> " + e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in getting leave details");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("LeaveTrackerDetailsApiService(getTodayLeavesbyOrgId) >> Exit");
			return response;
		}
		
		// this api only get details by emp id and start date and end date and ORG ID for leave type date restriction
		@PutMapping(value = "/getstartDateEndDate", headers = "Accept=application/json")
		public String getLeavebyEmpIdAndYearAndOrgIdforleavetype(@RequestBody final String details) {
			logger.info("LeaveTrackerDetailsApiService(getLeavebyEmpIdAndYearAndOrgIdforleavetype) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();	
			try {
				final JSONObject newJsonObject = new JSONObject(details);
				long orgId = newJsonObject.getLong("org_id");
				newJsonObject.remove("org_id");
				final LeaveTrackerDetails newDetails = MapperUtil.readAsObjectOf(LeaveTrackerDetails.class,
						newJsonObject.toString());
				OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
				newDetails.setOrgDetails(org);
				newDetails.setIs_deleted(false);
				List<LeaveTrackerDetails> leaveDetails = leaveTrackerDetailsService.getLeaveByEmpIdAndYearForLeavetype(newDetails);
				if (leaveDetails != null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, new Gson().toJson(leaveDetails));
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Error in getting leave details");
				}
				response = new Gson().toJson(jsonObject);
				logger.debug("LeaveTrackerDetailsApiService(getLeavebyEmpIdAndYearAndOrgIdForLeavetype) >> Response");
			} catch (Exception e) {
				Sentry.captureException(e);
				e.printStackTrace();
				logger.error("Exception occured in DesignationDetailsApiService(getLeavebyEmpIdAndYearAndOrgId) and Exception details >> " + e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in getting leave details");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("LeaveTrackerDetailsApiService(getLeavebyEmpIdAndYearAndOrgIdForLeavetype) >> Exit");
			return response;
		}
		
		// this api only get approved leave details by emp id and start date and end date
				@PutMapping(value = "/getEmpDateRangeApprovedLeaves", headers = "Accept=application/json")
				public String getEmpDateRangeApprovedLeaves(@RequestBody final String request) {
					logger.info("LeaveTrackerDetailsApiService(getEmpDateRangeApprovedLeaves) >> Entry");
					String response = "";
					final JSONObject jsonObject = new JSONObject();	
					try {
						final JSONObject newJsonObject = new JSONObject(request);
						String startdate = newJsonObject.getString("startdate");
						String enddate = newJsonObject.getString("enddate");
						String emp_id = newJsonObject.getString("empid");
						List<LeaveTrackerDetails> leaveDetails = leaveTrackerDetailsService.getEmpDateRangeApprovedLeaves(startdate, enddate, emp_id);
						if (leaveDetails != null) {
							List<LeaveTrackerReport> result = new ArrayList<LeaveTrackerReport>();
							for(int i = 0; i < leaveDetails.size(); i++) {
								LeaveTrackerReport data = new LeaveTrackerReport();
								data.setEmp_id(leaveDetails.get(i).getEmp_id());
								data.setEmp_name(leaveDetails.get(i).getEmp_name());
								data.setId(leaveDetails.get(i).getId());
								data.setLeaveComments(leaveDetails.get(i).getApproval_comments());
								data.setReporter(leaveDetails.get(i).getReporter());
								data.setReporter_name(leaveDetails.get(i).getReporter_name());
								data.setTotal_days(leaveDetails.get(i).getTotal_days());
								data.setLeaveComments(leaveDetails.get(i).getLeaveComments());
								data.setLeave_type(leaveDetails.get(i).getLeave_type());
								data.setStart_date(leaveDetails.get(i).getStart_date());
								data.setEnd_date(leaveDetails.get(i).getEnd_date());
								result.add(data);
							}
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
							jsonObject.put(RestConstants.DATA, new Gson().toJson(result));
						} else {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
							jsonObject.put(RestConstants.DATA, "Error in getting leave details");
						}
						response = new Gson().toJson(jsonObject);
						logger.debug("LeaveTrackerDetailsApiService(getEmpDateRangeApprovedLeaves) >> Response" );
					} catch (Exception e) {
						Sentry.captureException(e);
						e.printStackTrace();
						logger.error("Exception occured in LeaveTrackerDetailsApiService(getEmpDateRangeApprovedLeaves) and Exception details >> " + e);
						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
						jsonObject.put(RestConstants.DATA, "Error in getting leave details");
						response = new Gson().toJson(jsonObject);
					}
					logger.info("LeaveTrackerDetailsApiService(getEmpDateRangeApprovedLeaves) >> Exit");
					return response;
				}
				
				// this api is for leave tracker reports page details getting
//				@PutMapping(value = "/getEmpLeaveDetailsReports", headers = "Accept=application/json")
//				public String getEmpLeaveDetailsReports(@RequestBody final String request) {
//					logger.info("LeaveTrackerDetailsApiService(getEmpDateRangeApprovedLeaves) >> Entry");
//					String response = "";
//					final JSONObject jsonObject = new JSONObject();	
//					try {
//						final JSONObject newJsonObject = new JSONObject(request);
//						String startdate = newJsonObject.getString("startdate");
//						String enddate = newJsonObject.getString("enddate");
////						String emp_id = newJsonObject.getString("empid");
//						Integer check = 0;
//						Integer checkExist = 0;
//						Integer checkNew = 0;
//						JSONArray empIds = newJsonObject.getJSONArray("empid");
//						for (int i = 0; i < empIds.length(); i++) {
//							String id = empIds.getString(i);
//							final ProjectDetails oldDetails = projectDetailsService.getProjectById(id);
//						}
//						List<LeaveTrackerDetails> leaveDetails = leaveTrackerDetailsService.getEmpDateRangeApprovedLeaves(startdate, enddate, emp_id);
//						if (leaveDetails != null) {
//							List<LeaveTrackerReport> result = new ArrayList<LeaveTrackerReport>();
//							for(int i = 0; i < leaveDetails.size(); i++) {
//								LeaveTrackerReport data = new LeaveTrackerReport();
//								data.setEmp_id(leaveDetails.get(i).getEmp_id());
//								data.setEmp_name(leaveDetails.get(i).getEmp_name());
//								data.setId(leaveDetails.get(i).getId());
//								data.setLeaveComments(leaveDetails.get(i).getApproval_comments());
//								data.setReporter(leaveDetails.get(i).getReporter());
//								data.setReporter_name(leaveDetails.get(i).getReporter_name());
//								data.setTotal_days(leaveDetails.get(i).getTotal_days());
//								data.setLeaveComments(leaveDetails.get(i).getLeaveComments());
//								data.setLeave_type(leaveDetails.get(i).getLeave_type());
//								data.setStart_date(leaveDetails.get(i).getStart_date());
//								data.setEnd_date(leaveDetails.get(i).getEnd_date());
//								result.add(data);
//							}
//							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//							jsonObject.put(RestConstants.DATA, new Gson().toJson(result));
//						} else {
//							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//							jsonObject.put(RestConstants.DATA, "Error in getting leave details");
//						}
//						response = new Gson().toJson(jsonObject);
//						logger.debug("LeaveTrackerDetailsApiService(getEmpLeaveDetailsReports) >> Response :" + response);
//					} catch (Exception e) {
//						e.printStackTrace();
//						logger.error("Exception occured in LeaveTrackerDetailsApiService(getEmpLeaveDetailsReports) and Exception details >> " + e);
//						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
//						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//						jsonObject.put(RestConstants.DATA, "Error in getting leave details");
//						response = new Gson().toJson(jsonObject);
//					}
//					logger.info("LeaveTrackerDetailsApiService(getEmpLeaveDetailsReports) >> Exit");
//					return response;
//				}
				@PutMapping(value = "/getEmpLeaveDetailsReports", headers = "Accept=application/json")
				public String getEmpLeaveDetailsReports(@RequestBody final String details) {
					logger.info("LeaveTrackerDetailsApiService(getLeavebyEmpIdAndYearAndOrgId) >> Entry");
					String response = "";
					final JSONObject jsonObject = new JSONObject();	
					try {
						final JSONObject newJsonObject = new JSONObject(details);
						long orgId = newJsonObject.getLong("org_id");
						newJsonObject.remove("org_id");
						JSONArray empIds = newJsonObject.getJSONArray("emp_id");
						newJsonObject.remove("emp_id");
						final LeaveTrackerDetails newDetails = MapperUtil.readAsObjectOf(LeaveTrackerDetails.class,
								newJsonObject.toString());
						OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
						newDetails.setOrgDetails(org);
						List leaveDetails = leaveTrackerDetailsService.getLeaveByEmpIdAndYearForReports(newDetails,empIds);
						//Image Decompress
//						for(LeaveTrackerDetails i: leaveDetails) {
//							if(i.getEmp_img() != null) {
//								i.setEmp_img(ImageProcessor.decompressBytes(i.getEmp_img()));
//							}
//						}
						if (leaveDetails != null) {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
							jsonObject.put(RestConstants.DATA, new Gson().toJson(leaveDetails));
						} else {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
							jsonObject.put(RestConstants.DATA, "Error in getting leave details");
						}
						response = new Gson().toJson(jsonObject);
						logger.debug("LeaveTrackerDetailsApiService(getEmpLeaveDetailsReports) >> Response ");
					} catch (Exception e) {
						Sentry.captureException(e);
						e.printStackTrace();
						logger.error("Exception occured in DesignationDetailsApiService(getEmpLeaveDetailsReports) and Exception details >> " + e);
						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
						jsonObject.put(RestConstants.DATA, "Error in getting leave details");
						response = new Gson().toJson(jsonObject);
					}
					logger.info("LeaveTrackerDetailsApiService(getEmpLeaveDetailsReports) >> Exit");
					return response;
				}
				
//	To get approved causal leave and sick leave counts for current year
				@PutMapping(value = "/getEmpCausalAndSickLeaveCountForCurrentYear", headers = "Accept=application/json")
				public String getEmpCausalAndSickLeaveCountForCurrentYear(@RequestBody final String request) {
					logger.info("LeaveTrackerDetailsApiService(getEmpCausalAndSickLeaveCountForCurrentYear) >> Entry");
					String response = "";
					final JSONObject jsonObject = new JSONObject();	
					try {
						final JSONObject newJsonObject = new JSONObject(request);
						String emp_id = newJsonObject.getString("empid");
						String org_id = newJsonObject.getString("orgid");
						JSONObject leaveDetails = leaveTrackerDetailsService.getEmpCausalAndSickLeaveCount(org_id,emp_id);
						if (leaveDetails != null) {
							List<LeaveTrackerReport> result = new ArrayList<LeaveTrackerReport>();
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
							jsonObject.put(RestConstants.DATA, new Gson().toJson(leaveDetails));
						} else {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
							jsonObject.put(RestConstants.DATA, "Error in getting leave counts");
						}
						response = new Gson().toJson(jsonObject);
						logger.debug("LeaveTrackerDetailsApiService(getEmpCausalAndSickLeaveCountForCurrentYear) >> Response" );
					} catch (Exception e) {
						Sentry.captureException(e);
						e.printStackTrace();
						logger.error("Exception occured in LeaveTrackerDetailsApiService(getEmpCausalAndSickLeaveCountForCurrentYear) and Exception details >> " + e);
						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
						jsonObject.put(RestConstants.DATA, "Error in getting leave counts");
						response = new Gson().toJson(jsonObject);
					}
					logger.info("LeaveTrackerDetailsApiService(getEmpCausalAndSickLeaveCountForCurrentYear) >> Exit");
					return response;
				}
				
//				To update slack notification send or not on leave approve and approved leave cancelling
				@PutMapping(value = "/updateSlackNotificationStatus", headers = "Accept=application/json")
				public String updateSlackNotificationStatus(@RequestBody final String request) {
					logger.info("LeaveTrackerDetailsApiService(updateSlackNotificationStatus) >> Entry");
					String response = "";
					final JSONObject jsonObject = new JSONObject();	
					try {
						final JSONObject newJsonObject = new JSONObject(request);
						int notificationId = newJsonObject.getInt("notificationId");
						Boolean status = newJsonObject.getBoolean("status");
					    int  leaveDetails = leaveTrackerDetailsService.updateSlackNotificationStatus(notificationId,status);
						if (leaveDetails != 0) {
							List<LeaveTrackerReport> result = new ArrayList<LeaveTrackerReport>();
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
							jsonObject.put(RestConstants.DATA, new Gson().toJson(leaveDetails));
						} else {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
							jsonObject.put(RestConstants.DATA, "Error in updating slack notification status");
						}
						response = new Gson().toJson(jsonObject);
						logger.debug("LeaveTrackerDetailsApiService(updateSlackNotificationStatus) >> Response" );
					} catch (Exception e) {
						Sentry.captureException(e);
						e.printStackTrace();
						logger.error("Exception occured in LeaveTrackerDetailsApiService(updateSlackNotificationStatus) and Exception details >> " + e);
						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
						jsonObject.put(RestConstants.DATA, "Error in updating slack notification status");
						response = new Gson().toJson(jsonObject);
					}
					logger.info("LeaveTrackerDetailsApiService(updateSlackNotificationStatus) >> Exit");
					return response;
				}
				
				//get the request badge count message
				@PutMapping(value = "/getRequestApproveLeaveCount", headers = "Accept=application/json")
				public String getRequestApproveLeaveCount(@RequestBody final String details) {
					logger.info("LeaveTrackerDetailsApiService(getrequestleavecount) >> Entry");
					String response = "";
					final JSONObject jsonObject = new JSONObject();	
					try {
						final JSONObject newJsonObject = new JSONObject(details);
						long orgId = newJsonObject.getLong("org_id");
						newJsonObject.remove("org_id");
						int count= 0;
						final LeaveTrackerDetails newDetails = MapperUtil.readAsObjectOf(LeaveTrackerDetails.class,
								newJsonObject.toString());
						OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
						newDetails.setOrgDetails(org);
						newDetails.setIs_deleted(true);
						List<LeaveTrackerDetails> leaveDetails = leaveTrackerDetailsService.getRequestLeaveCount(newDetails);
						//Image Decompress
//						for(LeaveTrackerDetails i: leaveDetails) {
//							if(i.getEmp_img() != null) {
//								i.setEmp_img(ImageProcessor.decompressBytes(i.getEmp_img()));
//							}
//						}
						count = leaveDetails.size();
						if(count > 0) {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
							jsonObject.put(RestConstants.DATA, new Gson().toJson(count));
							
						} else {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
							jsonObject.put(RestConstants.DATA, new Gson().toJson(count));
							
						}
						response = new Gson().toJson(jsonObject);
						logger.debug("LeaveTrackerDetailsApiService() >> Response ");
						
					} catch(Exception e) {
						Sentry.captureException(e);
						e.printStackTrace();
						logger.error("Exception occured in LeaveTrackerDetailsApiService(getrequestleavecount) and Exception details >> " + e);
						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
						jsonObject.put(RestConstants.DATA, "Error in getrequestleavecount");
						response = new Gson().toJson(jsonObject);
					}
					logger.info("LeaveTrackerDetailsApiService(getrequestleavecount) >> Exit");
				return response;
				}
				
				@PutMapping(value = "/getRequestLeaveDetailsCount", headers = "Accept=application/json")
				public String getRequestLeaveDetailsCount(@RequestBody final String details) {
					logger.info("LeaveTrackerDetailsApiService(getRequestLeaveDetailsCount) >> Entry");
					String response = "";
					final JSONObject jsonObject = new JSONObject();	
					try {
						final JSONObject newJsonObject = new JSONObject(details);
						long orgId = newJsonObject.getLong("org_id");
						newJsonObject.remove("org_id");
						int count= 0;
						final LeaveTrackerDetails newDetails = MapperUtil.readAsObjectOf(LeaveTrackerDetails.class,
								newJsonObject.toString());
						OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
						newDetails.setOrgDetails(org);
						newDetails.setIs_deleted(true);
						List<LeaveTrackerDetails> leaveDetails = leaveTrackerDetailsService.getRequestLeaveDetailsCount(newDetails);
						//Image Decompress
//						for(LeaveTrackerDetails i: leaveDetails) {
//							if(i.getEmp_img() != null) {
//								i.setEmp_img(ImageProcessor.decompressBytes(i.getEmp_img()));
//							}
//						}
						count = leaveDetails.size();
						if(count > 0) {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
							jsonObject.put(RestConstants.DATA, new Gson().toJson(count));
							
						} else {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
							jsonObject.put(RestConstants.DATA, new Gson().toJson(count));
							
						}
						response = new Gson().toJson(jsonObject);
						logger.debug("LeaveTrackerDetailsApiService(getRequestLeaveDetailsCount) >> Response ");
						
					} catch(Exception e) {
						Sentry.captureException(e);
						e.printStackTrace();
						logger.error("Exception occured in LeaveTrackerDetailsApiService(getrequestleavecount) and Exception details >> " + e);
						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
						jsonObject.put(RestConstants.DATA, "Error in getRequestLeaveDetailsCount");
						response = new Gson().toJson(jsonObject);
						
					}
					logger.info("LeaveTrackerDetailsApiService(getRequestLeaveDetailsCount) >> Exit");
					return response;
						
					}
				// this api only get details by reporter id and start date and end date and ORG ID
				@PutMapping(value = "/getLeaveRequestPagination", headers = "Accept=application/json")
				public String getLeaveRequestPagination(@RequestBody final String details) {
					logger.info("LeaveTrackerDetailsApiService(getLeavebyReporterIdAndYearAndOrgId) >> Entry");
					String response = "";
					final JSONObject jsonObject = new JSONObject();	
					try {
						final JSONObject newJsonObject = new JSONObject(details);
						long orgId = newJsonObject.getLong("org_id");
						newJsonObject.remove("org_id");
						String reporter = newJsonObject.getString("reporter");
						newJsonObject.remove("reporter");
						String startDate = newJsonObject.getString("start_date");
						newJsonObject.remove("start_date");
						String endDate = newJsonObject.getString("end_date");
						newJsonObject.remove("end_date");
						final int pageSize = newJsonObject.getInt("page_size");
						newJsonObject.remove("page_size");
						final int pageIndex =newJsonObject.getInt("index_position");
						newJsonObject.remove("index_position");
						
						final JSONObject newJsonObject1 = new JSONObject();
						newJsonObject1.put("reporter", reporter);
						newJsonObject1.put("start_date",startDate);
						newJsonObject1.put("end_date", endDate);
						
						System.out.println(orgId+"\n"+reporter+"\n"+startDate+"\n"+endDate+"\n"+pageSize+"\n"+pageIndex);
						System.out.println(newJsonObject1);
						final LeaveTrackerDetails newDetails = MapperUtil.readAsObjectOf(LeaveTrackerDetails.class,
								newJsonObject1.toString());
						OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
						newDetails.setOrgDetails(org);
						newDetails.setIs_deleted(true);
						List<LeaveTrackerDetails> leaveDetails = leaveTrackerDetailsService.getLeaveByReporterIdAndYear(newDetails);
						//Image Decompress
//						for(LeaveTrackerDetails i: leaveDetails) {
//							if(i.getEmp_img() != null) {
//								i.setEmp_img(ImageProcessor.decompressBytes(i.getEmp_img()));
//							}
//						}
						
						ArrayList<JSONObject> leavePageData = new ArrayList<>();
	
						 int length = leaveDetails.size();
						 int tableSize = length/pageSize;	
						 System.out.println(tableSize);
						 int m = 0, n = pageSize, approveLeaveSize = 0;
						 	for (int j = 0; j <= tableSize; j++) {
					         while (m < n) {
					               // console.log(approveLeaveSize);
					               if (length-1 < m) {
					                 break;
					               }
					               JSONObject newJsonObject3 = new JSONObject();
					               newJsonObject3.put("request_Leave_index", approveLeaveSize);
					               newJsonObject3.put("approval_status",leaveDetails.get(m).getApproval_status());
					               newJsonObject3.put("created_time", leaveDetails.get(m).getCreated_time());
					               newJsonObject3.put("emp_id", leaveDetails.get(m).getEmp_id());
//					               newJsonObject3.put("emp_img", leaveDetails.get(m).getEmp_img());
					               newJsonObject3.put("emp_name",leaveDetails.get(m).getEmp_name());
//					               newJsonObject3.put("emp_name", leaveDetails.get(m).getEmp_name());
					               newJsonObject3.put("end_date", leaveDetails.get(m).getEnd_date());
					               newJsonObject3.put("end_date_str", leaveDetails.get(m).getEnd_date_str());
					               newJsonObject3.put("half_full_day", leaveDetails.get(m).getHalf_full_day());
					               newJsonObject3.put("id", leaveDetails.get(m).getId());
					               newJsonObject3.put("is_active",leaveDetails.get(m).getIs_active());
					               newJsonObject3.put("is_deleted", leaveDetails.get(m).getIs_deleted());
					               newJsonObject3.put("leave_type", leaveDetails.get(m).getLeave_type());
					               newJsonObject3.put("leave_type_id", leaveDetails.get(m).getLeave_type_id());
					               newJsonObject3.put("modified_time",leaveDetails.get(m).getModified_time());
					               newJsonObject3.put("reason_for_leave", leaveDetails.get(m).getReason_for_leave());
					               newJsonObject3.put("reporter",leaveDetails.get(m).getReporter());
					               newJsonObject3.put("reporter_name", leaveDetails.get(m).getReporter_name());
					               newJsonObject3.put("start_date",leaveDetails.get(m).getStart_date());
					               newJsonObject3.put("start_date_str",leaveDetails.get(m).getStart_date_str());
					               newJsonObject3.put("total_days",leaveDetails.get(m).getTotal_days());
					               newJsonObject3.put("approval_comments", leaveDetails.get(m).getApproval_comments());
					               newJsonObject3.put("is_notified_toslack", leaveDetails.get(m).getIs_notified_toslack());
					               newJsonObject3.put("slack_notify", leaveDetails.get(m).getSlack_notify());

					               leavePageData.add(newJsonObject3);
					              m++;

					             }
					        
					            m = n;
					            n = m + pageSize;
					            approveLeaveSize = approveLeaveSize + 1;
					           }
						 	
//						 	System.out.println("pageIndex"+pageIndex);
						 	ArrayList<JSONObject> leavePageIndex = new ArrayList<>();
						 	for(int i=0;i<leavePageData.size();i++) {
						 		JSONObject s1=leavePageData.get(i);
//						 		System.out.println(s1.getInt("request_Leave_index"));
						 		if(pageIndex == s1.getInt("request_Leave_index")) {
						 			leavePageIndex.add(s1);
//						 			System.out.println(s1.getInt("request_Leave_index"));
						 		}
						 	}
						 				
						if (leaveDetails != null) {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
							jsonObject.put(RestConstants.DATA,new Gson().toJson(leavePageIndex));
						} else {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
							jsonObject.put(RestConstants.DATA, "Error in getting leave details");
						}
						response = new Gson().toJson(jsonObject);
						logger.debug("LeaveTrackerDetailsApiService(getLeavebyReporterIdAndYearAndOrgId) >> Response");
					} catch (Exception e) {
						Sentry.captureException(e);
						e.printStackTrace();
						logger.error("Exception occured in DesignationDetailsApiService(getLeavebyReporterIdAndYearAndOrgId) and Exception details >> " + e);
						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
						jsonObject.put(RestConstants.DATA, "Error in getting leave details");
						response = new Gson().toJson(jsonObject);
					}
					logger.info("LeaveTrackerDetailsApiService(getLeavebyReporterIdAndYearAndOrgId) >> Exit");
					return response;
				}
				
				// @Scheduled(cron = "0 15 10 ? * MON-FRI", zone = "Asia/Calcutta")
//				@Scheduled(cron = "0 0 10 ? * MON-FRI", zone ="Asia/Calcutta")
//				@GetMapping(value = "/getTodayLeaveUserListBySlack", headers = "Accept=application/json")
//				public String getTodayLeaveUserListBySlack() {
//					logger.info("LeaveTrackerDetailsApiService(getTodayLeaveUserListBySlack) >> Entry");
//					logger.info(
//							"(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> Entry ----------");
//					String response = "";
//					final JSONObject jsonObject = new JSONObject();
//					try {
//						logger.info(
//								"(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> Entry request----------");
//						ArrayList<BigInteger> orgList = leaveTrackerDetailsService
//								.getActiveOrgIdsWithLeaveTrackerPlan();
//						// System.out.println(orgList);
//
//						for (int i = 0; i < orgList.size(); i++) {
//							final long orgId = orgList.get(i).longValue();
////							System.out.println("<----------------->"+orgList.get(i).longValue());
//							ManageIntegration manageIntegration = manageIntegrationService.getOrgAMdetails(orgId,
//									"slack", "all");
//							if (manageIntegration != null) {
//								if (manageIntegration.getisActive() == true) {
//									boolean isKeyPrimaryModule = false;
//
//									String slackUrl = appsIntegrationDetailsService
//											.getSlackUrlLeaveTracker(orgList.get(i).longValue());
////									System.out.println("slackUrl.." + slackUrl);
//									List<ReminderDetails> remainderTodayLeaveUpdate = remainderDetailsService
//											.getAllRemindersByOrgId(orgList.get(i).longValue());
//									for (int r = 0; r < remainderTodayLeaveUpdate.size(); r++) {
////										System.out.println(
////												"orgID..." + orgList.get(i) + "remainderTodayLeaveUpdateSize..."
////														+ remainderTodayLeaveUpdate.get(r).getKey_primary()
////														+ remainderTodayLeaveUpdate.get(r).getIs_active());
//										String keyValue = remainderTodayLeaveUpdate.get(r).getKey_primary();
//										boolean is_Active = remainderTodayLeaveUpdate.get(r).getIs_active();
//										if (keyValue.equals("slack-today-leave-userlist") && is_Active) {
//											isKeyPrimaryModule = true;
//											// System.out.println("true..." + isKeyPrimaryModule);
//											break;
//										}
//
//									}
//									if (slackUrl != "" && isKeyPrimaryModule == true) {
////								System.out.println("orgList..." + orgList.get(i).getOrg_id());
//										List<LeaveTrackerDetails> todayLeaveUserList = leaveTrackerDetailsService
//												.getTodayLeaveUserList(orgList.get(i).longValue());
////								leaveListData.addAll(todayLeaveUserList);
//
//										if (todayLeaveUserList.size() != 0) {
//											//  System.out.println("today leave...." + orgList.get(i).longValue()
//											//  		+ todayLeaveUserList.size());
//											todayLeaveUserListSendToSlackService
//													.sendMessageToSlackLeaveUserList(slackUrl, todayLeaveUserList);
//											logger.info(
//													"(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> response----------");
//										} else {
//											if (todayLeaveUserList.size() == 0) {
//												//  System.out.println("today leave...." + orgList.get(i).longValue()
//												//  		+ todayLeaveUserList.size());
//												todayLeaveUserListSendToSlackService.sendMessageToSlack(slackUrl);
//												logger.info(
//														"(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> no data response----------");
//											}
//										}
//									}
//								}
//							}
//						}
//
////
////							final PricingPlanDetails pricingplanDetailsOrg = orgList.get(i).getPricingPlanDetails();
////							System.out.println("pricingplanDetailsOrg..."+pricingplanDetailsOrg);
////							String strModule = pricingplanDetailsOrg.getModules();
////							System.out.println("strModule..."+strModule);
////							boolean isKeyPrimaryModule = false;
////							if(pricingplanDetailsOrg.getModules() != null) {
////
////							if (strModule.contains("leave-tracker")) {
////
//////								list.add(orgList.get(i));
////								System.out.println(orgList.get(i).getOrg_id());
////								String slackUrl = appsIntegrationDetailsService
////						.getSlackUrlLeaveTracker(orgList.get(i).getOrg_id());
////								List<ReminderDetails> remainderTodayLeaveUpdate = remainderDetailsService
////										.getAllRemindersByOrgId(orgList.get(i).getOrg_id());
////								for (int r = 0; r < remainderTodayLeaveUpdate.size(); r++) {
////									System.out.println(
////											"orgID..." + orgList.get(i).getOrg_id() + "remainderTodayLeaveUpdateSize..."
////													+ remainderTodayLeaveUpdate.get(r).getKey_primary()
////													+ remainderTodayLeaveUpdate.get(r).getIs_active());
////									String keyValue = remainderTodayLeaveUpdate.get(r).getKey_primary();
////									boolean is_Active = remainderTodayLeaveUpdate.get(r).getIs_active();
////									if (keyValue.equals("slack-today-leave-userlist") && is_Active) {
////										isKeyPrimaryModule = true;
////										System.out.println("true..." + isKeyPrimaryModule);
////										break;
////									}
////
////								}
//////								System.out.println("remainderTodayLeaveUpdate.."+remainderTodayLeaveUpdate.size());
////								System.out.println(slackUrl);
//////								ArrayList leaveListData = new ArrayList();
////								if (slackUrl != null && isKeyPrimaryModule == true) {
////									System.out.println("orgList..." + orgList.get(i).getOrg_id());
////									List<LeaveTrackerDetails> todayLeaveUserList = leaveTrackerDetailsService
////											.getTodayLeaveUserList(orgList.get(i).getOrg_id());
//////									leaveListData.addAll(todayLeaveUserList);
////									System.out.println("today leave...." + todayLeaveUserList.size());
////									if (todayLeaveUserList.size() != 0) {
////										todayLeaveUserListSendToSlackService.sendMessageToSlackLeaveUserList(slackUrl,
////												todayLeaveUserList);
////										logger.info(
////												"(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> response----------");
////									} else {
////										if (todayLeaveUserList.size() == 0) {
////											todayLeaveUserListSendToSlackService.sendMessageToSlack(slackUrl);
////											logger.info(
////													"(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> no data response----------");
////										}
////									}
////
//////									HttpResponse<JsonNode> res = Unirest.get(EncryptorUtil.decryptPropertyValue(slackUrl+"/rest/api/3/search")
//////											.basicAuth(EncryptorUtil.decryptPropertyValue(credDetails.getEmail()), EncryptorUtil.decryptPropertyValue(credDetails.getToken()))
//////											.header("Accept", "application/json")
//////											.queryString("jql", jql)
//////											.asJson();
////
////								}
////							}
////							}
//////							else {
//////								System.out.println("not matching");
//////							}
////
//////								if(slackUrl.get(i).getUrl()) {
//////
//////								}
////						}
//////							System.out.println(orgList.get(i).getOrg_id());
////
//////							String subStringValue = strModule.substring(1,strModule.length()-1);
//////							String strNew = subStringValue.replaceAll('"',"");
//////							System.out.println(subStringValue);
//////							String moduleSplitName = strModule.substring(1,strModule.length()-1);
//////							String[] strModuleArrayList = moduleSplitName.split(",");
//////							for(int j=0;j<strModuleArrayList.length;j++) {
//////								if(strModuleArrayList[j].equalsIgnoreCase("leave-tracker")) {
//////									list.add(orgList.get(i));
//////								}
//////							}
////
//////							System.out.println(moduleSplitName);
////
//////						}
//////						System.out.println(list.size());
//////
//////						System.out.println(orgList.get(0).getPlan())
////
//					} catch (Exception e) {
//						logger.info("exception occuerd------>" + e);
//					}
//					logger.info("---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> Exit ----------");
//					return response;
//				}
				
				// @Scheduled(cron = "0 20 10 * * ?", zone = "Asia/Calcutta")
				// @GetMapping(value = "/getDummyDataBySlack", headers = "Accept=application/json")
				// public String getDummyDataBySlack() {
				// 	logger.info("LeaveTrackerDetailsApiService(getTodayLeaveUserListBySlack) >> Entry");
				// 	logger.info(
				// 			"(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> Entry ----------");
				// 	String response = "";
				// 	final JSONObject jsonObject = new JSONObject();
				// 	try {
				// 		logger.info(
				// 				"(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> Entry request----------");
				// 		todayLeaveUserListSendToSlackService.sendDummyData();
						
				// 	} catch(Exception e) {
				// 		logger.info(e);
				// 	}
				// 	return response;
				// }
				
	
}
