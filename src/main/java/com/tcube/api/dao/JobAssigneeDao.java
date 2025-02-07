package com.tcube.api.dao;

import java.util.List;

import com.tcube.api.model.JobAssigneeDetails;

public interface JobAssigneeDao {
	public JobAssigneeDetails setLoggedhours(String empId, Long jobId, String duration, Double hours);
	
	public JobAssigneeDetails getJobAssigneeByEmpJobId(String empId, Long jobId);
	
	public JobAssigneeDetails updatereferencId(Long Job_id);
	
	public List<JobAssigneeDetails> updateAssigneesStatusByRefid(Long Job_id);
	
	public JobAssigneeDetails updateAssigneebyProjectupdate(List<Long> job_id, List<String> resource_id);
	
	public JobAssigneeDetails updatebulkassigneeRemovalStatus(Long Job_id, List<String> assignee_list);
	
	public JobAssigneeDetails updatebulkassigneeStatusActive(Long Job_id, String assigneeid);

	public boolean  deleteDuplicateAssigneeDetails(Long Job_id);

	public boolean jobsAssigneeDisableByProjectId(Long jobs_list, long projectId, String empId);
}
