package com.tcube.api.dao;

import java.util.List;

import org.json.JSONArray;

import com.tcube.api.model.DayPlannerDetails;

public interface DayPlannerDetailsDao {

	public DayPlannerDetails createDayTask(DayPlannerDetails details, String zone);

	public DayPlannerDetails getById(Long id);

	public DayPlannerDetails updateDayTask(DayPlannerDetails newDetails);

	public List<DayPlannerDetails> getDayTasksByEmpidAndDate(DayPlannerDetails newDetails);

	public int updateTaskDates(StringBuffer id_sb, String date);

	public int updateDayTaskSubmitStatus(StringBuffer id_sb, Boolean status);

	public int updateDayTaskupdateStatus(StringBuffer id_sb, Boolean status);

	public List<DayPlannerDetails> getDayTasksByOrgidAndDate(DayPlannerDetails newDetails);

	public DayPlannerDetails updateEmpImageDetails(String empId, byte[] compressBytes);

	public int bulkDelete(JSONArray deleteIds);

}
