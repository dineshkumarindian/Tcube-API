package com.tcube.api.controller;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Component;
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
import com.tcube.api.model.CustomInternshipDetails;
import com.tcube.api.model.DashboardOfferLetter;
import com.tcube.api.model.OfferLetterDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.service.OfferLetterService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/OfferLetterDetails" })
public class OfferLetterApiService {

	private static Logger logger = LogManager.getLogger(OfferLetterService.class);

	@Autowired
	OfferLetterService offerLetterService;

	@Autowired
	OrgDetailsService orgDetailService;

	@PostMapping(value = "/add-offer", headers = "Accept=application/json")
	public String createOfferLetterDetails(@RequestBody String offerLetterDetails) {
		logger.info("OfferLetterApiService(createOfferLetterDetails)>>Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(offerLetterDetails);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			final OrgDetails orgDetails = orgDetailService.getOrgDetailsById(orgId);
			logger.debug("OfferLetterApiService(createOfferLetterDetails) >> Request :" + offerLetterDetails);
			final OfferLetterDetails offerDetails = MapperUtil.readAsObjectOf(OfferLetterDetails.class,newJsonObject.toString());
			offerDetails.setOrgDetails(orgDetails);
			offerDetails.setIs_deleted(false);
			final OfferLetterDetails details = offerLetterService.createOfferLetter(offerDetails);
			if (details!= null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, details);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating new details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("OfferLetterApiService(createOfferLetterDetails) >> Response :" + response);

		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in offerLetterService(createOfferLetterDetails) and Exception details >>" + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error ocurs while creating the offerletter details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OfferLetterApiService(createOfferLetterDetails) >> Exit");
		return response;
	}

