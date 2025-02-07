package com.tcube.api.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.internet.MimeMessage.RecipientType;

import org.json.JSONObject;

import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.ForgetPassword;

public interface ForgetPasswordService {

	public String sendMailToUser(String email, String loginUrl);

	public String sendMailToSA(String email);
	
//	public String sendMailToOrgAdmin(String email, String loginUrl);

	public String userOtpVerification(String otpDetails, String respectiveUserEmailId);
	
	public String superAdminOtpVerification(String otpDetails, String respectiveSAEmailId);
	
	public String updateNewpassword(String email, String password);
}