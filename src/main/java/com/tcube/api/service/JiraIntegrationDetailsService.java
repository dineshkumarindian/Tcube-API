package com.tcube.api.service;

import com.tcube.api.model.JiraIntegrationDetails;

public interface JiraIntegrationDetailsService {

	public JiraIntegrationDetails createJiraCredentials(JiraIntegrationDetails details);
	
	public JiraIntegrationDetails getJiraCredByOrgid(Long id);
	
	public JiraIntegrationDetails updateJiraCred(JiraIntegrationDetails details);
	
	public boolean deleteJiraCred(Long id);
	
	public JiraIntegrationDetails getJiraCredByid(Long id);
	
	public String getJiraProjects(Long orgid);
}
