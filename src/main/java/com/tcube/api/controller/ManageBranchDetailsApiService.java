package com.tcube.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.tcube.api.model.ManageBranchDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.RoleDetails;
import com.tcube.api.service.ManageBranchDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/managebranch" })
public class ManageBranchDetailsApiService {
	
	private static Logger logger = LogManager.getLogger(ManageBranchDetailsApiService.class);

	@Autowired
	ManageBranchDetailsService manageBranchDetailsService;
	
	@Autowired
	OrgDetailsService orgDetailsService;
	
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createBranches(@RequestBody String details) {
		logger.info("ManageBranchDetailsApiService(createBranches) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			final ManageBranchDetails branchDetails = MapperUtil.readAsObjectOf(ManageBranchDetails.class, newJsonObject.toString());
			final ManageBranchDetails newdetails = manageBranchDetailsService.create(branchDetails);
			if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Branch details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to create Branch details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in ManageBranchDetailsApiService(createBranches) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating branch details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageBranchDetailsApiService(createBranches) >> Exit");
		return response;
	}
	
	@GetMapping(value = "/getallbranchesbyorgid/{id}", headers = "Accept=application/json")
	@ResponseBody
	public String getAllBranchesByOrgId(@PathVariable("id") final Long id) {
		logger.info("ManageBranchDetailsApiService(getAllBranchesByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final List<ManageBranchDetails> details = manageBranchDetailsService.getBranchesByOrgId(id);
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
					"Exception occured in ManageBranchDetailsApiService(getAllBranchesByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting Branch details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageBranchDetailsApiService(getAllBranchesByOrgId) >> Exit");
		return response;
	}
	
	@PutMapping(value = "/Deletebranch", headers = "Accept=application/json")
	@ResponseBody
	public String deleteBranchbynameandorgid(@RequestBody String details) {
		logger.info("ManageBranchDetailsApiService(deleteBranchbynameandorgid) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			String branchName = newJsonObject.getString("branchName");
			newJsonObject.remove("branchName");
			long id = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");
			final int Details = manageBranchDetailsService.deleteBranchbynameandorgid(branchName,id);
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
					"Exception occured in ManageBranchDetailsApiService(deleteBranchbynameandorgid) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting Branch details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageBranchDetailsApiService(deleteBranchbynameandorgid) >> Exit");
		return response;
	}
	
	@PutMapping(value = "/updateBranchDetails", headers = "Accept=application/json")
	@ResponseBody
	public String updateBranchDetails(@RequestBody String details) {
		logger.info("ManageBranchDetailsApiService(updateBranchDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long orgId = newJsonObject.getLong("org_id");
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("org_id");
			newJsonObject.remove("id");
			OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
			logger.debug("ManageBranchDetailsApiService(updateBranchDetails) >> Request");
			final ManageBranchDetails  branchdetails = MapperUtil.readAsObjectOf(ManageBranchDetails.class, newJsonObject.toString());
			ManageBranchDetails newBranchDetails = manageBranchDetailsService.getBranchById(Id);
			newBranchDetails.setOrgid(orgId);
			newBranchDetails.setBranch(branchdetails.getBranch());
			final ManageBranchDetails details1 = manageBranchDetailsService.updateBranchDetails(newBranchDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Branch details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating branch details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("ManageBranchDetailsApiService(updateBranchDetails) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in ManageBranchDetailsApiService(updateRoleDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating branch details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ManageBranchDetailsApiService(updateBranchDetails) >> Exit");
		return response;
	}
}
