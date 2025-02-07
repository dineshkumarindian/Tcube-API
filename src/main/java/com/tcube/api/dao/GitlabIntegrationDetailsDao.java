package com.tcube.api.dao;

import com.tcube.api.model.GitlabIntegrationDetails;
import com.tcube.api.model.JiraIntegrationDetails;

public interface GitlabIntegrationDetailsDao {

	public GitlabIntegrationDetails createGitlabCredentials(GitlabIntegrationDetails details);

	public GitlabIntegrationDetails getGitlabDetailsByOrgid(Long id);

	public GitlabIntegrationDetails getGitLabDetailsByid(Long id);

	public GitlabIntegrationDetails updateGitLabDetails(GitlabIntegrationDetails details);
}
