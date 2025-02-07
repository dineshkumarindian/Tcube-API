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
import com.tcube.api.model.DayPlannerDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.service.DayPlannerDetailsService;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/DayPlannerDetails" })
public class DayPlannerDetailsApiService {

	private static Logger logger = LogManager.getLogger(DayPlannerDetailsApiService.class);

	@Autowired
	DayPlannerDetailsService dayPlannerDetailsService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@Autowired
	EmployeeDetailsService employeeDetailsService;

	// create method
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createDayTask(@RequestBody String detailsOfTask) {
		logger.info("DayPlannerDetailsApiService(createDayTask) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newdetails = new JSONObject(detailsOfTask);
			JSONArray detailsArr = newdetails.getJSONArray("detailsArray");
			newdetails.remove("detailsArray");
			Long orgId = newdetails.getLong("org_id");
			newdetails.remove("org_id");
			String zone = newdetails.getString("timezone");
			newdetails.remove("timezone");

			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("DayPlannerDetailsApiService(createDayTask) >> Request ");

			int check = 0;
			for (int i = 0; i < detailsArr.length(); i++) {
				JSONObject details1 = (JSONObject) detailsArr.get(i);
				DayPlannerDetails details = MapperUtil.readAsObjectOf(DayPlannerDetails.class, details1.toString());
//				EmployeeDetails empDetails = employeeDetailsService.getAllEmployeeDetailsByID(details.getEmp_id());

//				if (empDetails.getProfile_image() != null) {
//					details.setEmp_image(empDetails.getProfile_image());
//				}
				details.setOrgDetails(orgDetails);
				final DayPlannerDetails createDetails = dayPlannerDetailsService.createDayTask(details, zone);
				if (createDetails != null) {
					check += 1;
				}
			}

			if (detailsArr.length() == check) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New task created successfully");
				response = new Gson().toJson(jsonObject);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to create day task");
				response = new Gson().toJson(jsonObject);
			}

		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in DayPlannerDetailsApiService(createDayTask) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to create day task");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DayPlannerDetailsApiService(createDayTask) >> Exit");
		return response;
	}

	// update method
	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateDayTask(@RequestBody final String details) {
		logger.info("DayPlannerDetailsApiService(updateDayTask) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("org_id");
			newJsonObject.remove("id");
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("DayPlannerDetailsApiService(updateDayTask) >> Request");
			final DayPlannerDetails incomingdetails = MapperUtil.readAsObjectOf(DayPlannerDetails.class,
					newJsonObject.toString());
			DayPlannerDetails newDetails = dayPlannerDetailsService.getById(Id);
			newDetails.setOrgDetails(orgDetails);
			newDetails.setDay_task(incomingdetails.getDay_task());
			newDetails.setDescription(incomingdetails.getDescription());
			newDetails.setProject_id(incomingdetails.getProject_id());
			newDetails.setProject_name(incomingdetails.getProject_name());
			newDetails.setIs_reminder(incomingdetails.getIs_reminder());
			newDetails.setReminder_date_time(incomingdetails.getReminder_date_time());
			newDetails.setStatus(incomingdetails.getStatus());
			final DayPlannerDetails updateDetails = dayPlannerDetailsService.updateDayTask(newDetails);
			if (updateDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Day task updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating day task");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("DayPlannerDetailsApiService(updateDayTask) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in DesignationDetailsApiService(updateDayTask) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating day task");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DayPlannerDetailsApiService(updateDayTask) >> Exit");
		return response;
	}

	// delete method
	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteDayTask(@PathVariable(value = "id") Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("DayPlannerDetailsApiService(deleteDayTask) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final DayPlannerDetails newDetails = dayPlannerDetailsService.getById(id);
			newDetails.setIs_deleted(true);
			final DayPlannerDetails details = dayPlannerDetailsService.updateDayTask(newDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Day task details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting day task");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in DayPlannerDetailsApiService(deleteDayTask) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting day task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DayPlannerDetailsApiService(deleteDayTask) >> Exit");
		return response;
	}

	// get by day task id
	@GetMapping(value = "/getDayTaskById/{id}", headers = "Accept=application/json")
	public String getDayTaskById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("DayPlannerDetailsApiService(getDayTaskById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("DayPlannerDetailsApiService(getDayTaskById)");
			final DayPlannerDetails details = dayPlannerDetailsService.getById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting day task by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in DayPlannerDetailsApiService(getDayTaskById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  day task id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DayPlannerDetailsApiService(getDayTaskById) >> Exit");
		return response;
	}

	// get by orgId , empId, date
	// is_deleted = false
	// get by module name and org id
	@PutMapping(value = "/getDayTask", headers = "Accept=application/json")
	public String getDayTasksByEmpidAndDate(@RequestBody final String details, final UriComponentsBuilder ucBuilder) {
		logger.info("DayPlannerDetailsApiService(getDayTasksByEmpidAndDate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("DayPlannerDetailsApiService(getDayTasksByEmpidAndDate)");
			final JSONObject newJsonObject = new JSONObject(details);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			final DayPlannerDetails newDetails = MapperUtil.readAsObjectOf(DayPlannerDetails.class,
					newJsonObject.toString());
			OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
			newDetails.setOrgDetails(org);
			newDetails.setIs_deleted(false);
			final List<DayPlannerDetails> details1 = dayPlannerDetailsService.getDayTasksByEmpidAndDate(newDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details1));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get task details by emp id , org id and date");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in DayPlannerDetailsApiService(getDayTasksByEmpidAndDate) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting task details by emp id , org id and date");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DayPlannerDetailsApiService(getDayTasksByEmpidAndDate) >> Exit");
		return response;
	}

	// get by orgId , date
	// is_deleted = false
	// get tasks by org id
	@PutMapping(value = "/getDayTaskByOrgid", headers = "Accept=application/json")
	public String getDayTasksByOrgidAndDate(@RequestBody final String details, final UriComponentsBuilder ucBuilder) {
		logger.info("DayPlannerDetailsApiService(getDayTasksByEmpidAndDate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("DayPlannerDetailsApiService(getDayTasksByEmpidAndDate)");
			final JSONObject newJsonObject = new JSONObject(details);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			final DayPlannerDetails newDetails = MapperUtil.readAsObjectOf(DayPlannerDetails.class,
					newJsonObject.toString());
			OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
			newDetails.setOrgDetails(org);
			newDetails.setIs_deleted(false);
			final List<DayPlannerDetails> details1 = dayPlannerDetailsService.getDayTasksByOrgidAndDate(newDetails);
//			for (DayPlannerDetails i : details1) {
//				if (i.getEmp_image() != null) {
//					i.setEmp_image(ImageProcessor.decompressBytes(i.getEmp_image()));
//				}
//			}
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details1));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get task details by org id and date");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in DayPlannerDetailsApiService(getDayTasksByEmpidAndDate) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting task details by org id and date");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DayPlannerDetailsApiService(getDayTasksByEmpidAndDate) >> Exit");
		return response;
	}

	// update status
	@PutMapping(value = "/updateDayTaskStatus", headers = "Accept=application/json")
	public String updateDayTaskStatus(@RequestBody final String details) {
		logger.info("DayPlannerDetailsApiService(updateDayTaskStatus) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			Long Id = newJsonObject.getLong("id");
			String taskStatus = newJsonObject.getString("task_status");
			newJsonObject.remove("org_id");
			newJsonObject.remove("id");
			newJsonObject.remove("task_status");
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("DayPlannerDetailsApiService(updateDayTaskStatus) >> Request");
			final DayPlannerDetails incomingdetails = MapperUtil.readAsObjectOf(DayPlannerDetails.class,
					newJsonObject.toString());
			DayPlannerDetails newDetails = dayPlannerDetailsService.getById(Id);
			newDetails.setOrgDetails(orgDetails);
			newDetails.setStatus(taskStatus);
			final DayPlannerDetails updateDetails = dayPlannerDetailsService.updateDayTask(newDetails);
			if (updateDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Day task status updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating day task status");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("DayPlannerDetailsApiService(updateDayTaskStatus) >> Response ");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in DesignationDetailsApiService(updateDayTask) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating day task status");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DayPlannerDetailsApiService(updateDayTaskStatus) >> Exit");
		return response;
	}

	// Bulk update status
	@PutMapping(value = "/bulkUpdateDayTaskStatus", headers = "Accept=application/json")
	public String bulkUpdateDayTaskStatus(@RequestBody final String details) {
		logger.info("DayPlannerDetailsApiService(bulkUpdateDayTaskStatus) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray detailsArr = newJsonObject.getJSONArray("detailsArray");
			newJsonObject.remove("detailsArray");
			Long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("DayPlannerDetailsApiService(bulkUpdateDayTaskStatus) >> Request");
			int check = 0;
			JSONArray updatedArr = new JSONArray();
			for (int i = 0; i < detailsArr.length(); i++) {
				JSONObject icDetails = (JSONObject) detailsArr.get(i);
				DayPlannerDetails tempDetails = dayPlannerDetailsService.getById(icDetails.getLong("id"));
				tempDetails.setStatus(icDetails.getString("status"));
				tempDetails.setOrgDetails(orgDetails);
				DayPlannerDetails updatedDetails = dayPlannerDetailsService.updateDayTask(tempDetails);
				if (updatedDetails != null) {
					updatedArr.put(updatedDetails.getId());
					check += 1;
				}
			}
//			if(check != detailsArr.length()) {
//				for(int i = 0; i < updatedArr.length() ; i++) {
//					long deleteId = (long) updatedArr.get(i);
//					DayPlannerDetails deleteDetails = dayPlannerDetailsService.getById(deleteId));
//					deleteDetails.setIs_deleted(true);
//					dayPlannerDetailsService.updateDayTask(deleteDetails);
//				}
//			}
			if (check == detailsArr.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Bulk task status updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating bulk task status");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("DayPlannerDetailsApiService(bulkUpdateDayTaskStatus) >> Response ");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in DesignationDetailsApiService(updateDayTask) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating bulk task status");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DayPlannerDetailsApiService(bulkUpdateDayTaskStatus) >> Exit");
		return response;
	}

	// update date of the task
	@PutMapping(value = "/updateDayTaskDate", headers = "Accept=application/json")
	public String updateDayTaskDate(@RequestBody final String details) {
		logger.info("DayPlannerDetailsApiService(updateDayTaskDate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray taskIdsArr = newJsonObject.getJSONArray("task_ids");
			String date = newJsonObject.getString("date");

			// array to string concatenate process
			String id_list = new String();
			for (int i = 0; i < taskIdsArr.length(); i++) {
				id_list += "'" + taskIdsArr.get(i) + "'" + ",";
			}
			StringBuffer id_sb = new StringBuffer(id_list);
			id_sb.deleteCharAt(id_sb.length() - 1);

			final int returnData = dayPlannerDetailsService.updateTaskDates(id_sb, date);
			if (returnData == taskIdsArr.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Day task date updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating day task date");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("DayPlannerDetailsApiService(updateupdateDayTaskDateDayTask) >> Response ");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in DesignationDetailsApiService(updateDayTaskSubmitStatus) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating day task date");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DayPlannerDetailsApiService(updateDayTaskDate) >> Exit");
		return response;
	}

	// update submit status of the task ==> is_submitted = true or false
	@PutMapping(value = "/updateDayTaskSubmitStatus", headers = "Accept=application/json")
	public String updateDayTaskSubmitStatus(@RequestBody final String details) {
		logger.info("DayPlannerDetailsApiService(updateDayTaskSubmitStatus) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray taskIdsArr = newJsonObject.getJSONArray("task_ids");
			Boolean status = newJsonObject.getBoolean("status");
			String toChange = newJsonObject.getString("to_change");

			// array to string concatenate process
			String id_list = new String();
			for (int i = 0; i < taskIdsArr.length(); i++) {
				id_list += "'" + taskIdsArr.get(i) + "'" + ",";
			}
			StringBuffer id_sb = new StringBuffer(id_list);
			id_sb.deleteCharAt(id_sb.length() - 1);
			int returnData = 0;
			if (toChange.equals("submit")) {
				returnData = dayPlannerDetailsService.updateDayTaskSubmitStatus(id_sb, status);
			} else if (toChange.equals("update")) {
				returnData = dayPlannerDetailsService.updateDayTaskupdateStatus(id_sb, status);
			}

			if (returnData == taskIdsArr.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Day task date updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating day task submit status");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("DayPlannerDetailsApiService(updateDayTaskSubmitStatus) >> Response ");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in DayPlannerDetailsApiService(updateDayTaskSubmitStatus) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating day task submit status");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DayPlannerDetailsApiService(updateDayTaskSubmitStatus) >> Exit");
		return response;
	}
	
	// bulk delete for manage role
	@PutMapping(value = "/bulkDelete", headers = "Accept=application/json")
	public String bulkDeleteDayTask(@RequestBody final String request) {
		logger.info("DayPlannerDetailsApiService(taskBulkDelete) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			final int details = dayPlannerDetailsService.bulkDelete(deleteIds);
			if (details == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "task bulk deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to bulk delete task");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("DayPlannerDetailsApiService(taskBulkDelete) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in bulk delete task");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DayPlannerDetailsApiService(taskBulkDelete) >> Exit");
		return response;
	}

}
