package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.AppsIntegrationDetailsDao;
import com.tcube.api.model.AppsIntegrationDetails;

@Service
@Transactional
public class AppsIntegrationDetailsServiceImpl implements AppsIntegrationDetailsService{

	@Autowired
	AppsIntegrationDetailsDao appsIntegrationDetailsDao;

	@Override
	public AppsIntegrationDetails createIntegration(AppsIntegrationDetails details) {
		// TODO Auto-generated method stub
		return appsIntegrationDetailsDao.createIntegration(details);
	}

	@Override
	public AppsIntegrationDetails getById(Long id) {
		// TODO Auto-generated method stub
		return appsIntegrationDetailsDao.getById(id);
	}

	@Override
	public AppsIntegrationDetails updateIntegration(AppsIntegrationDetails newDetails) {
		// TODO Auto-generated method stub
		return appsIntegrationDetailsDao.updateIntegration(newDetails);
	}

	@Override
	public List<AppsIntegrationDetails> getAllIntegrations() {
		// TODO Auto-generated method stub
		return appsIntegrationDetailsDao.getAllIntegrations();
	}

	@Override
	public List<AppsIntegrationDetails> getAllIntegrationByOrgId(Long id) {
		// TODO Auto-generated method stub
		return appsIntegrationDetailsDao.getAllIntegrationByOrgId(id);
	}

	@Override
	public List<AppsIntegrationDetails> getActiveIntegrationByOrgId(Long id) {
		// TODO Auto-generated method stub
		return appsIntegrationDetailsDao.getActiveIntegrationByOrgId(id);
	}

	@Override
	public AppsIntegrationDetails getIntegrationByOrgIdAndModule(AppsIntegrationDetails newDetails) {
		// TODO Auto-generated method stub
		return appsIntegrationDetailsDao.getIntegrationByOrgIdAndModule(newDetails);
	}

	@Override
	public String getSlackUrlLeaveTracker(Long id) {
		// TODO Auto-generated method stub
		return appsIntegrationDetailsDao.getSlackUrlLeaveTracker(id);
	}

	@Override
	public List<AppsIntegrationDetails> getActiveSlackIntegrationByOrgId(Long id) {
		return appsIntegrationDetailsDao.getActiveSlackIntegrationByOrgId(id);
	}

	@Override
	public List<AppsIntegrationDetails> getActiveWhatsappIntegrationByOrgId(Long id) {
		return appsIntegrationDetailsDao.getActiveWhatsappIntegrationByOrgId(id);
	}
//	@Override
//	public String getSlackUrlTimeTracker(Long id) {
//		// TODO Auto-generated method stub
//		return appsIntegrationDetailsDao.getSlackUrlTimeTracker(id);
//	}

	@Override
	public String getSlackUrlAttendance(Long id) {
		// TODO Auto-generated method stub
		return appsIntegrationDetailsDao.getSlackUrlAttendance(id);
	}
}
