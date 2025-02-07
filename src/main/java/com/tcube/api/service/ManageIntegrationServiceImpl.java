package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.ManageIntegrationDao;
import com.tcube.api.model.ManageIntegration;

@Service
@Transactional
public class ManageIntegrationServiceImpl implements ManageIntegrationService{

	@Autowired
	ManageIntegrationDao ManageIntegrationDao;

	@Override
	public ManageIntegration createManageIntegration(ManageIntegration ManageIntegration) {
		return ManageIntegrationDao.createManageIntegration(ManageIntegration);
	}

	@Override
	public ManageIntegration getManageIntegrationById(Long id) {
		return ManageIntegrationDao.getManageIntegrationById(id);
	}

	@Override
	public ManageIntegration updateManageIntegration(ManageIntegration newDetails) {
		return ManageIntegrationDao.updateManageIntegration(newDetails);
	}

	@Override
	public ManageIntegration deleteManageIntegration(ManageIntegration newDetails) {
		return ManageIntegrationDao.deleteManageIntegration(newDetails);
	}

	

	@Override
	public List<ManageIntegration> getManageIntegrationByOrgId(Long id) {
		// TODO Auto-generated method stub
		return ManageIntegrationDao.getManageIntegrationByOrgId(id);
	}

	@Override
	public ManageIntegration getOrgAMdetails(Long org_id, String app, String module) {
		return ManageIntegrationDao.getOrgAMdetails(org_id, app , module);
	}

	@Override
	public int updateAllSlackIntegration(Long org_id, String app, String module) {
		// TODO Auto-generated method stub
		return ManageIntegrationDao.updateAllSlackIntegration(org_id, app, module);
	}
	
}
