package com.tcube.api.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tcube.api.model.LeaveTrackerDetails;

public interface LeaveTrackerDetailsService {
	
	public LeaveTrackerDetails createLeave(LeaveTrackerDetails leaveDetails,String zone);

	public LeaveTrackerDetails updateLeaveStatus(LeaveTrackerDetails oldDetails);

	public LeaveTrackerDetails getById(Long id);

	public List<LeaveTrackerDetails> getActiveLeaveByEmpId(String id);

	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYear(LeaveTrackerDetails newDetails);

	public List<LeaveTrackerDetails> getLeaveByReporterIdAndYear(LeaveTrackerDetails newDetails);

	public LeaveTrackerDetails updateLeave(LeaveTrackerDetails newDetails);

//	List<LeaveTrackerDetails> getCancelLeavetype(LeaveTrackerDetails details);
	
	public List<LeaveTrackerDetails> getAllCancelLeaveType(LeaveTrackerDetails newDetails);

	public List<LeaveTrackerDetails> getTodayLeavesbyOrgId(LeaveTrackerDetails newDetails,String zone);

	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYearForLeavetype(LeaveTrackerDetails newDetails);
	
	public LeaveTrackerDetails updateEmpDetails(String empid, byte[] img);
	
	public List<LeaveTrackerDetails> getEmpDateRangeApprovedLeaves(String startdate, String enddate, String emp_id );

	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYearForReports(LeaveTrackerDetails newDetails,JSONArray ids);

	public JSONObject getEmpCausalAndSickLeaveCount(String org_id,String emp_id);
	
	public int updateReportingManagerAfterDeactivateUser(String id, Long orgId);
	
	public int updateReporterAfterBulkDeactivateUser(JSONArray id,Long orgId);

	public List<LeaveTrackerDetails> getLeaveTrackerDetailsByLeaveType(Long id);
	
	public int updateLeaveTrackerDetailksByLeaveType(Long id,String newLeaveType);

	public int updateSlackNotificationStatus(int notificationId, Boolean status);
	
	public List<LeaveTrackerDetails> getRequestLeaveCount(LeaveTrackerDetails details);
	
	public List<LeaveTrackerDetails> getRequestLeaveDetailsCount(LeaveTrackerDetails details);
	
	public int  getLeaveRequestByEmpIdByStatusPending(String emp_id,String activeAction);
	
	public int bulkGetLeaveRequestByEmpIdByStatusPending(JSONArray ids,String activeAction);
	
	public List<LeaveTrackerDetails> getTodayLeaveUserList(Long id);
	
	public ArrayList<BigInteger> getActiveOrgIdsWithLeaveTrackerPlan();
	
	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYearforappliedAndApprovedLeave(LeaveTrackerDetails newDetails);
	
	// public List<LeaveTrackerDetails> getRequestLeaveDetailsPaginationCountData(LeaveTrackerDetails newDetails,int pageSize,int pageIndexSize);
	
}
