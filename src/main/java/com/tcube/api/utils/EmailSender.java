package com.tcube.api.utils;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.tcube.api.model.MailConfigDetails;

public class EmailSender {
	private static Logger logger = (Logger) LogManager.getLogger(EmailSender.class);

	public Boolean sendEmail(MailConfigDetails credentials, String toAddress, String subject, String template, boolean isHtml) throws Exception {
		logger.info("EmailSender Utill(sendEmail) >> Entry");
//		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(EncryptorUtil.decryptPropertyValue(credentials.getHost()));
		sender.setPort(credentials.getPort());
		sender.setUsername(EncryptorUtil.decryptPropertyValue(credentials.getUsername()));
		sender.setPassword(EncryptorUtil.decryptPropertyValue(credentials.getPassword()));

		Properties properties = sender.getJavaMailProperties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		logger.info("Email Configuration Initialized");
		MimeMessage message = sender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(toAddress);
			helper.setSubject(subject);
			helper.setText(template, isHtml);
			helper.setFrom(EncryptorUtil.decryptPropertyValue(credentials.getSender()));
			sender.send(message);
			logger.info("EmailSender Utill(sendEmail) >> Exit");
			return true;
		} catch (MessagingException e1) {
			e1.printStackTrace();
			logger.info("exception in EmailSender Utill(sendEmail) >> Exit "+ e1);
			return false;
		}

	}
}
