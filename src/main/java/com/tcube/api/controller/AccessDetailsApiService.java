package com.tcube.api.controller;

import java.util.ArrayList;
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
import com.tcube.api.model.AccessDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.service.AccessDetailsService;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/AccessDetails" })
public class AccessDetailsApiService {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(AccessDetailsApiService.class);

	@Autowired
	OrgDetailsService orgDetailsService;
	
	@Autowired
	AccessDetailsService accessDetailsService;
	
	@Autowired
	EmployeeDetailsService employeeDetailsService;
	
	//Create api for access details
	
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createAccess(@RequestBody final String details, final UriComponentsBuilder ucBuilder) {
		
		logger.info("AccessDetailsApiService(createAccess) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			String empId = newJsonObject.getString("emp_id");
			newJsonObject.remove("emp_id");
			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			List<AccessDetails> allDetails = accessDetailsService.getAccessDetailsByOrgId(orgId);
			final List<AccessDetails> newDetails = new ArrayList<>();
			for(AccessDetails i: allDetails) {
				if(i.getIs_deleted() == false) {
					if(i.getEmployeeDetails().getId().equals(empId)) {
						newDetails.add(i);
					}
				}
			}
			final EmployeeDetails empDetails = employeeDetailsService.getAllEmployeeDetailsByID(empId);
			final AccessDetails accessDetails = MapperUtil.readAsObjectOf(AccessDetails.class, newJsonObject.toString());
			accessDetails.setOrgDetails(orgDetails);
			accessDetails.setEmployeeDetails(empDetails);
			for(AccessDetails i: newDetails) {
				i.setIs_deleted(true);
				accessDetailsService.deleteAccessDetails(i);
				if(i.getProject_jobs() == true) {
					accessDetails.setProject_jobs(true);
				}
				if(i.getTime_tracker() == true) {
					accessDetails.setTime_tracker(true);
				}
				if(i.getAttendance() == true) {
					accessDetails.setAttendance(true);
				}
				if(i.getSettings() == true) {
					accessDetails.setSettings(true);
				}
			}
			final AccessDetails newdetails = accessDetailsService.createAccess(accessDetails);
			
			if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New Access Details Created Successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to add access Details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured in AccessDetailsApiService(createAccess) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to add access details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AccessDetailsApiService(createAccess) >> Exit");
		return response;
	}
	
	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateAccessDetail(@RequestBody final String details) {
		logger.info("AccessDetailsApiService(updateAccessDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			long id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			String empId = newJsonObject.getString("emp_id");
			newJsonObject.remove("emp_id");
			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			final EmployeeDetails empDetails = employeeDetailsService.getAllEmployeeDetailsByID(empId);
			final AccessDetails oldDetails = accessDetailsService.getById(id);
			final AccessDetails accessDetails = MapperUtil.readAsObjectOf(AccessDetails.class, newJsonObject.toString());
			accessDetails.setOrgDetails(orgDetails);
			oldDetails.setEmployeeDetails(empDetails);
			oldDetails.setDashboard(accessDetails.getDashboard());
			oldDetails.setProject_jobs(accessDetails.getProject_jobs());
			oldDetails.setTime_tracker(accessDetails.getTime_tracker());
			oldDetails.setAttendance(accessDetails.getAttendance());
			oldDetails.setSettings(accessDetails.getSettings());
			
			final AccessDetails newdetails = accessDetailsService.updateAccess(oldDetails);
			
			if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Access Details Updated Successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to update access Details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured in AccessDetailsApiService(updateAccessDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to update access details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AccessDetailsApiService(updateAccessDetail) >> Exit");
		return response;
	}
	
	//soft delete api for access details (delete by id)
	
	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteAccessDetail(@PathVariable(value = "id") Long id) {
		logger.info("AccessDetailsApiService(deleteAccessDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final AccessDetails oldDetails = accessDetailsService.getById(id);
			oldDetails.setIs_deleted(true);
			final AccessDetails details = accessDetailsService.deleteAccessDetails(oldDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Access details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting access details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured in AccessDetailsApiService(deleteAccessDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting access details");
			response = new Gson().toJson(jsonObject);
		}	
		logger.info("AccessDetailsApiService(deleteAccessDetail) >> Exit");
		return response;
	}
	
	@GetMapping(value = "/getAllAccessDetails", headers = "Accept=application/json")
	public String getAllAccessDetails() {
		logger.info("AccessDetailsApiService(getAllAccessDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("AccessDetailsApiService(getAllAccessDetails) >> Request :");
			List<AccessDetails> details = accessDetailsService.getAllAccessDetails();

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			logger.debug("AccessDetailsApiService(getAllAccessDetails) >> Request");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			System.out.println(e);

			logger.error("Exception occured in AccessDetailsApiService(getAllAccessDetails) and Exception details >> " + e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting access details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AccessDetailsApiService(getAllAccessDetails) >> Exit");
		return response;
	}

	
	@GetMapping(value = "/getAllAccessDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getAllAccessDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("AccessDetailsApiService(getAllAccessDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("AccessDetailsApiService(getAllAccessDetailsByOrgId)");
			List<AccessDetails> details = accessDetailsService.getAccessDetailsByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get access details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			System.out.println(e);
			logger.error("Exception occured in AccessDetailsApiService(getAllAccessDetailsByOrgId) and Exception details >> "
					+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting access details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AccessDetailsApiService(getAllAccessDetailsByOrgId) >> Exit");
		return response;
	}

	@GetMapping(value = "/getActiveAccessDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getActiveAccessDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("AccessDetailsApiService(getActiveAccessDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("AccessDetailsApiService(getActiveAccessDetailsByOrgId)");
			final List<AccessDetails> details = accessDetailsService.getAccessDetailsByOrgId(id);
			final List<AccessDetails> newDetails = new ArrayList<>();
			for (AccessDetails i : details) {
				if (i.getIs_deleted() == false)
					newDetails.add(i);
			}
			if (newDetails != null && newDetails.size() > 0) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get access details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			System.out.println(e);
			logger.error(
					"Exception occured in AccessDetailsApiService(getActiveAccessDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  access details by Org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AccessDetailsApiService(getActiveAccessDetailsByOrgId) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAccessById/{id}", headers = "Accept=application/json")
	public String getAccessDetailById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("AccessDetailsApiService(getAccessDetailById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("AccessDetailsApiService(getAccessDetailById)");
			final AccessDetails details = accessDetailsService.getById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting access details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			System.out.println(e);
			logger.error("Exception occured in AccessDetailsApiService(getAccessDetailById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  access details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AccessDetailsApiService(getAccessDetailById) >> Exit");
		return response;
	}
	
	@GetMapping(value = "/getAccessByEmpId/{id}", headers = "Accept=application/json")
	public String getAccessDetailByEmpId(@PathVariable final String id, final UriComponentsBuilder ucBuilder) {
		logger.info("AccessDetailsApiService(getAccessDetailByEmpId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("AccessDetailsApiService(getAccessDetailByEmpId)");
			final AccessDetails details = accessDetailsService.getByEmpId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting access details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			System.out.println(e);
			logger.error("Exception occured in AccessDetailsApiService(getAccessDetailByEmpId) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  access details by emp id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AccessDetailsApiService(getAccessDetailByEmpId) >> Exit");
		return response;
	}
	

}
