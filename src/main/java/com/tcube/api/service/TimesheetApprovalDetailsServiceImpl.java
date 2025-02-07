package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.TimesheetApprovalDetailsDao;
import com.tcube.api.model.DesignationDetails;
import com.tcube.api.model.TimesheetApprovalDetails;

@Service
@Transactional
public class TimesheetApprovalDetailsServiceImpl implements TimesheetApprovalDetailsService{

	@Autowired 
	TimesheetApprovalDetailsDao timesheetApprovalDetailsDao;

	@Override
	public TimesheetApprovalDetails createTimesheet(TimesheetApprovalDetails timesheetDetails) {
		return timesheetApprovalDetailsDao.createTimesheet(timesheetDetails);
	}

	@Override
	public TimesheetApprovalDetails getById(Long id) {
		// TODO Auto-generated method stub
		return timesheetApprovalDetailsDao.getById(id);
	}

	@Override
	public TimesheetApprovalDetails updateTimesheetStatus(TimesheetApprovalDetails oldDetails) {
		// TODO Auto-generated method stub
		return timesheetApprovalDetailsDao.updateTimesheetStatus(oldDetails);
	}

	@Override
	public List<TimesheetApprovalDetails> getActiveTimesheetByEmpId(String id) {
		// TODO Auto-generated method stub
		return timesheetApprovalDetailsDao.getActiveTimesheetByEmpId(id);
	}

	@Override
	public JSONObject getActiveTimesheetByApproverId(String id) {
		// TODO Auto-generated method stub
		return timesheetApprovalDetailsDao.getActiveTimesheetByApproverId(id);
	}

	@Override
	public TimesheetApprovalDetails getTimesheetByEmpidAndDate(TimesheetApprovalDetails newDetails) {
		// TODO Auto-generated method stub
		return timesheetApprovalDetailsDao.getTimesheetByEmpidAndDate(newDetails);
	}

	@Override
	public List<TimesheetApprovalDetails> getTimesheetsByEmpidAndDate(TimesheetApprovalDetails toChecktDetails) {
		// TODO Auto-generated method stub
		return timesheetApprovalDetailsDao.getTimesheetsByEmpidAndDate(toChecktDetails);
	}

	@Override
	public TimesheetApprovalDetails getTimesheetById(Long id) {
		// TODO Auto-generated method stub
		return timesheetApprovalDetailsDao.getTimesheetById(id);
	}

	@Override
	public JSONObject getActivePendingTimesheetByApproverId(String id) {
		// TODO Auto-generated method stub
		return timesheetApprovalDetailsDao.getActivePendingTimesheetByApproverId(id);
	}

	@Override
	public JSONObject getActiveApprovedTimesheetByApproverId(String id) {
		// TODO Auto-generated method stub
		return timesheetApprovalDetailsDao.getActiveApprovedTimesheetByApproverId(id);
	}

	@Override
	public JSONObject getActiveRejectedTimesheetByApproverId(String id) {
		// TODO Auto-generated method stub
		return timesheetApprovalDetailsDao.getActiveRejectedTimesheetByApproverId(id);
	}

	@Override
	public boolean updateResubmittedTimesheetStatus(Long timesheetid) {
		return timesheetApprovalDetailsDao.updateResubmittedTimesheetStatus(timesheetid);
	}
		
}
