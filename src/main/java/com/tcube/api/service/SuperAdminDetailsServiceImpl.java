package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.SuperAdminDetailsDao;
import com.tcube.api.model.SuperAdminDetails;

@Service
@Transactional
public class SuperAdminDetailsServiceImpl implements SuperAdminDetailsService{
	
	@Autowired
	SuperAdminDetailsDao superAdminDetailsDao;
	
	@Override
	public SuperAdminDetails createSuperAdminDetails(SuperAdminDetails admindetails) {
//		admindetails.setPassword(EncryptorUtil.encryptPropertyValue(admindetails.getPassword()));
		return superAdminDetailsDao.createSuperAdminDetails(admindetails);
	}

	@Override
	public SuperAdminDetails getAdminDetailsById(Long adminId) {
		return superAdminDetailsDao.getAdminDetailsById(adminId);
	}

	@Override
	public SuperAdminDetails updateSuperAdminDetails(SuperAdminDetails oldAdminDetails) {
		return superAdminDetailsDao.updateSuperAdminDetails(oldAdminDetails);
	}

	@Override
	public SuperAdminDetails deleteSuperAdminDetails(SuperAdminDetails oldAdminDetails) {
		return superAdminDetailsDao.deleteSuperAdminDetails(oldAdminDetails);
	}
	
	@Override
	public List<SuperAdminDetails> getAllSuperAdminDetails() {
		return superAdminDetailsDao.getAllSuperAdminDetails();
	}

	@Override
	public SuperAdminDetails authenticateSA(SuperAdminDetails admindetails) {
		return superAdminDetailsDao.authenticateSA(admindetails);
	}
	
	@Override
	public SuperAdminDetails updateSuperAdminPassword(SuperAdminDetails oldSADetails) {
		return superAdminDetailsDao.updateSuperAdminPassword(oldSADetails);
	}

}
