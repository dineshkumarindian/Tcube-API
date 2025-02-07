package com.tcube.api.service;

import java.util.List;

import com.tcube.api.model.ActionCards;
import com.tcube.api.model.ManageAttendance;

public interface ManageAttendanceService {
  
	public ManageAttendance createattendancecard(ManageAttendance details);
	
	public ManageAttendance updateattendancecard(ManageAttendance details);
	
	public ManageAttendance getattendancecardById(Long id);
	
	public List<ActionCards> getAllattendancecardByOrgId(Long orgId);
	
}
