package com.tcube.api.dao;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tcube.api.model.CustomEmployeeDetails;
import com.tcube.api.model.CustomEmployeeDetails2;
import com.tcube.api.model.EmployeeDetails;

public interface EmployeeDetailsDao {
	public EmployeeDetails createEmployeeDetails(EmployeeDetails employeedetails);
	
	public EmployeeDetails createEmployeeDetailsWithTimeZone(EmployeeDetails employeedetails,String zone);
	
	public List<EmployeeDetails> getAllEmployeeDetails();
	
	public EmployeeDetails getAllEmployeeDetailsByID(String id);
	
	 public List<EmployeeDetails> getAllEmployeeDetailsByEmail();
	
	public EmployeeDetails updateEmployeeDetails(EmployeeDetails details);
	
	public EmployeeDetails updateEmployeeDetailsWithZone(EmployeeDetails details,String zone);
	
	public EmployeeDetails updateSkippedTimeEmployeeDetails(EmployeeDetails details);
	
	public EmployeeDetails deleteEmployeeDetails(EmployeeDetails details);
	
	 public int bulkUserdelete(JSONArray ids);
	
	public EmployeeDetails authenticateEmployee(EmployeeDetails employeedetails);
	
	public List<EmployeeDetails> getEmployeeDetailsByOrgID(Long id);
	
	public long getMaxSequenceId();

	public EmployeeDetails getEmployeeDetailsByEmail(String email);
	
	public EmployeeDetails updateEmployeePassword(EmployeeDetails details);

	public List<EmployeeDetails> getAllActiveEmployeeDetails();

	public List<EmployeeDetails> getInactiveEmployeeDetailsByOrgID(Long id);

	public List<EmployeeDetails> getAllEmployeeDetailsByOrgID(Long org_id);
	
	public List<EmployeeDetails> getAllEmployeeReportsByOrgId(Long id);
	
	public List<EmployeeDetails> getAllActiveEmployeeReportsByOrgId(Long id);
	
	public int bulkDeactiveEmp (JSONArray id,String action, Long orgId);
	
	public int bulkActivate (JSONArray id,String action);
	
	public List<EmployeeDetails> getOrgUsers(Long orgId);
	
	public List<EmployeeDetails> getinactiveOrgUsers(Long orgId);
	
	 public EmployeeDetails createUserFromExcelFile(EmployeeDetails employeedetails,String zone);

	EmployeeDetails updateSkippedTimeEmployeeDetails(EmployeeDetails details, String zone, String skippedTime,
			Boolean isSkipped);

	public List<EmployeeDetails> getActiveEmpDetailsByOrgId(Long id);
	
	public EmployeeDetails updateSkippedLeaveById(EmployeeDetails employeedetails);
	
	public List<EmployeeDetails> getAllSkippedLeaveEmployeeDetails();
	
	public int updateDailyOnceDisplayTodayLeave(StringBuffer id_sb);

	public List<CustomEmployeeDetails2> getCustomInactiveEmpDetailsByOrgID(Long id);

	public List<CustomEmployeeDetails2> getCustomActiveEmpDetailsByOrgID(Long id);
	
	public int activate(String id);
	
	public int deactivateEmp(String id, Long orgId);
	
	public List<EmployeeDetails> getActiveEmpByRoleAndOrgId(Long orgId, Long roleId);
	
	public List<EmployeeDetails> getInactiveEmpByRoleAndOrgId(Long orgId, Long roleId);

	public Boolean getNewReleaseByEmpId(String empid);
	
	public EmployeeDetails getReportingManagerByName(String details,Long id);
	
	public List<EmployeeDetails> getEmployeeDetailsByName(Long orgId);

	public JSONObject getEmpImagesByIds(JSONArray empIds);
	
	public List<EmployeeDetails> getActiveEmpByBranchAndOrgId(Long orgId, Long branchId);
	
	public List<EmployeeDetails> getInactiveEmpByBranchAndOrgId(Long orgId, Long branchId);
	
	public Boolean checkIsDetailsUpdatedColumnStatus(String empid);
	
	public int updateFalseInEmpDetailsUpdated(String empid);
}
