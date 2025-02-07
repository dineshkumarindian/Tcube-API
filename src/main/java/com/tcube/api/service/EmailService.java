package com.tcube.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.EmailSenderDao;
import com.tcube.api.dao.EmailSenderDaoImpl;

@Service
public class EmailService {

	private final EmailSenderDao emailSenderDao;
	
    @Autowired
    public EmailService(@Qualifier("email") EmailSenderDaoImpl emailSenderDao) {
        this.emailSenderDao = emailSenderDao;
    }

    public Boolean sendEmail(String toAddress, String subject, String template, boolean isHtml) {
        return emailSenderDao.sendEmail(toAddress,subject,template,isHtml);
    }
}
