package com.tcube.api.dao;

import com.google.gson.Gson;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.ForgetPassword;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.SuperAdminDetails;
import com.tcube.api.service.EmailService;
import com.tcube.api.utils.EmailTemplateMapperUtil;
import com.tcube.api.utils.EncryptorUtil;
import com.tcube.api.utils.ForgetPasswordUtil;
import com.tcube.api.utils.MailSender;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class ForgetPasswordDaoImpl implements ForgetPasswordDao {

	@PersistenceContext
	private EntityManager entityManager;
	private static Logger logger = (Logger) LogManager.getLogger(ForgetPasswordDaoImpl.class);
	static ForgetPassword forgetPassword;
	
	@Autowired
	EmailService emailService;

//	@Override
//	public String sendMailToUser(String email,String loginUrl) {
//		String message = "";
//		String firstName = "";
//		String randomPassword = "";
////		List<SuperAdminDetails> superadminDetails = new ArrayList<SuperAdminDetails>();
//		System.out.println("40==>"+email);
//		final Session session = entityManager.unwrap(Session.class);
//		EmployeeDetails employeedetails1 =  new EmployeeDetails();
//		System.out.println("47==>");
//		final Query query = session.createQuery("from EmployeeDetails where email=:e and is_deleted=:i and is_activated=:j and (is_forgot_pwd_enabled=:a or is_forgot_pwd_enabled=:b)");
//		query.setParameter("e", email);
//		query.setParameter("i", false);
//		query.setParameter("j", true);
//		query.setParameter("a", true);
//		query.setParameter("b", false);
//		System.out.println("55==>"+employeedetails1+" ===> "+query.getResultList().size()+" ===> ");
//		try {
//			@SuppressWarnings("unchecked")
//		List<EmployeeDetails> employeedetails = query.getResultList();
////		System.out.println("58==>"+employeedetails+" ===> "+employeedetails.size());
//		if(query.getResultList().size() > 0) {
//			System.out.println("60==>Inside If");
//			for(int j=0;j<employeedetails.size();j++) {
////		if (employeedetails.get(j).getEmail().equals(email)) {
//			firstName = employeedetails.get(j).getFirstname();
//			// Generate Random Password
//			final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//			SecureRandom random = new SecureRandom();
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < 6; i++) {
//				int randomIndex = random.nextInt(chars.length());
//				sb.append(chars.charAt(randomIndex));
//			}				
//			randomPassword = sb.toString();				
//			employeedetails.get(j).setModified_time(new Date());
//			employeedetails.get(j).setIsForgotPwdEnabled(true);
//			employeedetails.get(j).setPassword(EncryptorUtil.encryptPropertyValue(randomPassword));
//			System.out.println("firstName "+firstName);
//			session.update(employeedetails);
//			System.out.println("randomPassword "+randomPassword);
//			String template = EmailTemplateMapperUtil.getForgetPasswordMailTemplate(employeedetails,loginUrl);
//			String subject = "RE: T-CUBE | Temporary Password to Login";
//			emailService.sendEmail(employeedetails1.getEmail(),subject, template,true);
////			MailSender.sendEmail(employeedetails1.getEmail(), template, subject, "");	
//				message = "Please check the email to reset the password!";
////				}
//		}
//		}
//		else {
//			System.out.println("87==>Inside else");
//			message="Email does not exist";
//		}
//		else {
//			final Query query2 = session.createQuery("from SuperAdminDetails where email=:e is_deleted=:j and (is_forgot_pwd_enabled=:a or is_forgot_pwd_enabled=:b)");
//			query2.setParameter("e", email);
//			query2.setParameter("i", false);
//			query2.setParameter("a", true);
//			query2.setParameter("b", false);
//			@SuppressWarnings("unchecked")
//			SuperAdminDetails superadmindetails = (SuperAdminDetails) query.getSingleResult();
//			if(superadmindetails !=null) {
//			if (superadmindetails.getEmail().equals(email)) {
//					firstName = superadmindetails.getFirstname();
//					String tempPassword = ForgetPasswordUtil.sendMailtoUser(email, firstName);
//
//					if (tempPassword == "false") {
//						message = "Failed to send mail to the user";
//					} else {
//						superadmindetails.setModified_time(new Date());
//						superadmindetails.setIsForgotPwdEnabled(true);
//						superadmindetails.setPassword(EncryptorUtil.encryptPropertyValue(tempPassword));
//						session.update(superadmindetails);
//						message = "Please check the email to reset the password!";
//					}
//				}
//			}  else {
//				message = "Email does not exist";
//			} 
//			} 
//		}catch(Exception e) {
//			System.out.println("Inside catch block "+e);
//			message = "Failed to send mail to the user";
//			logger.debug("Errors " + e);
//		}
//		
//		logger.info("ForgetPasswordDaoImpl(sendMailToUser) Exit>>");
//		return message;
//	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public String sendMailToUser(String email,String loginUrl) {
		logger.info("ForgetPasswordDaoImpl(sendMailToUser)>> Entry");
		String message = "";
		final Session session = entityManager.unwrap(Session.class);
		
//		final Query query = session.createQuery("from EmployeeDetails where email=:i and is_deleted=:j and (is_forgot_pwd_enabled=:a or is_forgot_pwd_enabled=:b) ");
		final Query query = session.createQuery("from EmployeeDetails where email=:i and is_deleted=:j");
		query.setParameter("i",email);
		query.setParameter("j",false);
		String firstName = "";
		final Query query1 = session.createQuery("from OrgDetails where email=:i and is_deleted=:j and (status=:k or status=:l)");
		query1.setParameter("i",email);
		query1.setParameter("j",false);
		query1.setParameter("k","Approved");
		query1.setParameter("l","Trial");
		EmployeeDetails employeedetails1 = null;
		List<OrgDetails> orgDetails = new ArrayList();
		String randomPassword = "";
		try {
			employeedetails1 = (EmployeeDetails) query.getSingleResult();
			orgDetails = query1.getResultList();
			 if (employeedetails1.getEmail().equals(email) && employeedetails1.getIs_deleted().equals(false) ) {
				firstName = employeedetails1.getFirstname();
				// Generate Random Password
				final String chars = "0123456789";
				SecureRandom random = new SecureRandom();
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 6; i++) {
					int randomIndex = random.nextInt(chars.length());
					sb.append(chars.charAt(randomIndex));
				}				
//				
				randomPassword = sb.toString();				
				employeedetails1.setModified_time(new Date());
				employeedetails1.setIsForgotPwdEnabled(true);
				employeedetails1.setMail_otp(EncryptorUtil.encryptPropertyValue(randomPassword));
				session.update(employeedetails1);
				String template = EmailTemplateMapperUtil.getForgetPasswordMailTemplate(employeedetails1,loginUrl);
				String subject = "RE: T-CUBE | OTP to reset the new password";
				emailService.sendEmail(employeedetails1.getEmail(),subject, template,true);
//				MailSender.sendEmail(employeedetails1.getEmail(), template, subject, "");
				if(orgDetails.size() != 0) {
				for(int i=0;i<orgDetails.size();i++) {
				if(orgDetails.get(i).getEmp_id().equals(employeedetails1.getId()) ) {
//					if (orgDetails.get(i).getEmail().equals(email)) {
						orgDetails.get(i).setModified_time(new Date());
						orgDetails.get(i).setIsForgotPwdEnabled(true);
						orgDetails.get(i).setMail_otp(EncryptorUtil.encryptPropertyValue(randomPassword));
						session.update(orgDetails.get(i));
					message = "OTP is successfully sent to your mail id";
			}
				}
				} else {
				message = "OTP is successfully sent to your mail id";
				}
			 }
		} catch (Exception e) {
			message = "Failed to send mail to the user";
			logger.debug("Errors " + e);
		}
		if (employeedetails1 == null && orgDetails.size() == 0) {
			message = "User email does not exist";
		}
		return message;
	}
	

	@Override
	public String sendMailToSA(String email) {
		String message = "";

		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from SuperAdminDetails where email=:userEmail");
		query.setParameter("userEmail", email);
		String firstName = "";
		SuperAdminDetails superadmindetails = null;

		try {
			superadmindetails = (SuperAdminDetails) query.getSingleResult();
			if (superadmindetails.getEmail().equals(email) && superadmindetails.getIs_deleted().equals(false)) {
				firstName = superadmindetails.getFirstname();
				String tempPassword = ForgetPasswordUtil.sendMailtoUser(email, firstName);

				if (tempPassword == "false") {
					message = "Failed to send mail to the user";
				} else {
					superadmindetails.setModified_time(new Date());
					superadmindetails.setIsForgotPwdEnabled(true);
					superadmindetails.setPassword(EncryptorUtil.encryptPropertyValue(tempPassword));
					session.update(superadmindetails);
					message = "Please check the email to reset the password!";
				}
			}
		} catch (Exception e) {
			logger.debug("Errors " + e);
		}
		if (superadmindetails == null) {
			message = "User email does not exist";
		}
		return message;
	}
	
