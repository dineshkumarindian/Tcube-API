package com.tcube.api.controller;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.tcube.api.model.AuthenticatedDetails;
import com.tcube.api.model.CustomAuthenticateDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.SuperAdminDetails;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.SuperAdminDetailsService;
import com.tcube.api.utils.EncryptorUtil;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/login" })
public class AuthenticateApiService {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(AuthenticateApiService.class);

	@Autowired
	SuperAdminDetailsService superAdminDetailsService;

	@Autowired
	OrgDetailsService orgDetailsService;

	@Autowired
	EmployeeDetailsService employeeDetailsService;

	/**
	 * This service is to authenticate super admin.
	 *
	 * @param request
	 * @param ucBuilder
	 * @return
	 */
	@PostMapping(value = "/authenticateSA", headers = "Accept=application/json")
	public String AuthenticateSA(@RequestBody final String request, final UriComponentsBuilder ucBuilder) {
		logger.info("authenticateSuperAdmin(authenticateSuperAdmin) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final SuperAdminDetails admindetails = MapperUtil.readAsObjectOf(SuperAdminDetails.class, request);
			final SuperAdminDetails details = superAdminDetailsService.authenticateSA(admindetails);

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Super Admin Authentication success");
				CustomAuthenticateDetails data = new CustomAuthenticateDetails();
				data.setStatus("Approved");
				data.setOrgId(details.getId());
				data.setEmpId(details.getId().toString());
				data.setEmail(details.getEmail());
				data.setFirstName(details.getFirstname());
				data.setOrgName(details.getCompany_name());
				data.setRole("super_admin");
				data.setEmpRoleDeleted(false);
				jsonObject.put("details", data);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put("DATA", "Email and password mismatch.");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			logger.error(
					"Exception occured in OrgDetailsApiService(authenticateSuperAdmin) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in Super Admin authentication.");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AuthenticateApiService(authenticateSuperAdmin) >> Exit");
		return response;
	}

	/**
	 * This service is to authenticate Organization admin.
	 *
	 * @param request
	 * @param ucBuilder
	 * @return
	 */
	@PostMapping(value = "/authenticateorg", headers = "Accept=application/json")
	public String authenticateOrg(@RequestBody final String request, final UriComponentsBuilder ucBuilder) {
		logger.info("AuthenticateApiService(authenticateOrg) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final OrgDetails orgdetails = MapperUtil.readAsObjectOf(OrgDetails.class, request);
			final OrgDetails details = orgDetailsService.authenticateOrg(orgdetails);

			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Organization Authentication success");
				CustomAuthenticateDetails data = new CustomAuthenticateDetails();
				data.setStatus(details.getStatus());
				data.setOrgId(details.getOrg_id());
				data.setEmpId(details.getEmp_id());
				data.setEmail(details.getEmail());
				data.setFirstName(details.getFirstname());
				data.setOrgName(details.getCompany_name());
				data.setRole("org_admin");
				data.setEmpRoleDeleted(false);
				jsonObject.put("details", data);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Email and password mismatch.");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			logger.error("Exception occured in OrgDetailsApiService(authenticateOrg) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in Organization authentication.");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AuthenticateApiService(authenticateOrg) >> Exit");
		return response;
	}

	/**
	 * This service is to authenticate Employees.
	 *
	 * @param request
	 * @param ucBuilder
	 * @return
	 */
	@PostMapping(value = "/authenticateEmployee", headers = "Accept=application/json")
	public String authenticateEmployee(@RequestBody final String request, final UriComponentsBuilder ucBuilder) {
		logger.info("AuthenticateApiService(authenticateEmployee) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final EmployeeDetails employeedetails = MapperUtil.readAsObjectOf(EmployeeDetails.class, request);
			final EmployeeDetails details = employeeDetailsService.authenticateEmployee(employeedetails);
			if (details != null && details.getIs_activated().equals(true)) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Employee Authentication success");
				CustomAuthenticateDetails data = new CustomAuthenticateDetails();
				data.setStatus(details.getOrgDetails().getStatus());
				data.setOrgId(details.getOrgDetails().getOrg_id());
				data.setEmpId(details.getId());
				data.setEmail(details.getEmail());
				data.setFirstName(details.getFirstname());
				data.setOrgName(details.getOrgDetails().getCompany_name());
				data.setRole(details.getRoleDetails().getRole());
				data.setEmpRoleDeleted(details.getRoleDetails().getIs_deleted());
				jsonObject.put("details", data);
			} else if (details != null && details.getIs_activated().equals(false)) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Your account has been deactivated, Please contact your admin!");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Invalid login credentials");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			logger.error(
					"Exception occured in OrgDetailsApiService(authenticateEmployee) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in Employee authentication.");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AuthenticateApiService(authenticateEmployee) >> Exit");
		return response;
	}

