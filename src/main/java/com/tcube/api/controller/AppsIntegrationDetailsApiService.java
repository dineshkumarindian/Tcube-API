package com.tcube.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.tcube.api.model.AppsIntegrationDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.service.AppsIntegrationDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/AppsIntegrationDetails" })
public class AppsIntegrationDetailsApiService {

	private static Logger logger = LogManager.getLogger(AppsIntegrationDetailsApiService.class);

	@Autowired
	AppsIntegrationDetailsService appsIntegrationDetailsService;

	@Autowired
	OrgDetailsService orgDetailsService;
	
	// create method
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createIntegration(@RequestBody String detailsOfIntegration) {
		logger.info("AppsIntegrationDetailsApiService(createIntegration) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newdetails = new JSONObject(detailsOfIntegration);
			Long orgId = newdetails.getLong("org_id");
			newdetails.remove("org_id");

			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("AppsIntegrationDetailsApiService(createIntegration) >> Request ");
			AppsIntegrationDetails details = MapperUtil.readAsObjectOf(AppsIntegrationDetails.class, newdetails.toString());
			//the below loop - delete the previous unwanted integration by added before
			if(details.getApp_name().equals("slack") && details.getReason().equals("create-tasks")) {
				List<AppsIntegrationDetails> detailsOfIntegrations = appsIntegrationDetailsService.getActiveIntegrationByOrgId(orgId);
				for(AppsIntegrationDetails i : detailsOfIntegrations) {
					if(i.getIs_deleted() == false && i.getApp_name().equals("slack") && i.getReason().equals("create-tasks")) {
						i.setIs_deleted(true);
						appsIntegrationDetailsService.updateIntegration(i);
					}
				}
			}
			details.setOrgDetails(orgDetails);
			final AppsIntegrationDetails createDetails = appsIntegrationDetailsService.createIntegration(details);
			if (createDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New integration created successfully");
				response = new Gson().toJson(jsonObject);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to create integration");
				response = new Gson().toJson(jsonObject);
			}

		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in AppsIntegrationDetailsApiService(createIntegration) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to create integration");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AppsIntegrationDetailsApiService(createIntegration) >> Exit");
		return response;
	}
	
