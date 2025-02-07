package com.tcube.api.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.tcube.api.model.ActionCards;
import com.tcube.api.model.ManageAttendance;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.service.ManageAttendanceService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/manageAttendance" })
public class ManageAttendanceApiService {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(ManageAttendanceApiService.class);

	@Autowired
	ManageAttendanceService manageAttendanceService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createActionCard(@RequestParam(value = "org_id") Long org_id,
			@RequestParam(value = "action_type") String action_type, @RequestParam(value = "action") String action,
			@RequestParam(value = "current_section") String current_section,
			@RequestParam(value = "next_section") String next_section,
			@RequestParam(value = "action_img") MultipartFile action_img) {
		logger.info("ManageAttendanceApiService(createActionCard) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newjsonObject = new JSONObject();

			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(org_id);
			newjsonObject.put("action_image", ImageProcessor.compressBytes(action_img.getBytes()));
			newjsonObject.put("action_type", action_type);
			newjsonObject.put("action", action);
			newjsonObject.put("current_section", current_section);
			newjsonObject.put("next_section", next_section);
			final ManageAttendance details = MapperUtil.readAsObjectOf(ManageAttendance.class,
					newjsonObject.toString());
			details.setOrgDetails(orgDetails);
			ManageAttendance data = manageAttendanceService.createattendancecard(details);
			if (data != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Manage Attendance details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in create Manage Attendance details");
			}
			response = new Gson().toJson(jsonObject);
//			logger.debug("ManageAttendanceApiService(createActionCard) >> Response :" + response);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in create Manage Attendance details");
			response = new Gson().toJson(jsonObject);
			logger.error(
					"Exception occured in ManageAttendanceApiService(createActionCard) and Exception details >> "
							+ e);
		}
		logger.info("ManageAttendanceApiService(createActionCard) >> Exit");
		return response;
	}

	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateActionCard(@RequestParam(value = "id") Long id, @RequestParam(value = "org_id") Long org_id,
			@RequestParam(value = "action_type") String action_type, @RequestParam(value = "action") String action,
			@RequestParam(value = "current_section") String current_section,
			@RequestParam(value = "next_section") String next_section,
			@RequestParam(value = "action_img", required = false) MultipartFile action_img) {
		logger.info("ManageAttendanceApiService(updateActionCard) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newjsonObject = new JSONObject();

			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(org_id);
			newjsonObject.put("id", id);
			final ManageAttendance actiondata = manageAttendanceService.getattendancecardById(id);
			if(action_img!=null) {
				newjsonObject.put("action_image", ImageProcessor.compressBytes(action_img.getBytes()));
			}
			else {
				newjsonObject.put("action_image", actiondata.getAction_image());
			}
			newjsonObject.put("action_type", action_type);
			newjsonObject.put("action", action);
			newjsonObject.put("current_section", current_section);
			newjsonObject.put("next_section", next_section);
			final ManageAttendance details = MapperUtil.readAsObjectOf(ManageAttendance.class,
					newjsonObject.toString());
			details.setOrgDetails(orgDetails);
			details.setDelete(false);
			details.setId(id);
			ManageAttendance data = manageAttendanceService.updateattendancecard(details);
			if (data != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Manage Attendance details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in update Manage Attendance details");
			}
			response = new Gson().toJson(jsonObject);
//			logger.debug("ManageAttendanceApiService(updateActionCard) >> Response :" + response);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in update Manage Attendance details");
			response = new Gson().toJson(jsonObject);
			logger.error(
					"Exception occured in ManageAttendanceApiService(updateActionCard) and Exception details >> "
							+ e);
		}
		logger.info("ManageAttendanceApiService(updateActionCard) >> Exit");
		return response;
	}

	// Delete(soft delete) //
	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteActioncard(@PathVariable(value = "id") Long id) {
		logger.info("ManageAttendanceApiService(deleteActioncard) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final ManageAttendance data = manageAttendanceService.getattendancecardById(id);
			data.setDelete(true);
			final ManageAttendance details = manageAttendanceService.updateattendancecard(data);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Action card details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting Action card details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ManageAttendanceApiService(deletClientDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting Action card details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageAttendanceApiService(deleteActioncard) >> Exit");
		return response;
	}

	// This service for get all ActionCard details by org_id
	@GetMapping(value = "/getAllActionCardDetailsByOrgId/{org_id}", headers = "Accept=application/json")
	public String getAllActionCardDetailsByOrgId(@PathVariable(value = "org_id") final Long org_id) {
		logger.info("ManageAttendanceApiService(getAllActionCardDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ManageAttendanceApiService(getAllActionCardDetailsByOrgId)");

			/*
			 * Get All details service method call
			 */
			final List<ActionCards> details = manageAttendanceService.getAllattendancecardByOrgId(org_id);
			for(ActionCards i:details) {
				if(i.getAction_image()!=null) {
					i.setAction_image(ImageProcessor.decompressBytes(i.getAction_image()));
				}
			}
			if (details != null ) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
				jsonObject.put(RestConstants.DATA, details);

			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting Action card getall details by Org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ManageAttendanceApiService(getAllAttendanceDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting Action card get all details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageAttendanceApiService(getAllAttendanceDetailsByOrgId) >> Exit");
		return response;
	}
	
	   @GetMapping(value = "/getactioncardsbyid/{id}", headers = "Accept=application/json")
	    public String getActionCardByid(@PathVariable(value = "id") Long id) {
	        logger.info("ManageAttendanceApiService(getActionCardByid) >> Entry");
	        String response = "";
	        final JSONObject jsonObject = new JSONObject();
	        try {
	            JSONObject newjsonObject = new JSONObject();
	            final ManageAttendance actiondata = manageAttendanceService.getattendancecardById(id);
	            actiondata.setAction_image(ImageProcessor.decompressBytes(actiondata.getAction_image()));
	            if (actiondata != null) {
	                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
	                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
	                jsonObject.put(RestConstants.DATA, new Gson().toJson(actiondata));
	            } else {
	                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
	                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
	                jsonObject.put(RestConstants.DATA, "No action card by id");
	            }
	            response = new Gson().toJson(jsonObject);
//	            logger.debug("ManageAttendanceApiService(getActionCardByid) >> Response :" + response);
	        } catch (Exception e) {
	        	Sentry.captureException(e);
//	            System.out.println(e);
	            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
	            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
	            jsonObject.put(RestConstants.DATA, "Error in getting action card details by id");
	            response = new Gson().toJson(jsonObject);
	            logger.error(
						"Exception occured in ManageAttendanceApiService(getActionCardByid) and Exception details >> "
								+ e);
	        }
	        logger.info("ManageAttendanceApiService(getActionCardByid) >> Exit");
	        return response;
	    }

}
	

