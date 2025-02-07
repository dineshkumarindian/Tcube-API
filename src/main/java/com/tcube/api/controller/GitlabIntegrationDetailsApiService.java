package com.tcube.api.controller;

import java.util.Date;


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
import com.tcube.api.model.GitlabIntegrationDetails;
import com.tcube.api.model.JiraIntegrationDetails;
import com.tcube.api.service.GitlabIntegrationDetailsService;
import com.tcube.api.utils.EncryptorUtil;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/gitlabintegration" })
public class GitlabIntegrationDetailsApiService {
	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(GitlabIntegrationDetailsApiService.class);
	
	@Autowired
	GitlabIntegrationDetailsService GitlabIntegrationDetailsService;
	
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createGitlabCredentials(@RequestBody String details, final UriComponentsBuilder ucBuilder) {
		logger.info("GitlabIntegrationDetailsApiService(createGitlabCredentials) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);

			Long orgid = newJsonObject.getLong("org_id");
			newJsonObject.remove("org_id");

			String email = newJsonObject.getString("email");
			newJsonObject.remove("email");

			String token = newJsonObject.getString("token");
			newJsonObject.remove("token");

			String url = newJsonObject.getString("url");
			newJsonObject.remove("url");

			final GitlabIntegrationDetails newDetails = new GitlabIntegrationDetails();
			newDetails.setEmail(EncryptorUtil.encryptPropertyValue(email));
			newDetails.setAccess_token(EncryptorUtil.encryptPropertyValue(token));
			newDetails.setUrl(EncryptorUtil.encryptPropertyValue(url));
			newDetails.setOrg_id(orgid);
			newDetails.setCreated_time(new Date());
			newDetails.setModified_time(new Date());
			newDetails.setIs_deleted(false);
			newDetails.setGit_integration(true);

			final GitlabIntegrationDetails crDetails = GitlabIntegrationDetailsService.createGitlabCredentials(newDetails);
			if (crDetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "New Jira Credentials created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to create new jira credentials");
				response = new Gson().toJson(jsonObject);
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in JIRAIntegrationApiService(createJiraCredentials ) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating Jira credentials");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JIRAIntegrationApiService(createJiraCredentials) >> Exit");
		return response;
	}
	
//	To get gitlab credentials with org_id based
	@GetMapping(value = "/getgitlabdetailsbyorgid/{id}", headers = "Accept=application/json")
	@ResponseBody
	public String getJiraCredentilsByOrgId(@PathVariable("id") final Long id) {
		logger.info("GitlabIntegrationDetailsService(getgitlabDetailsByOrgid) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final GitlabIntegrationDetails details = GitlabIntegrationDetailsService.getGitlabDetailsByOrgid(id);
			GitlabIntegrationDetails newDetails = new GitlabIntegrationDetails();
			if (details != null) {
				newDetails.setEmail(EncryptorUtil.decryptPropertyValue(details.getEmail()));
				newDetails.setAccess_token(EncryptorUtil.decryptPropertyValue(details.getAccess_token()));
				newDetails.setUrl(EncryptorUtil.decryptPropertyValue(details.getUrl()));
				newDetails.setId(details.getId());
				newDetails.setCreated_time(details.getCreated_time());
				newDetails.setModified_time(details.getModified_time());
				newDetails.setOrg_id(details.getOrg_id());
				newDetails.setIs_deleted(details.getIs_deleted());
				newDetails.setGit_integration(details.getGit_integration());
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in GitlabIntegrationDetailsService(getgitlabDetailsByOrgid) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting jira credentials");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("GitlabIntegrationDetailsService(getgitlabDetailsByOrgid) >> Exit");
		return response;
	}
	
//	TO get gitlab details by id
	@GetMapping(value = "/getgitlabdetailsbyid/{id}", headers = "Accept=application/json")
	@ResponseBody
	public String getJiraCredentilsById(@PathVariable("id") final Long id) {
		logger.info("GitlabIntegrationDetailsService(getGitLabDetailsByID) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final GitlabIntegrationDetails details = GitlabIntegrationDetailsService.getGitLabDetailsByid(id);
			GitlabIntegrationDetails newDetails = new GitlabIntegrationDetails();
			if (details != null) {
				newDetails.setEmail(EncryptorUtil.decryptPropertyValue(details.getEmail()));
				newDetails.setAccess_token(EncryptorUtil.decryptPropertyValue(details.getAccess_token()));
				newDetails.setUrl(EncryptorUtil.decryptPropertyValue(details.getUrl()));
				newDetails.setId(details.getId());
				newDetails.setCreated_time(details.getCreated_time());
				newDetails.setModified_time(details.getModified_time());
				newDetails.setOrg_id(details.getOrg_id());
				newDetails.setIs_deleted(details.getIs_deleted());
				newDetails.setGit_integration(details.getGit_integration());
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in GitlabIntegrationDetailsService(getGitLabDetailsByID) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting jira credentials");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("GitlabIntegrationDetailsService(getGitLabDetailsByID) >> Exit");
		return response;
	}
	
//	Update git credential details  by id
	
	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateJiraCred(@RequestBody final String request) {
		logger.info("GitlabIntegrationDetailsService(updateGitLabDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");

			final GitlabIntegrationDetails oldDetails = GitlabIntegrationDetailsService.getGitLabDetailsByid(Id);

			final GitlabIntegrationDetails newDetails = MapperUtil.readAsObjectOf(GitlabIntegrationDetails.class,
					newJsonObject.toString());
			oldDetails.setEmail(EncryptorUtil.encryptPropertyValue(newDetails.getEmail()));
			oldDetails.setAccess_token(EncryptorUtil.encryptPropertyValue(newDetails.getAccess_token()));
			oldDetails.setUrl(EncryptorUtil.encryptPropertyValue(newDetails.getUrl()));
			final GitlabIntegrationDetails details = GitlabIntegrationDetailsService.updateGitLabDetails(oldDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Jira credentials updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating gitlab credentials");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			logger.error(
					"Exception occured in JIRAIntegrationApiService(updateJiraCred) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating gitlab credentials");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("GitlabIntegrationDetailsService(updateGitLabDetails) >> Exit");
		return response;

	}
//	yes or no update for git integration
	@PutMapping(value = "/updateGitLabIntegration", headers = "Accept=application/json")
	public String updateGitLabIntegration(@RequestBody final String request) {
		logger.info("GitlabIntegrationDetailsService(updateGitLabIntegration) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			
			Boolean gitlab_integration = newJsonObject.getBoolean("gitlab_integration");
			newJsonObject.remove("gitlab_integration");

			final GitlabIntegrationDetails oldDetails = GitlabIntegrationDetailsService.getGitLabDetailsByid(Id);

			final GitlabIntegrationDetails newDetails = MapperUtil.readAsObjectOf(GitlabIntegrationDetails.class,
					newJsonObject.toString());
			oldDetails.setGit_integration(gitlab_integration);
			final GitlabIntegrationDetails details = GitlabIntegrationDetailsService.updateGitLabDetails(oldDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Jira credentials updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating gitlab credentials");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			logger.error(
					"Exception occured in GitlabIntegrationDetailsService(updateJiraCred) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating gitlab credentials");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("GitlabIntegrationDetailsService(updateGitLabIntegration) >> Exit");
		return response;

	}
	
//	soft delete for git integration
	@PutMapping(value = "/getGitLabDetailsDeleteByid", headers = "Accept=application/json")
	public String deleteGitLabIntegration(@RequestBody final String request) {
		logger.info("GitlabIntegrationDetailsService(deleteGitLabIntegration) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			final GitlabIntegrationDetails oldDetails = GitlabIntegrationDetailsService.getGitLabDetailsByid(Id);

			final GitlabIntegrationDetails newDetails = MapperUtil.readAsObjectOf(GitlabIntegrationDetails.class,
					newJsonObject.toString());
			oldDetails.setIs_deleted(true);
			final GitlabIntegrationDetails details = GitlabIntegrationDetailsService.updateGitLabDetails(oldDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Jira credentials updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting gitlab credentials");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			logger.error(
					"Exception occured in GitlabIntegrationDetailsService(deleteGitLabIntegration) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting gitlab credentials");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("GitlabIntegrationDetailsService(deleteGitLabIntegration) >> Exit");
		return response;

	}


}
