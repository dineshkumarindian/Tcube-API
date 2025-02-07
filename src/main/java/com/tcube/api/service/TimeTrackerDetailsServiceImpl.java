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

import com.tcube.api.dao.TimeTrackerDetailsDao;
import com.tcube.api.model.CustomEmployeeDetails2;
import com.tcube.api.model.TimeTrackerDetails;

@Service
@Transactional
public class TimeTrackerDetailsServiceImpl implements TimeTrackerDetailsService {

	@Autowired
	TimeTrackerDetailsDao timeTrackerDetailsDao;

	@Override
	public TimeTrackerDetails createTask(TimeTrackerDetails timeTrackerDetails) {
		return timeTrackerDetailsDao.createTask(timeTrackerDetails);
	}
	
	@Override
	public TimeTrackerDetails getTaskById(Long id) {
		return timeTrackerDetailsDao.getTaskById(id);
	}

	@Override
	public TimeTrackerDetails updateEndTime(TimeTrackerDetails timeTrackerDetails) {
		return timeTrackerDetailsDao.updateEndTime(timeTrackerDetails);
	}

	@Override
	public TimeTrackerDetails updateTaskDetails(TimeTrackerDetails timeTrackerDetails) {
		return timeTrackerDetailsDao.updateTaskDetails(timeTrackerDetails);
	}

	@Override
	public TimeTrackerDetails deleteTaskDetails(TimeTrackerDetails details) {
		return timeTrackerDetailsDao.deleteTaskDetails(details);
	}

	@Override
	public JSONObject getTaskByEmpidAndDate(TimeTrackerDetails timeTrackerDetails) {
		return timeTrackerDetailsDao.getTaskByEmpidAndDate(timeTrackerDetails);
	}

	@Override
	public TimeTrackerDetails getTaskDetailsByActive(String empid) {
		return timeTrackerDetailsDao.getTaskDetailsByActive(empid);
	}

	@Override
	public TimeTrackerDetails updateNewTimeInterval(TimeTrackerDetails timeTrackerDetails) {
		return timeTrackerDetailsDao.updateNewTimeInterval(timeTrackerDetails);
	}

	@Override
	public JSONObject gettotaltimebyprojectjob(String project, String job, Long orgId) {
		return timeTrackerDetailsDao.gettotaltimebyprojectjob(project, job,orgId);
	}

	@Override
	public JSONObject gettotaltimebyEmpId(String project, String job,String empid){
		return timeTrackerDetailsDao.gettotaltimebyEmpId(project, job, empid);
	}

	@Override
	public JSONObject getFilterdata(String start_date, String end_date,JSONArray client_id, JSONArray project, JSONArray job, JSONArray bill, JSONArray status,TimeTrackerDetails details) {
		return timeTrackerDetailsDao.getFilterdata(start_date,end_date,client_id,project,job,bill,status,details);
	}
	
	@Override
	public JSONObject getBilChartEmp(String empid, String date) {
		return timeTrackerDetailsDao.getBilChartEmp(empid, date);
	}

	@Override
	public TimeTrackerDetails updateReporterdetails(TimeTrackerDetails details) {
		return timeTrackerDetailsDao.updateReporterdetails(details);
	}

	@Override
	public TimeTrackerDetails updateprojectname(String olddata,String newdata, Long id) {
		return timeTrackerDetailsDao.updateprojectname(olddata,newdata,id);
	}

	@Override
	public TimeTrackerDetails updateJobDetails(String olddata, String newdata, Long id,String project_name,String bill) {
		return timeTrackerDetailsDao.updateJobDetails(olddata,newdata,id,project_name,bill);
	}

	@Override
	public JSONObject getBilChartEmpMonth(String empid , String startdate , String enddate) {
		return timeTrackerDetailsDao.getBilChartEmpMonth(empid, startdate, enddate);
	}
//	@Override
//	public List<JSONObject> getTaskByEmpid(String id) {
//		return timeTrackerDetailsDao.getTaskByEmpid(id);
//	}
//
//	@Override
//	public List<JSONObject> getTaskByApproverId(String id) {
//		return timeTrackerDetailsDao.getTaskByApproverId(id);
//	}

	@Override
	public List<JSONObject> getProjectJobDropdownByOrgId(Long orgid,String empid) {
		return timeTrackerDetailsDao.getProjectJobDropdownByOrgId(orgid,empid);
	}

	@Override
	public JSONObject getSubmittedTaskByEmpidAndDate(TimeTrackerDetails newDetails) {
		// TODO Auto-generated method stub
		return timeTrackerDetailsDao.getSubmittedTaskByEmpidAndDate(newDetails);
	}

	@Override
	public JSONObject getHoursByOrgIdAndProject(long orgId, String sd_date, String ed_date) {
		return timeTrackerDetailsDao.getHoursByOrgIdAndProject(orgId,sd_date,ed_date);
	}
	
	@Override
	public List<JSONObject> getProjectAndJobNames(Long orgid,String empid) {
		return timeTrackerDetailsDao.getProjectAndJobNames(orgid,empid);
	}

	@Override
	public boolean updateResubmittedStatus(Long timesheetid) {
		return timeTrackerDetailsDao.updateResubmittedStatus(timesheetid);
	}

	@Override
	public List<JSONObject> getTaskDetailsForPerformanceMetrics(String empid, String start_date, String end_date) {
		return timeTrackerDetailsDao.getTaskDetailsForPerformanceMetrics(empid,start_date,end_date);
	}

	@Override
	public JSONObject gettotaltimebyproject(String project, Long orgId) {
		// TODO Auto-generated method stub
		return timeTrackerDetailsDao. gettotaltimebyproject(project,orgId);
	}

	@Override
	public ArrayList<BigInteger> getActiveOrgIdsWithTimetrackerPlan() {
		return timeTrackerDetailsDao.getActiveOrgIdsWithTimetrackerPlan();
	}

	@Override
	public List<String> getNotSubmittedUserListByOrgid(BigInteger id) {
		return timeTrackerDetailsDao.getNotSubmittedUserListByOrgid(id);
	}

	@Override
	public JSONObject getProjectJobLoggedDetails(long orgId, long projectId, String projectName ,String sdate, String edate) {
		return timeTrackerDetailsDao.getProjectJobLoggedDetails(orgId,projectId, projectName,sdate,edate);
	}
}
