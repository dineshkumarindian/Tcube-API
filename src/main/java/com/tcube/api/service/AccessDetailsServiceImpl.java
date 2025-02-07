package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.AccessDetailsDao;
import com.tcube.api.model.AccessDetails;

@Service
@Transactional
public class AccessDetailsServiceImpl  implements AccessDetailsService{

	@Autowired 
	AccessDetailsDao accessDetailsDao;

	@Override
	public AccessDetails createAccess(AccessDetails accessDetails) {
		return accessDetailsDao.createAccess(accessDetails);
	}

	@Override
	public AccessDetails getById(long id) {
		return accessDetailsDao.getById(id);
	}

	@Override
	public AccessDetails updateAccess(AccessDetails oldDetails) {
		return accessDetailsDao.updateAccess(oldDetails);
	}

	@Override
	public AccessDetails deleteAccessDetails(AccessDetails oldDetails) {
		return accessDetailsDao.deleteAccessDetails(oldDetails);
	}

	@Override
	public List<AccessDetails> getAllAccessDetails() {
		return accessDetailsDao.getAllAccessDetails();
	}

	@Override
	public List<AccessDetails> getAccessDetailsByOrgId(Long id) {
		return accessDetailsDao.getAccessDetailsByOrgId(id);
	}

	@Override
	public AccessDetails getByEmpId(String id) {
		return accessDetailsDao.getByEmpId(id);
	}
}
