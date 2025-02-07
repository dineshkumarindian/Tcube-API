package com.tcube.api.service;

import java.util.List;

import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.SuperAdminDashboard;

public interface OrgDetailsService {

	public OrgDetails createOrgDetails(OrgDetails admindetails);

	public OrgDetails updateOrgDetails(OrgDetails oldDetails);

	public OrgDetails getOrgDetailsById(Long id);

	public OrgDetails deleteOrgDetails(OrgDetails oldDetails);

	public List<OrgDetails> getAllOrgDetails();

	public OrgDetails authenticateOrg(OrgDetails orgDetails);

	public List<OrgDetails> getInactiveOrgDetails();

	public List<OrgDetails> getActiveOrgDetails();
	
	public OrgDetails updateEmpid(String email,String empid);
	
	public OrgDetails updatePricingplanDetails(OrgDetails oldDetails);
	
	public List<SuperAdminDashboard> getTotalOrgCount();
	
	public boolean getRejectedOrgDetailsById(Long id);
	
	public boolean bulkDeleteRejectedOrg(Long id);
	
	public List<OrgDetails> getAllPendingDetails();
	
	public List<OrgDetails> getAllRejectDetails();

	public boolean VerifyAccount(Long id);

	public int[] TrialDetails();
}
