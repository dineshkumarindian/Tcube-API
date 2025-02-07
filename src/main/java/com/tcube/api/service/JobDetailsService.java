package com.tcube.api.service;

import java.util.List;

import org.json.JSONArray;

import com.tcube.api.model.CustomJobsDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.TimeTrackerDetails;

public interface JobDetailsService {

	public JobDetails createJobDetails(JobDetails jobDetails, String zone);

	public JobDetails getJobById(Long id);

	public JobDetails getJobByJobNameAndProjectName(Long orgId, String project, String job);
	
	public JobDetails updateJobDetail(JobDetails jobDetails);

	public JobDetails deleteJobDetails(JobDetails oldDetails);

	public List<JobDetails> getAllJobDetails();

	public List<JobDetails> getJobDetailsByOrgId(Long id);

	public JobDetails updateJobStatus(JobDetails details);
	
	public JobDetails setLoggedhours(String project, String jobs, String duration);

	public List<JobDetails> getActiveJobDetailsByOrgId(Long id);
	
	public List<CustomJobsDetails> getActiveJobDetailsByOrgId_new(Long id);

	public List<CustomJobsDetails> getActiveJobDetailsByProjectId(Long id);
	
	public JobDetails updateProjectnameinjob(String olddata,String newdata,Long id,Long project_id);

	public List<JobDetails> getActiveJobsDetailsByProjectId(Long id);

	public List<JobDetails> getInactiveJobsDetailsByProjectId(Long id);

	public JobDetails updateJobDetailWithZone(JobDetails jobDetails, String zone);

	public boolean jobHardDeleteById(Long id);

	public boolean JobsBulkHardDelete(Long id);
	
	public int enableAssigneeInJob(String id);
	
	public int  enableBulkAssigneeInJob(JSONArray ids);
	
	public int removeJobAssigneeByEmployeeId(String id);
	
	public int removeBulkJobAssigneeByEmployeeId(JSONArray ids);

	public List<CustomJobsDetails> getActiveJobNameListWithProjectByOrgId(Long id);

	public List<CustomJobsDetails> getAllJobsByStatusByOrgId(Long org_Id, String status, boolean is_Activated);

}