	/**
	 * This service is to login authenticate.
	 *
	 * @param request
	 * @param ucBuilder
	 * @return
	 */
	@PostMapping(value = "/authenticate", headers = "Accept=application/json")
	public String authenticate(@RequestBody final String request, final UriComponentsBuilder ucBuilder) {
		logger.info("AuthenticateApiService(authenticate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final OrgDetails orgdetails = MapperUtil.readAsObjectOf(OrgDetails.class, request);
			final OrgDetails details = orgDetailsService.authenticateOrg(orgdetails);
			if(details !=null && details.getIs_activated().equals(true)) {
				if (EncryptorUtil.decryptPropertyValue(details.getPassword()).equals(orgdetails.getPassword())) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					AuthenticatedDetails data = new AuthenticatedDetails();
					data.setStatus(details.getStatus());
					data.setOrgId(details.getOrg_id());
					data.setEmpId(details.getEmp_id());
					data.setEmail(details.getEmail());
					data.setPassword(details.getPassword());
					data.setFirstName(details.getFirstname()+" "+details.getLastname());
					data.setOrgName(details.getCompany_name());
					data.setRole("org_admin");
					data.setEmpRoleDeleted(false);
					if (details.getStatus().equals("Expired")) {
						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
						jsonObject.put(RestConstants.DATA,"Your organization account "+details.getPlan()+" plan is expired, Please contact sales@servx.global");
						jsonObject.put("details", data);
					}else{
						boolean verify = orgDetailsService.VerifyAccount(details.getOrg_id());
						if(verify){
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
							jsonObject.put(RestConstants.DATA, "Organization Authentication success");
							jsonObject.put("details", data);
						}
						else{
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
							jsonObject.put(RestConstants.DATA,"Your organization account "+details.getPlan()+" plan is expired, Please contact sales@servx.global");
						    jsonObject.put("details", data);
						}

					}

				} else {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA,
							"Invalid credential, please check the password");
				}
			} else if(details != null && details.getIs_activated().equals(false)) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA,
						"Your organization account has been temporarily deactivated, Please contact sales@servx.global");
			} else if(details == null) {   // Authenticate Employee
				final EmployeeDetails employeedetails = MapperUtil.readAsObjectOf(EmployeeDetails.class, request);
				final EmployeeDetails detailsEmp = employeeDetailsService.authenticateEmployee(employeedetails);
				if (detailsEmp != null && detailsEmp.getIs_activated().equals(true)) {
					if(EncryptorUtil.decryptPropertyValue(detailsEmp.getPassword()).equals(employeedetails.getPassword())) {
						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//						jsonObject.put(RestConstants.DATA, "Employee Authentication success");
						AuthenticatedDetails data = new AuthenticatedDetails();
						data.setStatus(detailsEmp.getOrgDetails().getStatus());
						data.setOrgId(detailsEmp.getOrgDetails().getOrg_id());
						data.setEmpId(detailsEmp.getId());
						data.setEmail(detailsEmp.getEmail());
						data.setPassword(detailsEmp.getPassword());
						data.setFirstName(detailsEmp.getFirstname()+" "+detailsEmp.getLastname());
						data.setOrgName(detailsEmp.getOrgDetails().getCompany_name());
						data.setRole(detailsEmp.getRoleDetails().getRole());
						data.setEmpRoleDeleted(detailsEmp.getRoleDetails().getIs_deleted());
						if (detailsEmp.getOrgDetails().getStatus().equals("Expired")) {
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
							jsonObject.put(RestConstants.DATA,"Your organization account "+detailsEmp.getOrgDetails().getPlan()+" plan is expired, Please contact sales@servx.global");
							jsonObject.put("details", data);
						}else{
							boolean verify = orgDetailsService.VerifyAccount(detailsEmp.getOrgDetails().getOrg_id());
							if(verify){
								jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
								jsonObject.put(RestConstants.DATA, "Organization Authentication success");
								jsonObject.put("details", data);
							}
							else{
								jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
								jsonObject.put(RestConstants.DATA,"Your organization account "+detailsEmp.getOrgDetails().getPlan()+" plan is expired, Please contact sales@servx.global");
							    jsonObject.put("details", data);
							}

						}
					}else {
						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
						jsonObject.put(RestConstants.DATA,
								"Invalid credential, please check the password");
					}
				} else if (detailsEmp != null && detailsEmp.getIs_activated().equals(false)) {
					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
					jsonObject.put(RestConstants.DATA,
							"Your account has been deactivated, Please contact your admin!");
				} else if (detailsEmp == null){  // Authenticate Super Admin
					final SuperAdminDetails admindetails = MapperUtil.readAsObjectOf(SuperAdminDetails.class,
							request);
					final SuperAdminDetails detailsSA = superAdminDetailsService.authenticateSA(admindetails);
					if (detailsSA != null) {
						if(detailsSA.getPassword().equals(admindetails.getPassword())) {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
							jsonObject.put(RestConstants.DATA, "Super Admin Authentication success");
							AuthenticatedDetails data = new AuthenticatedDetails();
							data.setStatus("Approved");
							data.setOrgId(detailsSA.getId());
							data.setEmpId(detailsSA.getId().toString());
							data.setEmail(detailsSA.getEmail());
							data.setPassword(detailsSA.getPassword());
//							data.setPassword(EncryptorUtil.encryptPropertyValue(detailsSA.getPassword()));
							data.setFirstName(detailsSA.getFirstname()+" "+detailsSA.getLastname());
							data.setOrgName(detailsSA.getCompany_name());
							data.setRole("super_admin");
							data.setEmpRoleDeleted(false);
							jsonObject.put("details", data);
						} else {
							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
							jsonObject.put(RestConstants.DATA,
									"Invalid credential, please check the password");
						}
					} else {
						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
						jsonObject.put(RestConstants.DATA, "Error in Super Admin authentication.");
						response = new Gson().toJson(jsonObject);
					}
				}
			}
			response = new Gson().toJson(jsonObject);
		} catch(Exception e) {
			logger.error(
					"Exception occured in OrgDetailsApiService(authenticateSuperAdmin) and Exception details >> "
							+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "User doesnot exist");
			response = new Gson().toJson(jsonObject);
		}
		return response;
	}

	// To display the snackbar error message as "Invalid credential, please check the password" for invalid password with valid mail id

