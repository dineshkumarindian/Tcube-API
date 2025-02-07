package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.EmployeeDetailsDao;
import com.tcube.api.model.CustomEmployeeDetails;
import com.tcube.api.model.CustomEmployeeDetails2;
import com.tcube.api.model.EmployeeDetails;

@Service
@Transactional
public class EmployeeDetailsServiceImpl implements EmployeeDetailsService {

	@Autowired
	EmployeeDetailsDao employeeDetailsDao;
	
	@Override
	public EmployeeDetails createEmployeeDetails(EmployeeDetails employeedetails) {
		return employeeDetailsDao.createEmployeeDetails(employeedetails);
	}

	@Override
	public EmployeeDetails createEmployeeDetailsWithTimeZone(EmployeeDetails employeedetails,String zone) {
		return employeeDetailsDao.createEmployeeDetailsWithTimeZone(employeedetails,zone);
	}
	
	@Override
	public List<EmployeeDetails> getAllEmployeeDetails() {
		return employeeDetailsDao.getAllEmployeeDetails();
	}

	@Override
	public List<EmployeeDetails> getAllEmployeeDetailsByEmail() {
		return employeeDetailsDao.getAllEmployeeDetailsByEmail();
	 }
	
	@Override
	public EmployeeDetails getAllEmployeeDetailsByID(String id) {
		return employeeDetailsDao.getAllEmployeeDetailsByID(id);
	}
	
	@Override
	public EmployeeDetails updateEmployeeDetails(EmployeeDetails details) {
		return employeeDetailsDao.updateEmployeeDetails(details);
	}

	@Override
	public EmployeeDetails updateEmployeeDetailsWithZone(EmployeeDetails details,String zone) {
		return employeeDetailsDao.updateEmployeeDetailsWithZone(details,zone);
	}
	
	@Override
	public EmployeeDetails deleteEmployeeDetails(EmployeeDetails details) {
		return employeeDetailsDao.deleteEmployeeDetails(details);
	}
	
	@Override
	public int bulkUserdelete(JSONArray ids) {
		return employeeDetailsDao.bulkUserdelete(ids);
	}

	@Override
	public EmployeeDetails authenticateEmployee(EmployeeDetails employeedetails) {
		return employeeDetailsDao.authenticateEmployee(employeedetails);
	}

	@Override
	public long getMaxSequenceId() {
		return employeeDetailsDao.getMaxSequenceId();
	}
	
	@Override
	public List<EmployeeDetails> getAllEmployeeDetailsByOrgID(Long org_id) {
		return employeeDetailsDao.getAllEmployeeDetailsByOrgID(org_id);
	}

	@Override
	public List<EmployeeDetails> getEmployeeDetailsByOrgID(Long id) {
		return employeeDetailsDao.getEmployeeDetailsByOrgID(id);
	}

	@Override
	public EmployeeDetails getEmployeeDetailsByEmail(String email) {
		return employeeDetailsDao.getEmployeeDetailsByEmail(email);
	}
	
	@Override
	public EmployeeDetails updateEmployeePassword(EmployeeDetails details) {
		return employeeDetailsDao.updateEmployeePassword(details);
	}

	@Override
	public List<EmployeeDetails> getAllActiveEmployeeDetails() {
		return employeeDetailsDao.getAllActiveEmployeeDetails();
	}

	@Override
	public List<EmployeeDetails> getInactiveEmployeeDetailsByOrgID(Long id) {
		// TODO Auto-generated method stub
		return employeeDetailsDao.getInactiveEmployeeDetailsByOrgID(id);
	}

	@Override
	public List<EmployeeDetails> getAllEmployeeReportsByOrgId(Long id) {
		return employeeDetailsDao.getAllEmployeeReportsByOrgId(id) ;
	}

	@Override
	public List<EmployeeDetails> getAllActiveEmployeeReportsByOrgId(Long id) {
		return employeeDetailsDao.getAllActiveEmployeeReportsByOrgId(id) ;
	}

	@Override
	public int bulkDeactiveEmp(JSONArray id,String action, Long orgId){
		return employeeDetailsDao.bulkDeactiveEmp(id,action, orgId) ;
	}

	 @Override
	public int bulkActivate(JSONArray id,String action){
		return employeeDetailsDao.bulkActivate(id, action) ;
	}
//	@Override
//	public EmployeeDetails updateEmployeeActiveStatus(EmployeeDetails oldAdminDetails) {
//		// TODO Auto-generated method stub
//		return employeeDetailsDao.updateEmployeeActiveStatus(oldAdminDetails);
//	}

	@Override
	public List<EmployeeDetails> getOrgUsers(Long orgId) {
		// TODO Auto-generated method stub
		return employeeDetailsDao.getOrgUsers(orgId);
	}
	
