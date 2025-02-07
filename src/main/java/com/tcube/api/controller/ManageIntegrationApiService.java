package com.tcube.api.controller;

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
import com.tcube.api.model.ManageIntegration;
import com.tcube.api.service.ManageIntegrationService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/ManageIntegration" })
public class ManageIntegrationApiService {

	private static Logger logger = LogManager.getLogger(ManageIntegrationApiService.class);

	@Autowired
	ManageIntegrationService ManageIntegrationService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createManageIntegrationDetail(@RequestBody String details) {
		logger.info("ManageIntegrationApiService(createManageIntegrationDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			logger.debug("ManageIntegrationApiService(createManageIntegrationDetail) >> Request");
			final ManageIntegration ManageIntegration = MapperUtil.readAsObjectOf(ManageIntegration.class,
					newJsonObject.toString());
			ManageIntegration data = ManageIntegrationService.getOrgAMdetails(ManageIntegration.getOrg_id(),
					ManageIntegration.getApp(), ManageIntegration.getModule());
			ManageIntegration newdetails;

			if (data == null) {
				newdetails = ManageIntegrationService.createManageIntegration(ManageIntegration);
			} else {
				newdetails = ManageIntegrationService.updateManageIntegration(data);
			}
			if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "ManageIntegration details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating ManageIntegration details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ManageIntegrationApiService(createManageIntegrationDetail) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ManageIntegrationApiService(createManageIntegrationDetail) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating ManageIntegration details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageIntegrationApiService(createManageIntegrationDetail) >> Exit");
		return response;
	}
	@PutMapping(value = "/updategetallslack", headers = "Accept=application/json")
	public String updateGetAllSlackC(@RequestBody final String details) {
		logger.info("ManageIntegrationApiService(updategetallslack) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			String module = newJsonObject.getString("module");
			String app = newJsonObject.getString("app");
			final int newDetails = ManageIntegrationService.updateAllSlackIntegration(orgId, app, module);
			if(newDetails != 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "ManageIntegration details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating ManageIntegration details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ManageIntegrationApiService(updategetallslack) >> Response");
		} catch(Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ManageIntegrationApiService(updategetallslack) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating ManageIntegration details");
		}
		logger.info("ManageIntegrationApiService(updategetallslack) >> Exit");
		return response;
	}

	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateManageIntegrationDetail(@RequestBody final String details) {
		logger.info("ManageIntegrationApiService(updateManageIntegrationDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			logger.debug("ManageIntegrationApiService(updateManageIntegrationDetail) >> Request");
			final ManageIntegration upadedetails = MapperUtil.readAsObjectOf(ManageIntegration.class,
					newJsonObject.toString());
			ManageIntegration newDetails = ManageIntegrationService.getManageIntegrationById(Id);
			newDetails.setOrg_id(upadedetails.getOrg_id());
			newDetails.setActive(upadedetails.getisActive());
			newDetails.setApp(upadedetails.getApp());
			newDetails.setModule(upadedetails.getModule());
			final ManageIntegration data = ManageIntegrationService.updateManageIntegration(newDetails);
			if (data != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "ManageIntegration details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating ManageIntegration details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ManageIntegrationApiService(updateManageIntegrationDetail) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ManageIntegrationApiService(updateManageIntegrationDetail) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating ManageIntegration details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageIntegrationApiService(updateManageIntegrationDetail) >> Exit");
		return response;
	}

	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteManageIntegrationDetail(@PathVariable(value = "id") Long id) {
		logger.info("ManageIntegrationApiService(deleteManageIntegrationDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final ManageIntegration newDetails = ManageIntegrationService.getManageIntegrationById(id);
			newDetails.setDelete(true);
			final ManageIntegration details = ManageIntegrationService.deleteManageIntegration(newDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "ManageIntegration details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting ManageIntegration details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ManageIntegrationApiService(deleteManageIntegrationDetail) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting ManageIntegration details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageIntegrationApiService(deleteManageIntegrationDetail) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllManageIntegrationByOrgId/{id}", headers = "Accept=application/json")
	public String getActiveManageIntegrationByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ManageIntegrationApiService(getActiveManageIntegrationByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ManageIntegrationApiService(getActiveManageIntegrationByOrgId)");
			final List<ManageIntegration> details = ManageIntegrationService.getManageIntegrationByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get ManageIntegration details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in ManageIntegrationApiService(getActiveManageIntegrationByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  ManageIntegration details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageIntegrationApiService(ManageIntegrationApiService) >> Exit");
		return response;
	}

	@GetMapping(value = "/getManageIntegrationById/{id}", headers = "Accept=application/json")
	public String getManageIntegrationById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ManageIntegrationApiService(getManageIntegrationById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ManageIntegrationApiService(getManageIntegrationById)");
			final ManageIntegration details = ManageIntegrationService.getManageIntegrationById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting ManageIntegration details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in ManageIntegrationApiService(getManageIntegrationById) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  ManageIntegration details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageIntegrationApiService(getManageIntegrationById) >> Exit");
		return response;
	}

	@PutMapping(value = "/getOrgAMdetails", headers = "Accept=application/json")
	public String getOrgAMdetails(@RequestBody final String details) {
		logger.info("ManageIntegrationApiService(getOrgAMdetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			String module = newJsonObject.getString("module");
			String app = newJsonObject.getString("app");
			logger.debug("ManageIntegrationApiService(getOrgAMdetails) >> Request");
			ManageIntegration newDetails = ManageIntegrationService.getOrgAMdetails(orgId, app, module);

			final ManageIntegration data = ManageIntegrationService.updateManageIntegration(newDetails);
			if (data != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA,newDetails);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in get Org app Module details ManageIntegration details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ManageIntegrationApiService(getOrgAMdetails) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ManageIntegrationApiService(getOrgAMdetails) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating ManageIntegration details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageIntegrationApiService( get Org app Module details ) >> Exit");
		return response;
	}

}
