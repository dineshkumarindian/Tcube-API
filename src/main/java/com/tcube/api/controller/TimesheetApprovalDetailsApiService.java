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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.tcube.api.model.DesignationDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.TimeTrackerDetails;
import com.tcube.api.model.TimesheetApprovalDetails;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.TimeTrackerDetailsService;
import com.tcube.api.service.TimesheetApprovalDetailsService;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/TimesheetApprovalDetails" })
public class TimesheetApprovalDetailsApiService {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(TimesheetApprovalDetailsApiService.class);
	
	@Autowired
	TimesheetApprovalDetailsService timesheetApprovalDetailsService;
	
	@Autowired
	TimeTrackerDetailsService timeTrackerDetailsService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@Autowired
	EmployeeDetailsService employeeDetailsService;
	
	
	//create api for timesheet
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createTimesheetDetail(@RequestBody String details) {
		logger.info("TimesheetApprovalDetailsApiService(createTimesheetDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			String empId = newJsonObject.getString("emp_id");
			
			final TimesheetApprovalDetails timesheetDetails = MapperUtil.readAsObjectOf(TimesheetApprovalDetails.class,
					newJsonObject.toString());
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			EmployeeDetails empDeatils = employeeDetailsService.getAllEmployeeDetailsByID(empId);
			timesheetDetails.setEmp_designation(empDeatils.getDesignationDetails().getDesignation());
			timesheetDetails.setIs_deleted(false);
			timesheetDetails.setOrgDetails(orgDetails);
			final TimesheetApprovalDetails newdetails =timesheetApprovalDetailsService.createTimesheet(timesheetDetails);
			if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "timesheet details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating timesheet details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in TimesheetApprovalDetailsApiService(createTimesheetDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating timesheet details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimesheetApprovalDetailsApiService(createTimesheetDetail) >> Exit");
		return response;
	}
	
