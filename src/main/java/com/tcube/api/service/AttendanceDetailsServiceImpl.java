package com.tcube.api.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.AttendanceDetailsDao;
import com.tcube.api.model.AttendanceCurrentStatus;
import com.tcube.api.model.AttendanceDateReport;
import com.tcube.api.model.AttendanceDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.UserAttendanceReport;

@Service
@Transactional
public class AttendanceDetailsServiceImpl implements AttendanceDetailsService {

	@Autowired
	AttendanceDetailsDao attendanceDetailsDao;

	/**
	 *  Create Method Service
	 */
	@Override
	public AttendanceDetails createAttendanceDetails(AttendanceDetails details, String timezone) {
		return attendanceDetailsDao.createAttendanceDetails(details, timezone);
	}

	/**
	 * Get All Attendance details Service
	 */
	@Override
	public List<AttendanceDetails> getAllAttendanceDetails() {
		return attendanceDetailsDao.getAllAttendanceDetails();
	}

	/**
	 * Get All Attendance details Service By Org Id
	 */
	@Override
	public List<AttendanceDetails> getAllAttendanceDetailsByOrgId(Long org_id) {
		return attendanceDetailsDao.getAllAttendanceDetailsByOrgId(org_id);
	}
	
	/*
	 * getAllAttendanceDetailsByemail Service
	 */	
	@Override
	public List<AttendanceDetails> getAllAttendanceDetailsByemail(String email) {
		return attendanceDetailsDao.getAllAttendanceDetailsByemail(email);
	}
	
	/*
	 * getAttendanceReportsByDate Service
	 */
	@Override
	public AttendanceDateReport getAttendanceReportsByDate(String date, String email) {
		return attendanceDetailsDao.getAttendanceReportsByDate(date, email);
	}
	
	/*
	 * getcurrentstatusByemail Service
	 */
	@Override
	public AttendanceCurrentStatus getcurrentstatusByemail(String email,  String timezone) {
		return attendanceDetailsDao.getcurrentstatusByemail(email, timezone);
	}
	
	/*
	 * getAttendanceReportsByMonth Service
	 */
	@Override
	public JSONObject getAttendanceReportsByMonth(String startdate,String enddate, String email){
		return attendanceDetailsDao.getAttendanceReportsByMonth(startdate,enddate, email);
	}
	
	/**
	 * Get All active Attendance details Service By Org Id with date
	 */
	@Override
	public List<String> getActiveAttendanceDetailsByOrgIdwithDate(Long org_id, String date) {
		return attendanceDetailsDao.getActiveAttendanceDetailsByOrgIdwithDate(org_id, date);
	}

	@Override
	public JSONObject getAttendanceDateReport(String date, Long orgId, List<EmployeeDetails> users) {
		return attendanceDetailsDao.getAttendanceDateReport(date,orgId, users);
	}

	@Override
	public List<AttendanceDetails> getAttendanceActiveStatusByOrgId_emailwithDate(Long org_id, String email, String date) {
		return attendanceDetailsDao.getAttendanceActiveStatusByOrgId_emailwithDate(org_id, email, date);
	}

	@Override
	public JSONObject getAttendanceactionReportsByDate(String date, String email) {
		return attendanceDetailsDao.getAttendanceactionReportsByDate(date, email);
	}
	
	/*
	 * getAttendanceReportsByMonth Service
	 */
	@Override
	public UserAttendanceReport getUserAttendanceBarchartData(String startdate,String enddate, String email){
		return attendanceDetailsDao.getUserAttendanceBarchartData(startdate,enddate, email);
	}

	@Override
	public ArrayList<BigInteger> getActiveOrgIdsWithAttendance() {
		// TODO Auto-generated method stub
		return attendanceDetailsDao.getActiveOrgIdsWithAttendance();
	}

	@Override
	public List<AttendanceDetails> getActiveEmployeesTodayAttendanceList(Long org_id,String email) {
		// TODO Auto-generated method stub
		return  attendanceDetailsDao.getActiveEmployeesTodayAttendanceList(org_id,email);
	}

	@Override
	public List<String> getEmailForCheckInAttendanceUserList(Long org_id) {
		// TODO Auto-generated method stub
		return attendanceDetailsDao.getEmailForCheckInAttendanceUserList(org_id);
	}

}
