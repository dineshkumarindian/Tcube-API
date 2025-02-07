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
//import com.tcube.api.model.CustomJobsDetails;
import com.tcube.api.model.BusinessLetterDetails;
//import com.tcube.api.model.JobDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

import com.tcube.api.service.BusinessLetterService;
import com.tcube.api.model.CustomInternshipDetails;
import com.tcube.api.model.DashboardBusinessLetter;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.OfferLetterDetails;

@Component
@RestController
@CrossOrigin(origins = "*" , allowedHeaders ="*")
@RequestMapping(value = { "/api/businessDetails" })
public class BusinessLetterApiService {

	private static Logger logger = LogManager.getLogger(BusinessLetterService.class);
	
	@Autowired
	BusinessLetterService businessLetterService;
	
	@Autowired
	OrgDetailsService orgDetailService;
	
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createBusinessDetails(@RequestBody String internshipDetails) {
		logger.info("businessDetailsApiService(createBusinessDetails)<<Entry");
		String response= "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(internshipDetails);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			final OrgDetails orgDetails = orgDetailService.getOrgDetailsById(orgId);
			final BusinessLetterDetails internDetails = MapperUtil.readAsObjectOf(BusinessLetterDetails.class,newJsonObject.toString());
			internDetails.setOrgDetails(orgDetails);
 			final BusinessLetterDetails details = businessLetterService.createBusinessLetter(internDetails);
			if(details!=null) {
				jsonObject.put(RestConstants.STATUS_CODE,RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE,RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA,"New Task created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data creating new task details");
			}
			response = new Gson().toJson(jsonObject);
			} catch(Exception e) {
				Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in businessLetterService(createInternshipLetter) and Exception details >>"+e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating new task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("businessDetailsApiService(createBusinessDetails) >> Exit");
		return response;
	}
	
	@GetMapping(value ="/getall/{id}",headers ="Accept=application/json")
	public String getAllBusinessDetails(@PathVariable final Long id) {
		logger.info("businessDetailsApiService(getAllBusinessDetails)>>Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final List<BusinessLetterDetails> details = businessLetterService.getAllBusinessDetails(id);
			final List<BusinessLetterDetails> newDetails = new ArrayList<>();
				for(BusinessLetterDetails i : details) {
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
			logger.debug("businessDetailsApiService(getAllBusinessDetails) >> Request");
			response = new Gson().toJson(jsonObject);
			
		} catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in businessLetterApiServcie(getAllBusinessDetails) and Exception details >> " + e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting internshipdetails");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("businessDetailsApiService(getAllBusinessDetails) >> Exit");
		return response;
	}
	
