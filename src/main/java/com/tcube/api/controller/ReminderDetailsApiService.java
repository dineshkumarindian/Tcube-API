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
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.ReminderDetails;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.ReminderDetailsService;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/ReminderDetails" })
public class ReminderDetailsApiService {

	private static Logger logger = LogManager.getLogger(ReminderDetailsApiService.class);

	@Autowired
	ReminderDetailsService reminderDetailsService;

	@Autowired
	OrgDetailsService orgDetailsService;

	// create method
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createReminderDetail(@RequestBody String details) {

		logger.info("ReminderDetailsApiService(createReminderDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");

			logger.debug("ReminderDetailsApiService(createReminderDetail) >> Request");
			final ReminderDetails reminderDetails = MapperUtil.readAsObjectOf(ReminderDetails.class,
					newJsonObject.toString());
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			reminderDetails.setOrgDetails(orgDetails);

			final ReminderDetails newdetails = reminderDetailsService.createReminderDetails(reminderDetails, zone);
			if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Reminder details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating reminder details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ReminderDetailsApiService(createReminderDetail) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ReminderDetailsApiService(createReminderDetail) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating reminder details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ReminderDetailsApiService(createReminderDetail) >> Exit");
		return response;
	}

	// create method
	@PostMapping(value = "/createReminderWithoutZone", headers = "Accept=application/json")
	public String createReminderWithoutZone(@RequestBody String details) {

		logger.info("ReminderDetailsApiService(createReminderWithoutZone) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");

			logger.debug("ReminderDetailsApiService(createReminderWithoutZone) >> Request");
			final ReminderDetails reminderDetails = MapperUtil.readAsObjectOf(ReminderDetails.class,
					newJsonObject.toString());
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			reminderDetails.setOrgDetails(orgDetails);

			final ReminderDetails newdetails = reminderDetailsService.createReminderWithoutZone(reminderDetails);
			if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Reminder details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating reminder details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ReminderDetailsApiService(createReminderWithoutZone) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ReminderDetailsApiService(createReminderWithoutZone) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating reminder details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ReminderDetailsApiService(createReminderWithoutZone) >> Exit");
		return response;
	}
	
