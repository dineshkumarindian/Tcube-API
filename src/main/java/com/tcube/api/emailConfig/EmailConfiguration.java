package com.tcube.api.emailConfig;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfiguration {

	private static Logger logger = LogManager.getLogger(EmailConfiguration.class);
	
    @Autowired
    private EmailProviderConfiguration providerConfiguration;
    
    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(providerConfiguration.getHost());
        javaMailSender.setPort(providerConfiguration.getPort());
        javaMailSender.setUsername(providerConfiguration.getUsername());
        javaMailSender.setPassword(providerConfiguration.getPassword());

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        logger.info("Email Configuration Initialized");
        return javaMailSender;
    }
}
