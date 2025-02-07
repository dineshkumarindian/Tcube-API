	package com.tcube.api.controller;

import java.util.List;

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
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.tcube.api.model.HolidayDetails;
import com.tcube.api.model.ManageLeaveTypes;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.service.HolidayDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/HolidayDetails" })
public class HolidayDetailsApiService {

	private static Logger logger = LogManager.getLogger(HolidayDetailsApiService.class);

	@Autowired
	HolidayDetailsService holidayDetailsService;

	@Autowired
	OrgDetailsService orgDetailsService;

	// create method
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createHoliday(@RequestBody String detailsOfHoliday) {
		logger.info("HolidayDetailsApiService(createHoliday) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newdetails = new JSONObject(detailsOfHoliday);
			Long orgId = newdetails.getLong("org_id");
			newdetails.remove("org_id");
			String zone = newdetails.getString("timezone");
			newdetails.remove("timezone");

			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("HolidayDetailsApiService(createHoliday) >> Request");

			HolidayDetails details = MapperUtil.readAsObjectOf(HolidayDetails.class, newdetails.toString());
			details.setOrgDetails(orgDetails);
			final HolidayDetails createDetails = holidayDetailsService.createHoliday(details, zone);
			if (createDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New holiday created successfully");
				response = new Gson().toJson(jsonObject);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to create holiday");
				response = new Gson().toJson(jsonObject);
			}

		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in HolidayDetailsApiService(createHoliday) and Exception details" + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to create holiday");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("HolidayDetailsApiService(createHoliday) >> Exit");
		return response;
	}

	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateHoliday(@RequestBody String detailsOfHoliday) {
		logger.info("HolidayDetailsApiService(updateHoliday) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newdetails = new JSONObject(detailsOfHoliday);
			long id = newdetails.getLong("id");
			newdetails.remove("id");
			long orgId = newdetails.getLong("org_id");
			newdetails.remove("org_id");
			String zone = newdetails.getString("timezone");
			newdetails.remove("timezone");
			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("HolidayDetailsApiService(updateHoliday) >> Request");

			final HolidayDetails details = MapperUtil.readAsObjectOf(HolidayDetails.class, newdetails.toString());
			// get the old details by leave type id
			final HolidayDetails oldDetails = holidayDetailsService.getById(id);
			// set the new details
			oldDetails.setCreated_by(details.getCreated_by());
			oldDetails.setLeave_name(details.getLeave_name());
			oldDetails.setLeave_date(details.getLeave_date());
			oldDetails.setStart_date(details.getStart_date());
			oldDetails.setEnd_date(details.getEnd_date());

			final HolidayDetails updateDetails = holidayDetailsService.updateHoliday(oldDetails, zone);
			if (updateDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Holiday updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating holiday");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("HolidayDetailsApiService(updateHoliday) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating holiday");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("HolidayDetailsApiService(updateHoliday) >> Exit");
		return response;
	}

	// delete the holiday by id with comments
	@PutMapping(value = "/delete", headers = "Accept=application/json")
	public String deleteHoliday(@RequestBody final String details) {
		logger.info("HolidayDetailsApiService(deleteHoliday) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject Details = new JSONObject(details);
			long id = Details.getLong("id");
			Details.remove("id");
			String comments = Details.getString("comment");
			Details.remove("comment");

			final HolidayDetails newDetails = holidayDetailsService.getById(id);
			newDetails.setIs_deleted(true);
			newDetails.setComment(comments);
			final HolidayDetails details1 = holidayDetailsService.updateHolidayWithoutZone(newDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Holiday deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting holiday");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in HolidayDetailsApiService(deleteHoliday) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting holiday");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("HolidayDetailsApiService(deleteHoliday) >> Exit");
		return response;
	}
	//bulk delete holidays implementations
	@PutMapping(value = "/bulkdelete", headers = "Accept=application/json")
	public String deleteBulkHoliday(@RequestBody final String request) {
		logger.info("HolidayDetailsApiService(deleteBulkHoliday) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			int details1 = holidayDetailsService.bulkDelete(deleteIds);
			if (details1 == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Holiday deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting holiday");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in HolidayDetailsApiService(deleteHoliday) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting holiday");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("HolidayDetailsApiService(deleteBulkHoliday) >> Exit");
		return response;
	}

	// get all holidays type details
	@GetMapping(value = "/getAllHolidays", headers = "Accept=application/json")
	public String getAllHolidays() {
		logger.info("HolidayDetailsApiService(getAllHolidays) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("HolidayDetailsApiService(getAllHolidays) >> Request :");
			List<HolidayDetails> details = holidayDetailsService.getAllHolidays();

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in getting  holidays");
			}
			logger.debug("HolidayDetailsApiService(getAllHolidays) >> Request");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in HolidayDetailsApiService(getAllHolidays) and Exception details >> " + e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting  holidays");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("HolidayDetailsApiService(getAllHolidays) >> Exit");
		return response;
	}

	// get all holidays by org id
	@GetMapping(value = "/getAllHolidaysByOrgId/{id}", headers = "Accept=application/json")
	public String getAllHolidaysByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("HolidayDetailsApiService(getAllHolidaysByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("HolidayDetailsApiService(getAllLeaveTypesByOrgId)");
			List<HolidayDetails> details = holidayDetailsService.getAllHolidaysByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get holidays by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in HolidayDetailsApiService(getAllHolidaysByOrgId) and Exception details"
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting holidays by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("HolidayDetailsApiService(getAllHolidaysByOrgId) >> Exit");
		return response;
	}

	// get active holidays by org id
	// is_deletd = false
	@GetMapping(value = "/getActiveHolidayByOrgId/{id}", headers = "Accept=application/json")
	public String getActiveHolidaysByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("HolidayDetailsApiService(getActiveHolidaysByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("HolidayDetailsApiService(getActiveHolidaysByOrgId)");
			List<HolidayDetails> details = holidayDetailsService.getActiveHolidaysByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get leave type details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in HolidayDetailsApiService(getActiveHolidaysByOrgId) and Exception details"
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  leave type details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("HolidayDetailsApiService(getActiveHolidaysByOrgId) >> Exit");
		return response;
	}
	
	// get active holidays by org id
	// is_deletd = false and current year holidays only
	@PutMapping(value = "/getActiveHolidayByOrgIdAndDates", headers = "Accept=application/json")
	public String getActiveHolidaysByOrgIdAndDates(@RequestBody final String details, final UriComponentsBuilder ucBuilder) {
		logger.info("HolidayDetailsApiService(getActiveHolidaysByOrgIdAndDates) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject getDetails = new JSONObject(details);
			Long orgId = getDetails.getLong("org_id");
			getDetails.remove("org_id");
			String zone = getDetails.getString("timezone");
			String startDate=getDetails.getString("start_date");
			getDetails.remove(startDate);
			String endDate=getDetails.getString("end_date");
			getDetails.remove(endDate);
			getDetails.remove("timezone");
			logger.info("HolidayDetailsApiService(getActiveHolidaysByOrgIdAndDates)");
			final HolidayDetails getNewDetails = MapperUtil.readAsObjectOf(HolidayDetails.class, getDetails.toString());
			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			getNewDetails.setOrgDetails(orgDetails);
			List<HolidayDetails> returnDetails =holidayDetailsService.getActiveHolidaysByOrgIdAndDates(orgId,startDate,endDate,zone);
			if (returnDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(returnDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get holidays by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in HolidayDetailsApiService(getActiveHolidaysByOrgIdAndDates) and Exception details"
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  leave type details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("HolidayDetailsApiService(getActiveHolidaysByOrgIdAndDates) >> Exit");
		return response;
	}
	
	// get holiday by id
	@GetMapping(value = "/getHolidayById/{id}", headers = "Accept=application/json")
	public String getLeaveTypeById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("HolidayDetailsApiService(getLeaveTypeById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("HolidayDetailsApiService(getLeaveTypeById)");
			final HolidayDetails details = holidayDetailsService.getById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get holiday details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in HolidayDetailsApiService(getLeaveTypeById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting holiday details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("HolidayDetailsApiService(getLeaveTypeById) >> Exit");
		return response;
	}
}
