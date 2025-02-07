package com.tcube.api.dao;

import com.tcube.api.model.JiraIntegrationDetails;

public interface JiraIntegrationDetailsDao {

	public JiraIntegrationDetails createJiraCredentials(JiraIntegrationDetails details);
	
	public JiraIntegrationDetails getJiraCredByid(Long id);
	
	public JiraIntegrationDetails getJiraCredByOrgid(Long id);
	
	public JiraIntegrationDetails updateJiraCred(JiraIntegrationDetails details);
	
	public boolean deleteJiraCred(Long id);
	
	public String getJiraProjects(Long orgid);
}
