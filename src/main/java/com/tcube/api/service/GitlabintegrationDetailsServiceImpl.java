package com.tcube.api.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.GitlabIntegrationDetailsDao;
import com.tcube.api.model.GitlabIntegrationDetails;
import com.tcube.api.model.JiraIntegrationDetails;

@Service
@Transactional
public class GitlabintegrationDetailsServiceImpl implements GitlabIntegrationDetailsService{
	@Autowired
	GitlabIntegrationDetailsDao GitlabIntegrationDetailsDao;
	
	@Override
	public GitlabIntegrationDetails createGitlabCredentials(GitlabIntegrationDetails details) {
		return GitlabIntegrationDetailsDao.createGitlabCredentials(details);
	}

	@Override
	public GitlabIntegrationDetails getGitlabDetailsByOrgid(Long id) {
		// TODO Auto-generated method stub
		return GitlabIntegrationDetailsDao.getGitlabDetailsByOrgid(id);
	}

	@Override
	public GitlabIntegrationDetails getGitLabDetailsByid(Long id) {
		// TODO Auto-generated method stub
		return GitlabIntegrationDetailsDao.getGitLabDetailsByid(id);
	}

	@Override
	public GitlabIntegrationDetails updateGitLabDetails(GitlabIntegrationDetails details) {
		// TODO Auto-generated method stub
		return GitlabIntegrationDetailsDao.updateGitLabDetails(details);
	}

}
