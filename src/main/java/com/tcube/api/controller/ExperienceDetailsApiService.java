package com.tcube.api.controller;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.tcube.api.model.ExperienceLetterDetails;
import com.tcube.api.model.BusinessLetterDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.service.ExperienceLetterService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

import com.tcube.api.dao.ExperienceLetterDao;

@Component
@RestController
@CrossOrigin(origins ="*" , allowedHeaders ="*")
@RequestMapping(value = { "/api/experienceDetails" })
public class ExperienceDetailsApiService {
	
private static Logger logger = LogManager.getLogger(ExperienceLetterService.class);
	
@Autowired
private ExperienceLetterService experienceLetterService;

@Autowired
private OrgDetailsService orgDetailService;

@PostMapping(value = "/create", headers = "Accept=application/json")
public String createInternshipDetails(@RequestBody String experienceDetails) {
	logger.info("experinceLetterDetails(createInternshipDetails)<<Entry");
	String response= "";
	final JSONObject jsonObject = new JSONObject();
	final JSONObject newJsonObject = new JSONObject(experienceDetails);
	String getLogo = newJsonObject.getString("logoImage");
	String getSignImage = newJsonObject.getString("signImage");
	newJsonObject.put("logoImage", ImageProcessor.compressBytes(getLogo.getBytes()));
	newJsonObject.put("signImage", ImageProcessor.compressBytes(getSignImage.getBytes()));
	long orgId = newJsonObject.getLong("org_id");
	newJsonObject.remove("org_id");
	final OrgDetails orgDetails = orgDetailService.getOrgDetailsById(orgId);
	logger.debug("internshipLetterDetails(createTasks) >> Request :"+ experienceDetails );
	ExperienceLetterDetails experienceDetails1;
	try {
		experienceDetails1 = MapperUtil.readAsObjectOf(ExperienceLetterDetails.class,newJsonObject.toString());
		experienceDetails1.setOrgDetails(orgDetails);
		experienceDetails1.setIs_deleted(false);
		final ExperienceLetterDetails details = experienceLetterService.createExperienceLetter(experienceDetails1);
		if(details!=null) {
			jsonObject.put(RestConstants.STATUS_CODE,RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE,RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA,"New Task created successfully");
		} else {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating new task details");
		}
		response = new Gson().toJson(jsonObject);
		logger.debug("internshipLetterDetails(createTasks) >> Response :" + response);
		
	} catch (Exception e) {
		Sentry.captureException(e);
		e.printStackTrace();
		logger.error("Exception occured in internshipLetterService(createInternshipLetter) and Exception details >>"+e);
		jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
		jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
		jsonObject.put(RestConstants.DATA, "Error in creating ");
		response = new Gson().toJson(jsonObject);
		// TODO Auto-generated catch block
		
	}
	
	return response;
	}

@SuppressWarnings("unused")
@GetMapping(value = "/getActiveDetailsByOrgId/{id}", headers = "Accept=application/json")
public String getActiveExperienceDetailsByOrgId(@PathVariable final Long id,final UriComponentsBuilder ucBuilder) {
	logger.info("experienceLetterApiService(getActiveExperienceDetailsByOrgId)>>Entry");
	String response = "";
	final JSONObject jsonObject = new JSONObject();
	try {
		logger.info("getall experienceLetterApiService<<Entry");
		final List<ExperienceLetterDetails> details =  experienceLetterService.getActiveOrgIdInternsipDetail(id);
//		System.out.println(details.get(0).getCompanyName());
		if (details != null) {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
		} else {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to get experience letter details by org Id");
		}
		response = new Gson().toJson(jsonObject);
	} catch(Exception e) {
//		System.out.println(e);
		Sentry.captureException(e);
		logger.error("Exception occured in ExperienceDetailsApiService(getActiveInternshipDetailsByOrgId) and Exception details>>"+ e);
		jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
		jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
		jsonObject.put(RestConstants.DATA, "Error in extracting  experience details by Org Id");
		response = new Gson().toJson(jsonObject);
	}
	logger.info("ExperienceApiService(getActiveExperienceDetailsByOrgId) >> Exit");
	return response;
}

@PutMapping(value="/delete/{id}", headers = "Accept=appllication/json")
public String deleteExperienceLetter(@PathVariable(value ="id")Long id) {
	logger.info("deleteInternshipLetterApiService(deleteInternshipLetter)<< Entry");
	String response = "";
	final JSONObject jsonObject = new JSONObject();
	try {
		final ExperienceLetterDetails oldDetails = experienceLetterService.getById(id);
		System.out.println(oldDetails);
		oldDetails.setIs_deleted(true);
		logger.info("get the InternshipLetterApiService id<<entry");
		final ExperienceLetterDetails  details = experienceLetterService.deleteExperienceLetter(oldDetails);
		System.out.println(details);
		if (details != null) {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA, "internshipletter details deleted successfully");
		} else {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting internshipLetter details");
		}
		response = new Gson().toJson(jsonObject);
		
	} catch(Exception e) {
		Sentry.captureException(e);
		logger.info("Exception occured in internshipletterApiService(deleteInternshipLetter)<<"+e);
		jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
		jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
		jsonObject.put(RestConstants.DATA, "Error in deleting job details");
		response = new Gson().toJson(jsonObject);
	}
	logger.info("deleteInternshipLetterApiService>>Exit");
	return response;
	
}

