package com.tcube.api.service;

import java.util.List;

import org.json.JSONArray;

import com.tcube.api.model.ManageLeaveTypes;

public interface ManageLeaveTypesService {

	public ManageLeaveTypes createLeaveType(ManageLeaveTypes details, String zone);

	public ManageLeaveTypes getById(long id);

	public ManageLeaveTypes updateLeaveType(ManageLeaveTypes oldDetails, String zone);

	public List<ManageLeaveTypes> getAllLeaveTypes();

	public List<ManageLeaveTypes> getAllLeaveTypesByOrgId(Long id);

	public List<ManageLeaveTypes> getActiveLeaveTypesByOrgId(Long id);

	public List<ManageLeaveTypes> getInactiveLeaveTypesByOrgId(Long id);

	public List<ManageLeaveTypes> getUndeletedLeaveTypesByOrgId(Long id);

//	public List<ManageLeaveTypes> getActiveLeaveTypeByOrgIdAndDates(Long id, Date st_date, Date ed_date);

	public List<ManageLeaveTypes> getActiveLeaveTypeByOrgIdAndDates(ManageLeaveTypes newDetails, String zone);

	public ManageLeaveTypes updateLeaveTypeWithoutZone(ManageLeaveTypes newDetails);
	
	public int bulkDelete(JSONArray ids);
}
