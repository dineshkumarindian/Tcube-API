package com.tcube.api.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.JiraIntegrationDetailsDao;
import com.tcube.api.model.JiraIntegrationDetails;

@Service
@Transactional
public class JiraIntegrationDetailsServiceImpl implements JiraIntegrationDetailsService{

	@Autowired
	JiraIntegrationDetailsDao jiraIntegrationDetailsDao;
	
	@Override
	public JiraIntegrationDetails createJiraCredentials(JiraIntegrationDetails details) {
		return jiraIntegrationDetailsDao.createJiraCredentials(details);
	}

	@Override
	public JiraIntegrationDetails getJiraCredByOrgid(Long id) {
		return jiraIntegrationDetailsDao.getJiraCredByOrgid(id);
	}

	@Override
	public JiraIntegrationDetails updateJiraCred(JiraIntegrationDetails details) {
		return jiraIntegrationDetailsDao.updateJiraCred(details);
	}

	@Override
	public boolean deleteJiraCred(Long id) {
		return jiraIntegrationDetailsDao.deleteJiraCred(id);
	}

	@Override
	public JiraIntegrationDetails getJiraCredByid(Long id) {
		return jiraIntegrationDetailsDao.getJiraCredByid(id);
	}

	@Override
	public String getJiraProjects(Long orgid) {
		return jiraIntegrationDetailsDao.getJiraProjects(orgid);
	}

}
