package com.tcube.api.utils;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tcube.api.PropertiesConfig;
import com.tcube.api.model.EmployeeDetails;

public class MailSender {
	static PropertiesConfig config = null;
	private static Logger logger = (Logger) LogManager.getLogger(ForgetPasswordUtil.class);
	
	public static void sendEmail(String toAddress, String msg, String subject, String imageSource) {
		try {
			logger.debug("inside sendemail block"+toAddress+";"+msg+";"+subject);
			config = PropertiesConfig.getInstance();
			if (toAddress != null && !toAddress.equalsIgnoreCase("")) {
				Session session = formEmailSessionContent(config);
				MimeMessage mimeMessage;
				if (imageSource != null && !imageSource.equalsIgnoreCase("")) {
					mimeMessage = MailSender.formMutipartMimeMessage(session, toAddress, msg, subject,
							config.getUsername(), imageSource);
				} else {
					mimeMessage = MailSender.formMimeMessage(session, toAddress, msg, subject, config.getUsername());
				}
				send(session, config, mimeMessage);
			} else {
				System.out.println("To Address is empty");
			}
		} catch (Exception e) {
			logger.error(e+"");
		}
	}

	public static Session formEmailSessionContent(PropertiesConfig config) throws Exception {
		Properties props = new Properties();
		final String smtpHost = config.getsmtpHost();
		final String smtpPort = config.getsmtpPort();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", smtpPort);
		final String username = config.getUsername();
		final String password = config.getPassword();
		System.out.println(username + ";" + password);
		props.put("mail.smtp.user", username);
		props.put("mail.smtp.password", password);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props, null);
		return session;
	}
	
	public static MimeMessage formMimeMessage(Session session, String email, String msg, String subject,
			String username) throws Exception {
		String[] strArray = null; 
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
//		message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccmail));
//		message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccmailtwo));
		message.setSubject(subject);
		message.setContent(msg, "text/html; charset=UTF-8");
		message.saveChanges();
		return message;
	}

	public static MimeMessage formMutipartMimeMessage(Session session, String email, String msg, String subject,
			String username, String imgSource) throws Exception {
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
		message.setSubject(subject);
		// This mail has 2 part, the BODY and the embedded image
		MimeMultipart multipart = new MimeMultipart("related");
		// first part (the html)
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(msg, "text/html");
		// add it
		multipart.addBodyPart(messageBodyPart);
		// second part (the image)
		messageBodyPart = new MimeBodyPart();
		DataSource fds = new FileDataSource(imgSource);
		messageBodyPart.setDataHandler(new DataHandler(fds));
		messageBodyPart.setHeader("Content-ID", "<image>");
		// add image to the multipart
		multipart.addBodyPart(messageBodyPart);
		// put everything together
		message.setContent(multipart);
		message.saveChanges();
		return message;
	}

	public static void send(Session session, PropertiesConfig config, MimeMessage message) throws MessagingException {
		Transport transport = session.getTransport("smtp");
		transport.connect(config.getsmtpHost(), config.getUsername(), config.getPassword());
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		File f= new File("/opt/Table.pdf");  
	    if(f.exists()) {
	    	f.delete();
	    }
	}

	/* old code */ 
	