	// update method
	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateIntegration(@RequestBody final String details) {
		logger.info("AppsIntegrationDetailsApiService(updateIntegration) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();	
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("org_id");
			newJsonObject.remove("id");
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("AppsIntegrationDetailsApiService(updateIntegration) >> Request");
			final AppsIntegrationDetails incomingdetails = MapperUtil.readAsObjectOf(AppsIntegrationDetails.class,
					newJsonObject.toString());
			AppsIntegrationDetails newDetails = appsIntegrationDetailsService.getById(Id);
			newDetails.setOrgDetails(orgDetails);
			newDetails.setUrl(incomingdetails.getUrl());
			newDetails.setWhatsapp_access_token(incomingdetails.getWhatsapp_access_token());
			newDetails.setNumbers(incomingdetails.getNumbers());
//			newDetails.setMobile_number_2(incomingdetails.getMobile_number_2());
//			newDetails.setCountry_code_1(incomingdetails.getCountry_code_1());
//			newDetails.setCountry_code_2(incomingdetails.getCountry_code_2());
			final AppsIntegrationDetails updateDetails = appsIntegrationDetailsService.updateIntegration(newDetails);
			if (updateDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Integration updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating integration");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("AppsIntegrationDetailsApiService(updateIntegration) >> Response ");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in DesignationDetailsApiService(updateIntegration) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating integration");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AppsIntegrationDetailsApiService(updateIntegration) >> Exit");
		return response;
	}
	
	// delete method
	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteIntegratin(@PathVariable(value = "id") Long id,final UriComponentsBuilder ucBuilder) {
		logger.info("AppsIntegrationDetailsApiService(deleteIntegratin) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final AppsIntegrationDetails newDetails = appsIntegrationDetailsService.getById(id);
			newDetails.setIs_deleted(true);
			final AppsIntegrationDetails details = appsIntegrationDetailsService.updateIntegration(newDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Integration details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting integration details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in AppsIntegrationDetailsApiService(deleteIntegratin) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting integration details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AppsIntegrationDetailsApiService(deleteIntegratin) >> Exit");
		return response;
	}
	
	// pause Or resume the integration
	@PutMapping(value = "/pauseOrResume", headers = "Accept=application/json")
	public String pauseResumeIntegratin(@RequestBody String details) {
		logger.info("AppsIntegrationDetailsApiService(deleteIntegratin) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			String status = newJsonObject.getString("status");
			newJsonObject.remove("status");
			final AppsIntegrationDetails newDetails = appsIntegrationDetailsService.getById(Id);
			if(status.equals("paused")) {
				newDetails.setIs_paused(true);
			}else if(status.equals("resumed")) {
				newDetails.setIs_paused(false);
			}
			final AppsIntegrationDetails updatedetails = appsIntegrationDetailsService.updateIntegration(newDetails);
			if (updatedetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Integration status changed successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in integration status change");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in AppsIntegrationDetailsApiService(deleteIntegratin) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting integration  status change");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AppsIntegrationDetailsApiService(deleteIntegratin) >> Exit");
		return response;
	}
	
	// get all integration details
	@GetMapping(value = "/getAllIntegrations", headers = "Accept=application/json")
	public String getAllIntegrations() {
		logger.info("AppsIntegrationDetailsApiService(getAllIntegrations) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("AppsIntegrationDetailsApiService(getAllIntegrations) >> Request");
			List<AppsIntegrationDetails> details =appsIntegrationDetailsService.getAllIntegrations();

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "No data found in integration details");
			}
			logger.debug("AppsIntegrationDetailsApiService(getAllIntegrations) >> Response");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			
			 logger.error("Exception occured in AppsIntegrationDetailsApiService(getAllIntegrations) and Exception details >> "+ e);
			 
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting  integration details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AppsIntegrationDetailsApiService(getAllIntegrations) >> Exit");
		return response;

	}
	
	//get by organization id
	
	@GetMapping(value="/getAllIntegrationByOrgId/{id}",headers="Accept=application/json")
    public String getAllIntegrationByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder){
		logger.info("AppsIntegrationDetailsApiService(getAllIntegrationByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("AppsIntegrationDetailsApiService(getAllIntegrationByOrgId)" );
        	 List<AppsIntegrationDetails> details = appsIntegrationDetailsService.getAllIntegrationByOrgId(id);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get integration details by org Id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error("Exception occured in AppsIntegrationDetailsApiService(getAllDesignationByOrgId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  integration details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AppsIntegrationDetailsApiService(getAllIntegrationByOrgId) >> Exit");
		return response;
    }
	
	// get active integrations by org id
	// is deleted = false and  is_paused = true or false
	@GetMapping(value="/getActiveIntegrationByOrgId/{id}",headers="Accept=application/json")
    public String getActiveIntegrationByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder){
		logger.info("AppsIntegrationDetailsApiService(getActiveIntegrationByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("AppsIntegrationDetailsApiService(getActiveIntegrationByOrgId)" );
        	 final List<AppsIntegrationDetails> details = appsIntegrationDetailsService.getActiveIntegrationByOrgId(id);

        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get integration details by org Id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error("Exception occured in AppsIntegrationDetailsApiService(getActiveIntegrationByOrgId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  integration details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AppsIntegrationDetailsApiService(getActiveIntegrationByOrgId) >> Exit");
		return response;
    }
	
	@GetMapping(value="/getIntegrationById/{id}",headers="Accept=application/json")
    public String getIntegrationById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder){
		logger.info("AppsIntegrationDetailsApiService(getIntegrationById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("AppsIntegrationDetailsApiService(getIntegrationById)" );
        	final AppsIntegrationDetails details = appsIntegrationDetailsService.getById(id);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting integration details by id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error("Exception occured in AppsIntegrationDetailsApiService(getIntegrationById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  integration details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AppsIntegrationDetailsApiService(getIntegrationById) >> Exit");
		return response;
    }

	//get by module name and org id
	@PutMapping(value = "/getIntegrationByOrgIdAndModule", headers = "Accept=application/json")
	public String getIntegrationByOrgIdAndModule(@RequestBody final String details,final UriComponentsBuilder ucBuilder) {
		logger.info("AppsIntegrationDetailsApiService(getIntegrationByOrgIdAndModule) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("AppsIntegrationDetailsApiService(getIntegrationByOrgIdAndModule)");
			final JSONObject newJsonObject = new JSONObject(details);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			final AppsIntegrationDetails newDetails = MapperUtil.readAsObjectOf(AppsIntegrationDetails.class,
					newJsonObject.toString());
			OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
			newDetails.setOrgDetails(org);
			newDetails.setIs_deleted(false);
			final AppsIntegrationDetails details1 = appsIntegrationDetailsService.getIntegrationByOrgIdAndModule(newDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//				jsonObject.put(RestConstants.DATA, new Gson().toJson(details1));
				jsonObject.put(RestConstants.DATA, details1);
} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get integration details by org id and module");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in AppsIntegrationDetailsApiService(getIntegrationByOrgIdAndModule) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting integration details by org id and module");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AppsIntegrationDetailsApiService(getIntegrationByOrgIdAndModule) >> Exit");
		return response;
	}

	@GetMapping(value="/getActiveSlackIntegrationByOrgId/{id}",headers="Accept=application/json")
	public String getActiveSlackIntegrationByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder){
		logger.info("AppsIntegrationDetailsApiService(getActiveSlackIntegrationByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("AppsIntegrationDetailsApiService(getActiveSlackIntegrationByOrgId)" );
			final List<AppsIntegrationDetails> details = appsIntegrationDetailsService.getActiveSlackIntegrationByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get slack integration details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in AppsIntegrationDetailsApiService(getActiveSlackIntegrationByOrgId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting slack integration details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AppsIntegrationDetailsApiService(getActiveSlackIntegrationByOrgId) >> Exit");
		return response;
	}
	
	@GetMapping(value="/getActiveWhatsappIntegrationByOrgId/{id}",headers="Accept=application/json")
	public String getActiveWhatsappIntegrationByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder){
		logger.info("AppsIntegrationDetailsApiService(getActiveWhatsappIntegrationByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("AppsIntegrationDetailsApiService(getActiveWhatsappIntegrationByOrgId)" );
			final List<AppsIntegrationDetails> details = appsIntegrationDetailsService.getActiveWhatsappIntegrationByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get whatsapp integration details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in AppsIntegrationDetailsApiService(getActiveWhatsappIntegrationByOrgId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting whatsapp integration details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AppsIntegrationDetailsApiService(getActiveWhatsappIntegrationByOrgId) >> Exit");
		return response;
	}

	
}
