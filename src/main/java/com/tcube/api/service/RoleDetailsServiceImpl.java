package com.tcube.api.service;

import java.util.List;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.RoleDetailsDao;
import com.tcube.api.model.RoleDetails;
import org.json.JSONArray;

@Service
@Transactional
public class RoleDetailsServiceImpl implements RoleDetailsService{

	@Autowired
	RoleDetailsDao RoleDetailsDao; 
	@Override
	public RoleDetails createRoleDetails(RoleDetails roledetails) {
		return RoleDetailsDao.createRoleDetails(roledetails);
	}
	@Override
	public RoleDetails getRoleDetailsById(Long id) {
		return RoleDetailsDao.getRoleDetailsById(id);
	}
	@Override
	public RoleDetails updateRoleDetails(RoleDetails newDetails) {
		return RoleDetailsDao.updateRoleDetails(newDetails);
	}
	@Override
	public RoleDetails deleteRoleDetails(RoleDetails newRoleetails) {
		return RoleDetailsDao.deleteRoleDetails(newRoleetails);
	}
	@Override
	public List<RoleDetails> getAllRoleDetails() {
		return RoleDetailsDao.getAllRoleDetails();
	}
	@Override
	public List<RoleDetails> getRoleDetailsByOrgId(Long id) {
		return RoleDetailsDao.getRoleDetailsByOrgId(id);
	}
	@Override
	public RoleDetails getRoleDetailsByOrgidAndRoleId(Long orgid, Long roleid) {
		return RoleDetailsDao.getRoleDetailsByOrgidAndRoleId(orgid,roleid);
	}
	@Override
	public RoleDetails upgradeRoledetailsForOrgadmin(String roles, Long org_id) {
		return RoleDetailsDao.upgradeRoledetailsForOrgadmin(roles,org_id);
	}
	@Override
	public RoleDetails upgradeRoledetailsForEmployees(Long id, String roles) {
		return RoleDetailsDao.upgradeRoledetailsForEmployees(id,roles);
	}
	public int bulkDelete(JSONArray ids) {
		return RoleDetailsDao.bulkDelete(ids);
	}
  
	public RoleDetails getRoleByName(String details,Long id) {
		return RoleDetailsDao.getRoleByName(details,id);
	}

}
