package com.tcube.api.controller;

import java.math.BigDecimal;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import com.tcube.api.model.ClientDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.LeaveTrackerReport;
import com.tcube.api.model.MailConfigDetails;
import com.tcube.api.model.ManageIntegration;
import com.tcube.api.model.NotificationsDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.ProjectDetails;
import com.tcube.api.model.ProjectResourceDetails;
import com.tcube.api.model.TimeTrackerDetails;
import com.tcube.api.model.TimesheetApprovalDetails;
import com.tcube.api.service.ClientDetailsService;
import com.tcube.api.service.EmailService;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.JobAssigneeService;
import com.tcube.api.service.JobDetailsService;
import com.tcube.api.service.MailConfigDetailsService;
import com.tcube.api.service.ManageIntegrationService;
import com.tcube.api.service.NotificationDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.ProjectDetailsService;
import com.tcube.api.service.TimeTrackerDetailsService;
import com.tcube.api.service.TimesheetApprovalDetailsService;
import com.tcube.api.utils.EmailTemplateMapperUtil;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;
import com.tcube.api.utils.EmailSender;
import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/TimeTrackerDetails" })
public class TimeTrackerDetailsApiService {
	
	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(TimeTrackerDetailsApiService.class);

	@Autowired
	TimeTrackerDetailsService timeTrackerDetailsService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@Autowired
	JobDetailsService jobDetailsService;

	@Autowired
	ClientDetailsService clientDetailsService;

	@Autowired
	JobAssigneeService jobAssigneeService;

	@Autowired
	EmployeeDetailsService employeeDetailsService;

	@Autowired
	NotificationDetailsService notificationDetailsService;

	@Autowired
	ProjectDetailsService projectDetailsService;
	
	@Autowired
	TimesheetApprovalDetailsService timesheetApprovalDetailsService;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	MailConfigDetailsService mailConfigDetailsService;
	
	@Autowired
	ManageIntegrationService manageIntegrationService;

	//create tasks
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createTasks(@RequestBody String timetrackerdetails, final UriComponentsBuilder ucBuilder) {
		logger.info("TimeTrackerDetailsApiService(createTasks) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			final JSONObject newJsonObject = new JSONObject(timetrackerdetails);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");

			long clientId = newJsonObject.getLong("client_id");
			newJsonObject.remove("client_id");
			
			String loginUrl = newJsonObject.getString("login_str");
			newJsonObject.remove("login_str");

			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			final ClientDetails clienttDetails = clientDetailsService.getClientById(clientId);
			final TimeTrackerDetails timetrackerdetails1 = MapperUtil.readAsObjectOf(TimeTrackerDetails.class,
					newJsonObject.toString());
			timetrackerdetails1.setOrgDetails(orgDetails);
			timetrackerdetails1.setClientDetails(clienttDetails);
			timetrackerdetails1.setIs_deleted(false);
			timetrackerdetails1.setIs_active(true);
			timetrackerdetails1.setApproval_status("Not submitted");
			final TimeTrackerDetails details = timeTrackerDetailsService.createTask(timetrackerdetails1);
			if (details != null) {
				JSONObject durations = timeTrackerDetailsService.gettotaltimebyprojectjob(details.getProject(),
						details.getJob(),details.getOrgDetails().getOrg_id());
				String duration = durations.getString("duration_str");
				long duration_ms = durations.getLong("duration_ms");
				JSONObject empduration = timeTrackerDetailsService.gettotaltimebyEmpId(details.getProject(),
						details.getJob(), details.getEmp_id());

				JobDetails checkDetails = jobDetailsService.getJobByJobNameAndProjectName(details.getOrgDetails().getOrg_id(),details.getProject(),
						details.getJob());
				if (checkDetails.getMail_sended() == false || checkDetails.getMail_sended() == null) {
					ProjectDetails project = projectDetailsService.getProjectByProjectNameAndOrgId(details.getProject(),
							details.getOrgDetails().getOrg_id());
					JSONArray mailArr = new JSONArray();
					List<ProjectResourceDetails> resources = project.getResourceDetails();
					for (ProjectResourceDetails i : resources) {
						if (i.getDesignation().equals("project_manager")) {
							mailArr.put(i.getEmployeeDetails());
						}
					}
					long hoursMillis;
					if (checkDetails.getHours() != null) {
						hoursMillis = TimeUnit.HOURS.toMillis(checkDetails.getHours());
					} else {
						hoursMillis = 0;
					}

					if (hoursMillis == 0) {
//						System.out.println("Hours not entered");
					} else if (hoursMillis <= duration_ms) {
//						System.out.println("Logged hours reached");
						for (int v = 0; v < mailArr.length(); v++) {
							EmployeeDetails eDetails = (EmployeeDetails) mailArr.get(v);
							
							MailConfigDetails mailConfigDetails = mailConfigDetailsService
									.getMailConfigByOrgId(orgDetails.getOrg_id());

							ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(orgDetails.getOrg_id(), "mail", "time-tracker");
							
							if(mailConfigDetails != null && manageIntegrate != null) {
								if(mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
							String template = EmailTemplateMapperUtil.getHoursReachedMailTemplate(eDetails,
									checkDetails,loginUrl);
							String subject = "T-CUBE | " + details.getJob()
									+ " Estimated Hours Exceeded";
							EmailSender emailSender = new EmailSender();
							emailSender.sendEmail(mailConfigDetails,eDetails.getEmail(), subject, template, true);
//							emailService.sendEmail(eDetails.getEmail(),subject, template,true);
							checkDetails.setMail_sended(true);
							jobDetailsService.updateJobDetail(checkDetails);
								}
							}
						}
					}
				} else {
//					System.out.println("Already mail sended");
				}

				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New Tasks created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating new task details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(createTasks ) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in creating task details due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to creating task details");
			}
//			jsonObject.put(RestConstants.DATA, "Error in creating task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(createTasks) >> Exit");
		return response;
	}
	
	//get task details by id
	@GetMapping(value = "/getTaskById/{id}", headers = "Accept=application/json")
	@ResponseBody
	public String getTasktDetailsById(@PathVariable("id") final Long id) {
		logger.info("TimeTrackerDetailsApiService(getTasktDetailsById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			final TimeTrackerDetails details = timeTrackerDetailsService.getTaskById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(getTasktDetailsById) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting Task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(getTasktDetailsById) >> Exit");
		return response;
	}

