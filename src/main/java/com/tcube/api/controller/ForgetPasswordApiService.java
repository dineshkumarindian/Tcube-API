package com.tcube.api.controller;

import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.ForgetPasswordService;

import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.ForgetPassword;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/ForgetPassword" })
public class ForgetPasswordApiService {

	private static Logger logger = LogManager.getLogger(ForgetPasswordApiService.class);
	@Autowired
	ForgetPasswordService forgetpasswordService;
	
	@Autowired
	EmployeeDetailsService employeeDetailsService;

	@PostMapping(value = "/sendingMailToEmployee", headers = "Accept=application/json")
	public String sendMailToUser(@RequestBody String request) {
		logger.info("ForgetPasswordApiService(sendMailToUser) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		 String result = null;
		try {
			JSONObject newdetails = new JSONObject(request);
			String loginUrl = newdetails.getString("login_str");
			newdetails.remove("login_str");			
			String requestedEmail = newdetails.getString("to");
			newdetails.remove("to");
			 result = forgetpasswordService.sendMailToUser(requestedEmail,loginUrl);
			if (result != null && result == "OTP is successfully sent to your mail id") {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, result);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, result);
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in ForgetPasswordApiService(sendMailToUser) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in sending mail to the user");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ForgetPasswordApiService(sendMailToUser) >> Exit");
		return response;
	}
	
	@PostMapping(value = "/sendingMailToSA", headers = "Accept=application/json")
	public String sendMailToSA(@RequestBody ForgetPassword request) {
		logger.info("ForgetPasswordApiService(sendMailToSA) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final String result = forgetpasswordService.sendMailToSA(request.getTo());
			if (result != null && result == "Please check the email to reset the password!") {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, result);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, result);
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in ForgetPasswordApiService(sendMailToSA) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in sending mail to the user");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ForgetPasswordApiService(sendMailToSA) >> Exit");
		return response;
	}
	
	// Forgot password for Org Admin	
//	@PostMapping(value = "/sendMailToOrgAdmin", headers = "Accept=application/json")
//	public String sendMailToOrgAdmin(@RequestBody String request) {
//		logger.info("ForgetPasswordApiService(sendMailToOrgAdmin) >> Entry");
//		String response = "";
//		final JSONObject jsonObject = new JSONObject();
//		 String result = null;
//		try {
//			JSONObject newdetails = new JSONObject(request);
//			String loginUrl = newdetails.getString("login_str");
//			newdetails.remove("login_str");			
//			String requestedEmail = newdetails.getString("to");
//			newdetails.remove("to");
//			 result = forgetpasswordService.sendMailToOrgAdmin(requestedEmail,loginUrl);
//			if (result != null && result == "Please check the email to reset the password!") {
//				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//				jsonObject.put(RestConstants.DATA, result);
//			} else {
//				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//				jsonObject.put(RestConstants.DATA, result);
//			}
//			response = new Gson().toJson(jsonObject);
//		} catch (Exception e) {
//			Sentry.captureException(e);
//			logger.error("Exception occured in ForgetPasswordApiService(sendMailToOrgAdmin) and Exception details >> " + e);
//			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
//			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//			jsonObject.put(RestConstants.DATA, "Error in sending mail to the user");
//			response = new Gson().toJson(jsonObject);
//		}
//		logger.info("ForgetPasswordApiService(sendMailToOrgAdmin) >> Exit");
//		return response;
//	}
	
	// OTP verification for Employee
	@PostMapping(value = "/userOtpVerification", headers = "Accept=application/json")
	public String userOtpVerification(@RequestBody String request) {
		logger.info("ForgetPasswordApiService(userOtpVerification) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newdetails = new JSONObject(request);
			String email = newdetails.getString("mailId");
			newdetails.remove("mailId");			
			String otp = newdetails.getString("otp");
			newdetails.remove("otp");
			final String result = forgetpasswordService.userOtpVerification(otp,email);
			if (result != null && result == "OTP is verified successfully") {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, result);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, result);
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in ForgetPasswordApiService(userOtpVerification) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in verifying the otp given");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ForgetPasswordApiService(userOtpVerification) >> Exit");
		return response;
	}
	
	// OTP verification for Super Admin
	@PostMapping(value = "/superadminOtpVerification", headers = "Accept=application/json")
	public String superAdminOtpVerification(@RequestBody String request) {
		logger.info("ForgetPasswordApiService(superadminOtpVerification) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			JSONObject newdetails = new JSONObject(request);
			String email = newdetails.getString("mailId");
			newdetails.remove("mailId");			
			String otp = newdetails.getString("otp");
			newdetails.remove("otp");
			final String result = forgetpasswordService.superAdminOtpVerification(otp,email);
			if (result != null && result == "OTP is verified successfully") {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, result);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, result);
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in ForgetPasswordApiService(superadminOtpVerification) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in verifying the otp given");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ForgetPasswordApiService(superadminOtpVerification) >> Exit");
		return response;
	}
	
	// Update new password in forgot password page - emp and org
	@PutMapping(value = "/updateNewpassword", headers = "Accept=application/json")
	public String confirmNewpassword(@RequestBody String request) {
		logger.info("ForgetPasswordApiService(updateNewpassword) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		 String result = null;
		try {
			JSONObject newdetails = new JSONObject(request);
			String email = newdetails.getString("email");
			newdetails.remove("email");			
			String newPassword = newdetails.getString("newpassword");
			newdetails.remove("newpassword");
//			String confirmPassword = newdetails.getString("confirmpassword");
//			newdetails.remove("confirmpassword");
			result = forgetpasswordService.updateNewpassword(email,newPassword);
			
			if(result != null && result == "Your password has been successfully changed, Try to login with a new password!") {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, result);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, result);
			}
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
			logger.error("Exception occured in ForgetPasswordApiService(updateNewpassword) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in changing your password");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("ForgetPasswordApiService(updateNewpassword) >> Exit");
		return response;
	}
			
}
