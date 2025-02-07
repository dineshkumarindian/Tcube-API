/**
 * 
 */
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
import com.tcube.api.model.CustomJobsDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.JobAssigneeDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.MailConfigDetails;
import com.tcube.api.model.ManageIntegration;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.ProjectDetails;
import com.tcube.api.model.TimeTrackerDetails;
import com.tcube.api.service.EmailService;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.JobAssigneeService;
import com.tcube.api.service.JobDetailsService;
import com.tcube.api.service.MailConfigDetailsService;
import com.tcube.api.service.ManageIntegrationService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.ProjectDetailsService;
import com.tcube.api.service.TimeTrackerDetailsService;
import com.tcube.api.utils.EmailSender;
import com.tcube.api.utils.EmailTemplateMapperUtil;
import com.tcube.api.utils.MailSender;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

/**
 * @author User
 *
 */

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/JobDetails" })
public class JobDetailsApiService {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(JobDetailsApiService.class);

	@Autowired
	JobDetailsService jobDetailsService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@Autowired
	ProjectDetailsService projectDetailsService;

	@Autowired
	EmployeeDetailsService employeeDetailsService;
	
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


	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createjob(@RequestBody final String detailsOfJob, final UriComponentsBuilder ucBuilder) {
		logger.info("JobDetailsApiService(createjob) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfJob);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			long projectId = newJsonObject.getLong("project_id");
			newJsonObject.remove("project_id");
			JSONArray assigneesId = newJsonObject.getJSONArray("assignees");
			newJsonObject.remove("assignees");
			String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");
			String jobs_url = newJsonObject.getString("jobs_url");
			newJsonObject.remove("jobs_url");

			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			final ProjectDetails projectDetails = projectDetailsService.getProjectById(projectId);
			final JobDetails jobDetails = MapperUtil.readAsObjectOf(JobDetails.class, newJsonObject.toString());
			jobDetails.setOrgDetails(orgDetails);
			jobDetails.setJob_status("Inprogress");

//			jobDetails.setProjectDetails(projectDetails);
			jobDetails.setProject_id(projectId);
			jobDetails.setProject_name(projectDetails.getProject_name());
			//calculation for the job cost
			long cost = 0;
			if(jobDetails.getHours() != null && jobDetails.getRate_per_hour() != null) {
				cost = jobDetails.getHours() * jobDetails.getRate_per_hour();
			}
			jobDetails.setJob_cost(cost);
			/**
			 * assignee details save
			 */
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));
			String s_date = " " ;
			String e_date =" " ;
			String hours =" ";
			if( jobDetails.getStart_date() !=null) {
				Date date = jobDetails.getStart_date();  
				Date tempDate =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date));
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");  
				String strDate = dateFormat.format(tempDate);
				s_date = strDate;
				
			}else {
				s_date = "-" ;
			}
			if( jobDetails.getEnd_date() !=null) {
				Date date = jobDetails.getEnd_date();  
				Date tempdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date));
