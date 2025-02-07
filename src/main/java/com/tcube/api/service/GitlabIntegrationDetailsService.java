package com.tcube.api.service;

import com.tcube.api.model.GitlabIntegrationDetails;
import com.tcube.api.model.JiraIntegrationDetails;

public interface GitlabIntegrationDetailsService {

	public GitlabIntegrationDetails createGitlabCredentials(GitlabIntegrationDetails details);

	public GitlabIntegrationDetails getGitlabDetailsByOrgid(Long id);

	public GitlabIntegrationDetails getGitLabDetailsByid(Long id);

	public GitlabIntegrationDetails updateGitLabDetails(GitlabIntegrationDetails details);
	
}
