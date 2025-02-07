package com.tcube.api.controller;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.tcube.api.model.ClientDetails;
import com.tcube.api.model.CustomDetailsForProjectWithReferenceId;
import com.tcube.api.model.CustomJobsDetails;
import com.tcube.api.model.CustomProjectDetails;
import com.tcube.api.model.CustomProjectName;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.JobAssigneeDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.MailConfigDetails;
import com.tcube.api.model.ManageIntegration;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.ProjectDetails;
import com.tcube.api.model.ProjectResourceDetails;
import com.tcube.api.model.TimeTrackerDetails;
import com.tcube.api.service.ClientDetailsService;
import com.tcube.api.service.EmailService;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.JobAssigneeService;
import com.tcube.api.service.JobDetailsService;
import com.tcube.api.service.MailConfigDetailsService;
import com.tcube.api.service.ManageIntegrationService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.ProjectDetailsService;
import com.tcube.api.service.ProjectResourceService;
import com.tcube.api.service.TimeTrackerDetailsService;
import com.tcube.api.utils.EmailSender;
import com.tcube.api.utils.EmailTemplateMapperUtil;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/ProjectDetails" })
public class ProjectDetailsApiService {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(ProjectDetailsApiService.class);

	@Autowired
	ProjectDetailsService projectDetailsService;

	// service for organization details save
	@Autowired
	OrgDetailsService orgDetailsService;

	@Autowired
	ClientDetailsService clientDetailsService;
	// service for employee details save
	@Autowired
	EmployeeDetailsService employeeDetailsService;

	@Autowired
	JobDetailsService jobDetailsService;

	@Autowired
	TimeTrackerDetailsService timeTrackerDetailsService;

	@Autowired
	JobAssigneeService jobAssigneeService;

	@Autowired
	EmailService emailService;

	@Autowired
	MailConfigDetailsService mailConfigDetailsService;

	@Autowired
	ManageIntegrationService manageIntegrationService;

	@Autowired
	ProjectResourceService projectResourceService;

	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createproject(@RequestBody final String detailsOfProject, final UriComponentsBuilder ucBuilder) {
		logger.info("ProjectDetailsApiService(createproject) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfProject);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			long clientId = newJsonObject.getLong("client_id");
			newJsonObject.remove("client_id");
			String projectHeadId = newJsonObject.getString("project_head_id");
			String projects_url = newJsonObject.getString("projects_url");
			newJsonObject.remove("projects_url");
			long projectHeadRph = newJsonObject.getLong("project_head_rph");
			JSONArray projectManager = newJsonObject.getJSONArray("project_manager_details");
			JSONArray resources = newJsonObject.getJSONArray("resource_details");
			String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("project_head_id");
			newJsonObject.remove("project_head_rph");
			newJsonObject.remove("project_manager_details");
			newJsonObject.remove("resource_details");
			newJsonObject.remove("timezone");
			/**
			 * org details save
			 */
			final ProjectDetails details = MapperUtil.readAsObjectOf(ProjectDetails.class, newJsonObject.toString());
			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			details.setOrgDetails(orgDetails);
			details.setProject_status("Inprogress");
			details.setIs_activated(true);
			details.setTotal_jobs(0);

			List<ProjectResourceDetails> resourceDetailsList = new ArrayList<ProjectResourceDetails>();

			/**
			 * client details save
			 */
			final ClientDetails clientDetails = clientDetailsService.getClientById(clientId);
			details.setClientDetails(clientDetails);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));

			String s_date = " ";
			String e_date = " ";
			String cost = " ";
			if (details.getStart_date() == null) {
				s_date = "-";
			} else {
				Date date = details.getStart_date();
//			    SimpleDateFormat formatter = new SimpleDateFormat("DD-MM-yyyy");  
//			    String strDate = formatter.format(date); 
				Date tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date));
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				String strDate = dateFormat.format(tempDate);
				s_date = strDate;
			}
			if (details.getEnd_date() == null) {
				e_date = "-";
			} else {
				Date date = details.getEnd_date();
//			    SimpleDateFormat formatter = new SimpleDateFormat("DD-MM-yyyy");  
//			    String strDate = formatter.format(date); 
				Date tempdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date));
