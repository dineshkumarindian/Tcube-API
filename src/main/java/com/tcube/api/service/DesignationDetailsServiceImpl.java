package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.DesignationDetailsDao;
import com.tcube.api.model.DesignationDetails;

@Service
@Transactional
public class DesignationDetailsServiceImpl implements DesignationDetailsService{

	@Autowired
	DesignationDetailsDao designationDetailsDao;

	@Override
	public DesignationDetails createDesignation(DesignationDetails designationDetails) {
		return designationDetailsDao.createDesignation(designationDetails);
	}

	@Override
	public DesignationDetails getDesignationById(Long id) {
		return designationDetailsDao.getDesignationById(id);
	}

	@Override
	public DesignationDetails updateDesignation(DesignationDetails newDetails) {
		return designationDetailsDao.updateDesignation(newDetails);
	}

	@Override
	public DesignationDetails deleteDesignation(DesignationDetails newDetails) {
		return designationDetailsDao.deleteDesignation(newDetails);
	}

	@Override
	public List<DesignationDetails> getAllDesignation() {
		return designationDetailsDao.getAllDesignation();
	}

	@Override
	public List<DesignationDetails> getAllDesignationByOrgId(Long id) {
		// TODO Auto-generated method stub
		return designationDetailsDao.getAllDesignationByOrgId(id);
	}
	
	public int bulkDelete(JSONArray ids) {
		return designationDetailsDao.bulkDelete(ids);
	}
	
	 @Override
	public DesignationDetails getDesignationByName(String details, Long id) {
		// TODO Auto-generated method stub
		return designationDetailsDao.getDesignationByName(details,id);
	}
}
