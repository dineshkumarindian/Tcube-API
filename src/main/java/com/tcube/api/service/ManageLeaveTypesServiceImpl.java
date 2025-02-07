package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.ManageLeaveTypesDao;
import com.tcube.api.model.ManageLeaveTypes;

@Service
@Transactional
public class ManageLeaveTypesServiceImpl implements ManageLeaveTypesService{

	@Autowired
	ManageLeaveTypesDao manageLeaveTypesDao;

	@Override
	public ManageLeaveTypes createLeaveType(ManageLeaveTypes details ,String timezone) {
		return manageLeaveTypesDao.createLeaveType(details , timezone);
	}

	@Override
	public ManageLeaveTypes getById(long id) {
		return manageLeaveTypesDao.getById(id);
	}

	@Override
	public ManageLeaveTypes updateLeaveType(ManageLeaveTypes oldDetails , String timezone) {
		return manageLeaveTypesDao.updateLeaveType(oldDetails ,timezone);
	}

	@Override
	public List<ManageLeaveTypes> getAllLeaveTypes() {
		return manageLeaveTypesDao.getAllLeaveTypes();
	}

	@Override
	public List<ManageLeaveTypes> getAllLeaveTypesByOrgId(Long id) {
		return manageLeaveTypesDao.getAllLeaveTypesByOrgId(id);
	}

	@Override
	public List<ManageLeaveTypes> getActiveLeaveTypesByOrgId(Long id) {
		return manageLeaveTypesDao.getActiveLeaveTypesByOrgId(id);
	}

	@Override
	public List<ManageLeaveTypes> getInactiveLeaveTypesByOrgId(Long id) {
		return manageLeaveTypesDao.getInactiveLeaveTypesByOrgId(id);
	}

	@Override
	public List<ManageLeaveTypes> getUndeletedLeaveTypesByOrgId(Long id) {
		// TODO Auto-generated method stub
		return manageLeaveTypesDao.getUndeletedLeaveTypesByOrgId(id);
	}
	
	@Override
	public List<ManageLeaveTypes> getActiveLeaveTypeByOrgIdAndDates(ManageLeaveTypes newDetails ,String timezone) {
		return manageLeaveTypesDao.getActiveLeaveTypeByOrgIdAndDates(newDetails , timezone);
	}

	@Override
	public ManageLeaveTypes updateLeaveTypeWithoutZone(ManageLeaveTypes newDetails) {
		return manageLeaveTypesDao.updateLeaveTypeWithoutZone(newDetails);
	}
	public int bulkDelete(JSONArray ids) {
		return manageLeaveTypesDao.bulkDelete(ids);
	}
	}
