package com.tcube.api.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tcube.api.model.CustomEmployeeDetails2;
import com.tcube.api.model.TimeTrackerDetails;

public interface TimeTrackerDetailsDao {
	public TimeTrackerDetails createTask(TimeTrackerDetails timeTrackerDetails);
	
	public TimeTrackerDetails getTaskById(Long id);
	
	public TimeTrackerDetails updateEndTime(TimeTrackerDetails timeTrackerDetails);
	
	public TimeTrackerDetails updateTaskDetails(TimeTrackerDetails timeTrackerDetails);
	
	public TimeTrackerDetails deleteTaskDetails(TimeTrackerDetails details);
	
	public JSONObject getTaskByEmpidAndDate(TimeTrackerDetails timeTrackerDetails);
	
	public TimeTrackerDetails getTaskDetailsByActive(String empid);
	
	public TimeTrackerDetails updateNewTimeInterval(TimeTrackerDetails timeTrackerDetails);
	
	public JSONObject gettotaltimebyprojectjob(String project, String job, Long orgId);
	
	public JSONObject gettotaltimebyEmpId(String project, String job,String empid);
	
	public JSONObject getFilterdata(String start_date, String end_date, JSONArray client_id, JSONArray project, JSONArray job, JSONArray bill, JSONArray status, TimeTrackerDetails details);

	public JSONObject getBilChartEmp(String empid, String date);
	
	public TimeTrackerDetails updateReporterdetails(TimeTrackerDetails details);
	
	public TimeTrackerDetails updateprojectname(String olddata,String newdata,Long id);
	
	public TimeTrackerDetails updateJobDetails(String olddata,String newdata,Long id, String project_name,String bill);

//	public List<JSONObject> getTaskByEmpid(String id);

	public List<TimeTrackerDetails> getSubmittedTaskByEmpidAndDate(String date, String emp_id);

	public List<TimeTrackerDetails> getApprovedTaskByEmpidAndDate(String date, String emp_id);

	public JSONObject getBilChartEmpMonth(String empid , String startdate , String enddate);
	
	public List<JSONObject> getProjectJobDropdownByOrgId(Long orgid, String empid);

	public JSONObject getSubmittedTaskByEmpidAndDate(TimeTrackerDetails newDetails);

	public JSONObject getHoursByOrgIdAndProject(long orgId, String sd_date, String ed_date);
	
//	public List<JSONObject> getTaskByApproverId(String id);
	
	public List<JSONObject> getProjectAndJobNames(Long orgid,String empid);
	
	public boolean updateResubmittedStatus(Long timesheetid);

	public List<JSONObject> getTaskDetailsForPerformanceMetrics(String empid, String start_date, String end_date);

	public JSONObject gettotaltimebyproject(String project, Long orgId);
	
	public ArrayList<BigInteger> getActiveOrgIdsWithTimetrackerPlan();
	
	public List<String> getNotSubmittedUserListByOrgid(BigInteger id);

	public JSONObject getProjectJobLoggedDetails(long orgId, long projectId,String projectName, String sd_date, String ed_date);
}