	@GetMapping(value="/getAllDetails/{id}",headers="Accept=application/json")
	public String getAllOfferLetterDetails(@PathVariable final  Long id){
		logger.info("OfferLetterApiService(getAllOfferLetterDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("OfferLetterApiService(getAllOfferLetterDetails)");
        	final List<OfferLetterDetails> details = offerLetterService.getAllOfferLetterDetails(id);
        	final List<OfferLetterDetails> newDetails = new ArrayList<>();
          	for(OfferLetterDetails i : details) {
				if(i.getIs_deleted() == false) {
					newDetails.add(i);
				}
        	}
        	if (newDetails != null && newDetails.size()>0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			logger.debug("OfferLetterApiService(getAllOfferLetterDetails) >> Request");
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OfferLetterApiService(getAllOfferLetterDetails) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting  offer letter details");
			response = new Gson().toJson(jsonObject);
		}
        logger.info("OfferLetterApiService(getAllOfferLetterDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getActiveEmpWithOfferByOrgId/{id}", headers = "Accept=application/json")
	public String getActiveEmpWithOfferByOrgId(@PathVariable final Long id,final UriComponentsBuilder ucBuilder) {
		logger.info("OfferLetterApiService(getActiveEmpWithOfferByOrgId)>>Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("OfferLettterApiService(getActiveEmpWithOfferByOrgId)");
			final List<OfferLetterDetails> details = offerLetterService.getActiveEmpWithOfferByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get offerletter details by Org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch(Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in OfferLetterApiService(getActiveEmpWithOfferByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  offerletter details by Org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OfferLetterApiService(getActiveEmpWithOfferByOrgId) >> Exit");
		return response;
	}
	
	@GetMapping(value="/getActiveOfferLetterLength/{id}",headers="Accept=application/json")
	public String getOfferLetterCount(@PathVariable final  Long id){
		logger.info("OfferLetterApiService(getOfferLetterCount) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("OfferLetterApiService(getOfferLetterCount)");
        	final List<OfferLetterDetails> details = offerLetterService.getOfferLetterCount(id);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			logger.debug("OfferLetterApiService(getOfferLetterCount) >> Request");
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OfferLetterApiService(getOfferLetterCount) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting  offer letter length");
			response = new Gson().toJson(jsonObject);
		}
        logger.info("OfferLetterApiService(getOfferLetterCount) >> Exit");
		return response;
	}
	
	@GetMapping(value="/getOfferLetter/{id}",headers="Accept=application/json")
	public String getAddedOfferLetter(@PathVariable final  Long id){
		logger.info("OfferLetterApiService(getAddedOfferLetter) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("OfferLetterApiService(getAddedOfferLetter)");
        	final List<DashboardOfferLetter> details = offerLetterService.getAddedOfferLetter(id);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			logger.debug("OfferLetterApiService(getAddedOfferLetter) >> Request");
        	response = new Gson().toJson(jsonObject);
        	
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OfferLetterApiService(getAddedOfferLetter) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting  offer letter details");
			response = new Gson().toJson(jsonObject);
		}
        logger.info("OfferLetterApiService(getAddedOfferLetter) >> Exit");
		return response;
	}
	
	
	@GetMapping(value="/getById/{id}",headers ="Accept=application/json")
	public String getOfferLetterbyId(@PathVariable final Long id) {
		logger.info("OfferLetterApiService(getOfferLetterbyId) >> Entry");
		String response ="";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("OfferLetterApiService(getOfferLetterbyId)");
			final OfferLetterDetails details = offerLetterService.getById(id);
			if(details != null) {
				jsonObject.put(RestConstants.STATUS_CODE,RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE,RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to extract offerletter details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in (getOfferLetterbyId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  offerletter details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OfferLetterApiService(getOfferLetterbyId)<<exit");
		return response;
		
	}


	@PutMapping(value="/deleteOfferLetter/{id}", headers = "Accept=appllication/json")
	public String deleteOfferLetter(@PathVariable(value ="id")Long id) {
		logger.info("OfferLetterApiService(deleteOfferLetter)<< Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final OfferLetterDetails oldDetails = offerLetterService.getById(id);
			oldDetails.setIs_deleted(true);
			logger.info("OfferLetterApiService(deleteOfferLetter) >> Entry");
			final OfferLetterDetails details = offerLetterService.getDeleteOfferLetterDetails(oldDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Offerletter details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to delete offerletter details");
			}
			response = new Gson().toJson(jsonObject);
		} catch(Exception e) {
			Sentry.captureException(e);
			logger.info("Exception occured in offerletterApiService(deleteOfferLetter)<<"+e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting offerletter details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OfferLetterApiService(deleteOfferLetter)>>Exit");
		return response;
	}
	
	@SuppressWarnings("unused")
	@PutMapping(value="/updateOfferLetter/{id}",headers="Accept=application/json")
	public String updateOfferLetterDetails(@PathVariable(value="id")Long id,@RequestBody String body)
	{
		logger.info("OfferLetterApiService(updateOfferLetterDetails)>> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("OfferLetterApiService(updateOfferLetterDetails)<<Entry");
			final JSONObject newJsonObject = new JSONObject(body);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			newJsonObject.remove("fieldList");
			final OrgDetails orgDetails = orgDetailService.getOrgDetailsById(orgId);
			final OfferLetterDetails offerIdDetails = offerLetterService.getById(id);
			
			final OfferLetterDetails newOfferDetails = MapperUtil.readAsObjectOf(OfferLetterDetails.class, newJsonObject.toString());
//			offerIdDetails.setLetterTitle1(newOfferDetails.getLetterTitle1());
//			offerIdDetails.setDescription1(newOfferDetails.getDescription1());
			
			offerIdDetails.setOrgDetails(orgDetails);
			offerIdDetails.setName(newOfferDetails.getName());
			offerIdDetails.setDob(newOfferDetails.getDob());
			offerIdDetails.setCompanyName(newOfferDetails.getCompanyName());
			offerIdDetails.setCompanyLogo(newOfferDetails.getCompanyLogo());
			offerIdDetails.setCompanyLink(newOfferDetails.getCompanyLink());
			offerIdDetails.setCompanyAddress(newOfferDetails.getCompanyAddress());
//			offerIdDetails.setDesignation(newOfferDetails.getDesignation());
			offerIdDetails.setSignatureName(newOfferDetails.getSignatureName());
			offerIdDetails.setSignature(newOfferDetails.getSignature());
			offerIdDetails.setSignatureRole(newOfferDetails.getSignatureRole());
			offerIdDetails.setLogoFileName(newOfferDetails.getLogoFileName());
			offerIdDetails.setSignFileName(newOfferDetails.getSignFileName());
			offerIdDetails.setLetterTitle(newOfferDetails.getLetterTitle());
			offerIdDetails.setAnnexure_details(newOfferDetails.getAnnexure_details());
//			offerIdDetails.setDoj(newOfferDetails.getDoj());
			offerIdDetails.setDescription(newOfferDetails.getDescription());
//			offerIdDetails.setSalary(newOfferDetails.getSalary());
			offerIdDetails.setOfferLetterPdfFormat(newOfferDetails.getOfferLetterPdfFormat());
			final OfferLetterDetails details = offerLetterService.updateOfferLetterDetails(offerIdDetails);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, details);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to update offerletter");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("OfferletterApiService(updateOfferLetterDetails) >> Response:" + response);
		} catch (Exception e) 
		{
			Sentry.captureException(e);
			logger.info("Exception occured in offerletterApiService(updateOfferLetterDetails)<<"+e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating offerletter");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OfferletterApiService(updateOfferLetterDetails) >> Exit");
		return response;
		
	}

	@PutMapping(value ="/bulkDelete" , headers = "Accept=application/json")
	public String bulkOfferLetterDelete(@RequestBody final String details) {
		logger.info("OfferletterApiService(bulkOfferLetterDelete) >> Entry");
		String response ="";
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			for(int i = 0; i< deleteIds.length(); i++) {
				Long id = deleteIds.getLong(i);
				final OfferLetterDetails oldDetails = offerLetterService.getById(id);
				oldDetails.setIs_deleted(true);
				final OfferLetterDetails newDetails = offerLetterService.deleteAllOfferLetterDetails(oldDetails);
				if(newDetails != null) {
					check +=1;
				}
			}
			if ( check == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Offer Letters bulk deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting  offerletter details");
			}
			response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in OfferLetterApiService(bulkOfferLetterDelete) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting offerletter details");
			response = new Gson().toJson(jsonObject);
			
		}
		logger.info("OfferLetterApiService(bulkOfferLetterDelete) >> Exit");
		return response;
	}

//	@SuppressWarnings("unused")
//	@PutMapping(value="/updateOfferLetterpdf/{id}",headers="Accept=application/json")
//	public String updateOfferLetterPdfDetails(@PathVariable(value="id")Long id,@RequestBody byte[] body)
//	{
//		logger.info("update the updateOfferLetterDetailService(updateOfferLetterPdfDetails)>> Entry");
//		String response = "";
//		final JSONObject jsonObject = new JSONObject();
//		try {
////			System.out.println(body);
////			final JSONObject newJsonObject = new JSONObject(body);
//			final OfferLetterDetails offerIdDetails = offerLetterService.getById(id);
//			
////			final OfferLetterDetails newOfferDetails = MapperUtil.readAsObjectOf(OfferLetterDetails.class, newJsonObject.toString());
//			offerIdDetails.setOfferLetterPdfFormat(body);
//			final OfferLetterDetails details = offerLetterService.updateOfferLetterDetails(offerIdDetails);
//        	if (details != null) {
//				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//				jsonObject.put(RestConstants.DATA, details);
//			} else {
//				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//				jsonObject.put(RestConstants.DATA, "Error in updating offerletter");
//			}
//			response = new Gson().toJson(jsonObject);
//			logger.debug("offerletterApiService(updateOfferLetterPdfDetails) >> Response:" + response);
//		} catch (Exception e) 
//		{
//			logger.info("Exception occured in offerletterApiService(updateOfferLetterPdfDetails)<<"+e);
//			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
//			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//			jsonObject.put(RestConstants.DATA, "Error in updating offerletter");
//			response = new Gson().toJson(jsonObject);
//		}
//		logger.info("offerletterApiService(updateOfferLetterPdfDetails) >> Exit");
//		return response;
//		
//	}




}