	//put method for update endtime
	@PutMapping(value = "/updateendtime", headers = "Accept=application/json")
	public String updateTaskEndTime(@RequestBody final String request) {
		logger.info("TimeTrackerDetailsApiService(updateTaskEndTime) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			
			String loginUrl = newJsonObject.getString("login_str");
			newJsonObject.remove("login_str");

			final TimeTrackerDetails oldDetails = timeTrackerDetailsService.getTaskById(Id);
			final TimeTrackerDetails newDetails = MapperUtil.readAsObjectOf(TimeTrackerDetails.class,
					newJsonObject.toString());
			oldDetails.setTime_interval(newDetails.getTime_interval());
			oldDetails.setTask_duration(newDetails.getTask_duration());
			oldDetails.setTask_duration_ms(newDetails.getTask_duration_ms());
			final TimeTrackerDetails details = timeTrackerDetailsService.updateEndTime(oldDetails);
			if (details != null) {
				JSONObject durations = timeTrackerDetailsService.gettotaltimebyprojectjob(details.getProject(),
						details.getJob(),details.getOrgDetails().getOrg_id());
				String duration = durations.getString("duration_str");
				long duration_ms = durations.getLong("duration_ms");
				JSONObject empduration = timeTrackerDetailsService.gettotaltimebyEmpId(details.getProject(),
						details.getJob(), details.getEmp_id());
				JobDetails checkDetails = jobDetailsService.getJobByJobNameAndProjectName(details.getOrgDetails().getOrg_id(),details.getProject(),
						details.getJob());
				if (checkDetails.getMail_sended() == false || checkDetails.getMail_sended() == null) {
					ProjectDetails project = projectDetailsService.getProjectByProjectNameAndOrgId(details.getProject(),
							details.getOrgDetails().getOrg_id());
					JSONArray mailArr = new JSONArray();
					List<ProjectResourceDetails> resources = project.getResourceDetails();
					for (ProjectResourceDetails i : resources) {
						if (i.getDesignation().equals("project_manager")) {
							mailArr.put(i.getEmployeeDetails());
						}
					}
					long hoursMillis;
					if (checkDetails.getHours() != null) {
						hoursMillis = TimeUnit.HOURS.toMillis(checkDetails.getHours());
					} else {
						hoursMillis = 0;
					}

					if (hoursMillis == 0) {
//						System.out.println("Hours not entered");
					} else if (hoursMillis <= duration_ms) {
//						System.out.println("Logged hours reached");
						for (int v = 0; v < mailArr.length(); v++) {
							EmployeeDetails eDetails = (EmployeeDetails) mailArr.get(v);
							MailConfigDetails mailConfigDetails = mailConfigDetailsService
									.getMailConfigByOrgId(oldDetails.getOrgDetails().getOrg_id());

							ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(oldDetails.getOrgDetails().getOrg_id(), "mail", "time-tracker");
							if(mailConfigDetails != null && manageIntegrate != null) {
								if(mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
							String template = EmailTemplateMapperUtil.getHoursReachedMailTemplate(eDetails,
									checkDetails,loginUrl);
							String subject = "T-CUBE | " + details.getJob()
									+ " Estimated Hours Exceeded";
							EmailSender emailSender = new EmailSender();
							emailSender.sendEmail(mailConfigDetails,eDetails.getEmail(), subject, template, true);
//							emailService.sendEmail(eDetails.getEmail(), subject,template,true);
							checkDetails.setMail_sended(true);
							jobDetailsService.updateJobDetail(checkDetails);
							}
							}
						}
					}
				} else {
//					System.out.println("Already mail sended");
				}
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, " Endtime for the task details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating Endtime for the task details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(updateTaskEndTime) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in updating  Endtime for the task details due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Error in updating  Endtime for the task details");
			}
//			jsonObject.put(RestConstants.DATA, "Error in updating  Endtime for the task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(updateTaskEndTime) >> Exit");
		return response;
	}

