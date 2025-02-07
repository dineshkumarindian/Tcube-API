package com.tcube.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.tcube.api.model.MailConfigDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.service.MailConfigDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.EncryptorUtil;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/MailConfigDetails" })
public class MailConfigDetailsApiService {

	private static Logger logger=  LogManager.getLogger(MailConfigDetailsApiService.class);

	@Autowired
	MailConfigDetailsService MailConfigDetailsService;
	
	@Autowired
	OrgDetailsService orgDetailsService;
	
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createMailConfigDetail(@RequestBody String details) {
		logger.info("MailConfigDetailsApiService(createMailConfigDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			
			logger.debug("MailConfigDetailsApiService(createMailConfigDetail) >> Request");
			final MailConfigDetails MailConfigDetails = MapperUtil.readAsObjectOf(MailConfigDetails.class,
					newJsonObject.toString());
			MailConfigDetails.setHost(EncryptorUtil.encryptPropertyValue(MailConfigDetails.getHost()));
			MailConfigDetails.setPassword(EncryptorUtil.encryptPropertyValue(MailConfigDetails.getPassword()));
			MailConfigDetails.setUsername(EncryptorUtil.encryptPropertyValue(MailConfigDetails.getUsername()));
			MailConfigDetails.setSender(EncryptorUtil.encryptPropertyValue(MailConfigDetails.getSender()));
			final MailConfigDetails newdetails =MailConfigDetailsService.createMailConfig(MailConfigDetails);
			if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "MailConfig details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating MailConfig details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("MailConfigDetailsApiService(createMailConfigDetail) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in MailConfigDetailsApiService(createMailConfigDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating MailConfig details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("MailConfigDetailsApiService(createMailConfigDetail) >> Exit");
		return response;
	}
	
	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateMailConfigDetail(@RequestBody final String details) {
		logger.info("MailConfigDetailsApiService(updateMailConfigDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();	
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			Long Id = newJsonObject.getLong("id");
//			newJsonObject.remove("org_id");
			newJsonObject.remove("id");
			logger.debug("MailConfigDetailsApiService(updateMailConfigDetail) >> Request" );
			final MailConfigDetails upadedetails = MapperUtil.readAsObjectOf(MailConfigDetails.class,
					newJsonObject.toString());
			MailConfigDetails newDetails = MailConfigDetailsService.getMailConfigById(Id);
			newDetails.setOrg_id(upadedetails.getOrg_id());
			newDetails.setActive(upadedetails.getisActive());
			newDetails.setHost(EncryptorUtil.encryptPropertyValue(upadedetails.getHost()));
			newDetails.setPort(upadedetails.getPort());
			newDetails.setPassword(EncryptorUtil.encryptPropertyValue(upadedetails.getPassword()));
			newDetails.setUsername(EncryptorUtil.encryptPropertyValue(upadedetails.getUsername()));
			newDetails.setSender(EncryptorUtil.encryptPropertyValue(upadedetails.getSender()));
			final MailConfigDetails data = MailConfigDetailsService.updateMailConfig(newDetails);
			if (data != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "MailConfig details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating MailConfig details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("MailConfigDetailsApiService(updateMailConfigDetail) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in MailConfigDetailsApiService(updateMailConfigDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating MailConfig details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("MailConfigDetailsApiService(updateMailConfigDetail) >> Exit");
		return response;
	}

	@DeleteMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteMailConfigDetail(@PathVariable(value = "id") Long id) {
		logger.info("MailConfigDetailsApiService(deleteMailConfigDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final MailConfigDetails newDetails = MailConfigDetailsService.getMailConfigById(id);
			newDetails.setDelete(true);
			final MailConfigDetails details = MailConfigDetailsService.deleteMailConfig(newDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "MailConfig details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting MailConfig details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in MailConfigDetailsApiService(deleteMailConfigDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting MailConfig details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("MailConfigDetailsApiService(deleteMailConfigDetail) >> Exit");
		return response;
	}
	
	@GetMapping(value="/getMailConfigByOrgId/{id}",headers="Accept=application/json")
    public String getMailConfigByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder){
		logger.info("MailConfigDetailsApiService(getMailConfigByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("MailConfigDetailsApiService(getMailConfigByOrgId)" );
        	MailConfigDetails details = MailConfigDetailsService.getMailConfigByOrgId(id);
        	if (details != null) {
        		MailConfigDetails maildata = details;
        		maildata.setHost(EncryptorUtil.decryptPropertyValue(details.getHost()));
        		maildata.setPassword(EncryptorUtil.decryptPropertyValue(details.getPassword()));
        		maildata.setUsername(EncryptorUtil.decryptPropertyValue(details.getUsername()));
        		maildata.setSender(EncryptorUtil.decryptPropertyValue(details.getSender()));
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(maildata));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get MailConfig details by org Id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in MailConfigDetailsApiService(getMailConfigByOrgId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  MailConfig details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("MailConfigDetailsApiService(MailConfigDetailsApiService) >> Exit");
		return response;
    }
	

	@GetMapping(value="/getMailConfigById/{id}",headers="Accept=application/json")
    public String getMailConfigById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder){
		logger.info("MailConfigDetailsApiService(getMailConfigById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("MailConfigDetailsApiService(getMailConfigById)" );
        	final MailConfigDetails details = MailConfigDetailsService.getMailConfigById(id);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting MailConfig details by id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in MailConfigDetailsApiService(getMailConfigById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  MailConfig details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("MailConfigDetailsApiService(getMailConfigById) >> Exit");
		return response;
    }
	
	
}
