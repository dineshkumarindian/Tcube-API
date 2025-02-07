package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.HolidayDetailsDao;
import com.tcube.api.model.HolidayDetails;

@Service
@Transactional
public class HolidayDetailsServiceImpl implements HolidayDetailsService{

	@Autowired
	HolidayDetailsDao holidayDetailsDao;

	@Override
	public HolidayDetails createHoliday(HolidayDetails details, String zone) {
		return holidayDetailsDao.createHoliday(details, zone);
	}

	@Override
	public HolidayDetails getById(long id) {
		// TODO Auto-generated method stub
		return holidayDetailsDao.getById(id);
	}

	@Override
	public HolidayDetails updateHoliday(HolidayDetails oldDetails, String zone) {
		return holidayDetailsDao.updateHoliday(oldDetails, zone);
	}

	@Override
	public HolidayDetails updateHolidayWithoutZone(HolidayDetails newDetails) {
		return holidayDetailsDao.updateHolidayWithoutZone(newDetails);
	}

	@Override
	public List<HolidayDetails> getAllHolidays() {
		return holidayDetailsDao.getAllHolidays();
	}

	@Override
	public List<HolidayDetails> getAllHolidaysByOrgId(Long id) {
		return holidayDetailsDao.getAllHolidaysByOrgId(id);
	}

	@Override
	public List<HolidayDetails> getActiveHolidaysByOrgId(Long id) {
		// TODO Auto-generated method stub
		return holidayDetailsDao.getActiveHolidaysByOrgId(id);
	}

//	public List<HolidayDetails> getActiveHolidaysByOrgIdAndDates(String orgId,String startDate,String endDate,String zone) {
//		// TODO Auto-generated method stub
//		return holidayDetailsDao.getActiveHolidaysByOrgIdAndDates(orgId,startDate,endDate, zone);
//	}
//
//	@Override
//	public List<HolidayDetails> getActiveHolidaysByOrgIdAndDates(HolidayDetails getNewDetails, String zone) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public List<HolidayDetails> getActiveHolidaysByOrgIdAndDates(Long orgId, String startDate, String endDate,
			String zone) {
		// TODO Auto-generated method stub
		return holidayDetailsDao.getActiveHolidaysByOrgIdAndDates(orgId, startDate, endDate, zone);
	}

	@Override
	public List<HolidayDetails> getActiveHolidaysByOrgIdAndDates(HolidayDetails getNewDetails, String zone) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int bulkDelete(JSONArray ids) {
		// TODO Auto-generated method stub
		return holidayDetailsDao.bulkDelete(ids);
	}

	@Override
	public List<HolidayDetails> getTodayHolidayDetails(Long orgId,String Date) {
		// TODO Auto-generated method stub
		return holidayDetailsDao.getTodayHolidayDetails(orgId, Date);
	}
}