//	@PostMapping(value = "/authenticate", headers = "Accept=application/json")
//	public String authenticate(@RequestBody final String request, final UriComponentsBuilder ucBuilder) {
//		logger.info("AuthenticateApiService(authenticate) >> Entry");
//		String response = "";
//		final JSONObject jsonObject = new JSONObject();
//		try {
//			final OrgDetails orgdetails = MapperUtil.readAsObjectOf(OrgDetails.class, request);
//			final OrgDetails details = orgDetailsService.authenticateOrg(orgdetails);
//			if (details != null && details.getIs_activated().equals(true)) {
//				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//				jsonObject.put(RestConstants.DATA, "Organization Authentication success");
//				AuthenticatedDetails data = new AuthenticatedDetails();
//				data.setStatus(details.getStatus());
//				data.setOrgId(details.getOrg_id());
//				data.setEmpId(details.getEmp_id());
//				data.setEmail(details.getEmail());
//				data.setPassword(details.getPassword());
//				data.setFirstName(details.getFirstname()+" "+details.getLastname());
//				data.setOrgName(details.getCompany_name());
//				data.setRole("org_admin");
//				data.setEmpRoleDeleted(false);
//				jsonObject.put("details", data);
//			}else if(details != null && details.getIs_activated().equals(false)) {
//				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
//				jsonObject.put(RestConstants.DATA,
//						"Your organization account has been temporarily deactivated, Please contact sales@servx.global");
//			}
//			else {
//				try {
//					final EmployeeDetails employeedetails = MapperUtil.readAsObjectOf(EmployeeDetails.class, request);
//					final EmployeeDetails detailsEmp = employeeDetailsService.authenticateEmployee(employeedetails);
//					if (detailsEmp != null && detailsEmp.getIs_activated().equals(true)) {
//						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//						jsonObject.put(RestConstants.DATA, "Employee Authentication success");
//						AuthenticatedDetails data = new AuthenticatedDetails();
//						data.setStatus(detailsEmp.getOrgDetails().getStatus());
//						data.setOrgId(detailsEmp.getOrgDetails().getOrg_id());
//						data.setEmpId(detailsEmp.getId());
//						data.setEmail(detailsEmp.getEmail());
//						data.setPassword(detailsEmp.getPassword());
//						data.setFirstName(detailsEmp.getFirstname()+" "+detailsEmp.getLastname());
//						data.setOrgName(detailsEmp.getOrgDetails().getCompany_name());
//						data.setRole(detailsEmp.getRoleDetails().getRole());
//						data.setEmpRoleDeleted(detailsEmp.getRoleDetails().getIs_deleted());
//						jsonObject.put("details", data);
//					} else if (detailsEmp != null && detailsEmp.getIs_activated().equals(false)) {
//						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
//						jsonObject.put(RestConstants.DATA,
//								"Your account has been deactivated, Please contact your admin!");
//					} else {
//						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//						jsonObject.put(RestConstants.DATA, "Invalid login credentials");
//					}
//					response = new Gson().toJson(jsonObject);
//				} catch (Exception ex) {
//					try {
//						final SuperAdminDetails admindetails = MapperUtil.readAsObjectOf(SuperAdminDetails.class,
//								request);
//						final SuperAdminDetails detailsSA = superAdminDetailsService.authenticateSA(admindetails);
//
//						if (detailsSA != null) {
//							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//							jsonObject.put(RestConstants.DATA, "Super Admin Authentication success");
//							AuthenticatedDetails data = new AuthenticatedDetails();
//							data.setStatus("Approved");
//							data.setOrgId(detailsSA.getId());
//							data.setEmpId(detailsSA.getId().toString());
//							data.setEmail(detailsSA.getEmail());
//							data.setPassword(detailsSA.getPassword());
////							data.setPassword(EncryptorUtil.encryptPropertyValue(detailsSA.getPassword()));
//							data.setFirstName(detailsSA.getFirstname()+" "+detailsSA.getLastname());
//							data.setOrgName(detailsSA.getCompany_name());
//							data.setRole("super_admin");
//							data.setEmpRoleDeleted(false);
//							jsonObject.put("details", data);
//						} else {
//							jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//							jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//							jsonObject.put("DATA", "Email and password mismatch.");
//						}
//						response = new Gson().toJson(jsonObject);
//					} catch (Exception exp) {
//						logger.error(
//								"Exception occured in OrgDetailsApiService(authenticateSuperAdmin) and Exception details >> "
//										+ exp);
//						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
//						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//						jsonObject.put(RestConstants.DATA, "Error in Super Admin authentication.");
//						response = new Gson().toJson(jsonObject);
//					}
//				}
//			}
//			response = new Gson().toJson(jsonObject);
//		} catch (Exception e) {
//			try {
//				final EmployeeDetails employeedetails = MapperUtil.readAsObjectOf(EmployeeDetails.class, request);
//				final EmployeeDetails details = employeeDetailsService.authenticateEmployee(employeedetails);
//				if (details != null && details.getIs_activated().equals(true)) {
//					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//					jsonObject.put(RestConstants.DATA, "Employee Authentication success");
//					AuthenticatedDetails data = new AuthenticatedDetails();
//					data.setStatus(details.getOrgDetails().getStatus());
//					data.setOrgId(details.getOrgDetails().getOrg_id());
//					data.setEmpId(details.getId());
//					data.setEmail(details.getEmail());
//					data.setPassword(details.getPassword());
//					data.setFirstName(details.getFirstname()+" "+details.getLastname());
//					data.setOrgName(details.getOrgDetails().getCompany_name());
//					data.setRole(details.getRoleDetails().getRole());
//					data.setEmpRoleDeleted(details.getRoleDetails().getIs_deleted());
//					jsonObject.put("details", data);
//				} else if (details != null && details.getIs_activated().equals(false)) {
//					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
//					jsonObject.put(RestConstants.DATA, "Your account has been deactivated, Please contact your admin!");
//				} else {
//					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//					jsonObject.put(RestConstants.DATA, "Invalid login credentials");
//				}
//				response = new Gson().toJson(jsonObject);
//			} catch (Exception ex) {
//				try {
//					final SuperAdminDetails admindetails = MapperUtil.readAsObjectOf(SuperAdminDetails.class, request);
//					final SuperAdminDetails details = superAdminDetailsService.authenticateSA(admindetails);
//
//					if (details != null) {
//						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//						jsonObject.put(RestConstants.DATA, "Super Admin Authentication success");
//						AuthenticatedDetails data = new AuthenticatedDetails();
//						data.setStatus("Approved");
//						data.setOrgId(details.getId());
//						data.setEmpId(details.getId().toString());
//						data.setEmail(details.getEmail());
//						data.setPassword(details.getPassword());
////						data.setPassword(EncryptorUtil.encryptPropertyValue(details.getPassword()));
//						data.setFirstName(details.getFirstname()+" "+details.getLastname());
//						data.setOrgName(details.getCompany_name());
//						data.setRole("super_admin");
//						data.setEmpRoleDeleted(false);
//						jsonObject.put("details", data);
//					} else {
//						jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//						jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//						jsonObject.put("DATA", "Email and password mismatch.");
//					}
//					response = new Gson().toJson(jsonObject);
//				} catch (Exception exp) {
//					logger.error(
//							"Exception occured in OrgDetailsApiService(authenticateSuperAdmin) and Exception details >> "
//									+ e);
//					jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
//					jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//					jsonObject.put(RestConstants.DATA, "Error in Super Admin authentication.");
//					response = new Gson().toJson(jsonObject);
//				}
//			}
//		}
//		logger.info("AuthenticateApiService(authenticate) >> Exit");
//		return response;
//	}


	/**
	 * This service is to authenticate user data get.
	 *
	 * @param request
	 * @param ucBuilder
	 * @return
	 */
	@PostMapping(value = "/getauthenticatedUser", headers = "Accept=application/json")
	public String getauthenticatedUser(@RequestBody final String request, final UriComponentsBuilder ucBuilder) {
		logger.info("AuthenticateApiService(getauthenticatedUser) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject(request);
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			Long org_id = newJsonObject.getLong("org_id");
			String user_id= newJsonObject.getString("user_id");
			String role= newJsonObject.getString("role");
			AuthenticatedDetails data = new AuthenticatedDetails();
			if(role.equals("org_admin")) {
				final OrgDetails details = orgDetailsService.getOrgDetailsById(org_id);
				if(details.getStatus().equals("Expired")){
					data.setStatus("Expired");
				}
				else{
					boolean verify = orgDetailsService.VerifyAccount(org_id);
					if(verify){
						data.setStatus(details.getStatus());
					}else{
						data.setStatus("Expired");
					}
				}
				data.setOrgId(details.getOrg_id());
				data.setEmpId(details.getEmp_id());
				data.setEmail(details.getEmail());
				data.setPassword(details.getPassword());
				data.setFirstName(details.getFirstname()+" "+details.getLastname());
				data.setOrgName(details.getCompany_name());
				data.setRole("org_admin");
				data.setEmpRoleDeleted(false);
				jsonObject.put("details", data);
			} else if(role.equals("super_admin")) {
				final SuperAdminDetails detailsSA = superAdminDetailsService.getAdminDetailsById(org_id);
				data.setStatus("Approved");
				data.setOrgId(detailsSA.getId());
				data.setEmpId(detailsSA.getId().toString());
				data.setEmail(detailsSA.getEmail());
				data.setPassword(detailsSA.getPassword());
//				data.setPassword(EncryptorUtil.encryptPropertyValue(detailsSA.getPassword()));
				data.setFirstName(detailsSA.getFirstname()+" "+detailsSA.getLastname());
				data.setOrgName(detailsSA.getCompany_name());
				data.setRole("super_admin");
				data.setEmpRoleDeleted(false);
				jsonObject.put("details", data);

			} else {
				final EmployeeDetails details = employeeDetailsService.getAllEmployeeDetailsByID(user_id);
				if(details.getOrgDetails().getStatus().equals("Expired")){
					data.setStatus("Expired");
				}
				else{
					boolean verify = orgDetailsService.VerifyAccount(org_id);
					if(verify){
						data.setStatus(details.getOrgDetails().getStatus());
					}else{
						data.setStatus("Expired");
					}
				}
				data.setOrgId(details.getOrgDetails().getOrg_id());
				data.setEmpId(details.getId());
				data.setEmail(details.getEmail());
				data.setFirstName(details.getFirstname());
				data.setOrgName(details.getOrgDetails().getCompany_name());
				data.setPassword(details.getPassword());
				data.setRole(details.getRoleDetails().getRole());
				data.setEmpRoleDeleted(details.getRoleDetails().getIs_deleted());
				jsonObject.put("details", data);
			}
			if (data != null ) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Account found successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Account not found");
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			logger.error(
					"Exception occured in AuthenticateApiService(getauthenticatedUser) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in found a account.");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("AuthenticateApiService(authenticateEmployee) >> Exit");
		return response;
	}

}
