package com.tcube.api.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.tcube.api.model.AttendanceCurrentStatus;
import com.tcube.api.model.AttendanceDateReport;
import com.tcube.api.model.AttendanceDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.UserAttendanceReport;

public interface AttendanceDetailsDao {
	
	public AttendanceDetails createAttendanceDetails(AttendanceDetails details, String timezone);

	public List<AttendanceDetails> getAllAttendanceDetails();

	public List<AttendanceDetails> getAllAttendanceDetailsByOrgId(Long org_id);
	
	public List<AttendanceDetails> getAllAttendanceDetailsByemail(String email);
	
	public AttendanceDateReport getAttendanceReportsByDate(String date, String email);
	
	public AttendanceCurrentStatus getcurrentstatusByemail(String email,  String timezone);
	
	public JSONObject getAttendanceReportsByMonth(String startdate,String enddate,String email);
	
	public List<String> getActiveAttendanceDetailsByOrgIdwithDate(Long org_id, String date);
	
	public JSONObject getAttendanceDateReport(String date, Long orgId, List<EmployeeDetails> users);
	
	public List<AttendanceDetails> getAttendanceActiveStatusByOrgId_emailwithDate(Long org_id, String email, String date);
	
	public JSONObject getAttendanceactionReportsByDate(String date, String email);
	
	public UserAttendanceReport getUserAttendanceBarchartData(String startdate,String enddate, String email);
	
	public List<AttendanceDetails> getActiveEmployeesTodayAttendanceList(Long org_id,String email);
	
	public ArrayList<BigInteger> getActiveOrgIdsWithAttendance();
	
	public List<String> getEmailForCheckInAttendanceUserList(Long org_id);
	
	
	
}
