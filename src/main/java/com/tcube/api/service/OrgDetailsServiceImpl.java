package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.OrgDetailsDao;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.SuperAdminDashboard;

@Service
@Transactional
public class OrgDetailsServiceImpl implements OrgDetailsService{

	@Autowired
	OrgDetailsDao orgDetailsDao;

	@Override
	public OrgDetails createOrgDetails(OrgDetails admindetails) {
		return orgDetailsDao.createOrgDetails(admindetails);
	}

	@Override
	public OrgDetails updateOrgDetails(OrgDetails oldDetails) {
		return orgDetailsDao.updateOrgDetails(oldDetails);
	}

	@Override
	public OrgDetails getOrgDetailsById(Long id) {
		return orgDetailsDao.getOrgDetailsById(id);
	}

	@Override
	public OrgDetails deleteOrgDetails(OrgDetails oldDetails) {
		return orgDetailsDao.deleteOrgDetails(oldDetails);
	}

	@Override
	public List<OrgDetails> getAllOrgDetails() {
		return orgDetailsDao.getAllOrgDetails();
	}

	@Override
	public OrgDetails authenticateOrg(OrgDetails orgDetails) {
		return orgDetailsDao.authenticateOrg(orgDetails);
	}

	@Override
	public List<OrgDetails> getInactiveOrgDetails() {
		// TODO Auto-generated method stub
		return orgDetailsDao.getInactiveOrgDetails();
	}

	@Override
	public List<OrgDetails> getActiveOrgDetails() {
		// TODO Auto-generated method stub
		return orgDetailsDao.getActiveOrgDetails();
	}

	@Override
	public OrgDetails updateEmpid(String email, String empid) {
		return orgDetailsDao.updateEmpid(email,empid);
	}

	@Override
	public OrgDetails updatePricingplanDetails(OrgDetails oldDetails) {
		return orgDetailsDao.updatePricingplanDetails(oldDetails);
	}

	@Override
	public List<SuperAdminDashboard> getTotalOrgCount() {
		// TODO Auto-generated method stub
		return orgDetailsDao.getTotalOrgCount();
	}

	@Override
	public boolean getRejectedOrgDetailsById(Long id) {
		// TODO Auto-generated method stub
		return orgDetailsDao.getRejectedOrgDetailsById(id);
	}

	@Override
	public boolean bulkDeleteRejectedOrg(Long id) {
		// TODO Auto-generated method stub
		return orgDetailsDao.bulkDeleteRejectedOrgs(id);
	}

	@Override
	public List<OrgDetails> getAllPendingDetails() {
		// TODO Auto-generated method stub
		return orgDetailsDao.getAllPendingDetails();
	}

	@Override
	public List<OrgDetails> getAllRejectDetails() {
		// TODO Auto-generated method stub
		return orgDetailsDao.getAllRejectDetails();
	}

	/**
	 * @return true or false based on trail verification
	 */
	@Override
	public boolean VerifyAccount(Long id) {
		return orgDetailsDao.VerifyAccount(id);
	}

	/**
	 * @return Array of long position represent the number of days of trail period and
	 */
	@Override
	public int[] TrialDetails() {
		return orgDetailsDao.TrialDetails();
	}
}
