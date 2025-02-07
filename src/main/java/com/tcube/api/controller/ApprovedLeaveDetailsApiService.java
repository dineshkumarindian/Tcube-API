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
import com.tcube.api.model.ApprovedLeaveDetails;
import com.tcube.api.model.LeaveTrackerDetails;
import com.tcube.api.model.ManageLeaveTypes;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.service.ApprovedLeaveDetailsService;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.ManageLeaveTypesService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/ApprovedLeaveDetails" })
public class ApprovedLeaveDetailsApiService {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(ApprovedLeaveDetailsApiService.class);

	@Autowired
	ApprovedLeaveDetailsService approvedLeaveDetailsService;

	@Autowired
	ManageLeaveTypesService manageLeaveTypesService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@Autowired
	EmployeeDetailsService employeeDetailsService;

	// create method
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createApprovedLeaveDetaile(@RequestBody String details, final UriComponentsBuilder ucBuilder) {
		logger.info("ApprovedLeaveDetailsApiService(createLeaveType) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newdetails = new JSONObject(details);
			Long orgId = newdetails.getLong("org_id");
			newdetails.remove("org_id");
			Long leave_type_id = newdetails.getLong("leave_type_id");
			newdetails.remove("leave_type_id");

			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			ManageLeaveTypes leaveTypeDetails = manageLeaveTypesService.getById(leave_type_id);
			logger.debug("ApprovedLeaveDetailsApiService(createLeaveType) >> Request :" + details);

			final ApprovedLeaveDetails details1 = MapperUtil.readAsObjectOf(ApprovedLeaveDetails.class, newdetails.toString());
			details1.setOrgDetails(orgDetails);
			details1.setLeaveTypeDetails(leaveTypeDetails);
			final ApprovedLeaveDetails createDetails = approvedLeaveDetailsService.createApprovedLeaveDetails(details1);
			if (createDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Approved leave details created successfully");
				response = new Gson().toJson(jsonObject);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to create approved leave details");
				response = new Gson().toJson(jsonObject);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(
					"Exception occured in ApprovedLeaveDetailsApiService(createLeaveType) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to create approved leave details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ApprovedLeaveDetailsApiService(createLeaveType) >> Exit");
		return response;
	}
	
	@PutMapping(value = "/getapprovedLeaveDetailsByEmpIdAndLTId", headers = "Accept=application/json")
	public String getapprovedLeaveDetailsByEmpIdAndLTId(@RequestBody final String details , final UriComponentsBuilder ucBuilder) {
		logger.info("ApprovedLeaveDetailsApiService(getapprovedLeaveDetailsByEmpIdAndLTId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ApprovedLeaveDetailsApiService(getapprovedLeaveDetailsByEmpIdAndLTId)");
			final JSONObject newJsonObject = new JSONObject(details);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			long lt_id = newJsonObject.getLong("leave_type_id");
			newJsonObject.remove("leave_type_id");
			final ApprovedLeaveDetails newDetails = MapperUtil.readAsObjectOf(ApprovedLeaveDetails.class,
					newJsonObject.toString());
			OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
			ManageLeaveTypes lt_details = manageLeaveTypesService.getById(lt_id);
			newDetails.setOrgDetails(org);
			newDetails.setLeaveTypeDetails(lt_details);
			final List<ApprovedLeaveDetails> details1 = approvedLeaveDetailsService.getapprovedLeaveDetailsByEmpIdAndLTId(newDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details1));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get approved leave details by org id and dates");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			System.out.println(e);
			logger.error(
					"Exception occured in ApprovedLeaveDetailsApiService(getapprovedLeaveDetailsByEmpIdAndLTId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting approved leave details by org id and dates");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ApprovedLeaveDetailsApiService(getapprovedLeaveDetailsByEmpIdAndLTId) >> Exit");
		return response;
	}
	
	@PutMapping(value = "/getapprovedLeaveCountsByEmpIdAndLTId", headers = "Accept=application/json")
	public String getapprovedLeaveCountsByEmpIdAndLTId(@RequestBody final String details , final UriComponentsBuilder ucBuilder) {
		logger.info("ApprovedLeaveDetailsApiService(getapprovedLeaveCountsByEmpIdAndLTId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("ApprovedLeaveDetailsApiService(getapprovedLeaveCountsByEmpIdAndLTId)");
			final JSONObject newJsonObject = new JSONObject(details);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			long lt_id = newJsonObject.getLong("leave_type_id");
			newJsonObject.remove("leave_type_id");
			final ApprovedLeaveDetails newDetails = MapperUtil.readAsObjectOf(ApprovedLeaveDetails.class,
					newJsonObject.toString());
			OrgDetails org = orgDetailsService.getOrgDetailsById(orgId);
			ManageLeaveTypes lt_details = manageLeaveTypesService.getById(lt_id);
			newDetails.setOrgDetails(org);
			newDetails.setLeaveTypeDetails(lt_details);
			final Double counts = approvedLeaveDetailsService.getapprovedLeaveCountsByEmpIdAndLTId(newDetails);
//			System.out.println(counts);
			if (counts != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(counts));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get approved leave details by org id and dates");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			System.out.println(e);
			logger.error(
					"Exception occured in ApprovedLeaveDetailsApiService(getapprovedLeaveCountsByEmpIdAndLTId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting approved leave details by org id and dates");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ApprovedLeaveDetailsApiService(getapprovedLeaveDetailsByEmpIdAndLTId) >> Exit");
		return response;
	}
}
