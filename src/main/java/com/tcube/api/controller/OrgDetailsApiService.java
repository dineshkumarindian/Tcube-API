package com.tcube.api.controller;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.tcube.api.model.*;
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

import com.google.gson.Gson;
import com.tcube.api.service.DesignationDetailsService;
import com.tcube.api.service.EmailService;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.PricingPlanService;
import com.tcube.api.service.RoleDetailsService;
import com.tcube.api.utils.EmailTemplateMapperUtil;
import com.tcube.api.utils.EmployeeIdGenenrator;
import com.tcube.api.utils.EncryptorUtil;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/OrgDetails" })
public class OrgDetailsApiService {

	private static Logger logger = LogManager.getLogger(OrgDetailsApiService.class);

	@Autowired
	OrgDetailsService orgDetailsService;

	@Autowired
	EmployeeDetailsService employeeDetailsService;

	@Autowired
	RoleDetailsService roleDetailsService;

	@Autowired
	DesignationDetailsService designationDetailsService;

	@Autowired
	PricingPlanService pricingPlanService;

	@Autowired
	EmailService emailService;

	// Create api for org details
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createOrgDetail(@RequestBody String detailsOfAdmin) {
		logger.info("OrgDetailsApiService(createOrgDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfAdmin);

			String loginUrl = newJsonObject.getString("login_str");
			newJsonObject.remove("login_str");

			long plan_id = newJsonObject.getLong("plan_id");
			newJsonObject.remove("plan_id");

			String modules = newJsonObject.getString("modules");
			newJsonObject.remove("modules");
			
			String url = newJsonObject.getString("url");
			newJsonObject.remove("url");

			final PricingPlanDetails plandetails = pricingPlanService.getPlanDetailsById(plan_id);
			final OrgDetails admindetails = MapperUtil.readAsObjectOf(OrgDetails.class, newJsonObject.toString());

			admindetails.setIs_deleted(false);
			char firstLetter = admindetails.getCompany_name().charAt(0);
			char secondLetter = admindetails.getCompany_name().charAt(1);
			String f_name = admindetails.getFirstname();
//			StringBuilder sb = new StringBuilder();
//			String emp_id = sb.append(firstLetter).append(secondLetter).toString().toUpperCase() + f_name.toUpperCase() + "#" +"ADMIN";
//			admindetails.setEmp_id(emp_id);
			admindetails.setEmp_id(null);
			admindetails.setIs_activated(true);
			admindetails.setPricingPlanDetails(plandetails);
            admindetails.setUserslimit(plandetails.getUserslimit());
            if (admindetails.getStatus().equals("Approved") || admindetails.getStatus().equals("Trial")) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(admindetails.getPricingPlanDetails().getDays()));
                admindetails.setExpiry_date(calendar.getTime());
            }
			/* admindetails.setStatus("Pending"); */
			final OrgDetails details = orgDetailsService.createOrgDetails(admindetails);

			// role details
			final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(details.getOrg_id());
			final RoleDetails roleDetails = new RoleDetails();
			roleDetails.setRole("OrgAdmin");
			roleDetails.setOrgDetails(orgDetails);
			roleDetails.setIs_deleted(false);
//			String str = "[ \"dashboard\" , \"project/jobs\" , \"time-tracker\" , \"attendance\" , \"settings\", \"leave-tracker\" , \"HR-letters\"]";
//			String str = details.getPricingPlanDetails().getModules();
			String str = modules;
			roleDetails.setAccess_to(str);
			roleDetails.setTotal_counts(1);
			roleDetails.setActive_total_counts(1);
			RoleDetails RDetails = roleDetailsService.createRoleDetails(roleDetails);

			// Designation details
			final DesignationDetails designationDetails = new DesignationDetails();
			designationDetails.setDesignation("Administrator");
			designationDetails.setDesignation_responsibilities("Organization Owner");
			designationDetails.setOrgDetails(orgDetails);
			designationDetails.setIs_deleted(false);
			DesignationDetails Ddetails = designationDetailsService.createDesignation(designationDetails);

			// create in employee details also

