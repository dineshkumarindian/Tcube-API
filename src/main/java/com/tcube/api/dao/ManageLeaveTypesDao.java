package com.tcube.api.dao;

import java.util.List;

import org.json.JSONArray;

import com.tcube.api.model.ManageLeaveTypes;

public interface ManageLeaveTypesDao {

	public ManageLeaveTypes createLeaveType(ManageLeaveTypes details, String timezone);

	public ManageLeaveTypes getById(long id);

	public ManageLeaveTypes updateLeaveType(ManageLeaveTypes oldDetails, String timezone);

	public List<ManageLeaveTypes> getAllLeaveTypes();

	public List<ManageLeaveTypes> getAllLeaveTypesByOrgId(Long id);

	public List<ManageLeaveTypes> getActiveLeaveTypesByOrgId(Long id);

	public List<ManageLeaveTypes> getInactiveLeaveTypesByOrgId(Long id);

	public List<ManageLeaveTypes> getUndeletedLeaveTypesByOrgId(Long id);

	public List<ManageLeaveTypes> getActiveLeaveTypeByOrgIdAndDates(ManageLeaveTypes newDetails, String timezone);
	
	public ManageLeaveTypes updateLeaveTypeWithoutZone(ManageLeaveTypes newDetails);

	public int bulkDelete(JSONArray ids);

}
