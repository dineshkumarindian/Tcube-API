package com.tcube.api.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tcube.api.model.LeaveTrackerDetails;

public interface LeaveTrackerDetailsDao {

	public LeaveTrackerDetails createLeave(LeaveTrackerDetails leaveDetails,String zone);

	public LeaveTrackerDetails updateLeaveStatus(LeaveTrackerDetails oldDetails);

	public LeaveTrackerDetails getById(Long id);

	public List<LeaveTrackerDetails> getActiveLeaveByEmpId(String id);

	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYear(LeaveTrackerDetails newDetails);

	public List<LeaveTrackerDetails> getLeaveByReporterIdAndYear(LeaveTrackerDetails newDetails);

	public LeaveTrackerDetails updateLeave(LeaveTrackerDetails newDetails);
	
//	public List<LeaveTrackerDetails> getAllLeaveType(Long id);

	public List<LeaveTrackerDetails> getAllLeaveType(LeaveTrackerDetails details);

	public List<LeaveTrackerDetails> getTodayLeavesbyOrgId(LeaveTrackerDetails newDetails,String zone);

	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYearForLeavetype(LeaveTrackerDetails newDetails);

	public LeaveTrackerDetails updateEmpDetails(String empid, byte[] img);
	
	public List<LeaveTrackerDetails> getEmpDateRangeApprovedLeaves(String startdate, String enddate, String emp_id );
	
	public List getLeaveByEmpIdAndYearForReports(LeaveTrackerDetails newDetails,JSONArray ids);
	
	public JSONObject getEmpCausalAndSickLeaveCount(String org_id,String emp_id);
	
	public int updateReportingManagerAfterDeactivateUser(String id, Long orgId);
	
	public int updateReporterAfterBulkDeactivateUser(JSONArray id,Long orgId);
	public List<LeaveTrackerDetails> getLeaveTrackerDetailsByLeaveType(Long id);
	
	public int updateLeaveTrackerDetailksByLeaveType(Long id,String newLeaveType);

	public int updateSlackNotificationStatus(int notificationId, Boolean status);
	
	public List<LeaveTrackerDetails> getRequestLeaveCount(LeaveTrackerDetails newDetails);
	
	public List<LeaveTrackerDetails> getRequestLeaveDetailsCount(LeaveTrackerDetails newDetails);
	
	public int getLeaveRequestByEmpIdByStatusPending(String emp_id,String activeaction);
	
	public int bulkGetLeaveRequestByEmpIdByStatusPending(JSONArray ids,String activeAction);
	
	public List<LeaveTrackerDetails> getTodayLeaveUserList(Long id);
	
	public ArrayList<BigInteger> getActiveOrgIdsWithLeaveTrackerPlan();
	
	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYearforappliedAndApprovedLeave(LeaveTrackerDetails newDetails);
	
	// public List<LeaveTrackerDetails> getRequestLeaveDetailsPaginationCountData(LeaveTrackerDetails newDetails,int pageSize,int pageIndexSize);
}
