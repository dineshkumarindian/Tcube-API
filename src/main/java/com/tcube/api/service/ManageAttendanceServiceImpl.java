package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.ManageAttendanceDao;
import com.tcube.api.model.ActionCards;
import com.tcube.api.model.ManageAttendance;

@Service
@Transactional
public class ManageAttendanceServiceImpl implements ManageAttendanceService {

	@Autowired
	ManageAttendanceDao manageAttendanceDao;

	@Override
	public ManageAttendance createattendancecard(ManageAttendance details) {
		// TODO Auto-generated method stub
		return manageAttendanceDao.createattendancecard(details);
	}

	@Override
	public ManageAttendance updateattendancecard(ManageAttendance details) {
		// TODO Auto-generated method stub
		return manageAttendanceDao.updateattendancecard(details);
	}
	
	@Override
	public ManageAttendance getattendancecardById(Long id) {
		// TODO Auto-generated method stub
		return manageAttendanceDao.getattendancecardById(id);
	}

	@Override
	public List<ActionCards> getAllattendancecardByOrgId(Long orgId) {
		// TODO Auto-generated method stub
		return manageAttendanceDao.getAllattendancecardByOrgId(orgId);
	}

}