//public static void mailToUser(EmployeeDetails empDetail) {
//	
//	logger.info("Mail send while creating user - Entry>>-> ");
//	String to = empDetail.getEmail();
//	String from = "postmaster@email.tcube.io";
//	String password = "924f1e555273daee0e67a30b4f776fb5-5e7fba0f-08265fbd";
//	String host = "smtp.mailgun.org";
//
//	Properties properties = new Properties();
//	properties.put("mail.smtp.auth", "true");
//	properties.put("mail.smtp.starttls.enable", "true");
//	properties.put("mail.smtp.host", host);
//	properties.put("mail.smtp.port", "587");
//
//	// get the session object and pass user name and password
//	Session session = Session.getDefaultInstance(properties, new Authenticator() {
//		protected PasswordAuthentication getPasswordAuthentication() {
//
//			return new PasswordAuthentication(from, password);
//		}
//	});
//        // Used to debug SMTP issues
//        session.setDebug(true);
//
//        try {
//            // Create a default MimeMessage object.
//            MimeMessage message = new MimeMessage(session);
//
//            // Set From: header field of the header.
//            message.setFrom(new InternetAddress(from));
//
//            // Set To: header field of the header.
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//
//            // Set Subject: header field
//            message.setSubject("T-CUBE | "+empDetail.getFirstname()+ " "+empDetail.getLastname() + " Your Account is Activated");
//
//            //actual message
//            message.setContent("<html>" + "<head>"
//					+ "<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css'>"
//					+ "<style> " + ".card {" + "  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);" + "  max-width: 300px;"
//					+ "  margin: auto;" + "  text-align: center;" + "  font-family: arial;" + "} " + ".title {"
//					+ "  color: grey;" + "  font-size: 18px;" + "}" + "button {" + "  border: none;" + "  outline: 0;"
//					+ "  display: inline-block;" + "  padding: 8px;" + "  color: white;" + "  background-color: #000;"
//					+ "  text-align: center;" + "  cursor: pointer;" + "  width: 100%;" + "  font-size: 18px;" + "}"
//					+ "a {" + "  text-decoration: none;" + "  font-size: 13px;" + "  color: black;" + "}"
//					+ "button:hover, a:hover {" + "  opacity: 0.7;" + "}" + "</style>" + "</head>" + "<body>"
//					+ "<div class='card'>"
//					+ "  <img src='http://tcube.cloud-space.io/assets/images/t-c.png' style='width:20%'>"
//					+ "  <p class='title'>Hi " + empDetail.getFirstname()+" "+empDetail.getLastname() + "!</p>"
//					+ "<h2 style='text-align:center;'>Your Account is Activated</h2>"
//					+ "  <p style='margin:0;font-size: 15px;'>Here is your login credentials.</p>"
//					+" <div style='margin: 24px 0 0 0;font-size: 13px;'>" + "<p style='font-size: 15px !important;'>Email: <b>" + empDetail.getEmail() + "</b></p>"
//					+ " <div style='margin:0;font-size: 15px;'>" + "<p>Password: <b>" + empDetail.getPassword() + "</b></p>"
//					+ "<p> To keep connected with us please login , Click "
//					+ "on this below button to login.</p>" + "  </div>"
//					+ "  <p><a href='http://tcube.cloud-space.io/#/login'><button style='cursor: pointer;'><span style='color:white;'>Login</span></button></a></p>"
//					+ "</div>" + "</body>" + "</html>", "text/html");
//            Transport.send(message);
//            logger.info("Email Sent Successfully to " + empDetail.getEmail());
//        } catch (MessagingException mex) {
//            mex.printStackTrace();
//        }
//        logger.info("Mail send while creating user -  Exit>>-> ");
//	}
//
//public static void mailToRegistration(EmployeeDetails empDetail) {
//	
//	logger.info("Mail send while Register the Registration form - Entry>>-> ");
//	String to = empDetail.getEmail();
//	String from = "postmaster@email.tcube.io";
//	String password = "924f1e555273daee0e67a30b4f776fb5-5e7fba0f-08265fbd";
//	String host = "smtp.mailgun.org";
//
//	Properties properties = new Properties();
//	properties.put("mail.smtp.auth", "true");
//	properties.put("mail.smtp.starttls.enable", "true");
//	properties.put("mail.smtp.host", host);
//	properties.put("mail.smtp.port", "587");
//
//	// get the session object and pass user name and password
//	Session session = Session.getDefaultInstance(properties, new Authenticator() {
//		protected PasswordAuthentication getPasswordAuthentication() {
//
//			return new PasswordAuthentication(from, password);
//		}
//	});
//        // Used to debug SMTP issues
//        session.setDebug(true);
//
//        try {
//            // Create a default MimeMessage object.
//            MimeMessage message = new MimeMessage(session);
//
//            // Set From: header field of the header.
//            message.setFrom(new InternetAddress(from));
//
//            // Set To: header field of the header.
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//
//            // Set Subject: header field
//            message.setSubject("T-CUBE | "+empDetail.getFirstname()+ " "+empDetail.getLastname() + " " + "Your account has been registered successfully");
//
//            //actual message
//            message.setContent("<html>" + "<head>"
//					+ "<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css'>"
//					+ "<style> " + ".card {" + "  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);" + "  max-width: 300px;"
//					+ "  margin: auto;" + "  text-align: center;" + "  font-family: arial;" + "} " + ".title {"
//					+ "  color: grey;" + "  font-size: 18px;" + "}" + "button {" + "  border: none;" + "  outline: 0;"
//					+ "  display: inline-block;" + "  padding: 8px;" + "  color: white;" + "  background-color: #000;"
//					+ "  text-align: center;" + "  cursor: pointer;" + "  width: 100%;" + "  font-size: 18px;" + "}"
//					+ "a {" + "  text-decoration: none;" + "  font-size: 13px;" + "  color: black;" + "}"
//					+ "button:hover, a:hover {" + "  opacity: 0.7;" + "}" + "</style>" + "</head>" + "<body>"
//					+ "<div class='card'>"
//					+ "  <img src='http://tcube.cloud-space.io/assets/images/t-c.png' style='width:20%'>"
//					+ "  <p class='title'>Hi " + empDetail.getFirstname()+" "+empDetail.getLastname() + "!</p>"
//					+ "<h2 style='text-align:center;'>Your Details has registered successfully</h2>"
//					+ "  <p style='margin:0;font-size: 15px;'>Here is your login credentials.</p>"
//					+" <div style='margin: 24px 0 0 0;font-size: 13px;'>" + "<p style='font-size: 15px !important;'>Email: <b>" + empDetail.getEmail() + "</b></p>"
//					+ " <div style='margin:0;font-size: 15px;'>" + "<p>Password: <b>" + empDetail.getPassword() + "</b></p>"
//					+ "<p> To keep connected with us please login , Click "
//					+ "on this below button to login.</p>" + "  </div>"
//					+ "  <p><a href='http://tcube.cloud-space.io/#/login'><button style='cursor: pointer;'><span style='color:white;'>Login</span></button></a></p>"
//					+ "</div>" + "</body>" + "</html>", "text/html");
//            Transport.send(message);
//            logger.info("Email Sent Successfully to " + empDetail.getEmail());
//        } catch (MessagingException mex) {
//            mex.printStackTrace();
//        }
//        logger.info("Mail send while creating user -  Exit>>-> ");
//	}

}
