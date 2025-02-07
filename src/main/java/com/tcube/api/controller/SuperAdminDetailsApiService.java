package com.tcube.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.SuperAdminDetails;
import com.tcube.api.service.SuperAdminDetailsService;
import com.tcube.api.utils.EncryptorUtil;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/SuperAdminDetails" })
public class SuperAdminDetailsApiService {
	

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger=  LogManager.getLogger(SuperAdminDetailsApiService.class);

	@Autowired
	SuperAdminDetailsService superAdminDetailsService;

	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createSuperAdminDetail(@RequestBody String detailsOfAdmin) {
		logger.info("SuperAdminDetailsApiService(createSuperAdminDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfAdmin);
			final SuperAdminDetails admindetails = MapperUtil.readAsObjectOf(SuperAdminDetails.class,
					newJsonObject.toString());
			admindetails.setIs_deleted(false);
			final SuperAdminDetails details = superAdminDetailsService.createSuperAdminDetails(admindetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Super admin details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating super admin details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in superAdminDetailsService(createSuperAdminDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating super admin details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("SuperAdminDetailsApiService(createSuperAdminDetail) >> Exit");
		return response;
	}

	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateSuperAdminDetail(@RequestBody final String detailsOfAdmin) {
		logger.info("SuperAdminDetailsApiService(updateSuperAdminDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfAdmin);
			Long adminId = newJsonObject.getLong("id");
			newJsonObject.remove("id");

			final SuperAdminDetails oldAdminDetails = superAdminDetailsService.getAdminDetailsById(adminId);
			final SuperAdminDetails newAdminDetails = MapperUtil.readAsObjectOf(SuperAdminDetails.class,
					newJsonObject.toString());
			oldAdminDetails.setFirstname(newAdminDetails.getFirstname());
			oldAdminDetails.setLastname(newAdminDetails.getLastname());
			oldAdminDetails.setCompany_name(newAdminDetails.getCompany_name());
			oldAdminDetails.setEmail(newAdminDetails.getEmail());
			oldAdminDetails.setPassword(newAdminDetails.getPassword());

			final SuperAdminDetails details = superAdminDetailsService.updateSuperAdminDetails(oldAdminDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Super admin details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating super admin details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in superAdminDetailsService(updateSuperAdminDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating super admin details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("SuperAdminDetailsApiService(updateSuperAdminDetail) >> Exit");
		return response;
	}

	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteSuperAdminDetail(@PathVariable(value = "id") Long id) {
		logger.info("SuperAdminDetailsApiService(deleteSuperAdminDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final SuperAdminDetails oldAdminDetails = superAdminDetailsService.getAdminDetailsById(id);

			oldAdminDetails.setIs_deleted(true);
			final SuperAdminDetails details = superAdminDetailsService.deleteSuperAdminDetails(oldAdminDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Super admin details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting super admin details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in superAdminDetailsService(deleteSuperAdminDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting super admin details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("SuperAdminDetailsApiService(deleteSuperAdminDetail) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllSuperAdminDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getAllSuperAdminDetails() {
		logger.info("SuperAdminDetailsApiService(getAllSuperAdminDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			List<SuperAdminDetails> details = superAdminDetailsService.getAllSuperAdminDetails();

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			 logger.error("Exception occured in SuperAdminDetailsApiService(getAllSuperAdminDetails) and Exception details >> "+ e);
			 
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting super admin details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("SuperAdminDetailsApiService(getAllSuperAdminDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getActiveSuperAdminDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getActiveSuperAdminDetails() {
		logger.info("SuperAdminDetailsApiService(getActiveSuperAdminDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			List<SuperAdminDetails> details = superAdminDetailsService.getAllSuperAdminDetails();
			final List<SuperAdminDetails> newDetails = new ArrayList<>();
			for (SuperAdminDetails i : details) {
				if (i.getIs_deleted() == false) {
					newDetails.add(i);
				}
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in SuperAdminDetailsApiService(getActiveSuperAdminDetails) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting super admin details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("SuperAdminDetailsApiService(getActiveSuperAdminDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getSuperAdminDetailsById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getSuperAdminDetailsById(@PathVariable("id") final Long id) {
		logger.info("SuperAdminDetailsApiService(getSuperAdminDetailsById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			final SuperAdminDetails details = superAdminDetailsService.getAdminDetailsById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			  logger.
			  error("Exception occured in AsphaltDetailsApiService(getAllReportDetails) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting super admin details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("SuperAdminDetailsApiService(getSuperAdminDetailsById) >> Exit");
		return response;
	}
	@PutMapping(value = "/ChangePasswordSA", headers = "Accept=application/json")
	public String updatePassword(@RequestBody final String passwordDetails) {
		logger.info("SuperAdminDetailsApiService(updatePassword) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(passwordDetails);
			Long adminId = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			String oldPassword = newJsonObject.getString("oldPassword");
			newJsonObject.remove("oldPassword");
			final SuperAdminDetails oldSADetails = superAdminDetailsService.getAdminDetailsById(adminId);
			final SuperAdminDetails newSADetails = MapperUtil.readAsObjectOf(SuperAdminDetails.class,
					newJsonObject.toString());
			SuperAdminDetails details = null;
			if (oldSADetails.getPassword().equals(oldPassword)) {
				oldSADetails.setPassword(newSADetails.getPassword());
				details = superAdminDetailsService.updateSuperAdminPassword(oldSADetails);
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Password has been updated successfully, Try to login with a new password!");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Old password is incorrect");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in superAdminDetailsService(updatePassword) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating the password");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("SuperAdminDetailsApiService(updatePassword) >> Exit");
		return response;
	}
}