//			    Date strDate2 = details.getEnd_date(); 
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				String strDate = dateFormat.format(tempdate);
				e_date = strDate;
			}
			/**
			 * project head details save
			 */
			EmployeeDetails head_profile = employeeDetailsService.getAllEmployeeDetailsByID(projectHeadId);
			ProjectResourceDetails headResourceDetails = new ProjectResourceDetails();
			headResourceDetails.setEmployeeDetails(head_profile);
			headResourceDetails.setRate_per_hour(projectHeadRph);
			headResourceDetails.setDesignation("project_head");
			resourceDetailsList.add(headResourceDetails);
			MailConfigDetails config = mailConfigDetailsService.getMailConfigByOrgId(orgId);
			ManageIntegration access = manageIntegrationService.getOrgAMdetails(orgId, "mail", "projects/jobs");
			if (config != null && config.getisActive() == true) {
				if (access != null) {
					if (access.getisActive() == true) {
						String template = EmailTemplateMapperUtil.getNewProjectUsersMailTemplate(head_profile, s_date,
								e_date, details.getProject_name(), projects_url);
						String subject = "T-CUBE | " + "You Were Assigned To This Project As a Project Head";
						EmailSender emailSender = new EmailSender();
						emailSender.sendEmail(config, head_profile.getEmail(), subject, template, true);
//						emailService.sendEmail(head_profile.getEmail(), subject,template, true);
						logger.info(
								"ProjectDetailsApiService(createJob) >> Mail sended to :" + head_profile.getEmail());
					}
				}
			}

			/**
			 * project manager details save
			 */
			for (int i = 0; i < projectManager.length(); i++) {
				JSONObject managerObject = new JSONObject(projectManager.get(i).toString());
				String managerId = managerObject.getString("emp_id");
				Long managerRph = managerObject.getLong("rph");
				EmployeeDetails manager_profile = employeeDetailsService.getAllEmployeeDetailsByID(managerId);
				ProjectResourceDetails managerResourceDetails = new ProjectResourceDetails();
				managerResourceDetails.setEmployeeDetails(manager_profile);
				managerResourceDetails.setRate_per_hour(managerRph);
				managerResourceDetails.setDesignation("project_manager");
				resourceDetailsList.add(managerResourceDetails);
				String template2 = EmailTemplateMapperUtil.getNewProjectUsersMailTemplate(manager_profile, s_date,
						e_date, details.getProject_name(), projects_url);
				String subject2 = "T-CUBE | " + "You Were Assigned To This Project As a Manager";
				if (config != null && config.getisActive() == true) {
					if (access != null) {
						if (access.getisActive() == true) {
							EmailSender emailSender = new EmailSender();
							emailSender.sendEmail(config, manager_profile.getEmail(), subject2, template2, true);
//					emailService.sendEmail(manager_profile.getEmail(), subject2, template2, true);
							logger.info("ProjectDetailsApiService(createJob) >> Mail sended to :"
									+ manager_profile.getEmail());
						}
					}
				}

			}
			/**
			 * resource details save
			 */
			for (int i = 0; i < resources.length(); i++) {
				JSONObject emp_idObject = new JSONObject(resources.get(i).toString());
				String emp_id = emp_idObject.getString("emp_id");
				Long rph = emp_idObject.getLong("rph");
				EmployeeDetails employee_profile = employeeDetailsService.getAllEmployeeDetailsByID(emp_id);
				ProjectResourceDetails resourceDetails = new ProjectResourceDetails();
				resourceDetails.setEmployeeDetails(employee_profile);
				resourceDetails.setRate_per_hour(rph);
				resourceDetails.setDesignation("team_members");
				resourceDetailsList.add(resourceDetails);
				String template3 = EmailTemplateMapperUtil.getNewProjectUsersMailTemplate(employee_profile, s_date,
						e_date, details.getProject_name(), projects_url);
				String subject3 = "T-CUBE | " + "You Were Assigned To This Project";
				if (config != null && config.getisActive() == true) {
					if (access != null) {
						if (access.getisActive() == true) {
							EmailSender emailSender = new EmailSender();
							emailSender.sendEmail(config, employee_profile.getEmail(), subject3, template3, true);
//					emailService.sendEmail(employee_profile.getEmail(), subject3, template3, true);
							logger.info("ProjectDetailsApiService(createJob) >> Mail sended to :"
									+ employee_profile.getEmail());
						}
					}
				}
			}
			details.setResourceDetails(resourceDetailsList);

			final ProjectDetails details1 = projectDetailsService.createProject(details, zone);
			projectResourceService.updateProjectreferenceId(details1.getId());
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New project Created Successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to add Project");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ProjectDetailsApiService(createproject) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in JobDetailsApiService(createJob) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in add Project due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to add Project");
			}
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(createproject) >> Exit");
		return response;
	}

	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateProject(@RequestBody final String detailsOfProject) {
		logger.info("ProjectDetailsApiService(updateProject) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfProject);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			long clientId = newJsonObject.getLong("client_id");
			newJsonObject.remove("client_id");
			String projectHeadId = newJsonObject.getString("project_head_id");
			long projectHeadRph = newJsonObject.getLong("project_head_rph");
			JSONArray projectManager = newJsonObject.getJSONArray("project_manager_details");
			JSONArray resources = newJsonObject.getJSONArray("resource_details");
			String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("project_head_id");
			newJsonObject.remove("project_head_rph");
			newJsonObject.remove("project_manager_details");
			newJsonObject.remove("resource_details");
			newJsonObject.remove("timezone");

			final ProjectDetails details = MapperUtil.readAsObjectOf(ProjectDetails.class, newJsonObject.toString());
			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			final ClientDetails clientDetails = clientDetailsService.getClientById(clientId);
			final ProjectDetails projectDetails = projectDetailsService.getProjectById(Id);
			List<ProjectResourceDetails> oldresource = projectDetails.getResourceDetails();

			List<String> oldresource_list = new ArrayList<String>();
			for (int i = 0; i < oldresource.size(); i++) {
				oldresource_list.add(oldresource.get(i).getEmployeeDetails().getId());
			}
			// to update in time-tracker task details
			String newname = details.getProject_name();
			String oldname = projectDetails.getProject_name();
			if (!oldname.equals(newname)) {
				final TimeTrackerDetails details1 = timeTrackerDetailsService.updateprojectname(oldname, newname,
						orgId);
				final JobDetails details2 = jobDetailsService.updateProjectnameinjob(oldname, newname, orgId, Id);
			}
			projectDetails.setProject_name(details.getProject_name());
			projectDetails.setProject_cost(details.getProject_cost());
			projectDetails.setDescription(details.getDescription());
			projectDetails.setStart_date(details.getStart_date());
			projectDetails.setEnd_date(details.getEnd_date());
			projectDetails.setOrgDetails(orgDetails);
			projectDetails.setClientDetails(clientDetails);
			projectDetails.setProject_status(projectDetails.getProject_status());

			List<ProjectResourceDetails> resourceDetailsList = new ArrayList<ProjectResourceDetails>();
			/**
			 * project head details save
			 */
			EmployeeDetails head_profile = employeeDetailsService.getAllEmployeeDetailsByID(projectHeadId);
			ProjectResourceDetails headResourceDetails = new ProjectResourceDetails();
			headResourceDetails.setEmployeeDetails(head_profile);
			headResourceDetails.setRate_per_hour(projectHeadRph);
			headResourceDetails.setDesignation("project_head");
			resourceDetailsList.add(headResourceDetails);
			/**
			 * project manager details save
			 */
			for (int i = 0; i < projectManager.length(); i++) {
				JSONObject managerObject = new JSONObject(projectManager.get(i).toString());
				String managerId = managerObject.getString("emp_id");
				Long managerRph = managerObject.getLong("rph");
				EmployeeDetails manager_profile = employeeDetailsService.getAllEmployeeDetailsByID(managerId);
				ProjectResourceDetails managerResourceDetails = new ProjectResourceDetails();
				managerResourceDetails.setEmployeeDetails(manager_profile);
				managerResourceDetails.setRate_per_hour(managerRph);
				managerResourceDetails.setDesignation("project_manager");
				resourceDetailsList.add(managerResourceDetails);
			}
			/**
			 * resource details save
			 */
			for (int i = 0; i < resources.length(); i++) {
				JSONObject emp_idObject = new JSONObject(resources.get(i).toString());
				String emp_id = emp_idObject.getString("emp_id");
				Long rph = emp_idObject.getLong("rph");
				EmployeeDetails employee_profile = employeeDetailsService.getAllEmployeeDetailsByID(emp_id);
				ProjectResourceDetails resourceDetails = new ProjectResourceDetails();
				resourceDetails.setEmployeeDetails(employee_profile);
				resourceDetails.setRate_per_hour(rph);
				resourceDetails.setDesignation("team_members");
				resourceDetailsList.add(resourceDetails);
			}
			projectDetails.setResourceDetails(resourceDetailsList);
			final ProjectDetails details1 = projectDetailsService.updateProjectWithZone(projectDetails, zone);
			projectResourceService.updateProjectreferenceId(details1.getId());

			final List<ProjectResourceDetails> newresource = details1.getResourceDetails();
			List<String> resource_list = new ArrayList<String>();

			for (int r = 0; r < newresource.size(); r++) {
				if (oldresource_list.contains(newresource.get(r).getEmployeeDetails().getId())) {
					oldresource_list.remove(newresource.get(r).getEmployeeDetails().getId());
				} else {
					resource_list.add(newresource.get(r).getEmployeeDetails().getId());
				}
			}
			List<CustomJobsDetails> jobdetail = jobDetailsService.getActiveJobDetailsByProjectId(details1.getId());
			List<Long> jobs_list = new ArrayList<Long>();
			for (int j = 0; j < jobdetail.size(); j++) {
				jobs_list.add(jobdetail.get(j).getId());
			}

			if (resource_list.size() > 0) {
				jobAssigneeService.updateAssigneebyProjectupdate(jobs_list, resource_list);
			}

			if (oldresource_list.size() > 0) {
				jobAssigneeService.updateAssigneebyProjectupdate(jobs_list, oldresource_list);
			}

			projectDetailsService.deleteDuplicateProjectResources();

			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Project Updated Successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to Update Project");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("JobDetailsApiService(createJob) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in JobDetailsApiService(createJob) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to Update Project");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(updateProject) >> Exit");
		return response;
	}

	@PutMapping(value = "/updateProjectStatus/{id}", headers = "Accept=application/json")
	public String updateProjectStatus(@PathVariable(value = "id") Long id, @RequestBody String request) {
		logger.info("ProjectDetailsApiService(updateProjectStatus) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("ProjectDetailsApiService(updateProjectStatus) >> Request ");
			final ProjectDetails newDetails = MapperUtil.readAsObjectOf(ProjectDetails.class, request);
			final ProjectDetails details = projectDetailsService.getProjectById(id);

			if (newDetails.getProject_status().equals("Inactive")) {
				details.setProject_status(newDetails.getProject_status());
				details.setStatus_comment(newDetails.getStatus_comment());
//				details.setIs_deleted(true);
			} else {
				details.setProject_status(newDetails.getProject_status());
				details.setStatus_comment(newDetails.getStatus_comment());
			}

			final ProjectDetails allDetails = projectDetailsService.updateProjectDetails(details);
			if (allDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Project Status Updated Successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Project Status Update Failed");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ProjectDetailsApiService(updateProjectStatus) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in ProjectDetailsApiService(updateProjectStatus) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error during Project update");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(updateProjectStatus) >> Exit");
		return response;
	}

	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteProject(@PathVariable(value = "id") Long id) {
		logger.info("ProjectDetailsApiService(deleteProject) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final ProjectDetails oldDetails = projectDetailsService.getProjectById(id);
			Long OrgId = oldDetails.getOrgDetails().getOrg_id();
			oldDetails.setIs_deleted(true);
			List<JobDetails> JDetails = jobDetailsService.getActiveJobsDetailsByProjectId(id);
			for (JobDetails i : JDetails) {
//				if (i.getProject_id() == id) {
				i.setIs_deleted(true);
				jobDetailsService.deleteJobDetails(i);
//				} else
//					continue;
			}
			final ProjectDetails details = projectDetailsService.deleteProjectDetails(oldDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Project details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting project details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in ProjectDetailsApiService(deleteProject) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting project details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(deleteProject) >> Exit");
		return response;
	}

	// Bulk delete //

	@PutMapping(value = "/bulkDelete", headers = "Accept=application/json")
	public String bulkDelete(@RequestBody final String details) {
		logger.info("ProjectDetailsApiService(bulkDelete) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			for (int i = 0; i < deleteIds.length(); i++) {
				Long id = deleteIds.getLong(i);
				final ProjectDetails oldDetails = projectDetailsService.getProjectById(id);
				oldDetails.setIs_deleted(true);
				Long OrgId = oldDetails.getOrgDetails().getOrg_id();
				List<JobDetails> JDetails = jobDetailsService.getActiveJobDetailsByOrgId(OrgId);
				for (JobDetails a : JDetails) {
					if (a.getProject_id() == id) {
						a.setIs_deleted(true);
						JobDetails jobDetails = jobDetailsService.deleteJobDetails(a);
					} else
						continue;
				}
				final ProjectDetails newDetails = projectDetailsService.deleteProjectDetails(oldDetails);
				if (newDetails != null) {
					check += 1;
				}
			}

			if (check == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Project bulk deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting project details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in ProjectDetailsApiService(bulkDelete) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting project details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(bulkDelete) >> Exit");
		return response;
	}

	// bulk user add to multiple projects
	@PutMapping(value = "/bulkUsers", headers = "Accept=application/json")
	public String bulkProjectsUsers(@RequestBody final String details) {
		logger.info("ProjectDetailsApiService(bulkProjectsUsers) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			Integer checkExist = 0;
			Integer checkNew = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray projectIds = newJsonObject.getJSONArray("projectIds");
			JSONArray resources = newJsonObject.getJSONArray("resource_details");
			newJsonObject.remove("resource_details");
			String projects_url = newJsonObject.getString("projects_url");
			newJsonObject.remove("projects_url");
			List<String> newresource_list = new ArrayList<String>();

//			This code will work on while we select more then one project to add users

			if (projectIds.length() > 1) {
				for (int i = 0; i < projectIds.length(); i++) {
					Long id = projectIds.getLong(i);
					final ProjectDetails oldDetails = projectDetailsService.getProjectById(id);
					EmailSender emailSender = new EmailSender();
					MailConfigDetails config = mailConfigDetailsService
							.getMailConfigByOrgId(oldDetails.getOrgDetails().getOrg_id());
					ManageIntegration access = manageIntegrationService
							.getOrgAMdetails(oldDetails.getOrgDetails().getOrg_id(), "mail", "projects/jobs");

					List<ProjectResourceDetails> RDetails = oldDetails.getResourceDetails();
					List<String> oldresource_list = new ArrayList<String>();
					for (int k = 0; k < RDetails.size(); k++) {
						oldresource_list.add(RDetails.get(k).getEmployeeDetails().getId());
					}
					List<ProjectResourceDetails> tempResourceDetails = oldDetails.getResourceDetails();
					List<String> memberResourceDetails = new ArrayList<>();
					for (int k = 0; k < resources.length(); k++) {
						checkExist = 0;
						checkNew = 0;
						JSONObject emp_idObject = new JSONObject(resources.get(k).toString());
						String emp_id = emp_idObject.getString("emp_id");
						Long rph = emp_idObject.getLong("rph");
						for (int a = 0; a < tempResourceDetails.size(); a++) {
							if (tempResourceDetails.get(a).getDesignation().equals("team_members")) {
								memberResourceDetails.add(tempResourceDetails.get(a).getEmployeeDetails().getId());
							}
						}
						boolean oldEmp = memberResourceDetails.contains(emp_id);
						if (!oldEmp) {
							EmployeeDetails employee_profile = employeeDetailsService.getAllEmployeeDetailsByID(emp_id);
							ProjectResourceDetails resourceDetails = new ProjectResourceDetails();
							resourceDetails.setEmployeeDetails(employee_profile);
							resourceDetails.setRate_per_hour(rph);
							resourceDetails.setDesignation("team_members");
							RDetails.add(resourceDetails);

							/// send mail to new user
							String s_date = " ";
							String e_date = " ";
							String cost = " ";
							if (oldDetails.getStart_date() == null) {
								s_date = "-";
							} else {
								Date date = oldDetails.getStart_date();
								SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
								String strDate = formatter.format(date);
								s_date = strDate;
							}
							if (oldDetails.getEnd_date() == null) {
								e_date = "-";
							} else {
								Date date = oldDetails.getEnd_date();
								SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
								String strDate = formatter.format(date);
								e_date = strDate;
							}
							if (oldDetails.getProject_cost() == null) {
								cost = "-";
							} else {
								cost = oldDetails.getProject_cost().toString();
							}
							newresource_list.add(employee_profile.getId());
							String template = EmailTemplateMapperUtil.getAddUsersToProjectMailTemplate(employee_profile,
									s_date, e_date, oldDetails.getProject_name(), projects_url);
							String subject = "T-CUBE | " + " You Were Assigned To This Project";
							if (config != null && config.getisActive() == true) {
								if (access != null) {
									if (access.getisActive() == true) {
										emailSender.sendEmail(config, employee_profile.getEmail(), subject, template,
												true);
//										emailService.sendEmail(employee_profile.getEmail(), subject, template, true);
										logger.info("ProjectDetailsApiService(bulkProjectsUsers) >> Mail sended to :"
												+ employee_profile.getEmail());
									}
								}
							}
						}
					}

					oldDetails.setResourceDetails(RDetails);
					final ProjectDetails PDetails = projectDetailsService.updateProject(oldDetails);
					projectResourceService.updateProjectreferenceId(PDetails.getId());
					List<CustomJobsDetails> jobdetail = jobDetailsService
							.getActiveJobDetailsByProjectId(PDetails.getId());
					List<Long> jobs_list = new ArrayList<Long>();
					for (int j = 0; j < jobdetail.size(); j++) {
						jobs_list.add(jobdetail.get(j).getId());
					}
					jobAssigneeService.updateAssigneebyProjectupdate(jobs_list, newresource_list);
					if (PDetails != null) {
						check += 1;
					}
				}
			}
//			This code will work on while we select only one project to add users
			else {

//				To get project old details

				final ProjectDetails projectDetails1 = projectDetailsService.getProjectById(projectIds.getLong(0));
				List<ProjectResourceDetails> Rdetail_old = projectDetails1.getResourceDetails();
				List<ProjectResourceDetails> resourceDetailsList = new ArrayList<ProjectResourceDetails>();
				List<String> memberResourceDetails = new ArrayList<>();

//				mail configuration details

				EmailSender emailSender = new EmailSender();
				MailConfigDetails config = mailConfigDetailsService
						.getMailConfigByOrgId(projectDetails1.getOrgDetails().getOrg_id());
				ManageIntegration access = manageIntegrationService
						.getOrgAMdetails(projectDetails1.getOrgDetails().getOrg_id(), "mail", "projects/jobs");

				for (int i = 0; i < resources.length(); i++) {
					JSONObject Emp_idObject = new JSONObject(resources.get(i).toString());
					String Emp_id = Emp_idObject.getString("emp_id");
					Long Rph = Emp_idObject.getLong("rph");
					EmployeeDetails Employee_profile = employeeDetailsService.getAllEmployeeDetailsByID(Emp_id);
					ProjectResourceDetails ResourceDetails = new ProjectResourceDetails();
					ResourceDetails.setEmployeeDetails(Employee_profile);
					ResourceDetails.setRate_per_hour(Rph);
					ResourceDetails.setDesignation("team_members");
					resourceDetailsList.add(ResourceDetails);
					long[] ProjectId = new long[projectIds.length()];
//					The below code to check we assign new user to the proejct or not
					for (int a = 0; a < Rdetail_old.size(); a++) {
						if (Rdetail_old.get(a).getDesignation().equals("team_members")) {
							memberResourceDetails.add(Rdetail_old.get(a).getEmployeeDetails().getId());
						}
					}
					boolean oldEmp = memberResourceDetails.contains(Emp_id);
//					mail sending fuctions for select new user to the project
					if (!oldEmp) {
						EmployeeDetails employee_profile = employeeDetailsService.getAllEmployeeDetailsByID(Emp_id);
						ProjectResourceDetails resourceDetails = new ProjectResourceDetails();
						resourceDetails.setEmployeeDetails(employee_profile);
						resourceDetails.setRate_per_hour(Rph);
						resourceDetails.setDesignation("team_members");
						Rdetail_old.add(resourceDetails);

						/// send mail to new user
						String s_date = " ";
						String e_date = " ";
						String cost = " ";
						if (projectDetails1.getStart_date() == null) {
							s_date = "-";
						} else {
							Date date = projectDetails1.getStart_date();
							SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
							String strDate = formatter.format(date);
							s_date = strDate;
						}
						if (projectDetails1.getEnd_date() == null) {
							e_date = "-";
						} else {
							Date date = projectDetails1.getEnd_date();
							SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
							String strDate = formatter.format(date);
							e_date = strDate;
						}
						if (projectDetails1.getProject_cost() == null) {
							cost = "-";
						} else {
							cost = projectDetails1.getProject_cost().toString();
						}
						newresource_list.add(employee_profile.getId());
						String template = EmailTemplateMapperUtil.getAddUsersToProjectMailTemplate(employee_profile,
								s_date, e_date, projectDetails1.getProject_name(), projects_url);
						String subject = "T-CUBE | " + " You Were Assigned To This Project";
						if (config != null && config.getisActive() == true) {
							if (access != null) {
								if (access.getisActive() == true) {
									emailSender.sendEmail(config, employee_profile.getEmail(), subject, template, true);
//									emailService.sendEmail(employee_profile.getEmail(), subject, template, true);
									logger.info("ProjectDetailsApiService(bulkProjectsUsers) >> Mail sended to :"
											+ employee_profile.getEmail());
								}
							}
						}
					}
				}

//				TO add project head and project managers to new list which is going to be updated in resource table

				final ProjectDetails projectDetails = projectDetailsService.getProjectById(projectIds.getLong(0));
				List<ProjectResourceDetails> Rdetail = projectDetails.getResourceDetails();
				for (int x = 0; x < Rdetail.size(); x++) {
					if (Rdetail.get(x).getDesignation().equals("project_manager")
							|| Rdetail.get(x).getDesignation().equals("project_head")) {
						resourceDetailsList.add(Rdetail.get(x));
					}
				}
				projectDetails.setResourceDetails(resourceDetailsList);
				final ProjectDetails Details = projectDetailsService.updateProject(projectDetails);
				projectResourceService.updateProjectreferenceId(Details.getId());
				projectDetailsService.deleteDuplicateProjectResources();

//				for to set inactive and active job assgnees in job details under this project

				List<String> oldresource_list = new ArrayList<String>();
				for (int i = 0; i < Rdetail_old.size(); i++) {
					oldresource_list.add(Rdetail_old.get(i).getEmployeeDetails().getId());
				}
				final List<ProjectResourceDetails> newresource = Details.getResourceDetails();
				List<String> resource_list = new ArrayList<String>();

				for (int r = 0; r < newresource.size(); r++) {
					if (oldresource_list.contains(newresource.get(r).getEmployeeDetails().getId())) {
						oldresource_list.remove(newresource.get(r).getEmployeeDetails().getId());
					} else {
						resource_list.add(newresource.get(r).getEmployeeDetails().getId());
					}
				}
				List<CustomJobsDetails> jobdetail = jobDetailsService.getActiveJobDetailsByProjectId(Details.getId());
				List<Long> jobs_list = new ArrayList<Long>();
				for (int j = 0; j < jobdetail.size(); j++) {
					jobs_list.add(jobdetail.get(j).getId());
				}

				if (resource_list.size() > 0) {
					jobAssigneeService.updateAssigneebyProjectupdate(jobs_list, resource_list);
				}

				if (oldresource_list.size() > 0) {
					jobAssigneeService.updateAssigneebyProjectupdate(jobs_list, oldresource_list);
				}

			}

			if (projectIds.length() > 1) {
				if (check == projectIds.length()) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "Users added to projects successfully");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Error in add users to project");
				}
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Users added to projects successfully");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ProjectDetailsApiService(bulkProjectsUsers) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in add users to project due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed add users to project");
			}
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(bulkProjectsUsers) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllProjectDetails", headers = "Accept=application/json")
	public String getAllProjectDetails() {
		logger.info("ProjectDetailsApiService(getAllProjectDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("ProjectDetailsApiService(getAllProjectDetails) >> Request");
			List<ProjectDetails> details = projectDetailsService.getAllProjectDetails();

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			logger.debug("ProjectDetailsApiService(getAllProjectDetails) >> Request");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);

			logger.error("Exception occured in ProjectDetailsApiService(getAllProjectDetails) and Exception details >> "
					+ e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting project details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getAllProjectDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllProjectDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getAllProjectDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ProjectDetailsApiService(getAllProjectDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ProjectDetailsApiService(getAllProjectDetailsByOrgId)");
			List<ProjectDetails> details = projectDetailsService.getProjectDetailsByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get job details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ProjectDetailsApiService(getAllProjectDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting job details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getAllProjectDetailsByOrgId) >> Exit");
		return response;
	}

	@GetMapping(value = "/getActiveProjectDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getActiveProjectDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ProjectDetailsApiService(getActiveProjectDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ProjectDetailsApiService(getActiveProjectDetailsByOrgId)");
			final List<ProjectDetails> details = projectDetailsService.getProjectDetailsByOrgId(id);
			final List<ProjectDetails> newDetails = new ArrayList<>();
			List<ProjectResourceDetails> RDetails = new ArrayList();
			for (ProjectDetails i : details) {
				if (i.getIs_deleted() == false && i.getIs_activated() == true)
//					RDetails = i.getResourceDetails();
//					String check ="";
//					for(ProjectResourceDetails j : RDetails) {
//						if(j.getEmployeeDetails().getProfile_image() != null) {
//							if(check.contains(j.getEmployeeDetails().getId().toString())) {
//								j.getEmployeeDetails().setProfile_image(ImageProcessor.decompressBytes(j.getEmployeeDetails().getProfile_image()));
//								newDetails.add(i);
//							}
//							else check += j.getEmployeeDetails().getId();
//						}
//					}
					newDetails.add(i);

			}
			if (newDetails != null && newDetails.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get job details by orgorg Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ProjectDetailsApiService(getActiveProjectDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  job details by Client Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getActiveProjectDetailsByOrgId) >> Exit");
		return response;
	}

//	to get inactive project details by orgid
@GetMapping(value = "/getInactiveProjectDetailsByOrgId/{id}", headers = "Accept=application/json")
public String getInactiveProjectDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
	logger.info("ProjectDetailsApiService(getInactiveProjectDetailsByOrgId) >> Entry");
	String response = "";
	final JSONObject jsonObject = new JSONObject();
	try {
		logger.info("ProjectDetailsApiService(getInactiveProjectDetailsByOrgId)");
		final List<ProjectDetails> details = projectDetailsService.getProjectDetailsByOrgId(id);
		final List<ProjectDetails> newDetails = new ArrayList<>();
		List<ProjectResourceDetails> RDetails = new ArrayList();
		for (ProjectDetails i : details) {
			if (i.getIs_activated() == false && i.getIs_deleted() == false)
				newDetails.add(i);

		}
		if (newDetails != null && newDetails.size() > 0) {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails));
		} else {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
			jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails));
		}
		response = new Gson().toJson(jsonObject);
	} catch (Exception e) {
		Sentry.captureException(e);
//			System.out.println(e);
		logger.error(
				"Exception occured in ProjectDetailsApiService(getInactiveProjectDetailsByOrgId) and Exception details >> "
						+ e);
		jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
		jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
		jsonObject.put(RestConstants.DATA, "Error in extracting  job details by Client Id");
		response = new Gson().toJson(jsonObject);
	}
	logger.info("ProjectDetailsApiService(getInactiveProjectDetailsByOrgId) >> Exit");
	return response;
}

	@GetMapping(value = "/getProjectById/{id}", headers = "Accept=application/json")
	public String getProjectById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ProjectDetailsApiService(getProjectById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ProjectDetailsApiService(getProjectById)");
			final ProjectDetails details = projectDetailsService.getProjectById(id);
			final List<ProjectDetails> newDetails = new ArrayList<>();
			List<ProjectResourceDetails> RDetails = new ArrayList();
			List<ProjectResourceDetails> newRDetails = new ArrayList();
			if (details != null) {
				if (details.getIs_deleted() == false) {
					newDetails.add(details);
				}
				for (ProjectDetails i : newDetails) {
					RDetails = i.getResourceDetails();
					String check = "";
					for (ProjectResourceDetails j : RDetails) {
						if (j.getEmployeeDetails().getProfile_image() != null) {
							if (check.contains(j.getEmployeeDetails().getId().toString())) {
								newRDetails.add(j);
								continue;
							} else {
								j.getEmployeeDetails().setProfile_image(
										ImageProcessor.decompressBytes(j.getEmployeeDetails().getProfile_image()));
								check += j.getEmployeeDetails().getId();
							}
						}
						newRDetails.add(j);
					}
				}
				newDetails.get(0).setResourceDetails(newRDetails);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails.get(0)));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting project details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			Sentry.captureException(e);
			logger.error("Exception occured in ProjectDetailsApiService(getProjectById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  project details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getProjectById) >> Exit");
		return response;
	}

	@GetMapping(value = "/getActiveProjectDetailsByClientId/{id}", headers = "Accept=application/json")
	public String getActiveProjectDetailsByClientId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ProjectDetailsApiService(getActiveProjectDetailsByClientId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ProjectDetailsApiService(getActiveProjectDetailsByClientId)");
			final List<ProjectDetails> details = projectDetailsService.getActiveProjectDetailsByClientId(id);
			if (details != null && details.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get project details by client Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ProjectDetailsApiService(getActiveProjectDetailsByClientId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  project details by client id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getActiveProjectDetailsByClientId) >> Exit");
		return response;
	}

	@GetMapping(value = "/getInactiveProjectDetailsByClientId/{id}", headers = "Accept=application/json")
	public String getInactiveProjectDetailsByClientId(@PathVariable final Long id,
													  final UriComponentsBuilder ucBuilder) {
		logger.info("ProjectDetailsApiService(getInactiveProjectDetailsByClientId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ProjectDetailsApiService(getInactiveProjectDetailsByClientId)");
			final List<ProjectDetails> details = projectDetailsService.getInactiveProjectDetailsByClientId(id);
			if (details != null && details.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get project details by client Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ProjectDetailsApiService(getInactiveProjectDetailsByClientId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  job details by client id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getInactiveProjectDetailsByClientId) >> Exit");
		return response;
	}

	// Only returns the active projects name list --> [project1 , project2]
	@GetMapping(value = "/getActiveProjecttNameListByOrgId/{id}", headers = "Accept=application/json")
	public String getActiveProjecttNameListByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ProjectDetailsApiService(getActiveProjectDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ProjectDetailsApiService(getActiveProjectDetailsByOrgId)");
			final List<ProjectDetails> details = projectDetailsService.getActiveProjecttNameListByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get project details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in ProjectDetailsApiService(getActiveProjectDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  project details by Client Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getActiveProjectDetailsByOrgId) >> Exit");
		return response;
	}

//	Remove user from project
@PutMapping(value = "/projectUserRemove", headers = "Accept=application/json")
public String projectUserRemove(@RequestBody final String details) {
	logger.info("ProjectDetailsApiService(projectUserRemove) >> Entry");
	String response = "";
	final JSONObject jsonObject = new JSONObject();
	try {
		Integer check = 0;
		final JSONObject newJsonObject = new JSONObject(details);
		long projectId = newJsonObject.getLong("projectId");
		String empId = newJsonObject.getString("empId");
		final ProjectDetails oldDetails = projectDetailsService.getProjectById(projectId);
		List<ProjectResourceDetails> ResourceDetails = oldDetails.getResourceDetails();
		List<String> oldresource_list = new ArrayList<String>();
		for (int k = 0; k < ResourceDetails.size(); k++) {
			oldresource_list.add(ResourceDetails.get(k).getEmployeeDetails().getId());
		}
		final boolean Details = projectDetailsService.removeProjectUser(projectId, empId);
		List<CustomJobsDetails> jobdetail = jobDetailsService.getActiveJobDetailsByProjectId(projectId);
		List<Long> jobs_list = new ArrayList<Long>();
		for (int j = 0; j < jobdetail.size(); j++) {
			jobs_list.add(jobdetail.get(j).getId());
		}
		if (jobs_list.size() > 0) {
			for (int i = 0; i < jobs_list.size(); i++) {
				jobAssigneeService.jobsAssigneeDisableByProjectId(jobs_list.get(i), projectId, empId);
			}

		}

		if (Details != false) {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA, "Project user removed successfully");
		} else {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in removing project user");
		}
		response = new Gson().toJson(jsonObject);
	} catch (Exception e) {
		Sentry.captureException(e);
		e.printStackTrace();
		logger.error(
				"Exception occured in ProjectDetailsApiService(projectUserRemove) and Exception details >> " + e);
		jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
		jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
		jsonObject.put(RestConstants.DATA, "Error in deleting project details");
		response = new Gson().toJson(jsonObject);
	}
	logger.info("ProjectDetailsApiService(projectUserRemove) >> Exit");
	return response;
}

//	/  To get projectname,id,client_id and status based on org_id
@GetMapping(value = "/getActiveProjectDetailsByOrgIdForFilter/{id}", headers = "Accept=application/json")
public String getProjectActiveDetailsByOrgIdForFilter(@PathVariable final Long id,
													  final UriComponentsBuilder ucBuilder) {
	logger.info("ProjectDetailsApiService(getActiveProjectDetailsByOrgIdForFilter) >> Entry");
	String response = "";
	final JSONObject jsonObject = new JSONObject();
	try {
		logger.info("ProjectDetailsApiService(getActiveProjectDetailsByOrgIdForFilter)");
		final List<ProjectDetails> details = projectDetailsService.getActiveProjectDetailsByOrgIdForFilter(id);
		if (details != null) {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
		} else {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to get job details by orgorg Id");
		}
		response = new Gson().toJson(jsonObject);
	} catch (Exception e) {
		Sentry.captureException(e);
//          System.out.println(e);
		logger.error(
				"Exception occured in ProjectDetailsApiService(getActiveProjectDetailsByOrgIdForFilter) and Exception details >> "
						+ e);
		jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
		jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
		jsonObject.put(RestConstants.DATA, "Error in extracting  job details by Client Id");
		response = new Gson().toJson(jsonObject);
	}
	logger.info("ProjectDetailsApiService(getActiveProjectDetailsByOrgIdForFilter) >> Exit");
	return response;
}

	// To display the user with status active and inactive in project
	@PostMapping(value = "/disableUserAfterDeactivate", headers = "Accept=application/json")
	public String getProjectByIdToViewAllUsers(@RequestBody final String details) {
		logger.info("ProjectDetailsApiService(disableUserAfterDeactivate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			long projectId = newJsonObject.getLong("projId");
			long id = newJsonObject.getLong("org_id");
			logger.info("ProjectDetailsApiService(disableUserAfterDeactivate) >> Request -> OrgId :" + id);
			List<CustomProjectDetails> details1 = projectDetailsService.disableUserAfterDeactivate(id, projectId);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details1));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to extract the project details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in ProjectDetailsApiService(disableUserAfterDeactivate) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  project details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(disableUserAfterDeactivate) >> Exit");
		return response;
	}