//			    Date strDate2 = details.getEnd_date(); 
			    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");  
				String strDate = dateFormat.format(tempdate);
				e_date = strDate;
			}else {
				e_date = "-" ;
			}
			if( jobDetails.getHours() != null) {
				hours = jobDetails.getHours().toString() ;
			}else {
				hours = "-" ;
			}
			List<JobAssigneeDetails> jobAssigneeDetailsList = new ArrayList<JobAssigneeDetails>();
			MailConfigDetails config = mailConfigDetailsService.getMailConfigByOrgId(orgId);
			ManageIntegration access = manageIntegrationService.getOrgAMdetails(orgId, "mail", "projects/jobs");
			EmailSender emailSender = new EmailSender();
			for (int i = 0; i < assigneesId.length(); i++) {
				JSONObject emp_idObject = new JSONObject(assigneesId.get(i).toString());
				String emp_id = emp_idObject.getString("emp_id");
				Long rph = emp_idObject.getLong("rph");
				EmployeeDetails employee_profile = employeeDetailsService.getAllEmployeeDetailsByID(emp_id);
				JobAssigneeDetails jobAssigneeDetails = new JobAssigneeDetails();
				jobAssigneeDetails.setEmployeeDetails(employee_profile);
				jobAssigneeDetails.setRate_per_hour(rph);
				JobAssigneeDetails tempdata = jobAssigneeService.getJobAssigneeByEmpJobId(emp_id, jobDetails.getId());
				jobAssigneeDetails.setLogged_hours(tempdata.getLogged_hours());
				jobAssigneeDetailsList.add(jobAssigneeDetails);
				
				if (config != null && config.getisActive()==true) {
					if (access != null) {
						if (access.getisActive() == true) {
							String template = EmailTemplateMapperUtil.getNewJobAssigneesMailTemplate(employee_profile, s_date , e_date ,jobDetails.getJob_name() ,hours,jobs_url );
							String subject = "T-CUBE | " + "You Were Assigned To This Job";
							emailSender.sendEmail(config, employee_profile.getEmail(), subject, template, true);
							logger.info(
									"JobDetailsApiService(createJob) >> Mail sended to :" + employee_profile.getEmail());
						}
					}
				}
//				emailService.sendEmail(employee_profile.getEmail(), subject,template, true);
//				logger.info("ProjectDetailsApiService(createJob) >> Mail sended to :" + employee_profile.getEmail());
			}
			jobDetails.setJobAssigneeDetails(jobAssigneeDetailsList);
			logger.info("JobDetailsApiService(createjob) >> Create request -> Job Name :" + jobDetails.getJob_name());

			// add total counts of jobs in project row
			final List<CustomJobsDetails> jobData = jobDetailsService.getActiveJobDetailsByProjectId(projectId);
			projectDetails.setTotal_jobs(jobData.size() + 1);
			projectDetailsService.updateProject(projectDetails);

			final JobDetails details = jobDetailsService.createJobDetails(jobDetails ,zone);
			jobAssigneeService.updatereferencId(details.getId());
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New Job Details Created Successfully");
				response = new Gson().toJson(jsonObject);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to add Job Details");
				response = new Gson().toJson(jsonObject);
			}
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
				jsonObject.put(RestConstants.DATA, "Error in add job details due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to add job details");
			}
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(createJob) >> Exit");
		return response;
	}

	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateJobDetail(@RequestBody final String detailsOfJob) {
		logger.info("JobDetailsApiService(updateJobDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfJob);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			long projectId = newJsonObject.getLong("project_id");
			newJsonObject.remove("project_id");
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			JSONArray assignees = newJsonObject.getJSONArray("assignees");
			newJsonObject.remove("assignees");
			String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");
			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			final ProjectDetails projectDetails = projectDetailsService.getProjectById(projectId);
			final JobDetails jobDetails = jobDetailsService.getJobById(Id);
			logger.debug("JobDetailsApiService(JobDetailsApiService) >> Request" );
			final JobDetails newDetails = MapperUtil.readAsObjectOf(JobDetails.class, newJsonObject.toString());
			
			//to update job details in the task details table
			String newname = newDetails.getJob_name();
			String oldname = jobDetails.getJob_name();
			String oldbill = jobDetails.getBill();
			String project_name = jobDetails.getProject_name();
			String newbill  = newDetails.getBill();
			if(!oldname.equals(newname) || !oldbill.equals(newbill)) {
				final TimeTrackerDetails details = timeTrackerDetailsService.updateJobDetails(oldname, newname, orgId, project_name,newbill);
			}
			
			//to update job cost
		    long oldcost = jobDetails.getJob_cost();
		    if(newDetails.getHours() != null && newDetails.getRate_per_hour()!= null) {
		    	long newcost = newDetails.getHours() * newDetails.getRate_per_hour();
		    	if(newcost != oldcost) {
			    	jobDetails.setJob_cost(newcost);
			    }
		    }
			jobDetails.setOrgDetails(orgDetails);
//			jobDetails.setProjectDetails(projectDetails);
			jobDetails.setProject_id(projectId);
			jobDetails.setProject_name(projectDetails.getProject_name());
			jobDetails.setJob_name(newDetails.getJob_name());
			jobDetails.setStart_date(newDetails.getStart_date());
			jobDetails.setEnd_date(newDetails.getEnd_date());
			jobDetails.setHours(newDetails.getHours());
			jobDetails.setRate_per_hour(newDetails.getRate_per_hour());
			jobDetails.setDescription(newDetails.getDescription());
			jobDetails.setBill(newDetails.getBill());
//			if(jobDetails.getHours() != newDetails.getHours()) {
				jobDetails.setMail_sended(false);
//			}
			List<JobAssigneeDetails> jobAssigneeDetailsList = new ArrayList<JobAssigneeDetails>();
				for (int i = 0; i < assignees.length(); i++) {
						JSONObject emp_idObject = new JSONObject(assignees.get(i).toString());
						String emp_id = emp_idObject.getString("emp_id");
						Long rph = emp_idObject.getLong("rph");
						EmployeeDetails employee_profile = employeeDetailsService.getAllEmployeeDetailsByID(emp_id);
						JobAssigneeDetails jobAssigneeDetails = new JobAssigneeDetails();
						
						Double hours = emp_idObject.getDouble("assignee_hours");
						Long updated_cost = (long) (rph * hours);
						jobAssigneeDetails.setEmployeeDetails(employee_profile);
						jobAssigneeDetails.setRate_per_hour(rph);
						jobAssigneeDetails.setAssignee_hours(hours);
						jobAssigneeDetails.setRef_jobid(Id);
						jobAssigneeDetails.setStatus("Active");
						JobAssigneeDetails tempdata = jobAssigneeService.getJobAssigneeByEmpJobId(emp_id, jobDetails.getId());
						jobAssigneeDetails.setLogged_hours(tempdata.getLogged_hours());
						jobAssigneeDetails.setAssignee_cost(updated_cost);
						jobAssigneeDetailsList.add(jobAssigneeDetails);
					
			}
			
			jobDetails.setJobAssigneeDetails(jobAssigneeDetailsList);
			final JobDetails details = jobDetailsService.updateJobDetailWithZone(jobDetails , zone);
			jobAssigneeService.updatereferencId(details.getId());
			jobAssigneeService.updateAssigneesStatusByRefid(details.getId());
			jobAssigneeService.deleteDuplicateAssigneeDetails(details.getId());
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Job Details Updated Successfully");
				response = new Gson().toJson(jsonObject);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to Update Job Details");
				response = new Gson().toJson(jsonObject);
			}
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in JobDetailsApiService(JobDetailsApiService) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to Update job details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(JobDetailsApiService) >> Exit");
		return response;
	}

	@PutMapping(value = "/updateJobStatus/{id}", headers = "Accept=application/json")
	public String updateJobStatus(@PathVariable(value = "id") Long id, @RequestBody String request) {
		logger.info("JobDetailsApiService(updateJobStatus) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("JobDetailsApiService(updateJobStatus) >> Request");
			final JobDetails newDetails = MapperUtil.readAsObjectOf(JobDetails.class, request);
			final JobDetails details = jobDetailsService.getJobById(id);

			if (newDetails.getJob_status().equals("Inactive")) {
				details.setJob_status(newDetails.getJob_status());
				details.setStatus_comment(newDetails.getStatus_comment());
//				details.setIs_deleted(true);
			} else {
				details.setJob_status(newDetails.getJob_status());
				details.setStatus_comment(newDetails.getStatus_comment());
			}

			final JobDetails allDetails = jobDetailsService.updateJobStatus(details);
			if (allDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Job Status Updated Successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Job Status Update Failed");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("JobDetailsApiService(updateJobStatus) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in JobDetailsApiService(updateJobStatus) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error during Job status update");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(updateJobStatus) >> Exit");
		return response;
	}

	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteJobDetail(@PathVariable(value = "id") Long id) {
		logger.info("JobDetailsApiService(deleteJobDetail) >> Entry");
		String response = "";
		boolean rejDetails = false;
		final JSONObject jsonObject = new JSONObject();
		try {
			final JobDetails oldDetails = jobDetailsService.getJobById(id);
//			oldDetails.setIs_deleted(true);
//			rejDetails = jobDetailsService.jobHardDeleteById(id);
			oldDetails.setIs_deleted(true);
			jobDetailsService.deleteJobDetails(oldDetails);
			// reduce the job counts in projects
			Long ProjectId = oldDetails.getProject_id();
			Long orgId = oldDetails.getOrgDetails().getOrg_id();
			String project = oldDetails.getProject_name();
			final ProjectDetails projectDetails1 = projectDetailsService.getProjectById(ProjectId);
			projectDetails1.setTotal_jobs(projectDetails1.getTotal_jobs() - 1);
			projectDetailsService.updateProject(projectDetails1);
			JSONObject project_durarion = timeTrackerDetailsService.gettotaltimebyproject(project,orgId);
	                 String project_duration = project_durarion.getString("duration_str");
	                 long project_duration_ms = project_durarion.getLong("duration_ms");
	                 ProjectDetails project_detail = projectDetailsService.setLoggedhours(project, orgId,
	                         project_duration);
//			final JobDetails details = jobDetailsService.deleteJobDetails(oldDetails);
			if (oldDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Job details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting job details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in JobDetailsApiService(deleteJobDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting job details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(deleteJobDetail) >> Exit");
		return response;
	}

// bulk hard Delete for jobs // 
	@PutMapping(value = "/bulkDelete", headers = "Accept=application/json")
	public String bulkDelete(@RequestBody final String details) {
		logger.info("JobDetailsApiService(bulkDelete) >> Entry");
		String response = "";
		boolean rejbulkDelete = false;
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			for (int i = 0; i < deleteIds.length(); i++) {
				Long id = deleteIds.getLong(i);
				final JobDetails oldDetails = jobDetailsService.getJobById(id);
				rejbulkDelete= jobDetailsService.JobsBulkHardDelete(id);

				// reduce the job counts in project details
				Long ProjectId = oldDetails.getProject_id();
				final ProjectDetails projectDetails1 = projectDetailsService.getProjectById(ProjectId);
				projectDetails1.setTotal_jobs(projectDetails1.getTotal_jobs() - 1);
				projectDetailsService.updateProject(projectDetails1);
			}

			if (rejbulkDelete != false) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Jobs bulk deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting job details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in JobDetailsApiService(bulkDelete) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting job details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(bulkDelete) >> Exit");
		return response;
	}

	// bulk assignees add
	@PutMapping(value = "/bulkAssignee", headers = "Accept=application/json")
	public String bulkJobsAssignees(@RequestBody final String details) {
		logger.info("JobDetailsApiService(bulkJobsAssignees) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			Integer checkExist = 0;
			Integer checkNew = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray jobIds = newJsonObject.getJSONArray("jobIds");
			JSONArray assigneesId = newJsonObject.getJSONArray("assignees");
			String jobs_url = newJsonObject.getString("jobs_url");
			newJsonObject.remove("jobs_url");
			newJsonObject.remove("assignees");
			for (int i = 0; i < jobIds.length(); i++) {
				Long id = jobIds.getLong(i);
				final JobDetails oldDetails = jobDetailsService.getJobById(id);
				List<JobAssigneeDetails> newAssignees = oldDetails.getJobAssigneeDetails();
				List<JobAssigneeDetails> assignees = oldDetails.getJobAssigneeDetails();
				EmailSender emailSender = new EmailSender();
				MailConfigDetails config = mailConfigDetailsService.getMailConfigByOrgId(oldDetails.getOrgDetails().getOrg_id());
				ManageIntegration access = manageIntegrationService.getOrgAMdetails(oldDetails.getOrgDetails().getOrg_id(), "mail", "projects/jobs");
				String s_date = " " ;
				String e_date =" " ;
				String hours =" ";
				if( oldDetails.getStart_date() !=null) {
					Date date = oldDetails.getStart_date();  
				    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");  
				    String strDate = formatter.format(date); 
					s_date = strDate ;
					
				}else {
					s_date = "-" ;
				}
				if( oldDetails.getEnd_date() !=null) {
					Date date = oldDetails.getEnd_date();  
				    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");  
				    String strDate = formatter.format(date); 
					e_date = strDate;
				}else {
					e_date = "-" ;
				}
				if( oldDetails.getHours() != null) {
					hours = oldDetails.getHours().toString() ;
				}else {
					hours = "-" ;
				}
				List<String> assignee_list_active = new ArrayList<String>();
				List<String> assignee_list_inactive = new ArrayList<String>();
				for(int l=0;l<assignees.size();l++) {
					if(assignees.get(l).getStatus() != null) {
						if(assignees.get(l).getStatus().equals("Active")) {
							assignee_list_active.add(assignees.get(l).getEmployeeDetails().getId());
						}
						else {
							assignee_list_inactive.add(assignees.get(l).getEmployeeDetails().getId());
						}
					}
				}
				
				for (int k = 0; k < assigneesId.length(); k++) {
					JSONObject emp_idObject = new JSONObject(assigneesId.get(k).toString());
                    String emp_id = emp_idObject.getString("emp_id");
                    Long rph = emp_idObject.getLong("rph");
                    if(!(assignee_list_active.contains(emp_id)) && !(assignee_list_inactive.contains(emp_id))) {
                    	EmployeeDetails employee_profile = employeeDetailsService.getAllEmployeeDetailsByID(emp_id);
     					JobAssigneeDetails jobAssigneeDetails = new JobAssigneeDetails();
						jobAssigneeDetails.setEmployeeDetails(employee_profile);
						jobAssigneeDetails.setRate_per_hour(rph);
						newAssignees.add(jobAssigneeDetails);
						// send mail to assignee
						if (config != null && config.getisActive()==true) {
							if (access != null) {
								if (access.getisActive() == true) {
									String template = EmailTemplateMapperUtil.getNewJobAssigneesMailTemplate(employee_profile, s_date , e_date ,oldDetails.getJob_name() ,hours,jobs_url );
									String subject = "T-CUBE | " + "You Were Assigned To This Job";
									emailSender.sendEmail(config, employee_profile.getEmail(), subject, template, true);
//									emailService.sendEmail(head_profile.getEmail(), subject,template, true);
									logger.info(
											"JobDetailsApiService(createJob) >> Mail sended to :" + employee_profile.getEmail());
								}
							}
						}
//						String template = EmailTemplateMapperUtil.getNewJobAssigneesMailTemplate(employee_profile, s_date , e_date ,oldDetails.getJob_name() ,hours,jobs_url );
//						String subject = "T-CUBE | " + "You Were Assigned To This Job";
//						emailService.sendEmail(employee_profile.getEmail(), subject,template, true);
//						logger.info("ProjectDetailsApiService(createJob) >> Mail sended to :" + employee_profile.getEmail());
                    }
                    else if(assignee_list_inactive.contains(emp_id)) {
                    	jobAssigneeService.updatebulkassigneeStatusActive(id, emp_id);
                    }
                    else {
                    	assignee_list_active.remove(emp_id);
                    }
                    
				}
//				for (int k = 0; k < assigneesId.length(); k++) {
//					checkExist = 0;
//					checkNew = 0;
//					JSONObject emp_idObject = new JSONObject(assigneesId.get(k).toString());
//					String emp_id = emp_idObject.getString("emp_id");
//					Long rph = emp_idObject.getLong("rph");
//					for (JobAssigneeDetails j : assignees) {
//						if (j.getEmployeeDetails().getId().equals(emp_id)) {
//							checkNew += 1;
//						} else {
//							checkExist += 1;
//						}
//					}
//					if (checkExist == assignees.size()) {
//						EmployeeDetails employee_profile = employeeDetailsService.getAllEmployeeDetailsByID(emp_id);
//						JobAssigneeDetails jobAssigneeDetails = new JobAssigneeDetails();
//						jobAssigneeDetails.setEmployeeDetails(employee_profile);
//						jobAssigneeDetails.setRate_per_hour(rph);
//						newAssignees.add(jobAssigneeDetails);
//						
//						// send mail to assignee
//						String template = EmailTemplateMapperUtil.getNewJobAssigneesMailTemplate(employee_profile, s_date , e_date ,oldDetails.getJob_name() ,hours );
//						String subject = "T-CUBE | " + "You Were Assigned To This Job";
//						MailSender.sendEmail(employee_profile.getEmail(), template, subject, "");
//						logger.info("ProjectDetailsApiService(createJob) >> Mail sended to :" + employee_profile.getEmail());
//					}
//
//				}
				oldDetails.setJobAssigneeDetails(newAssignees);
				JobDetails Jdetails = jobDetailsService.updateJobDetail(oldDetails);
				jobAssigneeService.updatereferencId(Jdetails.getId());
				jobAssigneeService.updateAssigneesStatusByRefid(Jdetails.getId());
				if(assignee_list_active != null) {
					jobAssigneeService.updatebulkassigneeRemovalStatus(Jdetails.getId(), assignee_list_active);
				}
				if (Jdetails != null) {
					check += 1;
				}
			}

			if (check == jobIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Assignees added successfully to given jobs");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in adding assignees to given jobs");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in JobDetailsApiService(bulkDelete) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			String eclassName = e.getClass().getSimpleName();
			// Compare with a string
			String desiredClassName = "MailSendException";
			if(eclassName.toLowerCase().contains("mail")){
				jsonObject.put(RestConstants.DATA, "Error in adding assignees to given jobs due to mail configuration check the configuration details");
			} else{
				jsonObject.put(RestConstants.DATA, "Failed to add assignees");
			}
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(bulkJobsAssignees) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllJobDetails", headers = "Accept=application/json")
	public String getAllJobDetails() {
		logger.info("JobDetailsApiService(getAllJobDetails) >> Entry");
		String response = "";
//		JSONObject response = new JSONObject();
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("JobDetailsApiService(getAllJobDetails) >> Request");
			List<JobDetails> details = jobDetailsService.getAllJobDetails();

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			logger.debug("JobDetailsApiService(getAllJobDetails) >> Request");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);

			logger.error("Exception occured in JobDetailsApiService(getAllJobDetails) and Exception details >> " + e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting job details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(getAllJobDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllJobDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getAllJobDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("JobDetailsApiService(getAllJobDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("JobDetailsApiService(getAllJobDetailsByOrgId)");
			List<JobDetails> details = jobDetailsService.getJobDetailsByOrgId(id);
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
			logger.error("Exception occured in JobDetailsApiService(getRoleDetailsByClientId) and Exception details >> "
					+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting job details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(getAllJobDetailsByOrgId) >> Exit");
		return response;
	}

	@GetMapping(value = "/getActiveJobDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getActiveJobDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("JobDetailsApiService(getActiveJobDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("JobDetailsApiService(getActiveJobDetailsByOrgId) >> Request -> OrgId :" + id);
			final List<JobDetails> details = jobDetailsService.getActiveJobDetailsByOrgId(id);
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
					"Exception occured in JobDetailsApiService(getActiveJobDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  job details by Org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(getActiveJobDetailsByOrgId) >> Exit");
		return response;
	}
	
	@GetMapping(value = "/getActiveJobDetailsByOrgIdnew/{id}", headers = "Accept=application/json")
	public String getActiveJobDetailsByOrgId_new(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("JobDetailsApiService(getActiveJobDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("JobDetailsApiService(getActiveJobDetailsByOrgId) >> Request -> OrgId :" + id);
			final List<CustomJobsDetails> details = jobDetailsService.getActiveJobDetailsByOrgId_new(id);
			logger.info("JobDetailsApiService(getActiveJobDetailsByOrgId) Data :" + details.size());
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
					"Exception occured in JobDetailsApiService(getActiveJobDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  job details by Org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(getActiveJobDetailsByOrgId) >> Exit");
		return response;
	}


	@GetMapping(value = "/getJobById/{id}", headers = "Accept=application/json")
	public String getJobDetailById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("JobDetailsApiService(getJobDetailById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("JobDetailsApiService(getJobDetailById)");
			final JobDetails details = jobDetailsService.getJobById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting job details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error("Exception occured in JobDetailsApiService(getJobDetailById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  job details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(getJobDetailById) >> Exit");
		return response;
	}

	@GetMapping(value = "/getActiveJobDetailsByProjectId/{id}", headers = "Accept=application/json")
	public String getActiveJobDetailsByProjectId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("JobDetailsApiService(getActiveJobDetailsByProjectId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("JobDetailsApiService(getActiveJobDetailsByProjectId)");
			List<CustomJobsDetails> details = jobDetailsService.getActiveJobDetailsByProjectId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get job details by project Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in JobDetailsApiService(getActiveJobDetailsByProjectId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting job details by project Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(getActiveJobDetailsByProjectId) >> Exit");
		return response;
	}
	
	@GetMapping(value = "/getActiveJobsDetailsByProjectId/{id}", headers = "Accept=application/json")
	public String getActiveJobsDetailsByProjectId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("JobDetailsApiService(getActiveJobsDetailsByProjectId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("JobDetailsApiService(getActiveJobsDetailsByProjectId)");
			List<JobDetails> details = jobDetailsService.getActiveJobsDetailsByProjectId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get job details by project Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in JobDetailsApiService(getActiveJobsDetailsByProjectId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting job details by project Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(getActiveJobsDetailsByProjectId) >> Exit");
		return response;
	}
//	api for get Inactive JobsDetails By ProjectId
	@GetMapping(value = "/getInactiveJobsDetailsByProjectId/{id}", headers = "Accept=application/json")
	public String getInactiveJobsDetailsByProjectId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("JobDetailsApiService(getInactiveJobsDetailsByProjectId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("JobDetailsApiService(getInactiveJobsDetailsByProjectId)");
			List<JobDetails> details = jobDetailsService.getInactiveJobsDetailsByProjectId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get job details by project Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in JobDetailsApiService(getInactiveJobsDetailsByProjectId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting job details by project Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JobDetailsApiService(getInactiveJobsDetailsByProjectId) >> Exit");
		return response;
	}
	
	// Only returns the active projects name list --> [project1 , project2]
		@GetMapping(value = "/getActiveJobNameListWithProjectByOrgId/{id}", headers = "Accept=application/json")
		public String getActiveJobNameListWithProjectByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
			logger.info("JobDetailsApiService(getActiveJobNameListWithProjectByOrgId) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {
				logger.info("JobDetailsApiService(getActiveJobNameListWithProjectByOrgId)");
				final List<CustomJobsDetails> details = jobDetailsService.getActiveJobNameListWithProjectByOrgId(id);
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
				logger.error(
						"Exception occured in JobDetailsApiService(getActiveJobNameListWithProjectByOrgId) and Exception details >> "
								+ e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting  job details by org Id");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("JobDetailsApiService(getActiveJobNameListWithProjectByOrgId) >> Exit");
			return response;
		}
		
//		for to get status wise jobs
		@PutMapping(value = "/getAllJobsByStatusByOrgId", headers = "Accept=application/json")
		public String getAllJobsByStatusByOrgId(@RequestBody final String request) {
			logger.info("JobDetailsApiService(getAllJobsByStatusByOrgId) >> Entry");
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
				logger.info("JobDetailsApiService(getAllJobsByStatusByOrgId) >> Request -> OrgId :" + org_Id);
				final List<CustomJobsDetails> details = jobDetailsService.getAllJobsByStatusByOrgId(org_Id,status,is_Activated);
				logger.info("JobDetailsApiService(getAllJobsByStatusByOrgId) Data :" + details.size());
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
//				System.out.println(e);
				logger.error(
						"Exception occured in JobDetailsApiService(getAllJobsByStatusByOrgId) and Exception details >> "
								+ e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting  job details by Org Id");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("JobDetailsApiService(getAllJobsByStatusByOrgId) >> Exit");
			return response;
		}

}
