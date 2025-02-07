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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.tcube.api.model.LeaveTrackerDetails;
import com.tcube.api.model.ManageLeaveTypes;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.service.ApprovedLeaveDetailsService;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.ManageLeaveTypesService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.LeaveTrackerDetailsService;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/ManageLeaveTypes" })
public class ManageLeaveTypesApiService {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(ManageLeaveTypesApiService.class);

	@Autowired
	ManageLeaveTypesService manageLeaveTypesService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@Autowired
	EmployeeDetailsService employeeDetailsService;

	@Autowired
	ApprovedLeaveDetailsService approvedLeaveDetailsService;
	
	@Autowired
	LeaveTrackerDetailsService leaveTrackerDetailsService;  
	

	// create method
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createLeaveType(@RequestParam(value = "data") String request,
			@RequestParam(value = "org_id", required = true) Long org_id,
			@RequestParam(value = "image", required = false) MultipartFile image,
			final UriComponentsBuilder ucBuilder) {
		logger.info("ManageLeaveTypesApiService(createLeaveType) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newdetails = new JSONObject(request);
			String zone = newdetails.getString("timezone");
			newdetails.remove("timezone");

			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(org_id);
			logger.debug("ManageLeaveTypesApiService(createLeaveType) >> Request");

			final ManageLeaveTypes details = MapperUtil.readAsObjectOf(ManageLeaveTypes.class, newdetails.toString());
			details.setOrgDetails(orgDetails);
			details.setIs_activated(true);
			details.setCounts((long) 0);
			// image compress
			if (image != null) {
				details.setImage(ImageProcessor.compressBytes(image.getBytes()));
			}
			final ManageLeaveTypes createDetails = manageLeaveTypesService.createLeaveType(details, zone);
			if (createDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New leave type details created successfully");
				response = new Gson().toJson(jsonObject);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to create leave type details");
				response = new Gson().toJson(jsonObject);
			}

		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ManageLeaveTypesApiService(createLeaveType) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to create leave type details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(createLeaveType) >> Exit");
		return response;
	}

	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateLeaveType(@RequestParam(value = "data") String request,
			@RequestParam(value = "org_id", required = true) Long org_id,
			@RequestParam(value = "image", required = false) MultipartFile image,
			final UriComponentsBuilder ucBuilder) {
		logger.info("ManageLeaveTypesApiService(updateLeaveType) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newdetails = new JSONObject(request);
			long id = newdetails.getLong("id");
			newdetails.remove("id");
			String zone = newdetails.getString("timezone");
			newdetails.remove("timezone");
			String leaveType = newdetails.getString("leave_type");
			newdetails.remove("leave_type");
			// System.out.println(leaveType);
//			String oldLeaveTypeData = oldLeaveType;
//			if(!leaveType.equals(oldLeaveTypeData)) {
				List<LeaveTrackerDetails> leaveTypeUpdate = leaveTrackerDetailsService.getLeaveTrackerDetailsByLeaveType(id);
				// System.out.println(leaveTypeUpdate.size());
				for(int i=0;i<leaveTypeUpdate.size();i++) {
					Long id1 = leaveTypeUpdate.get(i).getId();
					// System.out.println(id1+" "+leaveType);
//					LeaveTrackerDetails getByIDDetails = leaveTrackerDetailsService.getById(id1);
					int updatedetails = leaveTrackerDetailsService.updateLeaveTrackerDetailksByLeaveType(id1,leaveType);
				}
				
//			} 
			
			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(org_id);
			logger.debug("ManageLeaveTypesApiService(updateLeaveType) >> Request");

			final ManageLeaveTypes details = MapperUtil.readAsObjectOf(ManageLeaveTypes.class, newdetails.toString());
			// get the old details by leave type id
			final ManageLeaveTypes oldDetails = manageLeaveTypesService.getById(id);
			// set the new details
			oldDetails.setCreated_by(details.getCreated_by());
			oldDetails.setLeave_type(leaveType);
//			oldDetails.setStart_date(details.getStart_date());
			oldDetails.setEnd_date(details.getEnd_date());
			oldDetails.setYear(details.getYear());
			oldDetails.setAvailable_days(details.getAvailable_days());
			oldDetails.setImage_name(details.getImage_name());
			// image update
			if (image != null) {
				oldDetails.setImage(ImageProcessor.compressBytes(image.getBytes()));
			}
			final ManageLeaveTypes updateDetails = manageLeaveTypesService.updateLeaveType(oldDetails, zone);
			if (updateDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Leave type details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating leave type details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ManageLeaveTypesApiService(updateLeaveType) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ManageLeaveTypesApiService(updateLeaveType) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating leave type details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(updateLeaveType) >> Exit");
		return response;
	}

	// delete the leave type by id with comments
	@PutMapping(value = "/delete", headers = "Accept=application/json")
	public String deleteLeaveTypeDetail(@RequestBody final String details) {
		logger.info("ManageLeaveTypesApiService(deleteLeaveTypeDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject leaveTypeDetails = new JSONObject(details);
			long id = leaveTypeDetails.getLong("id");
			leaveTypeDetails.remove("id");
			String comments = leaveTypeDetails.getString("comment");
			leaveTypeDetails.remove("comment");

			final ManageLeaveTypes newDetails = manageLeaveTypesService.getById(id);
			newDetails.setIs_deleted(true);
			newDetails.setDelete_comment(comments);
			final ManageLeaveTypes details1 = manageLeaveTypesService.updateLeaveTypeWithoutZone(newDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Leave type details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting leave type details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in ManageLeaveTypesApiService(deleteLeaveTypeDetail) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting leave type details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(deleteLeaveTypeDetail) >> Exit");
		return response;
	}

	// Activate OR deactivate the leave type by id with comments
	@PutMapping(value = "/changeActiveStatus", headers = "Accept=application/json")
	public String changeActiveStatus(@RequestBody final String details) {
		logger.info("ManageLeaveTypesApiService(changeActiveStatus) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject leaveTypeDetails = new JSONObject(details);
			long id = leaveTypeDetails.getLong("id");
			leaveTypeDetails.remove("id");
			String status = leaveTypeDetails.getString("status");
			leaveTypeDetails.remove("status");
			String comments = leaveTypeDetails.getString("comment");
			leaveTypeDetails.remove("comment");

			final ManageLeaveTypes newDetails = manageLeaveTypesService.getById(id);
//			newDetails.setIs_deleted(true);
			if (status.equals("activated")) {
				newDetails.setIs_activated(true);
			} else if (status.equals("deactivated")) {
				newDetails.setIs_activated(false);
			}
			newDetails.setActive_deactive_comment(comments);
			final ManageLeaveTypes details1 = manageLeaveTypesService.updateLeaveTypeWithoutZone(newDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Leave type status changed successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in status change in leave type details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in ManageLeaveTypesApiService(changeActiveStatus) and Exception details >> "
					+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in status change in leave type details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(changeActiveStatus) >> Exit");
		return response;
	}

	// get all leave type details
	@GetMapping(value = "/getAllLeaveTypeDetails", headers = "Accept=application/json")
	public String getAllLeaveTypeDetails() {
		logger.info("ManageLeaveTypesApiService(getAllLeaveTypeDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("ManageLeaveTypesApiService(getAllLeaveTypeDetails) >> Request :");
			List<ManageLeaveTypes> details = manageLeaveTypesService.getAllLeaveTypes();

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in getting  leave type details");
			}
			logger.debug("ManageLeaveTypesApiService(getAllLeaveTypeDetails) >> Request");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);

			logger.error(
					"Exception occured in ManageLeaveTypesApiService(getAllLeaveTypeDetails) and Exception details >> "
							+ e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting  leave type details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(getAllLeaveTypeDetails) >> Exit");
		return response;
	}

	// get all leave types by org id
	@GetMapping(value = "/getAllLeaveTypesByOrgId/{id}", headers = "Accept=application/json")
	public String getAllLeaveTypesByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ManageLeaveTypesApiService(getAllLeaveTypesByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ManageLeaveTypesApiService(getAllLeaveTypesByOrgId)");
			List<ManageLeaveTypes> details = manageLeaveTypesService.getAllLeaveTypesByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get leave types by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ManageLeaveTypesApiService(getAllLeaveTypesByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  leave types by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(getAllLeaveTypesByOrgId) >> Exit");
		return response;
	}

	// get undeleted leave types by org id
	// id_deleted = false and (is_activated = true or false )
	@GetMapping(value = "/getUndeletedLeaveTypesByOrgId/{id}", headers = "Accept=application/json")
	public String getUndeletedLeaveTypesByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ManageLeaveTypesApiService(getUndeletedLeaveTypesByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ManageLeaveTypesApiService(getUndeletedLeaveTypesByOrgId)");
			List<ManageLeaveTypes> details = manageLeaveTypesService.getUndeletedLeaveTypesByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get leave types by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ManageLeaveTypesApiService(getUndeletedLeaveTypesByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  leave types by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(getUndeletedLeaveTypesByOrgId) >> Exit");
		return response;
	}

	// get active Leave types by org id
	// is_deletd = false and is_activated = true
	@GetMapping(value = "/getActiveLeaveTypeByOrgId/{id}", headers = "Accept=application/json")
	public String getActiveLeaveTypeByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ManageLeaveTypesApiService(getActiveLeaveTypeByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ManageLeaveTypesApiService(getActiveLeaveTypeByOrgId)");
			List<ManageLeaveTypes> details = manageLeaveTypesService.getActiveLeaveTypesByOrgId(id);
			for (ManageLeaveTypes i : details) {
				if (i.getImage() != null) {
					i.setImage(ImageProcessor.decompressBytes(i.getImage()));
				}
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get leave type details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ManageLeaveTypesApiService(getActiveLeaveTypeByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  leave type details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(getActiveLeaveTypeByOrgId) >> Exit");
		return response;
	}

	// get active Leave types by org id
	// is_deletd = false and is_activated = true and start date and end date
	@PutMapping(value = "/getActiveLeaveTypeByOrgIdAndDates", headers = "Accept=application/json")
	public String getActiveLeaveTypeByOrgIdAndDates(@RequestBody final String details,
			final UriComponentsBuilder ucBuilder) {
		logger.info("ManageLeaveTypesApiService(getActiveLeaveTypeByOrgIdAndDates) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ManageLeaveTypesApiService(getActiveLeaveTypeByOrgId)");
			final JSONObject newJsonObject = new JSONObject(details);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");
			final ManageLeaveTypes newDetails = MapperUtil.readAsObjectOf(ManageLeaveTypes.class,
					newJsonObject.toString());
			OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
			newDetails.setOrgDetails(org);
			newDetails.setIs_deleted(false);
			List<ManageLeaveTypes> details1 = manageLeaveTypesService.getActiveLeaveTypeByOrgIdAndDates(newDetails,
					zone);
			for (ManageLeaveTypes i : details1) {
				if (i.getImage() != null) {
					i.setImage(ImageProcessor.decompressBytes(i.getImage()));
				}
			}
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details1));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get leave type details by org id and dates");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ManageLeaveTypesApiService(getActiveLeaveTypeByOrgIdAndDates) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  leave type details by org id and dates");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(getActiveLeaveTypeByOrgIdAndDates) >> Exit");
		return response;
	}

	// get active Leave types by org id
	// is_deletd = false and is_activated = false
	@GetMapping(value = "/getInactiveLeaveTypeByOrgId/{id}", headers = "Accept=application/json")
	public String getInactiveLeaveTypeByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ManageLeaveTypesApiService(getInactiveLeaveTypeByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ManageLeaveTypesApiService(getInactiveLeaveTypeByOrgId)");
			final List<ManageLeaveTypes> details = manageLeaveTypesService.getInactiveLeaveTypesByOrgId(id);
			for (ManageLeaveTypes i : details) {
				if (i.getImage() != null) {
					i.setImage(ImageProcessor.decompressBytes(i.getImage()));
				}
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get leave type details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ManageLeaveTypesApiService(getInactiveLeaveTypeByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  leave type details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(getInactiveLeaveTypeByOrgId) >> Exit");
		return response;
	}

	// get leave type by id
	@GetMapping(value = "/getLeaveTypeById/{id}", headers = "Accept=application/json")
	public String getLeaveTypeById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("ManageLeaveTypesApiService(getLeaveTypeById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ManageLeaveTypesApiService(getLeaveTypeById)");
			final ManageLeaveTypes details = manageLeaveTypesService.getById(id);
			if (details.getImage() != null) {
				details.setImage(ImageProcessor.decompressBytes(details.getImage()));
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get leave type details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in ManageLeaveTypesApiService(getLeaveTypeById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  leave type details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(getLeaveTypeById) >> Exit");
		return response;
	}

	// bulk delete for leave types
	@PutMapping(value = "/leavetypesbulkdelete", headers = "Accept=application/json")
	public String bulkDeletePlanDetail(@RequestBody final String request) {
		logger.info("ManageLeaveTypesApiService(leaveTypesBulkDelete) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			final int details = manageLeaveTypesService.bulkDelete(deleteIds);
			if (details == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "leave types bulk deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to bulk delete leave types");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ManageLeaveTypesApiService(leaveTypesBulkDelete) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
//					System.out.println(e);
			logger.error(
					"Exception occured in ManageLeaveTypesApiService(leaveTypesBulkDelete) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in bulk delete leave types");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageLeaveTypesApiService(leaveTypesBulkDelete) >> Exit");
		return response;
	}
}