package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.ReminderDetailsDao;
import com.tcube.api.model.ReminderDetails;

@Service
@Transactional
public class ReminderDetailsServiceImpl implements ReminderDetailsService{

	@Autowired
	ReminderDetailsDao reminderDetailsDao;

	@Override
	public ReminderDetails createReminderDetails(ReminderDetails reminderDetails , String zone) {
		return reminderDetailsDao.createReminderDetails(reminderDetails , zone);
	}

	@Override
	public ReminderDetails getById(Long id) {
		return reminderDetailsDao.getById(id);
	}

	@Override
	public ReminderDetails updateReminderDetails(ReminderDetails newDetails,String zone) {
		return reminderDetailsDao.updateReminderDetails(newDetails,zone);
	}

	@Override
	public List<ReminderDetails> getAllRemindersByOrgId(Long id) {
		return reminderDetailsDao.getAllRemindersByOrgId(id);
	}

	@Override
	public List<ReminderDetails> getReminderByOrgIdAndModule(ReminderDetails newDetails) {
		return reminderDetailsDao.getReminderByOrgIdAndModule(newDetails);
	}

	@Override
	public ReminderDetails updateReminderStatus(ReminderDetails newDetails) {
		// TODO Auto-generated method stub
		return reminderDetailsDao.updateReminderStatus(newDetails);
	}

	@Override
	public ReminderDetails createReminderWithoutZone(ReminderDetails reminderDetails) {
		// TODO Auto-generated method stub
		return reminderDetailsDao.createReminderWithoutZone(reminderDetails);
	}

	@Override
	public List<ReminderDetails> TodayUserListAccessUpdate(Long id) {
		// TODO Auto-generated method stub
		return reminderDetailsDao.TodayUserListAccessUpdate(id);
	}
	
}
