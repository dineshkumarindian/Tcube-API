package com.tcube.api.dao;

import java.util.List;

import com.tcube.api.model.AppsIntegrationDetails;

public interface AppsIntegrationDetailsDao {

	public AppsIntegrationDetails createIntegration(AppsIntegrationDetails details);

	public AppsIntegrationDetails getById(Long id);

	public AppsIntegrationDetails updateIntegration(AppsIntegrationDetails newDetails);

	public List<AppsIntegrationDetails> getAllIntegrations();

	public List<AppsIntegrationDetails> getAllIntegrationByOrgId(Long id);

	public List<AppsIntegrationDetails> getActiveIntegrationByOrgId(Long id);

	public AppsIntegrationDetails getIntegrationByOrgIdAndModule(AppsIntegrationDetails newDetails);
	
	public String getSlackUrlLeaveTracker(Long id);
	
	public String getSlackUrlAttendance(Long id);

	public List<AppsIntegrationDetails> getActiveSlackIntegrationByOrgId(Long id);

	public List<AppsIntegrationDetails> getActiveWhatsappIntegrationByOrgId(Long id);

}
