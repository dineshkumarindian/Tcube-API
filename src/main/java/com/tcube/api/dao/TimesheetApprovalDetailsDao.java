package com.tcube.api.dao;

import java.util.List;

import org.json.JSONObject;

import com.tcube.api.model.DesignationDetails;
import com.tcube.api.model.TimesheetApprovalDetails;

public interface TimesheetApprovalDetailsDao {

	public TimesheetApprovalDetails createTimesheet(TimesheetApprovalDetails timesheetDetails);

	public TimesheetApprovalDetails getById(Long id);

	public TimesheetApprovalDetails updateTimesheetStatus(TimesheetApprovalDetails oldDetails);

	public List<TimesheetApprovalDetails> getActiveTimesheetByEmpId(String id);

	public JSONObject getActiveTimesheetByApproverId(String id);

	public TimesheetApprovalDetails getTimesheetByEmpidAndDate(TimesheetApprovalDetails newDetails);

	public List<TimesheetApprovalDetails> getTimesheetsByEmpidAndDate(TimesheetApprovalDetails toChecktDetails);

	public TimesheetApprovalDetails getTimesheetById(Long id);

	public JSONObject getActivePendingTimesheetByApproverId(String id);

	public JSONObject getActiveApprovedTimesheetByApproverId(String id);

	public JSONObject getActiveRejectedTimesheetByApproverId(String id);
	
	public boolean updateResubmittedTimesheetStatus(Long timesheetid);

}
