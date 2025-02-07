package com.tcube.api.service;

import java.util.List;

import org.json.JSONArray;

import com.tcube.api.model.HolidayDetails;

public interface HolidayDetailsService {

	public HolidayDetails createHoliday(HolidayDetails details, String zone);

	public HolidayDetails getById(long id);

	public HolidayDetails updateHoliday(HolidayDetails oldDetails, String zone);

	public HolidayDetails updateHolidayWithoutZone(HolidayDetails newDetails);

	public List<HolidayDetails> getAllHolidays();

	public List<HolidayDetails> getAllHolidaysByOrgId(Long id);

	public List<HolidayDetails> getActiveHolidaysByOrgId(Long id);
	
	public List<HolidayDetails> getTodayHolidayDetails(Long orgId,String Date);

	public List<HolidayDetails> getActiveHolidaysByOrgIdAndDates(HolidayDetails getNewDetails, String zone);

	List<HolidayDetails> getActiveHolidaysByOrgIdAndDates(Long orgId, String startDate, String endDate, String zone);
	
	public int bulkDelete(JSONArray ids);

}
