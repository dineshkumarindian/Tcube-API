package com.tcube.api.dao;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.tcube.api.controller.TimeTrackerDetailsApiService;
import com.tcube.api.emailConfig.EmailProviderConfiguration;

@Service("email")
public class EmailSenderDaoImpl implements EmailSenderDao{

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private EmailProviderConfiguration providerConfiguration;
    
	private static Logger logger = LogManager.getLogger(EmailSenderDaoImpl.class);
	
	@Override
	public Boolean sendEmail(String toAddress, String subject, String template, boolean isHtml) {
		logger.info("EmailSenderDaoImpl(sendEmail) >> Entry");
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        MimeMessage message = sender.createMimeMessage();
        try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(toAddress);
			helper.setSubject(subject);
			helper.setText(template, isHtml);
			helper.setFrom(providerConfiguration.getSender());
			emailSender.send(message);
	        logger.info("EmailSenderDaoImpl(sendEmail) >> Exit");
			return true;
		} catch (MessagingException e1) {
			e1.printStackTrace();
			 return false;
		}
       
	}

	
}
