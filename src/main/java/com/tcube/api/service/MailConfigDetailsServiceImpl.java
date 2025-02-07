package com.tcube.api.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.MailConfigDetailsDao;
import com.tcube.api.model.MailConfigDetails;

@Service
@Transactional
public class MailConfigDetailsServiceImpl implements MailConfigDetailsService{

	@Autowired
	MailConfigDetailsDao MailConfigDetailsDao;

	@Override
	public MailConfigDetails createMailConfig(MailConfigDetails MailConfigDetails) {
		return MailConfigDetailsDao.createMailConfig(MailConfigDetails);
	}

	@Override
	public MailConfigDetails getMailConfigById(Long id) {
		return MailConfigDetailsDao.getMailConfigById(id);
	}

	@Override
	public MailConfigDetails updateMailConfig(MailConfigDetails newDetails) {
		return MailConfigDetailsDao.updateMailConfig(newDetails);
	}

	@Override
	public MailConfigDetails deleteMailConfig(MailConfigDetails newDetails) {
		return MailConfigDetailsDao.deleteMailConfig(newDetails);
	}

	

	@Override
	public MailConfigDetails getMailConfigByOrgId(Long id) {
		// TODO Auto-generated method stub
		return MailConfigDetailsDao.getMailConfigByOrgId(id);
	}
	
}