	@GetMapping(value="/getById/{id}",headers ="Accept=application/json")
	public String getByBusinessId(@PathVariable final Long id) {
		logger.info("businessDetailsApiService(getByBusinessId)<<Entry");
		String response ="";
		final JSONObject jsonObject = new JSONObject();
		try {
			final BusinessLetterDetails details = businessLetterService.getById(id);
			if(details.getLogoImage() != null && details.getSignImage()!= null) {
				
				details.setLogoImage(ImageProcessor.decompressBytes(details.getLogoImage()));
				details.setSignImage(ImageProcessor.decompressBytes(details.getSignImage()));
			}
			if(details != null) {
				jsonObject.put(RestConstants.STATUS_CODE,RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE,RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data Error in extracting Businessdetails details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in (getBusinessDetailById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  Businessdetails by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("businessDetailsApiService(getByBusinessId)<<exit");
		return response;
		
	}
	
	@PutMapping(value="/delete/{id}", headers = "Accept=appllication/json")
	public String deleteBusinessLetter(@PathVariable(value ="id")Long id) {
		logger.info("businessDetailsApiService(deleteBusinessLetter)<< Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final BusinessLetterDetails oldDetails = businessLetterService.getById(id);
			oldDetails.setIs_deleted(true);
			final BusinessLetterDetails details = businessLetterService.getDeleteBusinessDetail(oldDetails);
			
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "businessletter details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data Error in deleting businessLetter details");
			}
			response = new Gson().toJson(jsonObject);
		} catch(Exception e) {
			Sentry.captureException(e);
			logger.info("Exception occured in BusinessletterApiService(deleteInternshipLetter)<<"+e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting internship details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("businessDetailsApiService(deleteBusinessLetter)>>Exit");
		return response;
	}
	

	@SuppressWarnings("unused")
	@PutMapping(value="/update/{id}",headers="Accept=application/json")
	public String updateBusinessDetail(@PathVariable(value="id")Long id,@RequestBody String body)
	{
		logger.info("businessDetailsApiService(updateBusinessDetail)>> entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(body);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			final OrgDetails orgDetails = orgDetailService.getOrgDetailsById(orgId);
			final BusinessLetterDetails internIdDetails = businessLetterService.getById(id);
			final BusinessLetterDetails newInternDetails = MapperUtil.readAsObjectOf(BusinessLetterDetails.class, newJsonObject.toString());
			internIdDetails.setOrgDetails(orgDetails);
			internIdDetails.setAddress(newInternDetails.getAddress());
//			internIdDetails.setCompanyLogo(newInternDetails.getCompanyLogo());
			internIdDetails.setLogoImage(newInternDetails.getLogoImage());
			internIdDetails.setDescription(newInternDetails.getDescription());
			internIdDetails.setDirectorName(newInternDetails.getDirectorName());
//			internIdDetails.setDirectorSign(newInternDetails.getDirectorSign());
			internIdDetails.setSignImage(newInternDetails.getSignImage());
			internIdDetails.setDoj(newInternDetails.getDoj());
			internIdDetails.setInternPdfFormat(newInternDetails.getInternPdfFormat());
			internIdDetails.setName(newInternDetails.getName());
			internIdDetails.setProgram_title(newInternDetails.getProgram_title());
			internIdDetails.setCompanyName(newInternDetails.getCompanyName());
			internIdDetails.setCompanyAddress(newInternDetails.getCompanyAddress());
			internIdDetails.setCompanySite(newInternDetails.getCompanySite());
			internIdDetails.setSignatureRole(newInternDetails.getSignatureRole());
			internIdDetails.setLogoFileName(newInternDetails.getLogoFileName());
			internIdDetails.setSignFileName(newInternDetails.getSignFileName());
			internIdDetails.setPdfFileLink(newInternDetails.getPdfFileLink());		
			internIdDetails.setToday_Date(newInternDetails.getToday_Date());
			final BusinessLetterDetails details = businessLetterService.updateBusinessDetail(internIdDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "BusinessLetter Status Updated Successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "BusinessLetter Status Update Failed");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) 
		{
			Sentry.captureException(e);
			logger.info("Exception occured in internshipletterApiService(updateInternshipLetter)<<"+e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error during Business status update");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("businessDetailsApiService(updateBusinessDetail) >> Exit");
		return response;
		
	}
	
	@GetMapping(value = "/getActiveBusinessByOrgId/{id}", headers = "Accept=application/json")
	public String getActiveBusinessDetailsByOrgId(@PathVariable final Long id,final UriComponentsBuilder ucBuilder) {
		logger.info("businessDetailsApiService(getActiveBusinessByOrgId)>>Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final List<BusinessLetterDetails> details = businessLetterService.getActiveOrgIdBusinessDetail(id);
			List<BusinessLetterDetails> newDetails = new ArrayList();
			for(BusinessLetterDetails i : details) {
				if(i.getLogoImage() !=null && i.getSignImage() != null) {
					i.setLogoImage(ImageProcessor.decompressBytes(i.getLogoImage()));
					i.setSignImage(ImageProcessor.decompressBytes(i.getSignImage()));
					newDetails.add(i);
				} else {
					newDetails.add(i);
				}
			}
			if (newDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get Business letter details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch(Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in BusinessDetailsApiService(getActiveBusinessDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  job details by Org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("businessDetailsApiService(getActiveBusinessByOrgId) >> Exit");
		return response;
	}
	
	@PutMapping(value ="/bulkDelete" , headers = "Accept=application/json")
	public String BulkDelete(@RequestBody final String details) {
		logger.info("BusinessDetailsApiService(BulkDelete) >> Entry");
		String response ="";
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			for(int i = 0; i< deleteIds.length(); i++) {
				Long id = deleteIds.getLong(i);
				final BusinessLetterDetails oldDetails = businessLetterService.getById(id);
				oldDetails.setIs_deleted(true);
//				Long OrgId = oldDetails.getOrgDetails().getOrg_id();
//				List<InternshipLetterDetails>  = businessLetterService.getActiveOrgIdInternsipDetail(OrgId);
				final BusinessLetterDetails newDetails = businessLetterService.deleteAllBusinessDetails(oldDetails);
				if(newDetails != null) {
					check +=1;
				}
			}
			if ( check == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "BusinessLetter Dedails bulk deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting Business details");
			}
			response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in BulkDelete(bulkDelete) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting Business details");
			response = new Gson().toJson(jsonObject);
			
		}
		logger.info("businessDetailsApiService(BulkDelete) >> Exit");
		return response;
	}
	
	@PutMapping(value = "/getActiveBusinessDetailsByOrgIdNew", headers = "Accept=application/json")
	public String getActiveBusinessDetailsByOrgId_new(@RequestBody String data) {
		logger.info("businessDetailsApiService(getActiveBusinessDetailsByOrgIdNew) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(data);
				long orgId = newJsonObject.getLong("org_id");
				newJsonObject.remove("org_id");
				final List<CustomInternshipDetails> details = businessLetterService.getActiveOrgIdBusinessDetailNew(orgId);
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
			
		} catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in BusinessDetailsApiService(getActiveBusinessDetailsByOrgId) and Exception details >> "+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  Business details by Org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("BusinessDetailsApiService(getActiveBusinessDetailsByOrgIdNew) >> Exit");
		return response;
		
	}
	@GetMapping(value="/getActiveBusinessLetterLength/{id}",headers="Accept=application/json")
	public String getBusinessLetterCount(@PathVariable final  Long id){
		logger.info("BusinessDetailsApiService(getBusinessLetterCount) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	final List<BusinessLetterDetails> details = businessLetterService.getBusinessLetterCount(id);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			logger.debug("BusinessDetailsApiService(getBusinessLetterCount) >> Request");
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in BusinessDetailsApiService(getBusinessLetterCount) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting  business letter count");
			response = new Gson().toJson(jsonObject);
		}
        logger.info("BusinessDetailsApiService(getBusinessLetterCount) >> Exit");
		return response;
	}
	
	@GetMapping(value="/getBusinessLetter/{id}",headers="Accept=application/json")
	public String getAddedBusinessLetter(@PathVariable final  Long id){
		logger.info("BusinessDetailsApiService(getAddedBusinessLetter) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("BusinessDetailsApiService(getAddedBusinessLetter)");
        	final List<DashboardBusinessLetter> details = businessLetterService.getAddedBusinessLetter(id);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			logger.debug("BusinessDetailsApiService(getAddedBusinessLetter) >> Request");
        	response = new Gson().toJson(jsonObject);
        	
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in BusinessDetailsApiService(getAddedBusinessLetter) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting  business letter details");
			response = new Gson().toJson(jsonObject);
		}
        logger.info("BusinessDetailsApiService(getAddedBusinessLetter) >> Exit");
		return response;
	}
	
	
}