//       @PostMapping(value = "/disableBulkUserAfterBulkDeactivate", headers = "Accept=application/json")
//      	public String disableBulkUserAfterBulkDeactivate(@RequestBody final String request) {
//      		logger.info("ProjectDetailsApiService(getProjectByIdToViewAllUsers) >> Entry");
//      		String response = "";
//      		final JSONObject jsonObject = new JSONObject();
//      		try {
//      			final JSONObject newJsonObject = new JSONObject(request);
//   			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
//      			long id = newJsonObject.getLong("org_id");
//      			logger.info("ProjectDetailsApiService(getProjectByIdToViewAllUsers) >> Request -> OrgId :" + id);
//      			 List<CustomProjectDetails> details1 = projectDetailsService.disableBulkUserAfterBulkDeactivate(id,deleteIds);
//      			if (details1 != null) {
//      			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//      			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//      			jsonObject.put(RestConstants.DATA, new Gson().toJson(details1));
//      		} else {
//      			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//      			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//      			jsonObject.put(RestConstants.DATA, "Failed to extract the project details by id");
//      		}
//      		response = new Gson().toJson(jsonObject);
//      	} catch (Exception e) {
//      		logger.error("Exception occured in ProjectDetailsApiService(disableBulkUserAfterBulkDeactivate) and Exception details >> " + e);
//      		jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
//      		jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//      		jsonObject.put(RestConstants.DATA, "Error in extracting  project details by id");
//      		response = new Gson().toJson(jsonObject);
//      	}
//      	logger.info("ProjectDetailsApiService(disableBulkUserAfterBulkDeactivate) >> Exit");
//      	return response;
//      	}

	// To display the resource details in update project page with reference id
	@GetMapping(value = "/getProjectByReferenceId/{id}", headers = "Accept=application/json")
	public String getProjectByReferenceId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ProjectDetailsApiService(getProjectByReferenceId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ProjectDetailsApiService(getProjectByReferenceId)");
			List<CustomDetailsForProjectWithReferenceId> details = projectDetailsService.getProjectByReferenceId(id);
			final List<ProjectDetails> newDetails = new ArrayList<>();
			List<ProjectResourceDetails> RDetails = new ArrayList();
			List<ProjectResourceDetails> newRDetails = new ArrayList();
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to extract project details by reference id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//   			System.out.println(e);
			logger.error(
					"Exception occured in ProjectDetailsApiService(getProjectByReferenceId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  project details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getProjectByReferenceId) >> Exit");
		return response;
	}

	// To display the project details after reactivate the user with reference
	// project id in table and in update and in view project
	@GetMapping(value = "/getProjectDetailsByOrgIdWithRefProjectId/{id}", headers = "Accept=application/json")
	public String getProjectDetailsByOrgIdWithRefProjectId(@PathVariable final Long id,
														   final UriComponentsBuilder ucBuilder) {
		logger.info("ProjectDetailsApiService(getProjectDetailsByOrgIdWithRefProjectId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ProjectDetailsApiService(getProjectDetailsByOrgIdWithRefProjectId)");
			List<CustomDetailsForProjectWithReferenceId> details = projectDetailsService
					.getProjectDetailsByOrgIdWithRefProjectId(id);
//   			final List<CustomDetailsForProjectWithReferenceId> newDetails = new ArrayList<>();
//   			for (CustomDetailsForProjectWithReferenceId i : details) {
//   				if (i.getIs_deleted() == false && i.getIs_activated() ==true) {
//   					newDetails.add(i);
//   			}
//   		}
			if (details != null && details.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA,
						"Failed to get project details by orgorg Id along with reference id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//   			System.out.println(e);
			logger.error(
					"Exception occured in ProjectDetailsApiService(getProjectDetailsByOrgIdWithRefProjectId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  project details by org id with reference id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getProjectDetailsByOrgIdWithRefProjectId) >> Exit");
		return response;
	}

	// To display project name in dropdown in add job
	@GetMapping(value = "/getProjectNameAndId/{id}", headers = "Accept=application/json")
	public String getProjectNameAndId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ProjectDetailsApiService(getProjectNameAndId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ProjectDetailsApiService(getProjectNameAndId)");
			List<CustomProjectName> details = projectDetailsService.getProjectNameAndId(id);
			if (details != null && details.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get project name with project id by org id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in ProjectDetailsApiService(getProjectNameAndId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  project name with project id by org id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getProjectNameAndId) >> Exit");
		return response;
	}

	// To get the project status used custom(CustomProjectName) file in project
	// table
	@GetMapping(value = "/getProjectStatusById/{id}", headers = "Accept=application/json")
	public String getProjectStatusById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ProjectDetailsApiService(getProjectStatusById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ProjectDetailsApiService(getProjectStatusById)");
			CustomProjectName details = projectDetailsService.getProjectStatusById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get project status with project id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in ProjectDetailsApiService(getProjectStatusById) and Exception details >> "
					+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  project status with project id ");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getProjectStatusById) >> Exit");
		return response;
	}

	/**
	 * To get all project details by orgid and status required to show in the the
	 * table, removed the unwanted response string to avoid the loading time
	 * response is showing in the project module in all respective tables based on
	 * status
	 *
	 * @param request
	 * @return in CustomDetailsForProjectWithReferenceId
	 */
	@PutMapping(value = "/getallprojectsbyorgidstatuscustomdata", headers = "Accept=application/json")
	public String getAllProjectsByOrgIdStatusCustomData(@RequestBody final String request) {
		logger.info("ProjectDetailsApiService(getAllProjectsByOrgIdStatusCustomData) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			Long org_Id = newJsonObject.getLong("org_Id");
			newJsonObject.remove("org_Id");

			String status = newJsonObject.getString("status");
			newJsonObject.remove("status");

			boolean is_Activated = newJsonObject.getBoolean("is_Activated");
			newJsonObject.remove("is_Activated");
			logger.info("ProjectDetailsApiService(getAllProjectsByOrgIdStatusCustomData)");
			List<CustomDetailsForProjectWithReferenceId> details = projectDetailsService
					.getAllProjectsByOrgIdStatusCustomData(org_Id, status, is_Activated);
			if (details != null && details.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "No data found in getall projects by orgid and status");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in ProjectDetailsApiService(getAllProjectsByOrgIdStatusCustomData) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  getall projects by orgid and status");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ProjectDetailsApiService(getAllProjectsByOrgIdStatusCustomData) >> Exit");
		return response;
	}
}
