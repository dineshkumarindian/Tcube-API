package com.tcube.api.service;

import java.util.List;

import com.tcube.api.model.ManageIntegration;

public interface ManageIntegrationService {

	public ManageIntegration createManageIntegration(ManageIntegration ManageIntegration);

	public ManageIntegration getManageIntegrationById(Long id);
	
	public ManageIntegration getOrgAMdetails(Long org_id, String app , String module);

	public ManageIntegration updateManageIntegration(ManageIntegration newDetails);

	public ManageIntegration deleteManageIntegration(ManageIntegration newDetails);

	public List<ManageIntegration> getManageIntegrationByOrgId(Long id);

	public int updateAllSlackIntegration(Long org_id, String app , String module);

}