	// update method
	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateReminderDetail(@RequestBody final String details) {
		logger.info("ReminderDetailsApiService(updateReminderDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			Long Id = newJsonObject.getLong("id");
			String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("org_id");
			newJsonObject.remove("id");
			newJsonObject.remove("timezone");
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("ReminderDetailsApiService(updateReminderDetail) >> Request");
			final ReminderDetails reminderdetails = MapperUtil.readAsObjectOf(ReminderDetails.class,
					newJsonObject.toString());
			ReminderDetails newDetails = reminderDetailsService.getById(Id);
			newDetails.setOrgDetails(orgDetails);
			newDetails.setIs_active(reminderdetails.getIs_active());
			newDetails.setKey_primary(reminderdetails.getKey_primary());
			if(reminderdetails.getKey_secondary() != null) {
				newDetails.setKey_secondary(reminderdetails.getKey_secondary());
			}
			newDetails.setReminder_type(reminderdetails.getReminder_type());
			newDetails.setReminder_date(reminderdetails.getReminder_date());
			newDetails.setReminder_time_ms(reminderdetails.getReminder_time_ms());
			newDetails.setReminder_time_str(reminderdetails.getReminder_time_str());

			final ReminderDetails details1 = reminderDetailsService.updateReminderDetails(newDetails, zone);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Remider details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating reminder details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ReminderDetailsApiService(updateReminderDetail) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ReminderDetailsApiService(updateReminderDetail) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating reminder details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ReminderDetailsApiService(updateReminderDetail) >> Exit");
		return response;
	}

	// update attendance reminder method
	@PutMapping(value = "/updateAttendanceReminder", headers = "Accept=application/json")
	public String updateAttendanceReminder(@RequestBody final String details) {
		logger.info("ReminderDetailsApiService(updateAttendanceReminder) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("org_id");
			newJsonObject.remove("id");
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("ReminderDetailsApiService(updateAttendanceReminder) >> Request");
			final ReminderDetails reminderdetails = MapperUtil.readAsObjectOf(ReminderDetails.class,
					newJsonObject.toString());
			ReminderDetails newDetails = reminderDetailsService.getById(Id);
			newDetails.setOrgDetails(orgDetails);
			newDetails.setIs_active(reminderdetails.getIs_active());
			newDetails.setKey_primary(reminderdetails.getKey_primary());
			if(reminderdetails.getActive_users() != "" || reminderdetails.getActive_users() != null) {
			newDetails.setActive_users(reminderdetails.getActive_users());
			} else {
				newDetails.setActive_users(null);
			}

			final ReminderDetails details1 = reminderDetailsService.updateReminderStatus(newDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Remider details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating reminder details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ReminderDetailsApiService(updateAttendanceReminder) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ReminderDetailsApiService(updateAttendanceReminder) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating reminder details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ReminderDetailsApiService(updateAttendanceReminder) >> Exit");
		return response;
	}
	
	// update key_secondary as a emp details
	@PutMapping(value = "/updateReminderEmpDetails", headers = "Accept=application/json")
	public String updateReminderEmpDetails(@RequestBody final String details) {
		logger.info("ReminderDetailsApiService(updateReminderEmpDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			long id = newJsonObject.getLong("id");
			String empsArr = newJsonObject.getString("emps_arr");

			ReminderDetails newDetails = reminderDetailsService.getById(id);
			newDetails.setKey_secondary(empsArr);
			final ReminderDetails returnData = reminderDetailsService.updateReminderStatus(newDetails);
			if (returnData != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Emp details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating emp details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ReminderDetailsApiService(updateReminderEmpDetails) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ReminderDetailsApiService(updateReminderEmpDetails) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating emp details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ReminderDetailsApiService(updateReminderEmpDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllReminderDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getAllReminderDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ReminderDetailsApiService(getAllReminderDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ReminderDetailsApiService(getAllReminderDetailsByOrgId)");
			List<ReminderDetails> details = reminderDetailsService.getAllRemindersByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get reminder details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ReminderDetailsApiService(getAllReminderDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  reminder details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ReminderDetailsApiService(getAllReminderDetailsByOrgId) >> Exit");
		return response;
	}

	// get by module name and org id
	@PutMapping(value = "/getReminderByOrgIdAndModule", headers = "Accept=application/json")
	public String getReminderByOrgIdAndModule(@RequestBody final String details, final UriComponentsBuilder ucBuilder) {
		logger.info("ReminderDetailsApiService(getIntegrationByOrgIdAndModule) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ReminderDetailsApiService(getIntegrationByOrgIdAndModule)");
			final JSONObject newJsonObject = new JSONObject(details);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			final ReminderDetails newDetails = MapperUtil.readAsObjectOf(ReminderDetails.class,
					newJsonObject.toString());
			OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
			newDetails.setOrgDetails(org);
			newDetails.setIs_deleted(false);
			final List<ReminderDetails> details1 = reminderDetailsService.getReminderByOrgIdAndModule(newDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details1));
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
					"Exception occured in ReminderDetailsApiService(getIntegrationByOrgIdAndModule) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting integration details by org id and module");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ReminderDetailsApiService(getIntegrationByOrgIdAndModule) >> Exit");
		return response;
	}

	@GetMapping(value = "/getReminderById/{id}", headers = "Accept=application/json")
	public String getReminderById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ReminderDetailsApiService(getReminderById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ReminderDetailsApiService(getReminderById)");
			final ReminderDetails details = reminderDetailsService.getById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting reminder details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ReminderDetailsApiService(getReminderById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  reminder details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ReminderDetailsApiService(getReminderById) >> Exit");
		return response;
	}

	// update status
	@PutMapping(value = "/updateReminderStatus", headers = "Accept=application/json")
	public String updateReminderStatus(@RequestBody final String details) {
		logger.info("ReminderDetailsApiService(updateReminderStatus) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			Long Id = newJsonObject.getLong("id");
			Boolean activeStatus = newJsonObject.getBoolean("is_active");
			newJsonObject.remove("org_id");
			newJsonObject.remove("id");
			newJsonObject.remove("is_active");
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("ReminderDetailsApiService(updateReminderStatus) >> Request");
			final ReminderDetails incomingdetails = MapperUtil.readAsObjectOf(ReminderDetails.class,
					newJsonObject.toString());
			ReminderDetails newDetails = reminderDetailsService.getById(Id);
			newDetails.setOrgDetails(orgDetails);
			newDetails.setIs_active(activeStatus);
			final ReminderDetails updateDetails = reminderDetailsService.updateReminderStatus(newDetails);
			if (updateDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Reminder status updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating reminder status");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ReminderDetailsApiService(updateReminderStatus) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in DesignationDetailsApiService(updateDayTask) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating reminder status");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ReminderDetailsApiService(updateReminderStatus) >> Exit");
		return response;
	}

}
