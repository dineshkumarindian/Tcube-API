package com.tcube.api.service;

import java.util.List;

import org.json.JSONArray;

import com.tcube.api.model.RoleDetails;

public interface RoleDetailsService {

	public RoleDetails createRoleDetails(RoleDetails roledetails);

	public RoleDetails getRoleDetailsById(Long id);

	public RoleDetails updateRoleDetails(RoleDetails newDetails);

	public RoleDetails deleteRoleDetails(RoleDetails newRoleetails);

	public List<RoleDetails> getAllRoleDetails();

	public List<RoleDetails> getRoleDetailsByOrgId(Long id);

	public RoleDetails getRoleDetailsByOrgidAndRoleId(Long orgid,Long roleid);
	
	public RoleDetails upgradeRoledetailsForOrgadmin(String roles, Long org_id);
	
	public RoleDetails upgradeRoledetailsForEmployees(Long id, String roles);
	
	public int bulkDelete(JSONArray ids);
	
	public RoleDetails getRoleByName(String details,Long id);
}