	//put method for the update taskdetails
	@PutMapping(value = "/updateTaskDetails", headers = "Accept=application/json")
	public String updateTaskDetails(@RequestBody final String request) {
		logger.info("TimeTrackerDetailsApiService(updateTaskDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");

			long clientId = newJsonObject.getLong("client_id");
			newJsonObject.remove("client_id");
			
			String loginUrl = newJsonObject.getString("login_str");
			newJsonObject.remove("login_str");

			final TimeTrackerDetails oldDetails = timeTrackerDetailsService.getTaskById(Id);
			final ClientDetails clienttDetails = clientDetailsService.getClientById(clientId);
			final TimeTrackerDetails newDetails = MapperUtil.readAsObjectOf(TimeTrackerDetails.class,
					newJsonObject.toString());
			oldDetails.setTask(newDetails.getTask());
			oldDetails.setProject(newDetails.getProject());
			oldDetails.setJob(newDetails.getJob());
			oldDetails.setBill(newDetails.getBill());
			oldDetails.setClientDetails(clienttDetails);
			oldDetails.setTask_duration(newDetails.getTask_duration());
			oldDetails.setTask_duration_ms(newDetails.getTask_duration_ms());
			oldDetails.setDate_of_request(newDetails.getDate_of_request());
			final TimeTrackerDetails details = timeTrackerDetailsService.updateTaskDetails(oldDetails);
			if (details != null) {
				JSONObject durations = timeTrackerDetailsService.gettotaltimebyprojectjob(details.getProject(),
						details.getJob(),details.getOrgDetails().getOrg_id());
				String duration = durations.getString("duration_str");
				long duration_ms = durations.getLong("duration_ms");
				JSONObject empduration = timeTrackerDetailsService.gettotaltimebyEmpId(details.getProject(),
						details.getJob(), details.getEmp_id());
				JobDetails checkDetails = jobDetailsService.getJobByJobNameAndProjectName(details.getOrgDetails().getOrg_id(),details.getProject(),
						details.getJob());
				if (checkDetails.getMail_sended() == false || checkDetails.getMail_sended() == null) {
					ProjectDetails project = projectDetailsService.getProjectByProjectNameAndOrgId(details.getProject(),
							details.getOrgDetails().getOrg_id());
					JSONArray mailArr = new JSONArray();
					List<ProjectResourceDetails> resources = project.getResourceDetails();
					for (ProjectResourceDetails i : resources) {
						if (i.getDesignation().equals("project_manager")) {
							mailArr.put(i.getEmployeeDetails());
						}
					}
					long hoursMillis;
					if (checkDetails.getHours() != null) {
						hoursMillis = TimeUnit.HOURS.toMillis(checkDetails.getHours());
					} else {
						hoursMillis = 0;
					}

					if (hoursMillis == 0) {
//						System.out.println("Hours not entered");
					} else if (hoursMillis <= duration_ms) {
//						System.out.println("Logged hours reached");
						for (int v = 0; v < mailArr.length(); v++) {
							EmployeeDetails eDetails = (EmployeeDetails) mailArr.get(v);
							MailConfigDetails mailConfigDetails = mailConfigDetailsService
									.getMailConfigByOrgId(oldDetails.getOrgDetails().getOrg_id());

							ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(oldDetails.getOrgDetails().getOrg_id(), "mail", "time-tracker");
							if(mailConfigDetails != null && manageIntegrate != null) {
								if(mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
							String template = EmailTemplateMapperUtil.getHoursReachedMailTemplate(eDetails,
									checkDetails,loginUrl);
							String subject = "T-CUBE | " + details.getJob()
									+ " Estimated Hours Exceeded";
							EmailSender emailSender = new EmailSender();
							emailSender.sendEmail(mailConfigDetails,eDetails.getEmail(), subject, template, true);
//							emailService.sendEmail(eDetails.getEmail(),subject, template, true);
							checkDetails.setMail_sended(true);
							jobDetailsService.updateJobDetail(checkDetails);
							}
							}
						}
					}
				} else {
//					System.out.println("Already mail sended");
				}
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Taks details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating Task details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(updateTaskDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in updating Task details due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to updating Task details");
			}
//			jsonObject.put(RestConstants.DATA, "Error in updating Task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(updateTaskDetails) >> Exit");
		return response;
	}

	//delete method for taskdetails
	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteTaksDetail(@PathVariable(value = "id") Long id) {
		logger.info("TimeTrackerDetailsApiService(deleteTaksDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final TimeTrackerDetails Details = timeTrackerDetailsService.getTaskById(id);
			Details.setIs_deleted(true);
			final TimeTrackerDetails details = timeTrackerDetailsService.deleteTaskDetails(Details);
			if (details != null) {
				JSONObject durations = timeTrackerDetailsService.gettotaltimebyprojectjob(details.getProject(),
						details.getJob(),details.getOrgDetails().getOrg_id());
				String duration = durations.getString("duration_str");
				long duration_ms = durations.getLong("duration_ms");
				JSONObject empduration = timeTrackerDetailsService.gettotaltimebyEmpId(details.getProject(),
						details.getJob(), details.getEmp_id());
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Task details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting Task details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(deleteTaksDetail) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting  Task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(deleteTaksDetail) >> Exit");
		return response;
	}

	// Bulk delete //
	@PutMapping(value = "/bulkDelete", headers = "Accept=application/json")
	public String bulkDelete(@RequestBody final String details) {
		logger.info("TimeTrackerDetailsApiService(bulkDelete) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			for (int i = 0; i < deleteIds.length(); i++) {
				Long id = deleteIds.getLong(i);
				final TimeTrackerDetails oldDetails = timeTrackerDetailsService.getTaskById(id);
				oldDetails.setIs_deleted(true);
				final TimeTrackerDetails newDetails = timeTrackerDetailsService.deleteTaskDetails(oldDetails);
				if (newDetails != null) {
					check += 1;
					JSONObject durations = timeTrackerDetailsService.gettotaltimebyprojectjob(newDetails.getProject(),
							newDetails.getJob(),newDetails.getOrgDetails().getOrg_id());
					String duration = durations.getString("duration_str");
					JSONObject empduration = timeTrackerDetailsService.gettotaltimebyEmpId(newDetails.getProject(),
							newDetails.getJob(), newDetails.getEmp_id());
				}
			}

			if (check == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Tasks bulk deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in bulk deleting task details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in TimeTrackerDetailsApiService(bulkDelete) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in bulk deleting task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(bulkDelete) >> Exit");
		return response;
	}

	//get taskdetails by empid and date
	@PutMapping(value = "/gettaskbyempiddate", headers = "Accept=application/json")
	public String getTasksByEmpidAndDate(@RequestBody final String request) {
		logger.info("TimeTrackerDetailsApiService(getTasksByEmpidAndDate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			logger.info("TimeTrackerDetailsApiService(getTasksByEmpidAndDate)");
			final TimeTrackerDetails newDetails = MapperUtil.readAsObjectOf(TimeTrackerDetails.class,
					newJsonObject.toString());
			final JSONObject details = timeTrackerDetailsService.getTaskByEmpidAndDate(newDetails);
			if (details != null) {
				details.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				details.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				response = new Gson().toJson(details);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, details);
				response = new Gson().toJson(jsonObject);
			}
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(getTasksByEmpidAndDate) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting Tasks details by empid and date of request");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(getTasksByEmpidAndDate) >> Exit");
		return response;
	}

	// this api get Submitted tasks by emp id an date of request and timesheet id
	// request
	@PutMapping(value = "/getsubmittedtaskbyempiddate", headers = "Accept=application/json")
	public String getSubmittedTasksByEmpidAndDate(@RequestBody final String request) {
		logger.info("TimeTrackerDetailsApiService(getTasksByEmpidAndDate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			logger.info("TimeTrackerDetailsApiService(getTasksByEmpidAndDate)");
			final TimeTrackerDetails newDetails = MapperUtil.readAsObjectOf(TimeTrackerDetails.class,
					newJsonObject.toString());
			final JSONObject details = timeTrackerDetailsService.getSubmittedTaskByEmpidAndDate(newDetails);
			if (details != null) {
				details.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				details.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				response = new Gson().toJson(details);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, details);
				response = new Gson().toJson(jsonObject);
			}
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(getTasksByEmpidAndDate) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting Tasks details by empid and date of request");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(getTasksByEmpidAndDate) >> Exit");
		return response;
	}

	//get taskdetails by active by empid
	@GetMapping(value = "/gettaskdetailsbyactive/{empid}", headers = "Accept=application/json")
	public String getTaskByActive(@PathVariable("empid") final String empid) {
		logger.info("TimeTrackerDetailsApiService(getTaskByActive) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			TimeTrackerDetails details = timeTrackerDetailsService.getTaskDetailsByActive(empid);

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found in the active.");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(getTaskByActive) and Exception details >> " + e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting Tasks details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(getTaskByActive) >> Exit");
		return response;
	}

	// update the time interval
	@PutMapping(value = "/updatenewtimeinterval", headers = "Accept=application/json")
	public String updateNewTimeInterval(@RequestBody final String request) {
		logger.info("TimeTrackerDetailsApiService(updateNewTimeInterval) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");

			String loginUrl = newJsonObject.getString("login_str");
			newJsonObject.remove("login_str");

			final TimeTrackerDetails oldDetails = timeTrackerDetailsService.getTaskById(Id);
			final TimeTrackerDetails newDetails = MapperUtil.readAsObjectOf(TimeTrackerDetails.class,
					newJsonObject.toString());
			oldDetails.setTime_interval(newDetails.getTime_interval());
			final TimeTrackerDetails details = timeTrackerDetailsService.updateNewTimeInterval(oldDetails);
			if (details != null) {
				JSONObject durations = timeTrackerDetailsService.gettotaltimebyprojectjob(details.getProject(),
						details.getJob(), details.getOrgDetails().getOrg_id());
				String duration = durations.getString("duration_str");
				long duration_ms = durations.getLong("duration_ms");
				JSONObject empduration = timeTrackerDetailsService.gettotaltimebyEmpId(details.getProject(),
						details.getJob(), details.getEmp_id());
				JobDetails checkDetails = jobDetailsService.getJobByJobNameAndProjectName(
						details.getOrgDetails().getOrg_id(), details.getProject(), details.getJob());
				if (checkDetails.getMail_sended() == false || checkDetails.getMail_sended() == null) {
					ProjectDetails project = projectDetailsService.getProjectByProjectNameAndOrgId(details.getProject(),
							details.getOrgDetails().getOrg_id());
					JSONArray mailArr = new JSONArray();
					List<ProjectResourceDetails> resources = project.getResourceDetails();
					for (ProjectResourceDetails i : resources) {
						if (i.getDesignation().equals("project_manager")) {
							mailArr.put(i.getEmployeeDetails());
						}
					}
					long hoursMillis;
					if (checkDetails.getHours() != null) {
						hoursMillis = TimeUnit.HOURS.toMillis(checkDetails.getHours());
					} else {
						hoursMillis = 0;
					}

					if (hoursMillis == 0) {
//						System.out.println("Hours not entered");
					} else if (hoursMillis <= duration_ms) {
//						System.out.println("Logged hours reached");
						for (int v = 0; v < mailArr.length(); v++) {
							EmployeeDetails eDetails = (EmployeeDetails) mailArr.get(v);
							MailConfigDetails mailConfigDetails = mailConfigDetailsService
									.getMailConfigByOrgId(oldDetails.getOrgDetails().getOrg_id());

							ManageIntegration manageIntegrate = manageIntegrationService
									.getOrgAMdetails(oldDetails.getOrgDetails().getOrg_id(), "mail", "time-tracker");

							if (mailConfigDetails != null && manageIntegrate != null) {
								if (mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
									String template = EmailTemplateMapperUtil.getHoursReachedMailTemplate(eDetails,
											checkDetails, loginUrl);
									String subject = "T-CUBE | " + details.getJob() + " Estimated Hours Exceeded";
									EmailSender emailSender = new EmailSender();
									emailSender.sendEmail(mailConfigDetails,eDetails.getEmail(), subject, template, true);
//									emailService.sendEmail(eDetails.getEmail(), subject, template, true);
									checkDetails.setMail_sended(true);
									jobDetailsService.updateJobDetail(checkDetails);
								}
							}
						}
					}
				}
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New time interval for the task  updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating New time interval for the task ");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(updateNewTimeInterval) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in updating  New time interval for the task due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to updating  New time interval for the task");
			}
//			jsonObject.put(RestConstants.DATA, "Error in updating  New time interval for the task");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(updateNewTimeInterval) >> Exit");
		return response;
	}

	// get filter data
	@PutMapping(value = "/getfilterdata", headers = "Accept=application/json")
	public String getFilterData(@RequestBody final String request) {
		logger.info("TimeTrackerDetailsApiService(getFilterData) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			String start_date = newJsonObject.getString("start_date");
			newJsonObject.remove("start_date");
			String end_date = newJsonObject.getString("end_date");
			newJsonObject.remove("end_date");

			JSONArray client_id = newJsonObject.getJSONArray("client_id");
			newJsonObject.remove("client_id");

			JSONArray project = newJsonObject.getJSONArray("project");
			newJsonObject.remove("project");

			JSONArray job = newJsonObject.getJSONArray("job");
			newJsonObject.remove("job");

			JSONArray bill = newJsonObject.getJSONArray("bill");
			newJsonObject.remove("bill");

			JSONArray status = newJsonObject.getJSONArray("status");
			newJsonObject.remove("status");

			logger.info("TimeTrackerDetailsApiService(getFilterData)");
			final TimeTrackerDetails newDetails = MapperUtil.readAsObjectOf(TimeTrackerDetails.class,
					newJsonObject.toString());
			final JSONObject details = timeTrackerDetailsService.getFilterdata(start_date, end_date, client_id, project,
					job, bill, status, newDetails);
			if (details != null) {
				details.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				details.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				response = new Gson().toJson(details);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting Tasks details by filterdata");
				response = new Gson().toJson(jsonObject);
			}
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(getFilterData) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting Tasks details by filterdata");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(getFilterData) >> Exit");
		return response;
	}

	//get bill chart by empid
	@PutMapping(value = "/getbilchartemp", headers = "Accept=application/json")
	public String getBilChartEmp(@RequestBody final String request) {
		logger.info("TimeTrackerDetailsApiService(getBilChartEmp) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			String empid = newJsonObject.getString("empid");
			String date = newJsonObject.getString("date");
			JSONObject details = timeTrackerDetailsService.getBilChartEmp(empid, date);

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, details);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in getBilChartEmp .");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(getBilChartEmp) and Exception details >> " + e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getBilChartEmp");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(getBilChartEmp) >> Exit");
		return response;
	}

	// update the reporter details and submit task details
	@PutMapping(value = "/updatereporter", headers = "Accept=application/json")
	public String updateReporterDetails(@RequestBody final String details) {
		logger.info("TimeTrackerDetailsApiService(updateReporterDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray Ids = newJsonObject.getJSONArray("Ids");
			String billable = newJsonObject.getString("billable_time");
			String nonBillable = newJsonObject.getString("nonBillable_time");
			String total_time = newJsonObject.getString("total_time");
			BigDecimal total_time_ms = newJsonObject.getBigDecimal("total_time_ms");
			String approvals_url = newJsonObject.getString("approvals_url");
			for (int i = 0; i < Ids.length(); i++) {
				Long id = Ids.getLong(i);
				final TimeTrackerDetails oldDetails = timeTrackerDetailsService.getTaskById(id);
				oldDetails.setReporter(newJsonObject.getString("approver_id"));
				EmployeeDetails reporter = employeeDetailsService
						.getAllEmployeeDetailsByID(newJsonObject.getString("approver_id"));
				oldDetails.setReporter_name(reporter.getFirstname() + " " + reporter.getLastname());
				oldDetails.setApproval_status(newJsonObject.getString("status"));
				final TimeTrackerDetails newDetails = timeTrackerDetailsService.updateReporterdetails(oldDetails);
				check += 1;
			}
			
			Long taskid = Ids.getLong(0);
			final TimeTrackerDetails taskDetails = timeTrackerDetailsService.getTaskById(taskid);
			String empId = taskDetails.getEmp_id();
			String dateOfRequest = taskDetails.getDate_of_request();
			TimesheetApprovalDetails toChecktDetails = new TimesheetApprovalDetails();
			toChecktDetails.setEmp_id(empId);
			toChecktDetails.setDate_of_request(dateOfRequest);
			List<TimesheetApprovalDetails> oldTimesheet = timesheetApprovalDetailsService
					.getTimesheetsByEmpidAndDate(toChecktDetails);
			TimesheetApprovalDetails timesheetDetails = new TimesheetApprovalDetails();
			// OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(toChecktDetails.getId());
			long timeRequest = 0;
			if (oldTimesheet.size() == 0) {
				timeRequest = 0;
				TimesheetApprovalDetails newTimesheet = new TimesheetApprovalDetails();
				Long id = Ids.getLong(0);
				final TimeTrackerDetails oldDetails = timeTrackerDetailsService.getTaskById(id);
				OrgDetails org = orgDetailsService.getOrgDetailsById(oldDetails.getOrgDetails().getOrg_id());
				newTimesheet.setOrgDetails(org);
				String sdate = oldDetails.getDate_of_request();
			    SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd");
			    Date date=formatter1.parse(sdate);
			    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
			    String strDate = formatter.format(date);
				newTimesheet.setTimesheet_name("Timesheet" + " (" + strDate + ")");
				newTimesheet.setDate_of_request(oldDetails.getDate_of_request());
				newTimesheet.setEmp_id(oldDetails.getEmp_id());
				EmployeeDetails empDetails = employeeDetailsService.getAllEmployeeDetailsByID(oldDetails.getEmp_id());
				newTimesheet.setEmp_name(empDetails.getFirstname() + " " + empDetails.getLastname());
				newTimesheet.setEmp_designation(empDetails.getDesignationDetails().getDesignation());
				newTimesheet.setReporter(oldDetails.getReporter());
				EmployeeDetails reporter = employeeDetailsService.getAllEmployeeDetailsByID(oldDetails.getReporter());
				newTimesheet.setReporter_name(reporter.getFirstname() + " " + reporter.getLastname());
				newTimesheet.setApproval_status("Submitted");
				newTimesheet.setBillable_total_time(billable);
				newTimesheet.setNon_billable_total_time(nonBillable);
				newTimesheet.setTotal_time(total_time);
				newTimesheet.setTotal_time_ms(total_time_ms);
				timesheetDetails = timesheetApprovalDetailsService.createTimesheet(newTimesheet);
			} else {
				timeRequest = oldTimesheet.size() + 1;
				TimesheetApprovalDetails newTimesheet = new TimesheetApprovalDetails();
				Long id = Ids.getLong(0);
				final TimeTrackerDetails oldDetails = timeTrackerDetailsService.getTaskById(id);
				OrgDetails org = orgDetailsService.getOrgDetailsById(oldDetails.getOrgDetails().getOrg_id());
				// final Long orgIdValue = org.getOrg_id();
				newTimesheet.setOrgDetails(org);
				String sdate = oldDetails.getDate_of_request();
			    SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd");
			    Date date=formatter1.parse(sdate);
			    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
			    String strDate = formatter.format(date);
				newTimesheet
						.setTimesheet_name("Timesheet" + timeRequest + " (" + strDate + ")");
				newTimesheet.setDate_of_request(oldDetails.getDate_of_request());
				newTimesheet.setEmp_id(oldDetails.getEmp_id());
				EmployeeDetails empDetails = employeeDetailsService.getAllEmployeeDetailsByID(oldDetails.getEmp_id());
				newTimesheet.setEmp_name(empDetails.getFirstname() + " " + empDetails.getLastname());
				newTimesheet.setEmp_designation(empDetails.getDesignationDetails().getDesignation());
				newTimesheet.setReporter(oldDetails.getReporter());
				EmployeeDetails reporter = employeeDetailsService.getAllEmployeeDetailsByID(oldDetails.getReporter());
				newTimesheet.setReporter_name(reporter.getFirstname() + " " + reporter.getLastname());
				newTimesheet.setApproval_status("Submitted");
				newTimesheet.setBillable_total_time(billable);
				newTimesheet.setNon_billable_total_time(nonBillable);
				newTimesheet.setTotal_time(total_time);
				newTimesheet.setTotal_time_ms(total_time_ms);
				timesheetDetails = timesheetApprovalDetailsService.createTimesheet(newTimesheet);
			}
			if (timesheetDetails != null) {
				for (int i = 0; i < Ids.length(); i++) {
					Long id = Ids.getLong(i);
					final TimeTrackerDetails oldDetails = timeTrackerDetailsService.getTaskById(id);
					oldDetails.setTimesheet_id(timesheetDetails.getId());
					final TimeTrackerDetails newDetails = timeTrackerDetailsService.updateTaskDetails(oldDetails);
				}
			}
			Long id = Ids.getLong(0);
			final TimeTrackerDetails oldDetails = timeTrackerDetailsService.getTaskById(id);
			

			// To send mail for approver
//			if (timesheetDetails != null) {
				MailConfigDetails mailConfigDetails = mailConfigDetailsService
						.getMailConfigByOrgId(oldDetails.getOrgDetails().getOrg_id());

				ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(oldDetails.getOrgDetails().getOrg_id(), "mail", "time-tracker");
				
//				System.out.println("data.getIsActive...."+ mailConfigDetails.getisActive() +"data.getIsActive...."+data.getisActive());
				if(mailConfigDetails != null && manageIntegrate != null) {
					
				if (mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
					
					EmployeeDetails approverDetails = employeeDetailsService
						.getAllEmployeeDetailsByID(timesheetDetails.getReporter());
				EmployeeDetails empDetails = employeeDetailsService
						.getAllEmployeeDetailsByID(timesheetDetails.getEmp_id());
				String template = EmailTemplateMapperUtil.getTimelogsSubmittedMailTemplate(empDetails, approverDetails,
						timesheetDetails.getTotal_time(),approvals_url);
				String subject = "T-CUBE | " + timesheetDetails.getEmp_name() + " Submitted Timelogs";
				EmailSender emailSender = new EmailSender();
				emailSender.sendEmail(mailConfigDetails,empDetails.getEmail(), subject, template, true);
//				emailService.sendEmail(approverDetails.getEmail(), subject,template,true);
				
				}
				}

//			}
			if (check == Ids.length() && timesheetDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, timesheetDetails);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in Submitting task details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(updateReporterDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in Submitting task details due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to Submitting task details");
			}
//			jsonObject.put(RestConstants.DATA, "Error in Submitting task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(updateReporterDetails) >> Exit");
		return response;
	}

	// update Approval status
	@PutMapping(value = "/updateapprovalstatus", headers = "Accept=application/json")
	public String updateApprovalStatus(@RequestBody final String details) {
		logger.info("TimeTrackerDetailsApiService(updateApprovalStatus) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;

			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			String comments = newJsonObject.getString("approval_comments");
			String status = newJsonObject.getString("status");
			String empId = newJsonObject.getString("emp_id");
			String dateOfRequest = newJsonObject.getString("date_of_request");
			long tSheetID = newJsonObject.getLong("timesheet_id");
			JSONArray Ids = newJsonObject.getJSONArray("Ids");
			String timesheets_url = newJsonObject.getString("timesheets_url");
			TimesheetApprovalDetails timesheetDetails = new TimesheetApprovalDetails();
			TimesheetApprovalDetails timesheet = timesheetApprovalDetailsService.getTimesheetById(tSheetID);
			timesheet.setApproval_status(status);
			timesheet.setApproval_comments(comments);
			TimesheetApprovalDetails updated = timesheetApprovalDetailsService.updateTimesheetStatus(timesheet);
			for (int i = 0; i < Ids.length(); i++) {
				Long id = Ids.getLong(i);
				final TimeTrackerDetails oldDetails = timeTrackerDetailsService.getTaskById(id);
				oldDetails.setApproval_status(status);
				oldDetails.setApproval_comments(comments);
				final TimeTrackerDetails newDetails = timeTrackerDetailsService.updateReporterdetails(oldDetails);
//				To set job logged hours only after timesheet status approved
				if (status.equals("Approved")) {
					JSONObject durations = timeTrackerDetailsService.gettotaltimebyprojectjob(oldDetails.getProject(),
							oldDetails.getJob(), orgId);
					JSONObject project_durarion = timeTrackerDetailsService
							.gettotaltimebyproject(oldDetails.getProject(), orgId);
					String project_duration = project_durarion.getString("duration_str");
					long project_duration_ms = project_durarion.getLong("duration_ms");
					String duration = durations.getString("duration_str");
					long duration_ms = durations.getLong("duration_ms");
					JSONObject empduration = timeTrackerDetailsService.gettotaltimebyEmpId(oldDetails.getProject(),
							oldDetails.getJob(), oldDetails.getEmp_id());
					JobDetails jobdetail = jobDetailsService.setLoggedhours(oldDetails.getProject(),
							oldDetails.getJob(), duration);
					jobAssigneeService.setLoggedhours(oldDetails.getEmp_id(), jobdetail.getId(),
							empduration.getString("duration"), empduration.getDouble("hours"));
					ProjectDetails project_detail = projectDetailsService.setLoggedhours(oldDetails.getProject(), orgId,
							project_duration);
				}
				check += 1;
			}

			// for update the notification approval status of timesheet
			List<NotificationsDetails> notifications = notificationDetailsService
					.getNotificationsEmpidAndDateOfRequest(empId, dateOfRequest, "My-Approvals", tSheetID);
			for (NotificationsDetails i : notifications) {
				if (status.equals("Approved")) {
					i.setApproval_status("Approved");
//					i.setApproval_comments(comments);
					NotificationsDetails Ndetails = notificationDetailsService.updateStatus(i);
				} else {
					i.setApproval_status("Rejected");
//					i.setApproval_comments(comments);
					NotificationsDetails Ndetails = notificationDetailsService.updateStatus(i);
				}
			}
//			To send mail for user regarding timesheets approval status!
			String comment = "";
			if (timesheet.getApproval_comments() == null) {
				comment = "-";
			} else {
				comment = timesheet.getApproval_comments();
			}
			if (status.equals("Approved")) {
				MailConfigDetails mailConfigDetails = mailConfigDetailsService.getMailConfigByOrgId(orgId);

				ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(orgId, "mail", "time-tracker");
				
				if(mailConfigDetails != null && manageIntegrate != null) {
					
				if (mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
					
					EmployeeDetails approverDetails = employeeDetailsService
							.getAllEmployeeDetailsByID(timesheet.getReporter());
					EmployeeDetails empDetails = employeeDetailsService
							.getAllEmployeeDetailsByID(timesheet.getEmp_id());
					String template = EmailTemplateMapperUtil.getTimesheetsApprovedOrRejectedMailTemplate(empDetails,
							approverDetails, timesheet.getTotal_time(), "approved", comment, timesheets_url);
					String subject = "T-CUBE | " + approverDetails.getFirstname() + " approved your timesheet";
					EmailSender emailSender = new EmailSender();
					emailSender.sendEmail(mailConfigDetails,empDetails.getEmail(), subject, template, true);
//					emailService.sendEmail(empDetails.getEmail(), subject, template, true);
				}
				}
			} else if (status.equals("Rejected")) {
				MailConfigDetails mailConfigDetails = mailConfigDetailsService.getMailConfigByOrgId(orgId);

				ManageIntegration manageIntegrate = manageIntegrationService.getOrgAMdetails(orgId, "mail", "time-tracker");
				
				if(mailConfigDetails != null && manageIntegrate != null) {
					
				if (mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
					
					EmployeeDetails approverDetails = employeeDetailsService
							.getAllEmployeeDetailsByID(timesheet.getReporter());
					EmployeeDetails empDetails = employeeDetailsService
							.getAllEmployeeDetailsByID(timesheet.getEmp_id());
					String template = EmailTemplateMapperUtil.getTimesheetsApprovedOrRejectedMailTemplate(empDetails,
							approverDetails, timesheet.getTotal_time(), "rejected", comment, timesheets_url);
//				System.out.println(timesheet.getApproval_comments());
					String subject = "T-CUBE | " + approverDetails.getFirstname() + " rejected your timesheet";
//					emailService.sendEmail(empDetails.getEmail(), subject, template, true);
					EmailSender emailSender = new EmailSender();
					emailSender.sendEmail(mailConfigDetails,empDetails.getEmail(), subject, template, true);
				}
				}
			}

			if (check == Ids.length() && updated != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Approval status updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating approval status");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(updateApprovalStatus) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in updating approval status due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to updating approval status");
			}
//			jsonObject.put(RestConstants.DATA, "Error in updating approval status");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(updateApprovalStatus) >> Exit");
		return response;
	}

	@PutMapping(value = "/getbilchartempmonth", headers = "Accept=application/json")
	public String getBilChartEmpMonth(@RequestBody final String request) {
		logger.info("TimeTrackerDetailsApiService(getBilChartEmp) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			String empid = newJsonObject.getString("empid");
			String startdate = newJsonObject.getString("startdate");
			String enddate = newJsonObject.getString("enddate");
			JSONObject details = timeTrackerDetailsService.getBilChartEmpMonth(empid, startdate, enddate);

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, details);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in getBilChartEmp .");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(getBilChartEmp) and Exception details >> " + e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getBilChartEmp");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(getBilChartEmp) >> Exit");
		return response;
	}

	// get dropdowndetails of projects and jobs
	@PutMapping(value = "/getprojectjobdetails", headers = "Accept=application/json")
	public String getProjectJobDropdownDetails(@RequestBody final String request) {
		logger.info("TimeTrackerDetailsApiService(getProjectJobDropdownDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			String empid = newJsonObject.getString("empid");
			Long orgid = newJsonObject.getLong("orgid");
			List<JSONObject> details = timeTrackerDetailsService.getProjectJobDropdownByOrgId(orgid, empid);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, details);
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(getProjectJobDropdownDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting dropdown details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(getProjectJobDropdownDetails) >> Exit");
		return response;
	}
	
	// post method for quick add task
	@PostMapping(value = "/createQuickAdd", headers = "Accept=application/json")
	public String createAddQuickTask(@RequestBody String timetrackerdetails, final UriComponentsBuilder ucBuilder) {
		logger.info("TimeTrackerDetailsApiService(createAddQuickTask) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			final JSONObject newJsonObject = new JSONObject(timetrackerdetails);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");

			String loginUrl = newJsonObject.getString("login_str");
			newJsonObject.remove("login_str");

			long clientId = newJsonObject.getLong("client_id");
			newJsonObject.remove("client_id");
			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			final ClientDetails clienttDetails = clientDetailsService.getClientById(clientId);
			final TimeTrackerDetails timetrackerdetails1 = MapperUtil.readAsObjectOf(TimeTrackerDetails.class,
					newJsonObject.toString());
			timetrackerdetails1.setOrgDetails(orgDetails);
			timetrackerdetails1.setClientDetails(clienttDetails);
			timetrackerdetails1.setIs_deleted(false);
			timetrackerdetails1.setIs_active(false);
			timetrackerdetails1.setApproval_status("Not submitted");
			final TimeTrackerDetails details = timeTrackerDetailsService.createTask(timetrackerdetails1);
			if (details != null) {
				JSONObject durations = timeTrackerDetailsService.gettotaltimebyprojectjob(details.getProject(),
						details.getJob(), details.getOrgDetails().getOrg_id());
				String duration = durations.getString("duration_str");
				long duration_ms = durations.getLong("duration_ms");
				JSONObject empduration = timeTrackerDetailsService.gettotaltimebyEmpId(details.getProject(),
						details.getJob(), details.getEmp_id());
				JobDetails checkDetails = jobDetailsService.getJobByJobNameAndProjectName(
						details.getOrgDetails().getOrg_id(), details.getProject(), details.getJob());
				if (checkDetails.getMail_sended() == false || checkDetails.getMail_sended() == null) {
					ProjectDetails project = projectDetailsService.getProjectByProjectNameAndOrgId(details.getProject(),
							details.getOrgDetails().getOrg_id());
					JSONArray mailArr = new JSONArray();
					List<ProjectResourceDetails> resources = project.getResourceDetails();
					for (ProjectResourceDetails i : resources) {
						if (i.getDesignation().equals("project_manager")) {
							mailArr.put(i.getEmployeeDetails());
						}
					}
					long hoursMillis;
					if (checkDetails.getHours() != null) {
						hoursMillis = TimeUnit.HOURS.toMillis(checkDetails.getHours());
					} else {
						hoursMillis = 0;
					}

					if (hoursMillis == 0) {
						// System.out.println("Hours not entered");
					} else if (hoursMillis <= duration_ms) {
//						System.out.println("Logged hours reached");
						for (int v = 0; v < mailArr.length(); v++) {
							EmployeeDetails eDetails = (EmployeeDetails) mailArr.get(v);
							MailConfigDetails mailConfigDetails = mailConfigDetailsService
									.getMailConfigByOrgId(orgDetails.getOrg_id());

							ManageIntegration manageIntegrate = manageIntegrationService
									.getOrgAMdetails(orgDetails.getOrg_id(), "mail", "time-tracker");

							if (mailConfigDetails != null && manageIntegrate != null) {

								if (mailConfigDetails.getisActive() == true && manageIntegrate.getisActive() == true) {
									String template = EmailTemplateMapperUtil.getHoursReachedMailTemplate(eDetails,
											checkDetails, loginUrl);
									String subject = "T-CUBE | " + details.getJob() + " Estimated Hours Exceeded";
									EmailSender emailSender = new EmailSender();
									emailSender.sendEmail(mailConfigDetails,eDetails.getEmail(), subject, template, true);
//									emailService.sendEmail(eDetails.getEmail(), subject, template, true);
									checkDetails.setMail_sended(true);
									jobDetailsService.updateJobDetail(checkDetails);
								}
							}
						}
					}
				} else {
//					System.out.println("Already mail sended");
				}
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New QuickddTask created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating QuickddTask details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsAddQuickTaskApiService(createTasks ) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in creating quick add tasks due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to creating quick add tasks");
			}
//			jsonObject.put(RestConstants.DATA, "Error in creating quick add tasks");
			response = new Gson().toJson(jsonObject);

		}
		logger.info("TimeTrackerDetailsApiService(createAddQuickTask) >> Exit");
		return response;

	}
	
	// get the billable and non billable hours(date wise) based on project
	@PutMapping(value = "/getHoursByOrgIdAndProject", headers = "Accept=application/json")
	public String getHoursByOrgIdAndProject(@RequestBody final String request) {
		logger.info("TimeTrackerDetailsApiService(getHoursByOrgIdAndProject) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			long orgId = newJsonObject.getLong("org_id");
			String sdate = newJsonObject.getString("sd_date");
			String edate = newJsonObject.getString("ed_date");
		    newJsonObject.remove("org_id");
		    newJsonObject.remove("sd_date");
		    newJsonObject.remove("ed_date");
			logger.info("TimeTrackerDetailsApiService(getHoursByOrgIdAndProject)");
			final JSONObject details = timeTrackerDetailsService.getHoursByOrgIdAndProject(orgId,sdate,edate);
			if (details != null) {
				details.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				details.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				response = new Gson().toJson(details);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, details);
				response = new Gson().toJson(jsonObject);
			}
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(getHoursByOrgIdAndProject) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting Tasks details by orgid and dates");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(getHoursByOrgIdAndProject) >> Exit");
		return response;
	}
	
