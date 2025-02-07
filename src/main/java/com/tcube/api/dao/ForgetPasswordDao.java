package com.tcube.api.dao;

public interface ForgetPasswordDao {

	public String sendMailToUser(String email, String loginUrl);

	public String sendMailToSA(String email);
	
//	public String sendMailToOrgAdmin(String email, String loginUrl);

	public String userOtpVerification(String otpDetails, String respectiveUserEmailId);
	
	public String superAdminOtpVerification(String otpDetails, String respectiveSAEmailId);
	
	public String updateNewpassword(String email, String password);
}
