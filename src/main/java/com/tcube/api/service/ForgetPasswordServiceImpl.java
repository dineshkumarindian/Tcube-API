package com.tcube.api.service;

import org.json.JSONObject;
import javax.mail.Address;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.ForgetPasswordDao;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.ForgetPassword;

@Service
@Transactional
public class ForgetPasswordServiceImpl<Customer> implements ForgetPasswordService {

	@Autowired
	ForgetPasswordDao forgetPasswordDao;

	@Override
	public String sendMailToUser(String email,String loginUrl) {
		return forgetPasswordDao.sendMailToUser(email,loginUrl);
	}
	
	@Override
	public String sendMailToSA(String email) {
		return forgetPasswordDao.sendMailToSA(email);
	}
	
//	@Override
//	public String sendMailToOrgAdmin(String email, String loginUrl) {
//		return forgetPasswordDao.sendMailToOrgAdmin(email,loginUrl);
//	}
	
	@Override
	public String userOtpVerification(String otpDetails, String respectiveUserEmailId) {
		return forgetPasswordDao.userOtpVerification(otpDetails,respectiveUserEmailId);
	}
	
	@Override
	public String superAdminOtpVerification(String otpDetails, String respectiveSAEmailId) {
		return forgetPasswordDao.superAdminOtpVerification(otpDetails,respectiveSAEmailId);
	}
	
	@Override
	public String updateNewpassword(String email, String password) {
		return forgetPasswordDao.updateNewpassword(email, password);
	}

}
