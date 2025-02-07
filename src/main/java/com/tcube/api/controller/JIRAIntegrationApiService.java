package com.tcube.api.controller;

import java.util.Date;
import java.util.Map;
import java.util.ArrayList;
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
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.tcube.api.model.JiraIntegrationDetails;
import com.tcube.api.service.JiraIntegrationDetailsService;
import com.tcube.api.utils.EncryptorUtil;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/jiraintegration" })
public class JIRAIntegrationApiService {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(JIRAIntegrationApiService.class);

	@Autowired
	JiraIntegrationDetailsService jiraIntegrationDetailsService;

	/**
	 * get jira issues by orgid and jql(JIRA Query Language)
	 * 
	 * @param details
	 * @param ucBuilder
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/getjiraissues", headers = "Accept=application/json")
	public String getJiraIssues(@RequestBody String details, final UriComponentsBuilder ucBuilder) {
		logger.info("JIRAIntegrationApiService(getJiraIssues) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);  
			Long orgid = newJsonObject.getLong("orgid");
			newJsonObject.remove("orgid");

			String email = newJsonObject.getString("email");
			newJsonObject.remove("email");
			
//			String jql = newJsonObject.getString("jql");
//			newJsonObject.remove("jql");
			
			String boardIdDetails = newJsonObject.getString("board_id_details");
			newJsonObject.remove("board_id_details");
			
			
			Long startAt = newJsonObject.getLong("startAt");
			newJsonObject.remove("startAt");
			
			Long maxResults = newJsonObject.getLong("maxResults");
			newJsonObject.remove("maxResults");
			
			JSONParser jsonParser = new JSONParser();
			JSONArray jsonArray = (JSONArray) jsonParser.parse(boardIdDetails);
			
			String jqlString;
			
//			JSONObject objectIdDetails = new JSONObject(boardIdDetails);  
			
			ArrayList s1= new ArrayList();
			
//			System.out.println(jsonArray);
			
			final JiraIntegrationDetails credDetails = jiraIntegrationDetailsService.getJiraCredByOrgid(orgid);
			
			for(int z=0;z<jsonArray.size();z++) {
			
			final JSONObject JsonObject = new JSONObject((Map) jsonArray.get(z));
			String projectName = JsonObject.getString("project_name").split(" ")[0];
			
			jqlString = "project IN "+ "("+ projectName + ")" + " AND assignee = "+"'"+ email +"'"+" AND status IN ('TO DO','IN PROGRESS') AND sprint IN openSprints()";

			HttpResponse<JsonNode> res = Unirest.get(EncryptorUtil.decryptPropertyValue(credDetails.getUrl())+"/rest/api/"+JsonObject.getInt("id")+"/search?startAt="+startAt+"&maxResults="+maxResults)
					.basicAuth(EncryptorUtil.decryptPropertyValue(credDetails.getEmail()), EncryptorUtil.decryptPropertyValue(credDetails.getToken()))
					.header("Accept", "application/json")
					.queryString("jql", jqlString)
					.asJson();
			s1.add(res.getBody());
			System.out.print("res...."+res.getStatusText());
			
			}
//			res.getStatusText().equals("OK")
			if (s1.size() > 0 ) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, s1);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get jira issues");
			}
			
//			}

			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			logger.error("Exception occured in JIRAIntegrationApiService(getJiraIssues) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting the jira issues");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JIRAIntegrationApiService(getJiraIssues) >> Exit");
		return response;
	}

	/**
	 * create jira credentials for the org using the email, token and baseurl of the
	 * org
	 * 
	 * @param details
	 * @param ucBuilder
	 * @return
	 */
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createJiraCredentials(@RequestBody String details, final UriComponentsBuilder ucBuilder) {
		logger.info("JIRAIntegrationApiService(createJiraCredentials) >> Entry");
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
			
			String projects = newJsonObject.getString("projects");
			newJsonObject.remove("projects");

			final JiraIntegrationDetails newDetails = new JiraIntegrationDetails();
			newDetails.setEmail(EncryptorUtil.encryptPropertyValue(email));
			newDetails.setToken(EncryptorUtil.encryptPropertyValue(token));
			newDetails.setUrl(EncryptorUtil.encryptPropertyValue(url));
			newDetails.setOrg_id(orgid);
			newDetails.setProjects(projects);
			newDetails.setCreated_time(new Date());
			newDetails.setModified_time(new Date());
			newDetails.setIs_deleted(false);

			final JiraIntegrationDetails crDetails = jiraIntegrationDetailsService.createJiraCredentials(newDetails);
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
	
	@PostMapping(value = "/getAllboardId", headers = "Accept=application/json")
	public String getAllboardDetails(@RequestBody String details) {
		logger.info("JIRAIntegrationApiService(getAllboardDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject(details);
	try {	
		String apiUrl = jsonObject.getString("apiurl");
		jsonObject.remove("apiurl");
		String userName = jsonObject.getString("username");
		jsonObject.remove("username");
		String password = jsonObject.getString("password");
		jsonObject.remove("password");
//		System.out.println("apiUrl.."+apiUrl+"userName..."+userName+"passowrd..."+password);
//		final JiraIntegrationDetails credDetails = jiraIntegrationDetailsService.getJiraCredByOrgid(orgid);
		
		
		HttpResponse<JsonNode> res = Unirest.get(apiUrl+"/rest/agile/1.0/board")
				.basicAuth(userName, password)
				.header("Accept", "application/json")
				.asJson();
//		System.out.println("res......"+ res );
		if (res.getStatusText().equals("OK")) {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA, res.getBody());
		} else {
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to get jira issues");
		}

		response = new Gson().toJson(jsonObject);
	} catch (Exception e) {
		logger.error("Exception occured in JIRAIntegrationApiService(getJiraIssues) and Exception details >> " + e);
		jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
		jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
		jsonObject.put(RestConstants.DATA, "Error in getting the jira issues");
		response = new Gson().toJson(jsonObject);
	}
	
	logger.info("JIRAIntegrationApiService(getJiraIssues) >> Exit");
	
	return response;
	
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/getBacklogDetails", headers = "Accept=application/json")
	public String getBacklogTickets(@RequestBody String details, final UriComponentsBuilder ucBuilder) {
		logger.info("JIRAIntegrationApiService(getBacklogDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details);  
			
			Long orgid = newJsonObject.getLong("orgid");
			newJsonObject.remove("orgid");
			
			Long startAt = newJsonObject.getLong("startAt");
			newJsonObject.remove("startAt");
			
			Long maxResults = newJsonObject.getLong("maxResults");
			newJsonObject.remove("maxResults");

//			String jql = newJsonObject.getString("jql");
//			newJsonObject.remove("jql");
			
			String boardIdDetails = newJsonObject.getString("board_id_details");
			newJsonObject.remove("board_id_details");
			
			JSONParser jsonParser = new JSONParser();
			JSONArray jsonArray = (JSONArray) jsonParser.parse(boardIdDetails);
			
//			JSONObject objectIdDetails = new JSONObject(boardIdDetails);  
			
			ArrayList s1= new ArrayList();
			
//			System.out.println(jsonArray);
			
			final JiraIntegrationDetails credDetails = jiraIntegrationDetailsService.getJiraCredByOrgid(orgid);
			
			for(int z=0;z<jsonArray.size();z++) {
			
			final JSONObject JsonObject = new JSONObject((Map) jsonArray.get(z));
			
			HttpResponse<JsonNode> res = Unirest.get(EncryptorUtil.decryptPropertyValue(credDetails.getUrl())+"/rest/agile/1.0/board/"+JsonObject.getInt("id")  +"/backlog?startAt="+startAt+"&maxResults="+maxResults)
					.basicAuth(EncryptorUtil.decryptPropertyValue(credDetails.getEmail()), EncryptorUtil.decryptPropertyValue(credDetails.getToken()))
					.header("Accept", "application/json")
//					.queryString("jql", jql)
					.asJson();
			s1.add(res.getBody());
			
			System.out.println("res...."+res.getStatusText());
			
			}
//			res.getStatusText().equals("OK")
			if (s1.size() > 0 ) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, s1);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to get jira issues");
			}
			
//			}
			
			response = new Gson().toJson(jsonObject);
		
		} catch (Exception e) {
			logger.error("Exception occured in JIRAIntegrationApiService(getJiraIssues) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting the jira issues");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JIRAIntegrationApiService(getBacklogDetails) >> Exit");
		return response;
		
	}
	

	/**
	 * get the jira credentials by orgid
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/getjiracredbyorgid/{id}", headers = "Accept=application/json")
	@ResponseBody
	public String getJiraCredentilsByOrgId(@PathVariable("id") final Long id) {
		logger.info("JIRAIntegrationApiService(getJiraCredentilsByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JiraIntegrationDetails details = jiraIntegrationDetailsService.getJiraCredByOrgid(id);
			JiraIntegrationDetails newDetails = new JiraIntegrationDetails();
			if (details != null) {
				newDetails.setEmail(EncryptorUtil.decryptPropertyValue(details.getEmail()));
				newDetails.setToken(EncryptorUtil.decryptPropertyValue(details.getToken()));
				newDetails.setUrl(EncryptorUtil.decryptPropertyValue(details.getUrl()));
				newDetails.setId(details.getId());
				newDetails.setCreated_time(details.getCreated_time());
				newDetails.setModified_time(details.getModified_time());
				newDetails.setOrg_id(details.getOrg_id());
				newDetails.setIs_deleted(details.getIs_deleted());
				newDetails.setProjects(details.getProjects());
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
					"Exception occured in JIRAIntegrationApiService(getJiraCredentilsByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting jira credentials");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JIRAIntegrationApiService(getJiraCredentilsByOrgId) >> Exit");
		return response;
	}

	/**
	 * get the jira credentials by id
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/getjiracredbyid/{id}", headers = "Accept=application/json")
	@ResponseBody
	public String getJiraCredentilsById(@PathVariable("id") final Long id) {
		logger.info("JIRAIntegrationApiService(getJiraCredentilsById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JiraIntegrationDetails details = jiraIntegrationDetailsService.getJiraCredByid(id);
			JiraIntegrationDetails newDetails = new JiraIntegrationDetails();
			if (details != null) {
				newDetails.setEmail(EncryptorUtil.decryptPropertyValue(details.getEmail()));
				newDetails.setToken(EncryptorUtil.decryptPropertyValue(details.getToken()));
				newDetails.setUrl(EncryptorUtil.decryptPropertyValue(details.getUrl()));
				newDetails.setId(details.getId());
				newDetails.setCreated_time(details.getCreated_time());
				newDetails.setModified_time(details.getModified_time());
				newDetails.setOrg_id(details.getOrg_id());
				newDetails.setIs_deleted(details.getIs_deleted());
				newDetails.setProjects(details.getProjects());
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
					"Exception occured in JIRAIntegrationApiService(getJiraCredentilsById) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting jira credentials");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JIRAIntegrationApiService(getJiraCredentilsById) >> Exit");
		return response;
	}

	/**
	 * update the jira credentials
	 * 
	 * @param request
	 * @return
	 */
	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateJiraCred(@RequestBody final String request) {
		logger.info("JIRAIntegrationApiService(updateJiraCred) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");

			final JiraIntegrationDetails oldDetails = jiraIntegrationDetailsService.getJiraCredByid(Id);

			final JiraIntegrationDetails newDetails = MapperUtil.readAsObjectOf(JiraIntegrationDetails.class,
					newJsonObject.toString());
			oldDetails.setEmail(EncryptorUtil.encryptPropertyValue(newDetails.getEmail()));
			oldDetails.setToken(EncryptorUtil.encryptPropertyValue(newDetails.getToken()));
			oldDetails.setUrl(EncryptorUtil.encryptPropertyValue(newDetails.getUrl()));
			oldDetails.setProjects(newDetails.getProjects());
			final JiraIntegrationDetails details = jiraIntegrationDetailsService.updateJiraCred(oldDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Jira credentials updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating jira credentials");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			logger.error(
					"Exception occured in JIRAIntegrationApiService(updateJiraCred) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating jira credentials");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JIRAIntegrationApiService(updateJiraCred) >> Exit");
		return response;

	}

	/**
	 * delete jira credentials by id
	 * 
	 * @param request
	 * @return
	 */
	@PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
	public String deleteTaksDetail(@PathVariable(value = "id") Long id) {
		{
			logger.info("JIRAIntegrationApiService(deleteJiraCred) >> Entry");
			String response = "";
			final JSONObject jsonObject = new JSONObject();
			try {

				final JiraIntegrationDetails oldDetails = jiraIntegrationDetailsService.getJiraCredByid(id);
				oldDetails.setIs_deleted(true);
				final JiraIntegrationDetails details = jiraIntegrationDetailsService.updateJiraCred(oldDetails);
				if (details != null) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
					jsonObject.put(RestConstants.DATA, "Jira credentials deleted successfully");
				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA, "Error in delete jira credentials");
				}
				response = new Gson().toJson(jsonObject);
			} catch (Exception e) {
				logger.error(
						"Exception occured in JIRAIntegrationApiService(oldDetails) and Exception details >> " + e);
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting jira credentials");
				response = new Gson().toJson(jsonObject);
			}
			logger.info("JIRAIntegrationApiService(deleteJiraCred) >> Exit");
			return response;
		}
	}
	
	/**
	 * get jira configured projects in the board by org_id 
	 * @param id
	 * @return
	 */
	
	@GetMapping(value = "/getjiraprojectsbyorgid/{id}", headers = "Accept=application/json")
	@ResponseBody
	public String getJiraProjectByOrgId(@PathVariable("id") final Long id) {
		logger.info("JIRAIntegrationApiService(getJiraProjectByOrgId) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final String details = jiraIntegrationDetailsService.getJiraProjects(id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, details);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in JIRAIntegrationApiService(getJiraProjectByOrgId) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting jira credentials");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("JIRAIntegrationApiService(getJiraProjectByOrgId) >> Exit");
		return response;
	}
}
