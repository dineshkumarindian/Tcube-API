package com.tcube.api.utils;

import java.security.SecureRandom;

import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.ForgetPassword;

public class ForgetPasswordUtil {
	@PersistenceContext
	private static EntityManager entityManager;
	private static Logger logger = (Logger) LogManager.getLogger(ForgetPasswordUtil.class);
	static ForgetPassword forgetPassword;

	public static String sendMailtoUser(String mailid, String firstName) {

		logger.info("ForgetPasswordDaoImpl Entry>>-> ");
		String senderEmail = "postmaster@email.tcube.io";
		String senderPassword = "924f1e555273daee0e67a30b4f776fb5-5e7fba0f-08265fbd";
		String host = "smtp.mailgun.org";
		boolean foo = false;
		String randomPassword = "";

		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587");

		// get the session object and pass user name and password
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication(senderEmail, senderPassword);
			}
		});

		try {
			// Generate Random Password
			final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
			SecureRandom random = new SecureRandom();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 6; i++) {
				int randomIndex = random.nextInt(chars.length());
				sb.append(chars.charAt(randomIndex));
			}
			randomPassword = sb.toString();
//			String firstName = employeedetails1.getFirstname();
			MimeMessage msg = new MimeMessage(session);
			logger.debug("appInfo obj:" + new Gson().toJson(forgetPassword));
			msg.setFrom(new InternetAddress(senderEmail));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mailid));
			msg.setSubject("RE: T-CUBE | Temporary Password to Login");
			msg.setContent("<html>" + "<head>"
					+ "<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css'>"
					+ "<style> " + ".card {" + "  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);" + "  max-width: 300px;"
					+ "  margin: auto;" + "  text-align: center;" + "  font-family: arial;" + "} " + ".title {"
					+ "  color: grey;" + "  font-size: 18px;" + "}" + "button {" + "  border: none;" + "  outline: 0;"
					+ "  display: inline-block;" + "  padding: 8px;" + "  color: white;" + "  background-color: #000;"
					+ "  text-align: center;" + "  cursor: pointer;" + "  width: 100%;" + "  font-size: 18px;" + "}"
					+ "a {" + "  text-decoration: none;" + "  font-size: 22px;" + "  color: black;" + "}"
					+ "button:hover, a:hover {" + "  opacity: 0.7;" + "}" + "</style>" + "</head>" + "<body>"
					+ "<div class='card'>"
					+ "  <img src='http://tcube.cloud-space.io/assets/images/t-c.png' style='width:25%'>"
					+ "  <p class='title'>Hi " + firstName + "!</p>"
					+ "<h2 style='text-align:center;'>Forgot Your Password?</h2>"
					+ "  <p>We have sent you this email in response to your request to reset your password.</p>"
					+ " <div style='margin: 24px 0;'>" + "<p>Your temporary password: <b>" + randomPassword + "</b></p>"
					+ "<p><span style='color:red'>*</span>This password will expire within <b>30 minutes</b>. Click "
					+ "on this below button to login. Once logged in, change your password.</p>" + "  </div>"
					+ "  <p><a href='http://tcube.cloud-space.io/#/login'><button><span style='color:white;'>Login</span></button></a></p>"
					+ "</div>" + "</body>" + "</html>", "text/html");
			Transport.send(msg);
			foo = true;
			logger.info("Email Sent Successfully to " + mailid);
		} catch (Exception e) {
			System.out.println("EmailService File Error" + e);
		}
		logger.info("ForgetPasswordDaoImpl Exit>>-> ");

		if (foo) {
			return randomPassword;
		} else {
			return "false";
		}
	}
}
