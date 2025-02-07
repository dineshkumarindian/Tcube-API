package com.tcube.api.dao;

import java.util.List;

import com.tcube.api.model.ReminderDetails;

public interface ReminderDetailsDao {

	public ReminderDetails createReminderDetails(ReminderDetails reminderDetails, String zone);

	public ReminderDetails getById(Long id);

	public ReminderDetails updateReminderDetails(ReminderDetails newDetails, String zone);

	public List<ReminderDetails> getAllRemindersByOrgId(Long id);

	public List<ReminderDetails> getReminderByOrgIdAndModule(ReminderDetails newDetails);

	public ReminderDetails updateReminderStatus(ReminderDetails newDetails);

	public ReminderDetails createReminderWithoutZone(ReminderDetails reminderDetails);
	
	public List<ReminderDetails> TodayUserListAccessUpdate(Long id);

}
