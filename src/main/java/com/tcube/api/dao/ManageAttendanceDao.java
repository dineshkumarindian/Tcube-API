package com.tcube.api.dao;

import java.util.List;

import com.tcube.api.model.ActionCards;
import com.tcube.api.model.ManageAttendance;

public interface ManageAttendanceDao {

	public ManageAttendance createattendancecard(ManageAttendance details);
	
	public ManageAttendance updateattendancecard(ManageAttendance details);
	
	public ManageAttendance getattendancecardById(Long id);
	
	public List<ActionCards> getAllattendancecardByOrgId(Long orgId);
}
