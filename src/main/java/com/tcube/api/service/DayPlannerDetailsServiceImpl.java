package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.DayPlannerDetailsDao;
import com.tcube.api.model.DayPlannerDetails;

@Service
@Transactional
public class DayPlannerDetailsServiceImpl implements DayPlannerDetailsService{

	@Autowired
	DayPlannerDetailsDao dayPlannerDetailsDao;
	
	@Override
	public DayPlannerDetails createDayTask(DayPlannerDetails details ,String zone) {
		return dayPlannerDetailsDao.createDayTask(details,zone);
	}

	@Override
	public DayPlannerDetails getById(Long id) {
		return dayPlannerDetailsDao.getById(id);
	}

	@Override
	public DayPlannerDetails updateDayTask(DayPlannerDetails newDetails) {
		return dayPlannerDetailsDao.updateDayTask(newDetails);
	}

	@Override
	public List<DayPlannerDetails> getDayTasksByEmpidAndDate(DayPlannerDetails newDetails) {
		return dayPlannerDetailsDao.getDayTasksByEmpidAndDate(newDetails);
	}

	@Override
	public int updateTaskDates(StringBuffer id_sb , String date) {
		return dayPlannerDetailsDao.updateTaskDates(id_sb , date);
	}

	@Override
	public int updateDayTaskSubmitStatus(StringBuffer id_sb, Boolean status) {
		// TODO Auto-generated method stub
		return dayPlannerDetailsDao.updateDayTaskSubmitStatus(id_sb , status);
	}

	@Override
	public int updateDayTaskupdateStatus(StringBuffer id_sb, Boolean status) {
		// TODO Auto-generated method stub
		return dayPlannerDetailsDao.updateDayTaskupdateStatus(id_sb , status);
	}

	@Override
	public List<DayPlannerDetails> getDayTasksByOrgidAndDate(DayPlannerDetails newDetails) {
		// TODO Auto-generated method stub
		return dayPlannerDetailsDao.getDayTasksByOrgidAndDate(newDetails);
	}

	@Override
	public DayPlannerDetails updateEmpImageDetails(String empId, byte[] compressBytes) {
		// TODO Auto-generated method stub
		return dayPlannerDetailsDao.updateEmpImageDetails(empId , compressBytes);
	}

	@Override
	public int bulkDelete(JSONArray deleteIds) {
		// TODO Auto-generated method stub
		return dayPlannerDetailsDao.bulkDelete(deleteIds);
	}

}