	@Override
	public List<EmployeeDetails> getinactiveOrgUsers(Long orgId) {
		// TODO Auto-generated method stub
		return employeeDetailsDao.getinactiveOrgUsers(orgId);
	}
	
	 @Override
	public EmployeeDetails createUserFromExcelFile(EmployeeDetails employeedetails,String zone) {
		return employeeDetailsDao.createUserFromExcelFile(employeedetails,zone);
	}

	@Override
	public EmployeeDetails updateskippedTimeEmployeeDetails(EmployeeDetails details,String zone,String skipped_time,Boolean isSkipped) {
		// TODO Auto-generated method stub
		return employeeDetailsDao.updateSkippedTimeEmployeeDetails(details,zone,skipped_time,isSkipped);
	}

	@Override
	public List<EmployeeDetails> getActiveEmpDetailsByOrgId(Long id) {
		// TODO Auto-generated method stub
		return employeeDetailsDao.getActiveEmpDetailsByOrgId(id);
	}

	@Override
	public EmployeeDetails updateSkippedLeaveById(EmployeeDetails employeedetails) {
		// TODO Auto-generated method stub
		return employeeDetailsDao.updateSkippedLeaveById(employeedetails);
	}

	@Override
	public List<EmployeeDetails> getAllSkippedLeaveEmployeeDetails() {
		// TODO Auto-generated method stub
		return employeeDetailsDao.getAllSkippedLeaveEmployeeDetails();
	}

	@Override
	public int updateDailyOnceDisplayTodayLeave(StringBuffer id_sb) {
		// TODO Auto-generated method stub
		return employeeDetailsDao.updateDailyOnceDisplayTodayLeave(id_sb);
	}

	@Override
	public List<CustomEmployeeDetails2> getCustomInactiveEmpDetailsByOrgID(Long id) {
		// TODO Auto-generated method stub
		return employeeDetailsDao.getCustomInactiveEmpDetailsByOrgID(id);
	}

	@Override
	public List<CustomEmployeeDetails2> getCustomActiveEmpDetailsByOrgID(Long id) {
		// TODO Auto-generated method stub
		return employeeDetailsDao.getCustomActiveEmpDetailsByOrgID(id);
	}
	
	
//	@Override
//	public int updateOnceADayDisplayLeaveDeatils(Long orgId) {
//		// TODO Auto-generated method stub
//		return employeeDetailsDao.updateOnceADayDisplayLeaveDeatils();
//	}
	
	@Override
	public int activate(String id) {
		return employeeDetailsDao.activate(id);
	}
	
	@Override
	public int deactivateEmp(String id,Long orgId) {
		return employeeDetailsDao.deactivateEmp(id,orgId);
	}
	
	@Override
	public List<EmployeeDetails> getActiveEmpByRoleAndOrgId(Long orgId, Long roleId) {
		return employeeDetailsDao.getActiveEmpByRoleAndOrgId(orgId,roleId);
	}
	
	@Override
	public List<EmployeeDetails> getInactiveEmpByRoleAndOrgId(Long orgId, Long roleId) {
		return employeeDetailsDao.getInactiveEmpByRoleAndOrgId(orgId,roleId);
	}

	@Override
	public Boolean getNewReleaseByEmpId(String empid) {
		return employeeDetailsDao.getNewReleaseByEmpId(empid);
	}
	
	@Override
	public EmployeeDetails getReportingManagerByName(String details,Long id) {
		return employeeDetailsDao.getReportingManagerByName(details, id);
	}
	
	@Override
	public List<EmployeeDetails> getEmployeeDetailsByName(Long orgId) {
		return employeeDetailsDao.getEmployeeDetailsByName(orgId);
	}
	
	@Override
	public List<EmployeeDetails> getActiveEmpByBranchAndOrgId(Long orgId, Long branchId) {
		return employeeDetailsDao.getActiveEmpByBranchAndOrgId(orgId,branchId);
	}
	
	@Override
	public List<EmployeeDetails> getInactiveEmpByBranchAndOrgId(Long orgId, Long branchId) {
		return employeeDetailsDao.getInactiveEmpByBranchAndOrgId(orgId,branchId);
	}

	@Override
	public JSONObject getEmpImagesByIds(JSONArray empIds) {
		return employeeDetailsDao.getEmpImagesByIds(empIds);
	}

	@Override
	public Boolean checkIsDetailsUpdatedColumnStatus(String empid) {
		return employeeDetailsDao.checkIsDetailsUpdatedColumnStatus(empid);
	}

	@Override
	public int updateFalseInEmpDetailsUpdated(String empid) {
		return employeeDetailsDao.updateFalseInEmpDetailsUpdated(empid);
	}
}
