package com.tcube.api.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.tcube.api.model.CustomJobsDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.ProjectDetails;
import com.tcube.api.model.RoleDetails;
import com.tcube.api.service.ClientDetailsService;
import com.tcube.api.service.JobDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.ProjectDetailsService;
import com.tcube.api.utils.EmailTemplateMapperUtil;
import com.tcube.api.utils.EncryptorUtil;
import com.tcube.api.utils.MailSender;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/ClientDetails" })
public class ClientDetailsApiService {

	private static Logger logger=  LogManager.getLogger(ClientDetailsApiService.class);
	
	@Autowired
	ClientDetailsService clientDetailsService;
	
	@Autowired
	OrgDetailsService orgDetailsService;
	
	@Autowired
	ProjectDetailsService projectDetailsService;
	
	@Autowired
	JobDetailsService jobDetailsService;
	
	
	// create method for client details //
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createClientDetail(@RequestBody String details) {
		logger.info("ClientDetailsApiService(createClientDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");	
			logger.debug("ClientDetailsApiService(createClientDetail) >> Request");
			final ClientDetails clientdetails = MapperUtil.readAsObjectOf(ClientDetails.class,
					newJsonObject.toString());
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			clientdetails.setIs_deleted(false);
			clientdetails.setIs_activated(true);
			clientdetails.setOrgDetails(orgDetails);
			final ClientDetails details1 = clientDetailsService.createClientDetails(clientdetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Client details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating client details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ClientDetailsApiService(createClientDetail) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in ClientDetailsApiService(createClientDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating client details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ClientDetailsApiService(createClientDetail) >> Exit");
		return response;
	}

	// update method for client details //
	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateClientDetail(@RequestBody final String details) {
		logger.info("ClientDetailsApiService(updateClientDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			Long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");

			final OrgDetails orgDetails= orgDetailsService.getOrgDetailsById(orgId);
			final ClientDetails clientDetails =clientDetailsService.getClientById(Id);
			logger.debug("ClientDetailsApiService(updateClientDetail) >> Request");
			final ClientDetails newDetails = MapperUtil.readAsObjectOf(ClientDetails.class,
					newJsonObject.toString());
			clientDetails.setClient_name(newDetails.getClient_name());
			clientDetails.setOrgDetails(orgDetails);
			clientDetails.setFirstname(newDetails.getFirstname());
			clientDetails.setLastname(newDetails.getLastname());
			clientDetails.setCurrency(newDetails.getCurrency());
			clientDetails.setBilling_method(newDetails.getBilling_method());
			clientDetails.setEmail(newDetails.getEmail());
			clientDetails.setMobile_number(newDetails.getMobile_number());

			final ClientDetails details1 = clientDetailsService.updateClientDetails(clientDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Client details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating client details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ClientDetailsApiService(updateClientDetail) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating client details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ClientDetailsApiService(updateClientDetail) >> Exit");
		return response;
	}

	//Delete(soft delete) //
	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deletClientDetail(@PathVariable(value = "id") Long id) {
		logger.info("ClientDetailsApiService(deletClientDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final ClientDetails oldDetails = clientDetailsService.getClientById(id);
			oldDetails.setIs_deleted(true);
			final ClientDetails details = clientDetailsService.deleteClientDetails(oldDetails);
			final List<ProjectDetails> pjtDetails = projectDetailsService.getInactiveProjectDetailsByClientId(id);
			for(ProjectDetails p : pjtDetails) {
				p.setIs_deleted(true);
				ProjectDetails pjtDetails1 = projectDetailsService.updateProjectDetails(p);
				final List<JobDetails> jobdetails = jobDetailsService.getInactiveJobsDetailsByProjectId(p.getId());
				for(JobDetails j : jobdetails) {
					j.setIs_deleted(true);
				    JobDetails jobdetails1 = jobDetailsService.updateJobDetail(j);
				}
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Client details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting client details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in ClientDetailsApiService(deletClientDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting client details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ClientDetailsApiService(deletClientDetail) >> Exit");
		return response;
	}
	 @PutMapping(value = "/bulkDelete", headers = "Accept=application/json")
		public String bulkDeletePlanDetail(@RequestBody final String request) {
			logger.info("ClientDetailsApiService(bulkDelete) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {
				final JSONObject newJsonObject = new JSONObject(request);
				JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
				final int details = clientDetailsService.bulkDelete(deleteIds);
				if (details == deleteIds.length()) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "leave types bulk deleted successfully");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA, "Failed to bulk delete leave types");
				}
				response = new Gson().toJson(jsonObject);
				logger.debug("ClientDetailsApiService(bulkDelete) >> Response");
			} catch (Exception e) {
				Sentry.captureException(e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in bulk delete leave types");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("ClientDetailsApiService(bulkDelete) >> Exit");
			return response;
	 }
	 @PutMapping(value = "/bulkactiveDeactiveDelete", headers = "Accept=application/json")
		public String bulkDeactivateDelete(@RequestBody final String request) {
			logger.info("ClientDetailsApiService(bulkactiveDeactiveDelete) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {
				final JSONObject newJsonObject = new JSONObject(request);
				JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
				final int details = clientDetailsService.bulkDelete(deleteIds);
				for(Object id : deleteIds) {
					 String stringToConvert = String.valueOf(id);
				     Long convertedLongId = Long.parseLong(stringToConvert);
				     final List<ProjectDetails> pjtDetails = projectDetailsService.getActiveProjectDetailsByClientId(convertedLongId);
						for(ProjectDetails p : pjtDetails) {
							p.setIs_deleted(true);
							ProjectDetails pjtDetails1 = projectDetailsService.updateProjectDetails(p);
							final List<JobDetails> jobdetails = jobDetailsService.getActiveJobsDetailsByProjectId(p.getId());
							for(JobDetails j : jobdetails) {
								j.setIs_deleted(true);
							    JobDetails jobdetails1 = jobDetailsService.updateJobDetail(j);
							    
							}
						}
				}
				if (details == deleteIds.length()) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "bulk deleted active and deactive successfully");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA, "Failed to bulk delete active and deactive");
				}
				response = new Gson().toJson(jsonObject);
				logger.debug("ClientDetailsApiService(bulkactiveDeactiveDelete) >> Response");
			} catch (Exception e) {
				Sentry.captureException(e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in bulk delete leave types");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("ClientDetailsApiService(bulkDelete) >> Exit");
			return response;
	 }
	 @PutMapping(value = "/bulkDeactiveClient", headers = "Accept=application/json")
		public String bulkDeactiveClientDetails(@RequestBody final String request) {
			logger.info("ClientDetailsApiService(bulkDeactiveClient) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {
				final JSONObject newJsonObject = new JSONObject(request);
				JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
				String action = newJsonObject.getString("action");
				newJsonObject.remove("action");
				String comment = newJsonObject.getString("comments");
				newJsonObject.remove("comments");
				final int details = clientDetailsService.bulkDeactive(deleteIds,action);
				for(Object id : deleteIds) {
					 String stringToConvert = String.valueOf(id);
				     Long convertedLongId = Long.parseLong(stringToConvert);
				final ClientDetails oldDetails= clientDetailsService.getClientById(convertedLongId);
				String oldClientEmail = oldDetails.getEmail();
				logger.debug("OrgDetailsApiService(DeactivateOrg) >> Request");
				if(comment == "") {
					oldDetails.setComments("-");
					
				}else {
					oldDetails.setComments(comment);
				}
				final ClientDetails details1 = clientDetailsService.updateClientDetails(oldDetails);
				final List<ProjectDetails> pjtDetails = projectDetailsService.getActiveProjectDetailsByClientId(convertedLongId);
				for(ProjectDetails p : pjtDetails) {
					p.setIs_activated(false);
					ProjectDetails pjtDetails1 = projectDetailsService.updateProjectDetails(p);
					final List<JobDetails> jobdetails = jobDetailsService.getActiveJobsDetailsByProjectId(p.getId());
					for(JobDetails j : jobdetails) {
						j.setIs_activated(false);
					    JobDetails jobdetails1 = jobDetailsService.updateJobDetail(j);
					}
				}
				}
				if (details == deleteIds.length()) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "deactivate bulk clients successfully");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA, "Failed to deactivate bulk clients");
				}
				response = new Gson().toJson(jsonObject);
				logger.debug("ClientDetailsApiService(bulkDeactiveClient) >> Response");
			} catch (Exception e) {
				Sentry.captureException(e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in bulk delete leave types");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("ClientDetailsApiService(bulkDeactiveClient) >> Exit");
			return response;
	 }
	 @PutMapping(value = "/bulkactiveClient", headers = "Accept=application/json")
		public String bulkactiveClientDetails(@RequestBody final String request) {
			logger.info("ClientDetailsApiService(bulkactiveClient) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {
				final JSONObject newJsonObject = new JSONObject(request);
				JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
				String action = newJsonObject.getString("action");
				newJsonObject.remove("action");
				String comment = newJsonObject.getString("comments");
				newJsonObject.remove("comments");
				final int details = clientDetailsService.bulkActivate(deleteIds,action);
				for(Object id : deleteIds) {
					 String stringToConvert = String.valueOf(id);
				     Long convertedLongId = Long.parseLong(stringToConvert);
				
				final ClientDetails oldDetails= clientDetailsService.getClientById(convertedLongId);
				String oldOrgEmail = oldDetails.getEmail();
				logger.debug("OrgDetailsApiService(ActivateOrg) >> Request");
				oldDetails.setIs_activated(true);
				if(comment == "") {
					oldDetails.setComments("-");
				}else {
					oldDetails.setComments(comment);
				}
				final ClientDetails details1 = clientDetailsService.updateClientDetails(oldDetails);
				final List<ProjectDetails> pjtDetails = projectDetailsService.getInactiveProjectDetailsByClientId(convertedLongId);
				for(ProjectDetails p : pjtDetails) {
					p.setIs_activated(true);
					ProjectDetails pjtDetails1 = projectDetailsService.updateProjectDetails(p);
					final List<JobDetails> jobdetails = jobDetailsService.getInactiveJobsDetailsByProjectId(p.getId());
					for(JobDetails j : jobdetails) {
						j.setIs_activated(true);
					    JobDetails jobdetails1 = jobDetailsService.updateJobDetail(j);
					}
				}
			} 
				if (details == deleteIds.length()) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "bulk clients activated successfully");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA, "Failed to bulk clients activated");
				}
				response = new Gson().toJson(jsonObject);
				logger.debug("ClientDetailsApiService(bulkDelete) >> Response");
			}catch(Exception e) {
				Sentry.captureException(e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in bulk delete leave types");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("ClientDetailsApiService(bulkactiveClientDetails) >> Exit");
			return response;
	 }
	 
	//get api for get all client details ///
	@GetMapping(value = "/getAllClientDetails", headers = "Accept=application/json")
	public String getAllClientDetails() {
		logger.info("ClientDetailsApiService(getAllClientDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("ClientDetailsApiService(getAllClientDetails) >> Request");
			List<ClientDetails> details = clientDetailsService.getAllClientDetails();

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			logger.debug("ClientDetailsApiService(getAllClientDetails) >> Request");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			 logger.error("Exception occured in ClientDetailsApiService(getAllClientDetails) and Exception details >> "+ e);
			 
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting  client details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ClientDetailsApiService(getAllClientDetails) >> Exit");
		return response;
	}

	// get api for get details by org id with deleted client details also//
	@GetMapping(value="/getAllClientDetailsByOrgId/{id}",headers="Accept=application/json")
    public String getAllClientDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder){
		logger.info("ClientDetailsApiService(getAllClientDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("ClientDetailsApiService(getAllClientDetailsByOrgId)" );
        	 List<ClientDetails> details = clientDetailsService.getClientDetailsByOrgId(id);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get role details by org Id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in ClientDetailsApiService(getAllClientDetailsByOrgId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  client details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ClientDetailsApiService(getAllClientDetailsByOrgId) >> Exit");
		return response;
    }
	
	// get api for get details by org id //
	@GetMapping(value="/getActiveClientDetailsByOrgId/{id}",headers="Accept=application/json")
    public String getActiveClientDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder){
		logger.info("ClientDetailsApiService(getActiveClientDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("ClientDetailsApiService(getActiveClientDetailsByOrgId)" );
        	 final List<ClientDetails> details = clientDetailsService.getAllActiveClientReportsByOrgId(id);
//        	 final List<ClientDetails> newDetails = new ArrayList<>();
//        	 for(ClientDetails i: details) {
//        		 if(i.getIs_deleted() == false)
//        			 newDetails.add(i);
//        	 }
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get client details by org Id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in ClientDetailsApiService(getActiveClientDetailsByOrgId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  client details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ClientDetailsApiService(getActiveClientDetailsByOrgId) >> Exit");
		return response;
    }
//	get api for inactive clients by orgid
	@GetMapping(value = "/getInactiveClientDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getInactiveDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ClientDetailsApiService(getInactiveClientDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ClientDetailsApiService(getInactiveClientDetailsByOrgId)");
			List<ClientDetails> details = clientDetailsService.getInactiveClientDetailsByOrgID(id);
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
					"Exception occured in ClientDetailsApiService(getInactiveClientDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  client details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ClientDetailsApiService(getInactiveClientDetailsByOrgId) >> Exit");
		return response;
	}
	
	
	
	@GetMapping(value="/getClientById/{id}",headers="Accept=application/json")
    public String getClientDetailById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder){
		logger.info("ClientDetailsApiService(getClientDetailById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("ClientDetailsApiService(getClientDetailById)" );
        	final ClientDetails details = clientDetailsService.getClientById(id);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting client details by id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in ClientDetailsApiService(getClientDetailById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  client details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ClientDetailsApiService(getClientDetailById) >> Exit");
		return response;
    }
    //api for activate client
	@PutMapping(value = "/activateClient", headers = "Accept=application/json")
	public String ActivateClient(@RequestBody final String details1) {
		logger.info("ClientDetailsApiService(ActivateOrg) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details1);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			String comment = newJsonObject.getString("comments");
			newJsonObject.remove("comments");
			final ClientDetails oldDetails= clientDetailsService.getClientById(Id);
			String oldOrgEmail = oldDetails.getEmail();
			logger.debug("OrgDetailsApiService(ActivateOrg) >> Request");
			oldDetails.setIs_activated(true);
			if(comment == "") {
				oldDetails.setComments("-");
			}else {
				oldDetails.setComments(comment);
			}
			final ClientDetails details = clientDetailsService.updateClientDetails(oldDetails);
			final List<ProjectDetails> pjtDetails = projectDetailsService.getInactiveProjectDetailsByClientId(Id);
			for(ProjectDetails p : pjtDetails) {
				p.setIs_activated(true);
				ProjectDetails pjtDetails1 = projectDetailsService.updateProjectDetails(p);
				final List<JobDetails> jobdetails = jobDetailsService.getInactiveJobsDetailsByProjectId(p.getId());
				for(JobDetails j : jobdetails) {
					j.setIs_activated(true);
				    JobDetails jobdetails1 = jobDetailsService.updateJobDetail(j);
				}
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Client details activated successfully");
//				String template = EmailTemplateMapperUtil.getClientActivateAccountMailTemplate(details,comment);
//				String subject = "T-CUBE | " + " Your Account is Activated ";
//				MailSender.sendEmail(oldOrgEmail, template, subject, "");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in activate Client details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ClientDetailsApiService(ActivateClient) >> Response");
		}
			catch (Exception e) {
				Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in activate client details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ClientDetailsApiService(ActivateClient) >> Exit");
		return response;
	}
	
	//Api for deactivate the client
		@PutMapping(value = "/deactivateClient", headers = "Accept=application/json")
		public String DeactivateClient(@RequestBody final String details1) {
			logger.info("ClientDetailsApiService(DeactivateClient) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {
				final JSONObject newJsonObject = new JSONObject(details1);
				Long Id = newJsonObject.getLong("id");
				newJsonObject.remove("id");
				String comment = newJsonObject.getString("comments");
				newJsonObject.remove("comments");
				final ClientDetails oldDetails= clientDetailsService.getClientById(Id);
				String oldClientEmail = oldDetails.getEmail();
				logger.debug("OrgDetailsApiService(DeactivateOrg) >> Request");
				oldDetails.setIs_activated(false);
				if(comment == "") {
					oldDetails.setComments("-");
					
				}else {
					oldDetails.setComments(comment);
				}
				final ClientDetails details = clientDetailsService.updateClientDetails(oldDetails);
				final List<ProjectDetails> pjtDetails = projectDetailsService.getActiveProjectDetailsByClientId(Id);
				for(ProjectDetails p : pjtDetails) {
					p.setIs_activated(false);
					ProjectDetails pjtDetails1 = projectDetailsService.updateProjectDetails(p);
					final List<JobDetails> jobdetails = jobDetailsService.getActiveJobsDetailsByProjectId(p.getId());
					for(JobDetails j : jobdetails) {
						j.setIs_activated(false);
					    JobDetails jobdetails1 = jobDetailsService.updateJobDetail(j);
					}
				}
				if (details != null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "Client details deactivated successfully");
//					String template = EmailTemplateMapperUtil.getClientDeactivateAccountMailTemplate(details,comment);
//					String subject = "T-CUBE | " + " Your Account is Deactivated ";
//					MailSender.sendEmail(oldClientEmail, template, subject, "");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Error in deactivate client details");
				}
				response = new Gson().toJson(jsonObject);
				logger.debug("ClientDetailsApiService(DeactivateClient) >> Response");
			} catch (Exception e) {
				Sentry.captureException(e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deactivate client details");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("ClientDetailsApiService(DeactivateClient) >> Exit");
			return response;
		}
		@GetMapping(value="/getTotalCPJCount/{id}",headers="Accept=application/json")
	    public String getTotalCPJByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder){
			logger.info("ClientDetailsApiService(getTotalCPJByOrgId) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {
	        	logger.info("ClientDetailsApiService(getTotalCPJByOrgId)" );
	        	JSONObject details = clientDetailsService.getTotalCPJByOrgId(id);
	        	if (details.length() != 0) {
				details.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				details.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				response = new Gson().toJson(details);
			} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
					jsonObject.put(RestConstants.DATA, "Failed to get client, project and jobs count details by org Id");
	        	    response = new Gson().toJson(jsonObject);
			}
			}catch(Exception e) {
				Sentry.captureException(e);
				logger.error("Exception occured in ClientDetailsApiService(getTotalCPJByOrgId) and Exception details >> " + e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting  client, project and jobs count details by org Id");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("ClientDetailsApiService(getTotalCPJByOrgId) >> Exit");
			return response;
	    }
}




