package com.tcube.api.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.LeaveTrackerDetailsDao;
import com.tcube.api.dao.ManageAttendanceDao;
import com.tcube.api.model.LeaveTrackerDetails;

@Service
@Transactional
public class LeaveTrackerDetailsServiceImpl implements LeaveTrackerDetailsService{

	@Autowired
	LeaveTrackerDetailsDao leaveTrackerDetailsDao;

	@Override
	public LeaveTrackerDetails createLeave(LeaveTrackerDetails leaveDetails,String zone) {
		return leaveTrackerDetailsDao.createLeave(leaveDetails,zone);
	}

	@Override
	public LeaveTrackerDetails updateLeaveStatus(LeaveTrackerDetails oldDetails) {
		return leaveTrackerDetailsDao.updateLeaveStatus(oldDetails);
	}

	@Override
	public LeaveTrackerDetails getById(Long id) {
		return leaveTrackerDetailsDao.getById(id);
	}

	@Override
	public List<LeaveTrackerDetails> getActiveLeaveByEmpId(String id) {
		return leaveTrackerDetailsDao.getActiveLeaveByEmpId(id);
	}

	@Override
	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYear(LeaveTrackerDetails newDetails) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getLeaveByEmpIdAndYear(newDetails);
	}

	@Override
	public List<LeaveTrackerDetails> getLeaveByReporterIdAndYear(LeaveTrackerDetails newDetails) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getLeaveByReporterIdAndYear(newDetails);
	}

	@Override
	public LeaveTrackerDetails updateLeave(LeaveTrackerDetails newDetails) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.updateLeave(newDetails);
	}
	
	@Override
	public List<LeaveTrackerDetails>  getAllCancelLeaveType(LeaveTrackerDetails details){
		return leaveTrackerDetailsDao.getAllLeaveType(details);

	}

	@Override
	public List<LeaveTrackerDetails> getTodayLeavesbyOrgId(LeaveTrackerDetails newDetails,String zone) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getTodayLeavesbyOrgId(newDetails,zone);
	}

	@Override
	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYearForLeavetype(LeaveTrackerDetails newDetails) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getLeaveByEmpIdAndYearForLeavetype(newDetails);
	}

	@Override
	public LeaveTrackerDetails updateEmpDetails(String empid, byte[] img) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.updateEmpDetails(empid,img);
	}

	@Override
	public List<LeaveTrackerDetails> getEmpDateRangeApprovedLeaves(String startdate, String enddate, String emp_id) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getEmpDateRangeApprovedLeaves(startdate, enddate, emp_id);
	}

	@Override
	public List getLeaveByEmpIdAndYearForReports(LeaveTrackerDetails newDetails,JSONArray ids) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getLeaveByEmpIdAndYearForReports(newDetails,ids);
	}

	@Override
	public JSONObject getEmpCausalAndSickLeaveCount(String org_id,String emp_id) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getEmpCausalAndSickLeaveCount(org_id,emp_id);
	}

	@Override
	public List<LeaveTrackerDetails> getLeaveTrackerDetailsByLeaveType(Long id) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getLeaveTrackerDetailsByLeaveType(id);
	}

	@Override
	public int updateLeaveTrackerDetailksByLeaveType(Long id,String newLeaveType) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.updateLeaveTrackerDetailksByLeaveType(id,newLeaveType);
	}

//	@Override
//	public List<LeaveTrackerDetails> getCancelLeavetype(LeaveTrackerDetails details) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	@Override
	public int updateReportingManagerAfterDeactivateUser(String id, Long orgId) {
		return leaveTrackerDetailsDao.updateReportingManagerAfterDeactivateUser(id, orgId);
	}
	
	@Override
	public int updateReporterAfterBulkDeactivateUser(JSONArray id,Long orgId) {
		return leaveTrackerDetailsDao.updateReporterAfterBulkDeactivateUser(id,orgId);
	}

	@Override
	public int updateSlackNotificationStatus(int notificationId, Boolean status) {
		return leaveTrackerDetailsDao.updateSlackNotificationStatus(notificationId,status);
	}

	@Override
	public List<LeaveTrackerDetails> getRequestLeaveCount(LeaveTrackerDetails newDetails) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getRequestLeaveCount(newDetails);
	}

	@Override
	public List<LeaveTrackerDetails> getRequestLeaveDetailsCount(LeaveTrackerDetails details) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getRequestLeaveDetailsCount(details);
	}

	@Override
	public int getLeaveRequestByEmpIdByStatusPending(String emp_id, String activeAction) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getLeaveRequestByEmpIdByStatusPending(emp_id,activeAction);
	}

	@Override
	public int bulkGetLeaveRequestByEmpIdByStatusPending(JSONArray ids, String activeAction) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.bulkGetLeaveRequestByEmpIdByStatusPending(ids, activeAction);
	}

	@Override
	public List<LeaveTrackerDetails> getTodayLeaveUserList(Long id) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getTodayLeaveUserList(id);
	}

	@Override
	public ArrayList<BigInteger> getActiveOrgIdsWithLeaveTrackerPlan() {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getActiveOrgIdsWithLeaveTrackerPlan();
	}

	@Override
	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYearforappliedAndApprovedLeave(LeaveTrackerDetails newDetails) {
		// TODO Auto-generated method stub
		return leaveTrackerDetailsDao.getLeaveByEmpIdAndYearforappliedAndApprovedLeave(newDetails);
	}
	
	

	// @Override
	// public List<LeaveTrackerDetails> getRequestLeaveDetailsPaginationCountData(LeaveTrackerDetails newDetails, int pageSize, int pageIndexSize) {
	// 	// TODO Auto-generated method stub
	// 	return leaveTrackerDetailsDao.getRequestLeaveDetailsPaginationCountData(newDetails, pageSize, pageIndexSize);
	// }
}