	//update api for timesheet
	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateTimesheetDetail(@RequestBody final String details) {
		logger.info("TimesheetApprovalDetailsApiService(updateTimesheetDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();	
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			TimesheetApprovalDetails oldDetails = timesheetApprovalDetailsService.getById(Id);
			final TimesheetApprovalDetails newDetails = MapperUtil.readAsObjectOf(TimesheetApprovalDetails.class,
					newJsonObject.toString());
			oldDetails.setDate_of_request(newDetails.getDate_of_request());
			oldDetails.setBillable_total_time(newDetails.getBillable_total_time());
			oldDetails.setNon_billable_total_time(newDetails.getNon_billable_total_time());
			oldDetails.setTotal_time(newDetails.getTotal_time());
			oldDetails.setTotal_time_ms(newDetails.getTotal_time_ms());
			oldDetails.setApproval_status(newDetails.getApproval_status());
			oldDetails.setApproval_comments(newDetails.getApproval_comments());
			
			final TimesheetApprovalDetails details1 = timesheetApprovalDetailsService.updateTimesheetStatus(oldDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Timesheet status updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating timesheet status");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in DesignationDetailsApiService(updateTimesheetDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updatiing timesheet status");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimesheetApprovalDetailsApiService(updateTimesheetDetail) >> Exit");
		return response;
	}
	
	// this api only update the timesheet status
	@PutMapping(value = "/updateStatus", headers = "Accept=application/json")
	public String updateTimesheetDetailStatus(@RequestBody final String details) {
		logger.info("TimesheetApprovalDetailsApiService(updateTimesheetDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();	
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			TimesheetApprovalDetails oldDetails = timesheetApprovalDetailsService.getById(Id);
			final TimesheetApprovalDetails statusDetails = MapperUtil.readAsObjectOf(TimesheetApprovalDetails.class,
					newJsonObject.toString());
			oldDetails.setApproval_status(statusDetails.getApproval_status());
			oldDetails.setApproval_comments(statusDetails.getApproval_comments());
			
			final TimesheetApprovalDetails details1 = timesheetApprovalDetailsService.updateTimesheetStatus(oldDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Timesheet status updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating timesheet status");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in DesignationDetailsApiService(updateTimesheetDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updatiing timesheet status");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimesheetApprovalDetailsApiService(updateTimesheetDetail) >> Exit");
		return response;
	}
	
	// get api for get timesheet by id
	@GetMapping(value = "/getTimesheetById/{id}", headers = "Accept=application/json")
	@ResponseBody
	public String getTimesheetById(@PathVariable("id") final Long id) {
		logger.info("TimesheetApprovalDetailsApiService(getTimesheetById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			final TimesheetApprovalDetails details = timesheetApprovalDetailsService.getTimesheetById(id);
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
			logger.error(
					"Exception occured in TimesheetApprovalDetailsApiService(getTimesheetById) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting Task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimesheetApprovalDetailsApiService(getTimesheetById) >> Exit");
		return response;
	}
	
	
	// get api for get timesheets by emp id
	@GetMapping(value="/getActiveTimesheetByEmpId/{id}",headers="Accept=application/json")
    public String getActiveTimesheetByEmpId(@PathVariable final String id, final UriComponentsBuilder ucBuilder){
		logger.info("TimesheetApprovalDetailsApiService(getActiveTimesheetByEmpId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("TimesheetApprovalDetailsApiService(getActiveTimesheetByEmpId)" );
        	 List<TimesheetApprovalDetails> details = timesheetApprovalDetailsService.getActiveTimesheetByEmpId(id);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get timesheet details by emp Id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in TimesheetApprovalDetailsApiService(getActiveTimesheetByEmpId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  timesheet details by emp Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimesheetApprovalDetailsApiService(getActiveTimesheetByEmpId) >> Exit");
		return response;
    }
	
	// get api for get timesheets by reporter (approver) id
	@GetMapping(value="/getActiveTimesheetByApproverId/{id}",headers="Accept=application/json")
    public String getActiveTimesheetByApproverId(@PathVariable final String id, final UriComponentsBuilder ucBuilder){
		logger.info("TimesheetApprovalDetailsApiService(getActiveTimesheetByApproverId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("TimesheetApprovalDetailsApiService(getActiveTimesheetByApproverId)" );
        	 JSONObject details = timesheetApprovalDetailsService.getActiveTimesheetByApproverId(id);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get designation details by reporter Id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in TimesheetApprovalDetailsApiService(getActiveTimesheetByApproverId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  designation details by reporter Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimesheetApprovalDetailsApiService(getActiveTimesheetByApproverId) >> Exit");
		return response;
    }

	@GetMapping(value="/getTimesheetByEmpIdAndDate",headers="Accept=application/json")
    public String getTimesheetByEmpIdAndDate(@RequestBody final String details, final UriComponentsBuilder ucBuilder){
		logger.info("DesignationDetailsApiService(getTimesheetByEmpIdAndDate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("DesignationDetailsApiService(getTimesheetByEmpIdAndDate)" );
        	final JSONObject newJsonObject = new JSONObject(details);
        	final TimesheetApprovalDetails newDetails = MapperUtil.readAsObjectOf(TimesheetApprovalDetails.class,
					newJsonObject.toString());
        	final TimesheetApprovalDetails newdetails = timesheetApprovalDetailsService.getTimesheetByEmpidAndDate(newDetails);
        	if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(newdetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting timesheet details by id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in DesignationDetailsApiService(getTimesheetByEmpIdAndDate) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  timesheet details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("DesignationDetailsApiService(getTimesheetByEmpIdAndDate) >> Exit");
		return response;
    }
	
	// get api for get active pending timesheets by reporter (approver) id
	@GetMapping(value="/getActivePendingTimesheetByApproverId/{id}",headers="Accept=application/json")
    public String getActivePendingTimesheetByApproverId(@PathVariable final String id, final UriComponentsBuilder ucBuilder){
		logger.info("TimesheetApprovalDetailsApiService(getActivePendingTimesheetByApproverId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("TimesheetApprovalDetailsApiService(getActivePendingTimesheetByApproverId)" );
        	 JSONObject details = timesheetApprovalDetailsService.getActivePendingTimesheetByApproverId(id);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get designation details by reporter Id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in TimesheetApprovalDetailsApiService(getActivePendingTimesheetByApproverId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  designation details by reporter Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimesheetApprovalDetailsApiService(getActivePendingTimesheetByApproverId) >> Exit");
		return response;
    }
	
	// get api for get active approved timesheets by reporter (approver) id
	@GetMapping(value="/getActiveApprovedTimesheetByApproverId/{id}",headers="Accept=application/json")
    public String getActiveApprovedTimesheetByApproverId(@PathVariable final String id, final UriComponentsBuilder ucBuilder){
		logger.info("TimesheetApprovalDetailsApiService(getActiveApprovedTimesheetByApproverId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("TimesheetApprovalDetailsApiService(getActiveApprovedTimesheetByApproverId)" );
        	 JSONObject details = timesheetApprovalDetailsService.getActiveApprovedTimesheetByApproverId(id);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get designation details by reporter Id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in TimesheetApprovalDetailsApiService(getActiveApprovedTimesheetByApproverId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  designation details by reporter Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimesheetApprovalDetailsApiService(getActiveApprovedTimesheetByApproverId) >> Exit");
		return response;
    }
	
	// get api for get active rejected timesheets by reporter (approver) id
	@GetMapping(value="/getActiveRejectedTimesheetByApproverId/{id}",headers="Accept=application/json")
    public String getActiveRejectedTimesheetByApproverId(@PathVariable final String id, final UriComponentsBuilder ucBuilder){
		logger.info("TimesheetApprovalDetailsApiService(getActiveRejectedTimesheetByApproverId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("TimesheetApprovalDetailsApiService(getActiveRejectedTimesheetByApproverId)" );
        	 JSONObject details = timesheetApprovalDetailsService.getActiveRejectedTimesheetByApproverId(id);
        	if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get designation details by reporter Id");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in TimesheetApprovalDetailsApiService(getActiveRejectedTimesheetByApproverId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  designation details by reporter Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("TimesheetApprovalDetailsApiService(getActiveRejectedTimesheetByApproverId) >> Exit");
		return response;
    }
}