@PutMapping(value="/update/{id}",headers="Accept=application/json")
public String updateExperienceDetail(@PathVariable(value="id")Long id,@RequestBody String body)
{
	logger.info("update the updateExperienceDetailService(updateInternshipDetail)>> entry");
	String response = "";
	final JSONObject jsonObject = new JSONObject();
	try {
		logger.info("InternshipLetterApiService(updateApi)<<Entry");
		final JSONObject newJsonObject = new JSONObject(body);
		long orgId = newJsonObject.getLong("org_id");
		newJsonObject.remove("org_id");
		final OrgDetails orgDetails = orgDetailService.getOrgDetailsById(orgId);
		final ExperienceLetterDetails experienceIdDetails = experienceLetterService.getById(id);
		final ExperienceLetterDetails newInternDetails = MapperUtil.readAsObjectOf(ExperienceLetterDetails.class, newJsonObject.toString());
		experienceIdDetails.setOrgDetails(orgDetails);
		experienceIdDetails.setEmployeeName(newInternDetails.getEmployeeName());
		experienceIdDetails.setIdNo(newInternDetails.getIdNo());
		experienceIdDetails.setDesignation(newInternDetails.getDesignation());
		experienceIdDetails.setJoiningDate(newInternDetails.getJoiningDate());
		experienceIdDetails.setCompletingDate(newInternDetails.getCompletingDate());
		experienceIdDetails.setCompanyName(newInternDetails.getCompanyName());
		experienceIdDetails.setCompanyPlace(newInternDetails.getCompanyPlace());
		experienceIdDetails.setIssueDate(newInternDetails.getIssueDate());
		experienceIdDetails.setCompanyLogo(newInternDetails.getCompanyLogo());
		experienceIdDetails.setCompanyName(newInternDetails.getCompanyName());
		experienceIdDetails.setCompanyPlace(newInternDetails.getCompanyPlace());
		experienceIdDetails.setSignRole(newInternDetails.getSignRole());
		experienceIdDetails.setSign(newInternDetails.getSign());
		experienceIdDetails.setNameOfSign(newInternDetails.getNameOfSign());
		experienceIdDetails.setDescription(newInternDetails.getDescription());
		final ExperienceLetterDetails details = experienceLetterService.updateExperienceLetter(experienceIdDetails);
		if (details != null) {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA, "internshipLetter Status Updated Successfully");
		} else {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "InternshipLetter Status Update Failed");
		}
		response = new Gson().toJson(jsonObject);
		logger.debug("ExperienceDetailsApiService(updateJobStatus) >> Response:" + response);
		
		
	} catch(Exception e) {
		Sentry.captureException(e);
		logger.info("Exception occured in ExperienceletterApiService(updateInternshipLetter)<<"+e);
		jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
		jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
		jsonObject.put(RestConstants.DATA, "Error during Job status update");
		response = new Gson().toJson(jsonObject);
	}
	logger.info("experienceDetailsApiService(updateJobStatus) >> Exit");
	return response;

}

@GetMapping(value="/getById/{id}",headers="Accept=application/json")
public String getByInternshipId(@PathVariable final Long id) {
	logger.info("getExperienceDetails(getByExperienceById)<<Entry");
	String response ="";
	final JSONObject jsonObject = new JSONObject();
	try {
		logger.info("getExperinceDetailById(getByExperienceId)");
		final ExperienceLetterDetails details = experienceLetterService.getById(id);
		if(details != null) {
			jsonObject.put(RestConstants.STATUS_CODE,RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE,RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
		} else {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting job details by id");
		}
		response = new Gson().toJson(jsonObject);
	} catch(Exception e) {
//		System.out.println(e);
		Sentry.captureException(e);
		logger.error("Exception occured in (getInternshipDetailById) and Exception details >> " + e);
		jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
		jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
		jsonObject.put(RestConstants.DATA, "Error in extracting  job details by id");
		response = new Gson().toJson(jsonObject);
	}
	logger.info("getInternshipLetterById(getByInternshipId)<<exit");
	return response;
	
}


@PutMapping(value ="/bulkDelete" , headers = "Accept=application/json")
public String BulkDelete(@RequestBody final String details) {
	logger.info("InternshipDetailsApiService(bulkDelete) >> Entry");
	String response ="";
	final JSONObject jsonObject = new JSONObject();
	try {
		Integer check = 0;
		final JSONObject newJsonObject = new JSONObject(details);
		JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
		System.out.println(deleteIds);
		for(int i=0;i< deleteIds.length();i++) {
			Long id = deleteIds.getLong(i);
			final ExperienceLetterDetails oldDetails=experienceLetterService.getById(id);
			oldDetails.setIs_deleted(true);
			final ExperienceLetterDetails newDetails = experienceLetterService.DeleteAllSelectExperienceLetter(oldDetails);
			if(newDetails != null) {
				check +=1;
			}
					
	}
		if ( check == deleteIds.length()) {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA, "ExperienceLetter Details bulk deleted successfully");
		} else {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting Internship details");
		}
		response = new Gson().toJson(jsonObject);
		
	} catch(Exception e) {
		Sentry.captureException(e);
		e.printStackTrace();
		logger.error("Exception occured in ExperienceDetailsApiService(bulkDelete) and Exception details >> " + e);
		jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
		jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
		jsonObject.put(RestConstants.DATA, "Error in deleting Internship details");
		response = new Gson().toJson(jsonObject);
	}
	logger.info("experienceDetailsApiService(bulkDelete) >> Exit");
	return response;
		
	}


}