	@GetMapping(value = "/mailcheck", headers = "Accept=application/json")
	public boolean checkMail() throws Exception {
		logger.info("TimeTrackerDetailsApiService(checkMail) >> Entry");
		String subject = "T-CUBE | " + "check"
				+ "Mail template";
            emailService.sendEmail("hari@servx.global",subject,"<html><p>hari</p><h3>testing</h3></html>",true);
//	        if(mailSend) {
//	        	 return true;
//	        }
		logger.info("TimeTrackerDetailsApiService(checkMail) >> Exit");
		return false;
	}
	
	// get dropdowndetails of projects and jobs by org and reference id
		@PutMapping(value = "/getProjectAndJobNames", headers = "Accept=application/json")
		public String getprojectandjobNames(@RequestBody final String request) {
			logger.info("TimeTrackerDetailsApiService(getProjectAndJobNames) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {
				final JSONObject newJsonObject = new JSONObject(request);
				String empid = newJsonObject.getString("empid");
				Long orgid = newJsonObject.getLong("orgid");
				List<JSONObject> details = timeTrackerDetailsService.getProjectAndJobNames(orgid, empid);
				if (details != null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA, details);
				}
				response = new Gson().toJson(jsonObject);
			} catch (Exception e) {
				Sentry.captureException(e);
				logger.error(
						"Exception occured in TimeTrackerDetailsApiService(getProjectAndJobNames) and Exception details >> "
								+ e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in getting project and job names details");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("TimeTrackerDetailsApiService(getProjectAndJobNames) >> Exit");
			return response;
		}
		
		
		/**
		 * update the status of the rejected timesheet with the respective tasks to the resubmittedin the timetracker and timesheets table
		 * @param id
		 * @return
		 */
		@PutMapping(value = "/rejectedtimelogresubmit/{id}", headers = "Accept=application/json")
		public String rejectedTimelogResubmitByTimesheetid(@PathVariable(value = "id") Long id) {
			logger.info("TimeTrackerDetailsApiService(rejectedTimelogResubmitByTimesheetid) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {
				final boolean timetrackerDetails = timeTrackerDetailsService.updateResubmittedStatus(id);
				final boolean timesheetDetails = timesheetApprovalDetailsService.updateResubmittedTimesheetStatus(id);
				if (timetrackerDetails && timesheetDetails) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "Resubmitted status updated successfully");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA, "No tasks to update the status");
				}
				response = new Gson().toJson(jsonObject);
			} catch (Exception e) {
				Sentry.captureException(e);
				logger.error(
						"Exception occured in TimeTrackerDetailsApiService(rejectedTimelogResubmitByTimesheetid) and Exception details >> "
								+ e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating the status of the rejected timesheet and tasks");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("TimeTrackerDetailsApiService(rejectedTimelogResubmitByTimesheetid) >> Exit");
			return response;
		}
		
//		To get details with employee id, and start date and end date 
		@PutMapping(value = "/getTaskDetails", headers = "Accept=application/json")
		public String getTaskDetails(@RequestBody final String request) {
			logger.info("TimeTrackerDetailsApiService(getTaskDetails) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();	
			try {
				final JSONObject newJsonObject = new JSONObject(request);
				String empid = newJsonObject.getString("empid");
				String start_date = newJsonObject.getString("start_date");
				String end_date = newJsonObject.getString("enddate");
				List<JSONObject> details = timeTrackerDetailsService.getTaskDetailsForPerformanceMetrics(empid,start_date,end_date);
				if (details != null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Error in get Task Details");
				}
				response = new Gson().toJson(jsonObject);
				logger.debug("TimeTrackerDetailsApiService(getTaskDetails) >> Response" );
			} catch (Exception e) {
				Sentry.captureException(e);
				e.printStackTrace();
				logger.error("Exception occured in TimeTrackerDetailsApiService(getTaskDetails) and Exception details >> " + e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in get Task Details");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("TimeTrackerDetailsApiService(getTaskDetails) >> Exit");
			return response;
		}
	
	@PutMapping(value = "/getProjectJobLoggedDetails", headers = "Accept=application/json")
	public String getProjectJobLoggedDetails(@RequestBody final String request) {
		logger.info("TimeTrackerDetailsApiService(getProjectJobLoggedDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			long orgId = newJsonObject.getLong("org_id");
			String sdate = newJsonObject.getString("start_date");
			String edate = newJsonObject.getString("end_date");
			long projectId = newJsonObject.getLong("project_id");
			String projectName = newJsonObject.getString("project_name");
			logger.info("TimeTrackerDetailsApiService(getProjectJobLoggedDetails)");
			final JSONObject details = timeTrackerDetailsService.getProjectJobLoggedDetails(orgId,projectId,projectName,sdate,edate);
			if (details != null) {
				details.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				details.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				response = new Gson().toJson(details);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, details);
				response = new Gson().toJson(jsonObject);
			}
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in TimeTrackerDetailsApiService(getProjectJobLoggedDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting Tasks details by orgid and dates");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimeTrackerDetailsApiService(getProjectJobLoggedDetails) >> Exit");
		return response;
	}
	
	
//		@GetMapping(value = "/functioncheck", headers = "Accept=application/json")
//		public String check() throws Exception {
//			System.out.println(timeTrackerDetailsDao.getActiveOrgIdsWithTimetrackerPlan());
//			String response = "";
//			response = new Gson().toJson(timeTrackerDetailsDao.getNotSubmittedUserListByOrgid((1));
//			return response;
//		}
	
}