			final EmployeeDetails empDetails = new EmployeeDetails();
			empDetails.setEmail(admindetails.getEmail());
			empDetails.setFirstname(admindetails.getFirstname());
			empDetails.setLastname(admindetails.getLastname());
			empDetails.setRoleDetails(RDetails);
			empDetails.setDesignationDetails(designationDetails);
			empDetails.setPassword(EncryptorUtil.decryptPropertyValue(admindetails.getPassword()));
			empDetails.setOrgDetails(orgDetails);
			empDetails.setDate_of_joining(new Date());

			empDetails.setUser_login_type(RDetails.getRole());
			/* Emp Id generation */

			final List<EmployeeDetails> emp = employeeDetailsService.getAllEmployeeDetailsByOrgID(details.getOrg_id());
			Integer maxSeqId = emp.size();
			String idPrefix = EmployeeIdGenenrator.genarateId(admindetails.getCompany_name(),
					empDetails.getFirstname());
//			long maxSeqId = employeeDetailsService.getMaxSequenceId();
			if (maxSeqId >= 0) {
				long seqid = ++maxSeqId;
				empDetails.setSeq(seqid);
				String idSuffix = String.format(empDetails.getOrgDetails().getOrg_id() + "%02d", seqid);
				empDetails.setId(idPrefix + idSuffix);
			}
			empDetails.setIs_activated(true);
			if (details.getStatus().equals("Pending")) {
				try {
					String template = EmailTemplateMapperUtil.getOrgRegistrationMailTemplate(empDetails,url);
					String subject = "T-CUBE | " + empDetails.getFirstname() + " " + empDetails.getLastname()
							+ " Your organization account has been created successfully";
					emailService.sendEmail(empDetails.getEmail(), subject, template, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (details.getStatus().equals("Approved")) {
				try {
					String template = EmailTemplateMapperUtil.getApprovedOrgMailTemplate(details, "-", loginUrl);
					String subject = "T-CUBE | " + empDetails.getFirstname() + " " + empDetails.getLastname()
							+ " Your organization account has been approved successfully";
					emailService.sendEmail(empDetails.getEmail(), subject, template, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (details.getStatus().equals("Trial")) {
				try {
					String template = EmailTemplateMapperUtil.getTrialApprovedOrgMailTemplate(details, "-", loginUrl);
					String subject = "T-CUBE | " + empDetails.getFirstname() + " " + empDetails.getLastname()
							+ " Your Trial organization account has been approved successfully";
					emailService.sendEmail(empDetails.getEmail(), subject, template, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			EmployeeDetails Edetails = employeeDetailsService.createEmployeeDetails(empDetails);

			orgDetailsService.updateEmpid(Edetails.getEmail(), Edetails.getId());
			if (details != null && RDetails != null && Edetails != null && Ddetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org details created successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in creating org details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(createOrgDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in creating org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(createOrgDetail) >> Exit");
		return response;
	}

	// Update api for org detail
	@PutMapping(value = "/update", headers = "Accept=application/json")
	public String updateOrgDetail(@RequestBody final String detailsOfOrg) {
		logger.info("OrgDetailsApiService(updateOrgDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfOrg);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");

			final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(Id);
			String oldOrgEmail = oldDetails.getEmail();
			final OrgDetails newDetails = MapperUtil.readAsObjectOf(OrgDetails.class, newJsonObject.toString());
			oldDetails.setFirstname(newDetails.getFirstname());
			oldDetails.setLastname(newDetails.getLastname());
			oldDetails.setCompany_name(newDetails.getCompany_name());
			oldDetails.setEmail(newDetails.getEmail());
			oldDetails.setFirst_time(newDetails.getFirst_time());
			if (newDetails.getPassword() != null) {
				oldDetails.setPassword(EncryptorUtil.encryptPropertyValue(newDetails.getPassword()));
			}
//			oldDetails.setPassword(newDetails.getPassword());
			oldDetails.setDesc(newDetails.getDesc());
			final OrgDetails details = orgDetailsService.updateOrgDetails(oldDetails);

			final EmployeeDetails empDetails = employeeDetailsService.getEmployeeDetailsByEmail(oldOrgEmail);

			empDetails.setEmail(newDetails.getEmail());
			empDetails.setFirstname(newDetails.getFirstname());
			empDetails.setLastname(newDetails.getLastname());
			empDetails.setRoleDetails(empDetails.getRoleDetails());
			if (newDetails.getPassword() != null && newDetails.getPassword().length() > 0) {
				empDetails.setPassword(newDetails.getPassword());
			} else {
				empDetails.setPassword(EncryptorUtil.decryptPropertyValue(empDetails.getPassword()));
			}
			empDetails.setOrgDetails(details);
			EmployeeDetails Edetails = employeeDetailsService.updateEmployeeDetails(empDetails);

			if (details != null && Edetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org details details updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating org details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(updateOrgDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(updateOrgDetail) >> Exit");
		return response;
	}

	// delete api for delete the org details //
	@PutMapping(value = "/delete", headers = "Accept=application/json")
	public String deleteOrgDetail(@RequestBody final String details1) {
		logger.info("OrgDetailsApiService(deleteOrgDetail) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details1);
			Long id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			String comment = newJsonObject.getString("comments");
			newJsonObject.remove("comments");
			Integer check = 0;
			final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(id);
			oldDetails.setIs_deleted(true);
			if (comment == "") {
				oldDetails.setComments("-");
			} else {
				oldDetails.setComments(comment);
			}

			List<EmployeeDetails> newEmpDetails = employeeDetailsService.getAllEmployeeDetailsByOrgID(id);
			for (EmployeeDetails v : newEmpDetails) {
				v.setIs_deleted(true);
				v.setPassword(EncryptorUtil.decryptPropertyValue(v.getPassword()));
				EmployeeDetails newEmpDetails1 = employeeDetailsService.updateEmployeeDetails(v);
				if (newEmpDetails1 != null) {
					check += 1;
				}
			}
			final OrgDetails details = orgDetailsService.deleteOrgDetails(oldDetails);
			if (details != null && check == newEmpDetails.size()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting org details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(deleteOrgDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(deleteOrgDetail) >> Exit");
		return response;
	}

	@PutMapping(value = "/deleteRejectOrgDelete", headers = "Accept=application/json")
	public String deleteRejectOrgDetails(@RequestBody final String details1) {
		logger.info("OrgDetailsApiService(deleteOrgDetail) >> Entry");
		String response = "";
		boolean rejDetails = false;
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details1);
			Long orgId = newJsonObject.getLong("org_id");

			newJsonObject.remove("org_id");
			rejDetails = orgDetailsService.getRejectedOrgDetailsById(orgId);
			if (rejDetails != false) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org details deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting org details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(deleteOrgDetail) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(deleteOrgDetail) >> Exit");
		return response;
	}

	// bulk Delete //
	@PutMapping(value = "/bulkDelete", headers = "Accept=application/json")
	public String bulkDelete(@RequestBody final String details) {
		logger.info("OrgDetailsApiService(bulkDelete) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			for (int i = 0; i < deleteIds.length(); i++) {
				Long id = deleteIds.getLong(i);
				final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(id);
				oldDetails.setIs_deleted(true);
				List<EmployeeDetails> newEmpDetails = employeeDetailsService.getAllEmployeeDetailsByOrgID(id);
				for (EmployeeDetails v : newEmpDetails) {
					v.setIs_deleted(true);
					v.setPassword(EncryptorUtil.decryptPropertyValue(v.getPassword()));
					EmployeeDetails newEmpDetails1 = employeeDetailsService.updateEmployeeDetails(v);
				}
				final OrgDetails newdetails = orgDetailsService.deleteOrgDetails(oldDetails);
				if (newdetails != null) {
					check += 1;
				}
			}

			if (check == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org bulk deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting org details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(bulkDelete) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(bulkDelete) >> Exit");
		return response;
	}

	@PutMapping(value = "/bulkDeleteRejectedOrgs", headers = "Accept=application/json")
	public String bulkDeleteRejectedOrgs(@RequestBody final String details) {
		logger.info("OrgDetailsApiService(bulkDeleteRejectedOrgs) >> Entry");
		String response = "";
		boolean rejbulkDelete = false;
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			for (int i = 0; i < deleteIds.length(); i++) {
				Long id = deleteIds.getLong(i);
				rejbulkDelete = orgDetailsService.bulkDeleteRejectedOrg(id);
				if (rejbulkDelete != false) {
					check += 1;
				}
			}
			if (check == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org bulk deleted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deleting org details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(bulkDelete) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deleting org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(bulkDeleteRejectedOrgs) >> Exit");
		return response;
	}

	// bulk Deactivate //
	@PutMapping(value = "/bulkDeactivate", headers = "Accept=application/json")
	public String bulkDeactivate(@RequestBody final String details) {
		logger.info("OrgDetailsApiService(bulkDeactivate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			String comment = newJsonObject.getString("comments");
			newJsonObject.remove("comments");
			String url = newJsonObject.getString("url");
			newJsonObject.remove("url");
			for (int i = 0; i < deleteIds.length(); i++) {
				Long id = deleteIds.getLong(i);
				JSONArray idsOfActive = new JSONArray();
				final List<EmployeeDetails> activeEmpDetails = employeeDetailsService
						.getAllActiveEmployeeReportsByOrgId(id);
				for (EmployeeDetails v : activeEmpDetails) {
					idsOfActive.put(v.getId());
					v.setIs_activated(false);
					v.setPassword(EncryptorUtil.decryptPropertyValue(v.getPassword()));
					EmployeeDetails checkEmp = employeeDetailsService.updateEmployeeDetails(v);
				}

				final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(id);
				oldDetails.setIs_activated(false);
				if (comment == "") {
					oldDetails.setComments("-");
				} else {
					oldDetails.setComments(comment);
				}
				oldDetails.setDeactivated_ids(idsOfActive.toString());
				oldDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldDetails.getPassword()));
				final OrgDetails newDetails = orgDetailsService.updateOrgDetails(oldDetails);
				try {
					String template = EmailTemplateMapperUtil.getOrganizationDeactivateAccountMailTemplate(newDetails,
							comment,url);
					String subject = "T-CUBE | " + " Your Account is Deactivated ";
					emailService.sendEmail(newDetails.getEmail(), subject, template, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (newDetails != null) {
					check += 1;
				}
			}

			if (check == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org details deactivated successfully");

			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deactivate org details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(bulkDeactivate) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deactivate org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(bulkDeactivate) >> Exit");
		return response;
	}

	// bulk Activate //
	@PutMapping(value = "/bulkActivate", headers = "Accept=application/json")
	public String bulkActivate(@RequestBody final String details) {
		logger.info("OrgDetailsApiService(bulkActivate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			Integer check = 0;
			final JSONObject newJsonObject = new JSONObject(details);
			String loginUrl = newJsonObject.getString("login_str");
			newJsonObject.remove("login_str");
			JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
			String comment = newJsonObject.getString("comments");
			newJsonObject.remove("comments");
			for (int i = 0; i < deleteIds.length(); i++) {
				Long id = deleteIds.getLong(i);
				final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(id);
				oldDetails.setIs_activated(true);
				oldDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldDetails.getPassword()));
				String empIds = oldDetails.getDeactivated_ids();
				JSONArray empIdsArr = new JSONArray(empIds);
				oldDetails.setDeactivated_ids(null);
				final OrgDetails details1 = orgDetailsService.updateOrgDetails(oldDetails);
				if (details1 != null) {
					check += 1;
				}
				for (int v = 0; v < empIdsArr.length(); v++) {
					EmployeeDetails activateEmp = employeeDetailsService
							.getAllEmployeeDetailsByID(empIdsArr.getString(v));
					activateEmp.setIs_activated(true);
					if (comment == "") {
						oldDetails.setComments("-");
					} else {
						oldDetails.setComments(comment);
					}

					try {
						String template = EmailTemplateMapperUtil.getOrganizationActivateAccountMailTemplate(details1,
								comment, loginUrl);
						String subject = "T-CUBE | " + " Your Account is Activated ";
						emailService.sendEmail(details1.getEmail(), subject, template, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					activateEmp.setPassword(EncryptorUtil.decryptPropertyValue(activateEmp.getPassword()));
					employeeDetailsService.updateEmployeeDetails(activateEmp);
				}
			}

			if (check == deleteIds.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org details activated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in activate org details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(bulkActivate) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in activate org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(bulkActivate) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllOrgDetails", headers = "Accept=application/json")
	public String getAllOrgDetails() {
		logger.info("OrgDetailsApiService(getAllClientDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			List<OrgDetails> details = orgDetailsService.getAllOrgDetails();

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
			logger.error("Exception occured in OrgDetailsApiService(getAllOrgDetails) and Exception details >> " + e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(getAllOrgDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllPendingDetails", headers = "Accept=application/json")
	public String getAllPendingDetails() {
		logger.info("OrgDetailsApiService(getAllPendingDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			List<OrgDetails> details = orgDetailsService.getAllPendingDetails();

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
					"Exception occured in OrgDetailsApiService(getAllPendingDetails) and Exception details >> " + e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(getAllPendingDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getAllRejectDetails", headers = "Accept=application/json")
	public String getAllRejectDetails() {
		logger.info("OrgDetailsApiService(getAllRejectDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			List<OrgDetails> details = orgDetailsService.getAllRejectDetails();

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
					"Exception occured in OrgDetailsApiService(getAllRejectDetails) and Exception details >> " + e);

			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(getAllRejectDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getActiveOrgDetails", headers = "Accept=application/json")
	public String getActiveOrgDetails() {
		logger.info("OrgDetailsApiService(getActiveOrgDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			List<OrgDetails> details = orgDetailsService.getActiveOrgDetails();
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
					"Exception occured in OrgDetailsApiService(getActiveOrgDetails) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(getActiveOrgDetails) >> Exit");
		return response;
	}

	// To get inactive org details (is_deleted == false) //

	@GetMapping(value = "/getInactiveOrgDetails", headers = "Accept=application/json")
	public String getInactiveOrgDetails() {
		logger.info("OrgDetailsApiService(getInactiveOrgDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			List<OrgDetails> details = orgDetailsService.getInactiveOrgDetails();
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
					"Exception occured in OrgDetailsApiService(getInactiveOrgDetails) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(getInactiveOrgDetails) >> Exit");
		return response;
	}

	@GetMapping(value = "/getorgDetailsById/{id}", headers = "Accept=application/json")
	public String getOrgDetailsById(@PathVariable("id") final Long id) {
		logger.info("OrgDetailsApiService(getOrgDetailsById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			final OrgDetails details = orgDetailsService.getOrgDetailsById(id);
			details.setPassword(EncryptorUtil.decryptPropertyValue(details.getPassword()));
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
			logger.error("Exception occured in OrgDetailsApiService(getOrgDetailsById) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(getOrgDetailsById) >> Exit");
		return response;
	}

	// Api for activate the org
	@PutMapping(value = "/activateOrg", headers = "Accept=application/json")
	public String ActivateOrg(@RequestBody final String details1) {
		logger.info("OrgDetailsApiService(ActivateOrg) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(details1);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			String comment = newJsonObject.getString("comments");
			newJsonObject.remove("comments");
			String loginUrl = newJsonObject.getString("login_str");
			newJsonObject.remove("login_str");
			final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(Id);
			String oldOrgEmail = oldDetails.getEmail();
			oldDetails.setIs_activated(true);
//			oldDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldDetails.getPassword()));
			String empIds = oldDetails.getDeactivated_ids();
			JSONArray empIdsArr = new JSONArray(empIds);
			oldDetails.setDeactivated_ids(null);
			final OrgDetails details = orgDetailsService.updateOrgDetails(oldDetails);

            for (int v = 0; v < empIdsArr.length(); v++) {
                EmployeeDetails activateEmp = employeeDetailsService.getAllEmployeeDetailsByID(empIdsArr.getString(v));
                activateEmp.setIs_activated(true);
                if (comment == "") {
                    oldDetails.setComments("-");
                } else {
                    oldDetails.setComments(comment);
                }
				activateEmp.setPassword(null);
//                activateEmp.setPassword(EncryptorUtil.decryptPropertyValue(activateEmp.getPassword()));
                employeeDetailsService.updateEmployeeDetails(activateEmp);
            }
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, "Org details activated successfully");
                try {
				String template = EmailTemplateMapperUtil.getOrganizationActivateAccountMailTemplate(details, comment,
						loginUrl);
				String subject = "T-CUBE | " + " Your Account is Activated ";
				emailService.sendEmail(oldOrgEmail, subject,template,true);
                }catch(Exception e) {
                	e.printStackTrace();
                }
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in activate org details");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
            Sentry.captureException(e);
            logger.error("Exception occured in OrgDetailsApiService(ActivateOrg) and Exception details >> " + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in activate org details");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("OrgDetailsApiService(ActivateOrg) >> Exit");
        return response;
    }

	// Api for deactivate the org
	@PutMapping(value = "/deactivateOrg", headers = "Accept=application/json")
	public String DeactivateOrg(@RequestBody final String details1) {
		logger.info("OrgDetailsApiService(DeactivateOrg) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {

			/// to update the active employees ids in org details

			final JSONObject newJsonObject = new JSONObject(details1);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			String comment = newJsonObject.getString("comments");
			newJsonObject.remove("comments");
			String url = newJsonObject.getString("url");
			newJsonObject.remove("url");
			JSONArray idsOfActive = new JSONArray();
			final List<EmployeeDetails> activeEmpDetails = employeeDetailsService
					.getAllActiveEmployeeReportsByOrgId(Id);
			EmployeeDetails checkEmp = new EmployeeDetails();
			for (EmployeeDetails i : activeEmpDetails) {
				idsOfActive.put(i.getId());
				i.setIs_activated(false);
//				i.setPassword(EncryptorUtil.decryptPropertyValue(i.getPassword()));
				i.setPassword(null);
				checkEmp = employeeDetailsService.updateEmployeeDetails(i);
			}

			///
			final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(Id);
			oldDetails.setIs_activated(false);
			if (comment == "") {
				oldDetails.setComments("-");
			} else {
				oldDetails.setComments(comment);
			}
//			EncryptorUtil.decryptPropertyValue(oldDetails.getPassword())
			oldDetails.setDeactivated_ids(idsOfActive.toString());
////			oldDetails.setPassword(activeEmpDetails.get(0).getPassword());
			final OrgDetails details = orgDetailsService.updateOrgDetails(oldDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org details deactivated successfully");
				try {
					String template = EmailTemplateMapperUtil.getOrganizationDeactivateAccountMailTemplate(details,
							comment,url);
					String subject = "T-CUBE | " + " Your Account is Deactivated ";
					emailService.sendEmail(checkEmp.getEmail(), subject, template, true);
				}catch(Exception e) {
					e.printStackTrace();
				}
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in deactivate org details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(DeactivateOrg) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in deactivate org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(DeactivateOrg) >> Exit");
		return response;
	}

	// Update api for org detail
	@PutMapping(value = "/updateDefaultLeaveTyeStatus/{id}", headers = "Accept=application/json")
	public String updateDefaultLeaveTypeStatus(@PathVariable(value = "id") Long id) {
		logger.info("OrgDetailsApiService(updateDefaultLeaveTypeStatus) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(id);

			oldDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldDetails.getPassword()));
			oldDetails.setIs_leavetype_created(true);

			final OrgDetails details = orgDetailsService.updateOrgDetails(oldDetails);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org leave type status updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating org leave type status");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in OrgDetailsApiService(updateDefaultLeaveTypeStatus) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating org leave type status");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(updateDefaultLeaveTypeStatus) >> Exit");
		return response;
	}

	// Update api for org detail
	@PutMapping(value = "/updatestatus", headers = "Accept=application/json")
	public String updateOrgStatus(@RequestBody final String detailsOfOrg) {
		logger.info("OrgDetailsApiService(updateOrgStatus) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfOrg);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");

			String status = newJsonObject.getString("status");
			newJsonObject.remove("status");

			String comments = newJsonObject.getString("comments");
			newJsonObject.remove("comments");

			String loginUrl = newJsonObject.getString("login_str");
			newJsonObject.remove("login_str");

            final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(Id);
            String Email = oldDetails.getEmail();
            oldDetails.setStatus(status);

//            oldDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldDetails.getPassword()));

//           System.out.print(oldDetails.getPassword());
            if (comments == "") {
                oldDetails.setApporrejcomments("-");
            } else {
                oldDetails.setApporrejcomments(comments);
            }
            oldDetails.setDate_of_joining(new Date());
            oldDetails.setApprejtime(new Date());
            if (status.equals("Approved")) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(oldDetails.getPricingPlanDetails().getDays()));
                oldDetails.setExpiry_date(calendar.getTime());
            }
            final OrgDetails details = orgDetailsService.updateOrgDetails(oldDetails);

			final EmployeeDetails empDetails = employeeDetailsService.getEmployeeDetailsByEmail(Email);
			if (status.equals("Approved")) {
				try {
					String template = EmailTemplateMapperUtil.getApprovedOrgMailTemplate(details, comments, loginUrl);
					String subject = "T-CUBE | " + empDetails.getFirstname() + " " + empDetails.getLastname()
							+ " Your organization account has been approved successfully";
					emailService.sendEmail(empDetails.getEmail(), subject, template, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					String template = EmailTemplateMapperUtil.getRejectedOrgMailTemplate(details, comments,loginUrl);
					String subject = "T-CUBE | " + empDetails.getFirstname() + " " + empDetails.getLastname()
							+ " Your organization account rejected";
					emailService.sendEmail(empDetails.getEmail(), subject, template, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org details Status updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating org details status");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(updateOrgStatus) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating org details status");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(updateOrgStatus) >> Exit");
		return response;
	}

	// Update plan details api for org detail
	@PutMapping(value = "/updateplan", headers = "Accept=application/json")
	public String updateOrgPlan(@RequestBody final String detailsOfOrg) {
		logger.info("OrgDetailsApiService(updateOrgPlan) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(detailsOfOrg);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");

			Long planid = newJsonObject.getLong("plan_id");
			newJsonObject.remove("plan_id");

			String planrequest = newJsonObject.getString("planrequest");
			newJsonObject.remove("planrequest");

			String modules = newJsonObject.getString("modules");
			newJsonObject.remove("modules");
			
			String url = newJsonObject.getString("url");
			newJsonObject.remove("url");

            final PricingPlanDetails plandetails = pricingPlanService.getPlanDetailsById(planid);
            final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(Id);
            String Email = oldDetails.getEmail();
            oldDetails.setPlan(plandetails.getPlan());
            oldDetails.setPricingPlanDetails(plandetails);
            oldDetails.setApprejtime(new Date());
            Calendar calendar = Calendar.getInstance();
            if (planrequest.equals("renew")) {
                oldDetails.setStatus("Approved");
                oldDetails.setPlan_renewal(false);
                Date date1 = new Date();
                if(oldDetails.getExpiry_date().after(date1)){
                    // Convert Date to Calendar
                    Calendar exp_date = Calendar.getInstance();
                    exp_date.setTime(oldDetails.getExpiry_date());
                    // Calculate the difference in days
                    long differenceInMilliseconds =  exp_date.getTimeInMillis() - calendar.getTimeInMillis();
                    long differenceInDays = differenceInMilliseconds / (24 * 60 * 60 * 1000);
                    // Include the start and end dates in the count
                    int inclusiveDifferenceInDays = (int) (differenceInDays + 1);
                    calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(oldDetails.getPricingPlanDetails().getDays())+inclusiveDifferenceInDays);
                    oldDetails.setExpiry_date(calendar.getTime());
                }
                else {
                    calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(oldDetails.getPricingPlanDetails().getDays()));
                    oldDetails.setExpiry_date(calendar.getTime());
                }

            }
            if (planrequest.equals("upgrade")) {
                oldDetails.setStatus("Approved");
                if(Integer.parseInt(plandetails.getUserslimit()) > Integer.parseInt(oldDetails.getUserslimit())){
                    oldDetails.setUserslimit(plandetails.getUserslimit());
                }
                oldDetails.setPlan_upgrade(false);
                calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(oldDetails.getPricingPlanDetails().getDays()));
                oldDetails.setExpiry_date(calendar.getTime());
            }
            if(oldDetails.getStatus().equals("Expired")){
                oldDetails.setStatus("Approved");
                calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(oldDetails.getPricingPlanDetails().getDays()));
                oldDetails.setExpiry_date(calendar.getTime());
            }

//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.YEAR, 1);
//            oldDetails.setExpiry_date(calendar.getTime());
            oldDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldDetails.getPassword()));
            final OrgDetails details = orgDetailsService.updatePricingplanDetails(oldDetails);
            final RoleDetails roledetaisl = roleDetailsService.upgradeRoledetailsForOrgadmin(modules,
                    Id);
            final EmployeeDetails empDetails = employeeDetailsService.getEmployeeDetailsByEmail(Email);

			if (planrequest.equals("upgrade")) {
				try {
					String template = EmailTemplateMapperUtil.getupgradeOrgPlanMailTemplate(details,url);
					String subject = "T-CUBE | " + empDetails.getFirstname() + " " + empDetails.getLastname()
							+ " Your organization pricing plan details has been upgraded successfully";
					emailService.sendEmail(empDetails.getEmail(), subject, template, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org details plan updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating org plan details");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(updateOrgPlan) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating org plan details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(updateOrgPlan) >> Exit");
		return response;
	}

	// Get Count details (count for super admin dashboard)
	@GetMapping(value = "getTotalOrgDetailsCount", headers = "Accept=application/json")
	public String getTotalOrgCount() {
		logger.info("OrgDetailsApiService(getTotalOrgCount) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final List<SuperAdminDashboard> details = orgDetailsService.getTotalOrgCount();
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(getTotalOrgCount) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting org details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(getTotalOrgCount) >> Exit");
		return response;
	}

	// Update plan upgrade status api for org detail
	@PutMapping(value = "/updateplanupgradestatus/{id}", headers = "Accept=application/json")
	public String updatePlanUpgradeStauts(@PathVariable(value = "id") Long id) {
		logger.info("OrgDetailsApiService(updatePlanUpgradeStauts) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject();

			final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(id);
			String Email = oldDetails.getEmail();

            oldDetails.setPlan_upgrade(true);
//            oldDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldDetails.getPassword()));
            final OrgDetails details = orgDetailsService.updateOrgDetails(oldDetails);

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org admin plan upgrade request submitted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Request failed for plan upgrade by org admin");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error(
					"Exception occured in OrgDetailsApiService(updatePlanUpgradeStauts) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in submitting the request for the plan upgrade by the org admin");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(updatePlanUpgradeStauts) >> Exit");
		return response;
	}

	// Update plan renewal status api for org detail
	@PutMapping(value = "/updateplanrenewalstatus/{id}", headers = "Accept=application/json")
	public String updatePlanRenewaleStauts(@PathVariable(value = "id") Long id) {
		logger.info("OrgDetailsApiService(updatePlanRenewaleStauts) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject();

			final OrgDetails oldDetails = orgDetailsService.getOrgDetailsById(id);
			String Email = oldDetails.getEmail();

            oldDetails.setPlan_renewal(true);
//            oldDetails.setPassword(EncryptorUtil.decryptPropertyValue(oldDetails.getPassword()));
            final OrgDetails details = orgDetailsService.updateOrgDetails(oldDetails);

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Org admin plan renewal request submitted successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Request failed for plan renewal by org admin");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error(
					"Exception occured in OrgDetailsApiService(updatePlanUpgradeStauts) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in submitting the request for the plan renewal by the org admin");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(updatePlanRenewaleStauts) >> Exit");
		return response;
	}

	@GetMapping(value = "/trailDetails", headers = "Accept=application/json")
	public String getTrialDetails() {
		logger.info("OrgDetailsApiService(getTrialDetails) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final int[] details = orgDetailsService.TrialDetails();
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
			jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in OrgDetailsApiService(getTrialDetails) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting Trial details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("OrgDetailsApiService(getTrialDetails) >> Exit");
		return response;
	}

}