//	@Override
//	public String sendMailToOrgAdmin(String email,String loginUrl) {
//	logger.info("ForgetPasswordDaoImpl(sendMailToGetOTP)>> Entry");
//	String message = "";
//	final Session session = entityManager.unwrap(Session.class);
//	final Query query = session.createQuery("from OrgDetails where email=:i");
//	query.setParameter("i",email);
//	String firstName = "";
//	OrgDetails orgDetails = null;
//	String randomPassword = "";
//	try {
//		orgDetails = (OrgDetails) query.getSingleResult();
//		if (orgDetails.getEmail().equals(email) && orgDetails.getIs_deleted().equals(false)) {
//			firstName = orgDetails.getFirstname();
//			// Generate Random Password
//			final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//			SecureRandom random = new SecureRandom();
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < 6; i++) {
//				int randomIndex = random.nextInt(chars.length());
//				sb.append(chars.charAt(randomIndex));
//			}				
//			randomPassword = sb.toString();				
//			orgDetails.setModified_time(new Date());
//			orgDetails.setIsForgotPwdEnabled(true);
//			orgDetails.setPassword(EncryptorUtil.encryptPropertyValue(randomPassword));
//			session.update(orgDetails);
//			
//			String template = EmailTemplateMapperUtil.getForgetPasswordMailTemplate(orgDetails,loginUrl);
//			String subject = "RE: T-CUBE | Temporary Password to Login";
//			emailService.sendEmail(orgDetails.getEmail(),subject, template,true);
////			MailSender.sendEmail(employeedetails1.getEmail(), template, subject, "");	
//				message = "Please check the email to reset the password!";
//		}
//	} catch (Exception e) {
//		message = "Failed to send mail";
//		logger.debug("Errors " + e);
//	}
//	if (orgDetails == null) {
//		message = "OrgAdmin email does not exist";
//	}
//	return message;
//}
	
	@SuppressWarnings("unchecked")
	@Override
	public String userOtpVerification(String otpDetails, String respectiveUserEmailId) {
		logger.info("ForgetPasswordDaoImpl(userOtpVerification) >> Entry ");
		String message = "";
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from EmployeeDetails where email=:i and is_deleted=:j and is_activated=:k and is_forgot_pwd_enabled=:l");
		query.setParameter("i", respectiveUserEmailId);
		query.setParameter("j", false);
		query.setParameter("k", true);
		query.setParameter("l", true);
		List<EmployeeDetails> employeedetails = null;
		try {
			employeedetails =  query.getResultList();
//			String employeedetails1 = EncryptorUtil.decryptPropertyValue(employeedetails.get(0).getMail_otp());
			if(EncryptorUtil.decryptPropertyValue(employeedetails.get(0).getMail_otp()).equals(otpDetails)) {
				message = "OTP is verified successfully";
			} else {
				message = "OTP is incorrect,Try again";
		}
		}catch (Exception e) {
			message = "OTP is incorrect,Try again";
			logger.debug("Errors " + e);
		}
		logger.info("ForgetPasswordDaoImpl(userOtpVerification) >> Exit ");
		return message;
	}
	
	@Override
	public String superAdminOtpVerification(String otpDetails, String respectiveSAEmailId) {
		logger.info("ForgetPasswordDaoImpl(superAdminOtpVerification) >> Entry ");
		String message = "";
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from SuperAdminDetails where email=:i");
		query.setParameter("i", respectiveSAEmailId);
		SuperAdminDetails superadminDetails = null;
		try {
			superadminDetails = (SuperAdminDetails) query.getSingleResult();
			if(EncryptorUtil.decryptPropertyValue(superadminDetails.getPassword()).equals(otpDetails) && superadminDetails.getIs_deleted().equals(false)) {
				message = "OTP is verified successfully";

			} else {
				message = "OTP is incorrect,Try again";
		}
		}catch (Exception e) {
			message = "OTP is incorrect,Try again";
			logger.debug("Errors " + e);
		}
		logger.info("ForgetPasswordDaoImpl(superAdminOtpVerification) >> Exit ");
		return message;
	}

	@Override
	public String updateNewpassword(String email, String password) {
		logger.info("ForgetPasswordDaoImpl(updateNewpassword) >> Entry ");
		String message = "";
		EmployeeDetails employeedetails = null;
		List<OrgDetails> orgDetails = null;
		SuperAdminDetails superadminDetails = null;
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from EmployeeDetails where email=:i and is_forgot_pwd_enabled =:j and is_deleted=:k");
		query.setParameter("i", email);
		query.setParameter("j", true);
		query.setParameter("k", false);
		employeedetails = (EmployeeDetails) query.getSingleResult();
		if(employeedetails != null) {
			employeedetails.setModified_time(new Date());
		try {
			employeedetails.setPassword(EncryptorUtil.encryptPropertyValue(password));
			employeedetails.setIsForgotPwdEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.update(employeedetails);
		final Query query1 = session.createQuery("from OrgDetails where email=:i and is_forgot_pwd_enabled =:j and is_deleted=:k");
		query1.setParameter("i", email);
		query1.setParameter("j", true);
		query1.setParameter("k", false);
		orgDetails = query1.getResultList();
		if(orgDetails.size() != 0) {
			orgDetails.get(0).setModified_time(new Date());
			try {
				orgDetails.get(0).setPassword(EncryptorUtil.encryptPropertyValue(password));
				orgDetails.get(0).setIsForgotPwdEnabled(false);
			} catch(Exception e) {
				e.printStackTrace();
			}
			session.update(orgDetails.get(0));
			message = "Your password has been successfully changed, Try to login with a new password!";
		} else {
		message = "Your password has been successfully changed, Try to login with a new password!";
		}
		} else {
			final Query query1 = session.createQuery("from SuperAdminDetails where email=:i and is_forgot_pwd_enabled =:j and is_deleted=:k");
			query1.setParameter("i", email);
			query1.setParameter("j", true);
			query1.setParameter("k", false);
			superadminDetails = (SuperAdminDetails) query1.getSingleResult();
			if(superadminDetails != null) {
				superadminDetails.setModified_time(new Date());
				try {
					superadminDetails.setPassword(EncryptorUtil.encryptPropertyValue(password));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				session.update(employeedetails);
				message = "Your password has been successfully changed";
				
			} else {
				message = "Password can't be changed..Try again with correct mailId";
			}
		}
		logger.info("ForgetPasswordDaoImpl(updateNewpassword) >> Exit ");
		return message;
	}
	
}
