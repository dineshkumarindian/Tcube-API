package com.tcube.api.controller;

import java.util.ArrayList;
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
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.RoleDetails;
import com.tcube.api.service.ManageLeaveTypesService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.RoleDetailsService;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/RoleDetails" })
public class RoleDetailsApiService {

	@Autowired
	ManageLeaveTypesService RoleDetailsService;

	private static Logger logger = LogManager.getLogger(RoleDetailsApiService.class);

	@Autowired
	RoleDetailsService roleDetailsService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createRoleDetail(@RequestBody String details) {

		logger.info("RoleDetailsApiService(createRoleDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");

			logger.debug("RoleDetailsApiService(createRoleDetail) >> Request");
			final RoleDetails Roledetails = MapperUtil.readAsObjectOf(RoleDetails.class, newJsonObject.toString());
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			Roledetails.setIs_deleted(false);
			Roledetails.setOrgDetails(orgDetails);
			Roledetails.setActive_total_counts(0);
			Roledetails.setInactive_total_counts(0);
			Roledetails.setTotal_counts(0);
			final RoleDetails newdetails = roleDetailsService.createRoleDetails(Roledetails);
			if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Role details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating role details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("RoleDetailsApiService(createRoleDetail) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in RoleDetailsApiService(createRoleDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating role details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("RoleDetailsApiService(createRoleDetail) >> Exit");
		return response;

	}

	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateRoleDetail(@RequestBody final String details) {
		logger.info("RoleDetailsApiService(updateRoleDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("org_id");
			newJsonObject.remove("id");
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("RoleDetailsApiService(updateRoleDetail) >> Request");
			final RoleDetails Roledetails = MapperUtil.readAsObjectOf(RoleDetails.class, newJsonObject.toString());
			RoleDetails newDetails = roleDetailsService.getRoleDetailsById(Id);
			newDetails.setOrgDetails(orgDetails);
			newDetails.setRole(Roledetails.getRole());
			newDetails.setAccess_to(Roledetails.getAccess_to());

			final RoleDetails details1 = roleDetailsService.updateRoleDetails(newDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Role details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating role details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("RoleDetailsApiService(updateRoleDetail) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in RoleDetailsApiService(updateRoleDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating role details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("RoleDetailsApiService(updateRoleDetail) >> Exit");
		return response;
	}

	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteRoleDetail(@PathVariable(value = "id") Long id) {
		logger.info("RoleDetailsApiService(deleteRoleDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final RoleDetails newRoleetails = roleDetailsService.getRoleDetailsById(id);
			newRoleetails.setIs_deleted(true);
			final RoleDetails details = roleDetailsService.deleteRoleDetails(newRoleetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Role details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting role details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in RoleDetailsApiService(deleteRoleDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting role details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("RoleDetailsApiService(deleteRoleDetail) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllRoleDetails", headers = "Accept=application/json")
	public String getAllRoleDetails() {
		logger.info("RoleDetailsApiService(getAllRoleDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("RoleDetailsApiService(getAllRoleDetails) >> Request");
			List<RoleDetails> details = roleDetailsService.getAllRoleDetails();

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			logger.debug("RoleDetailsApiService(getAllRoleDetails) >> Response");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);

			logger.error("Exception occured in RoleDetailsApiService(getAllRoleDetails) and Exception details >> " + e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting  role details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("RoleDetailsApiService(getAllRoleDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllRoleDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getAllRoleDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("RoleDetailsApiService(getAllRoleDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("RoleDetailsApiService(getAllRoleDetailsByOrgId)");
			List<RoleDetails> details = roleDetailsService.getRoleDetailsByOrgId(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get role details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in RoleDetailsApiService(getAllRoleDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  role details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("RoleDetailsApiService(getAllRoleDetailsByOrgId) >> Exit");
		return response;
	}

	@GetMapping(value = "/getActiveRoleDetailsByOrgId/{id}", headers = "Accept=application/json")
	public String getActiveRoleDetailsByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("RoleDetailsApiService(getActiveRoleDetailsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("RoleDetailsApiService(getActiveRoleDetailsByOrgId)");
			final List<RoleDetails> details = roleDetailsService.getRoleDetailsByOrgId(id);
			final List<RoleDetails> newDetails = new ArrayList<>();
			for (RoleDetails i : details) {
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
				jsonObject.put(RestConstants.DATA, "Failed to get role details by org Id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error(
					"Exception occured in RoleDetailsApiService(getActiveRoleDetailsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  role details by org Id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("RoleDetailsApiService(getActiveRoleDetailsByOrgId) >> Exit");
		return response;
	}

	@GetMapping(value = "/getRoleById/{id}", headers = "Accept=application/json")
	public String getRoleDetailById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("RoleDetailsApiService(getRoleDetailById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("RoleDetailsApiService(getRoleDetailById)");
			final RoleDetails details = roleDetailsService.getRoleDetailsById(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting role details by id");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error("Exception occured in RoleDetailsApiService(getRoleDetailById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  role details by id");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("RoleDetailsApiService(getRoleDetailById) >> Exit");
		return response;
	}

	@GetMapping(value = "/getRoleByName/{details}", headers = "Accept=application/json")
	public String getRoleByName(@PathVariable String details, Long id, final UriComponentsBuilder ucBuilder) {
		logger.info("RoleDetailsApiService(getRoleByName) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.info("RoleDetailsApiService(getRoleByName)");
			RoleDetails details1 = roleDetailsService.getRoleByName(details, id);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details1));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting role details by name");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error("Exception occured in RoleDetailsApiService(getRoleByName) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  role details by name");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("RoleDetailsApiService(getRoleDetailById) >> Exit");
		return response;
	}

	@PutMapping(value = "/getroledetailbyorgidroleid", headers = "Accept=application/json")
	public String getRoledetailbyOrgidRoleid(@RequestBody final String details) {
		logger.info("RoleDetailsApiService(getRoledetailbyOrgidRoleid) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			Long roleId = newJsonObject.getLong("role_id");
			newJsonObject.remove("org_id");
			newJsonObject.remove("role_id");

			logger.debug("RoleDetailsApiService(getRoledetailbyOrgidRoleid) >> Request");

			final RoleDetails details1 = roleDetailsService.getRoleDetailsByOrgidAndRoleId(orgId, roleId);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details1));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get the role details based on the orgid and roleid");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("RoleDetailsApiService(getRoledetailbyOrgidRoleid) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in RoleDetailsApiService(getRoledetailbyOrgidRoleid) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting role details based on the orgid and roleid");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("RoleDetailsApiService(getRoledetailbyOrgidRoleid) >> Exit");
		return response;
	}

	// update the role details of the employee while updating the pricing plan of
	// the org
	@PutMapping(value = "/updateroleemployee", headers = "Accept=application/json")
	public String updateRoleDetailForEmployee(@RequestBody final String details) {
		logger.info("RoleDetailsApiService(updateRoleDetailForEmployee) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long id = newJsonObject.getLong("id");
			String roles = newJsonObject.getString("roles");
			newJsonObject.remove("id");
			newJsonObject.remove("roles");

			final RoleDetails details1 = roleDetailsService.upgradeRoledetailsForEmployees(id, roles);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Role details updated successfully for employees");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating role details for employees");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("RoleDetailsApiService(updateRoleDetailForEmployee) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in RoleDetailsApiService(updateRoleDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating role details for employees");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("RoleDetailsApiService(updateRoleDetailForEmployee) >> Exit");
		return response;
	}

	// bulk delete for manage role
	@PutMapping(value = "/rolebulkdelete", headers = "Accept=application/json")
	public String bulkDeletePlanDetail(@RequestBody final String request) {
		logger.info("RoleDetailsApiService(roleBulkDelete) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			final int details = roleDetailsService.bulkDelete(deleteIds);
			if (details == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "role bulk deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to bulk delete role");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("roleDetailsService(roleBulkDelete) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in bulk delete role");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("roleDetailsService(roleBulkDelete) >> Exit");
		return response;
	}

}
