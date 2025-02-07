package com.tcube.api.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

import com.google.gson.Gson;
import com.tcube.api.model.PricingPlanDetails;
import com.tcube.api.service.PricingPlanService;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/plandetails" })
public class PricingPlanDetailsApiService {

	private static Logger logger=  LogManager.getLogger(PricingPlanDetailsApiService.class);
	
	@Autowired
	PricingPlanService pricingPlanService;
	
	//plan details create method
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createPricingPlanDetails(@RequestBody String details) {
		logger.info("PricingPlanDetailsApiService(createPricingPlanDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			final PricingPlanDetails planDetails = MapperUtil.readAsObjectOf(PricingPlanDetails.class,newJsonObject.toString());
			final PricingPlanDetails newdetails = pricingPlanService.create(planDetails);
			if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Pricing plan details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating Pricing plan details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in PricingPlanDetailsApiService(createPricingPlanDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating Pricing plan details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("PricingPlanDetailsApiService(createPricingPlanDetails) >> Exit");
		return response;
	}
	
	//get all pricing plan details
	@GetMapping(value = "/getall", headers = "Accept=application/json")
	public String getAllPricingPlanDetails() {
		logger.info("PricingPlanDetailsApiService(getAllPricingPlanDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject();
			logger.info("PricingPlanDetailsApiService(getAllPricingPlanDetails)");
			final List<PricingPlanDetails> details = pricingPlanService.getAllPlanDetails();
			if (details.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
				
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "No data Found");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in PricingPlanDetailsApiService(getAllPricingPlanDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting get all plan details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("PricingPlanDetailsApiService(getAllPricingPlanDetails) >> Exit");
		return response;
	}
	
	//get pricing plan details by id
	@GetMapping(value = "/getplan/{id}", headers = "Accept=application/json")
	public String getPricingPlanDetailById(@PathVariable("id") final Long id) {
		logger.info("PricingPlanDetailsApiService(getPricingPlanDetailById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("PricingPlanDetailsApiService(getPricingPlanDetailById)");
			final PricingPlanDetails details = pricingPlanService.getPlanDetailsById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "No data Found");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in PricingPlanDetailsApiService(getPricingPlanDetailById) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting get plan details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("PricingPlanDetailsApiService(getPricingPlanDetailById) >> Exit");
		return response;
	}
	
	//update pricing plan details
	@PutMapping(value = "/updateplan", headers = "Accept=application/json")
	public String updatePlanDetails(@RequestBody final String request) {
		logger.info("PricingPlanDetailsApiService(updatePlanDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");

			final PricingPlanDetails oldDetails = pricingPlanService.getPlanDetailsById(Id);
			final PricingPlanDetails newDetails = MapperUtil.readAsObjectOf(PricingPlanDetails.class,
					newJsonObject.toString());
			oldDetails.setPlan(newDetails.getPlan());
			oldDetails.setCurrency(newDetails.getCurrency());
			oldDetails.setAmount(newDetails.getAmount());
			oldDetails.setModules(newDetails.getModules());
			oldDetails.setDays(newDetails.getDays());
			oldDetails.setUserslimit(newDetails.getUserslimit());
			oldDetails.setDesc(newDetails.getDesc());
			final PricingPlanDetails details = pricingPlanService.updatePlanDetails(oldDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "pricing plan details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to update pricing plan details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in PricingPlanDetailsApiService(updatePlanDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating  pricing plan details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("PricingPlanDetailsApiService(updatePlanDetails) >> Exit");
		return response;
	}
	
	    //soft delete pricing plan details
		@PutMapping(value = "/softdelete/{id}", headers = "Accept=application/json")
		public String softDeletePlanDetail(@PathVariable("id") final Long id) {
			logger.info("PricingPlanDetailsApiService(softDeletePlanDetail) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {
				final PricingPlanDetails oldDetails = pricingPlanService.getPlanDetailsById(id);
				oldDetails.setIs_deleted(true);
				final PricingPlanDetails details = pricingPlanService.updatePlanDetails(oldDetails);
				if (details != null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "pricing plan details soft deleted successfully");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA, "Failed to soft delete pricing plan details");
				}
				response = new Gson().toJson(jsonObject);
			} catch (Exception e) {
				Sentry.captureException(e);
				logger.error(
						"Exception occured in PricingPlanDetailsApiService(softDeletePlanDetail) and Exception details >> "
								+ e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in soft delete  pricing plan details");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("PricingPlanDetailsApiService(softDeletePlanDetail) >> Exit");
			return response;
		}
		
		//bulk delete
		@PutMapping(value = "/bulkdelete", headers = "Accept=application/json")
		public String bulkDeletePlanDetail(@RequestBody final String request) {
			logger.info("PricingPlanDetailsApiService(bulkDeletePlanDetail) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {
				final JSONObject newJsonObject = new JSONObject(request);
				JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
				
				final int details = pricingPlanService.bulkDelete(deleteIds);
				if (details == deleteIds.length()) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "pricing plan details bulk deleted successfully");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA, "Failed to bulk delete pricing plan details");
				}
				response = new Gson().toJson(jsonObject);
			} catch (Exception e) {
				Sentry.captureException(e);
				logger.error(
						"Exception occured in PricingPlanDetailsApiService(bulkDeletePlanDetail) and Exception details >> "
								+ e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in bulk delete pricing plan details");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("PricingPlanDetailsApiService(bulkDeletePlanDetail) >> Exit");
			return response;
		}
}
