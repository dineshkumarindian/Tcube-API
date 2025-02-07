package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.JobDetailsDao;
import com.tcube.api.model.CustomJobsDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.TimeTrackerDetails;

@Service
@Transactional
public class JobDetailsServiceImpl implements JobDetailsService{

	@Autowired
	JobDetailsDao jobDetailsDao; 
	@Override
	public JobDetails createJobDetails(JobDetails jobDetails , String zone) {
		return jobDetailsDao.createJobDetails(jobDetails , zone);
	}
	@Override
	public JobDetails getJobById(Long id) {
		return jobDetailsDao.getJobById(id);
	}
	@Override
	public JobDetails updateJobDetail(JobDetails jobDetails) {
		return jobDetailsDao.updateJobDetail(jobDetails);
	}
	@Override
	public JobDetails deleteJobDetails(JobDetails oldDetails) {
		return jobDetailsDao.deleteJobDetails(oldDetails);
	}
	@Override
	public List<JobDetails> getAllJobDetails() {
		return jobDetailsDao.getAllJobDetails();
	}
	@Override
	public List<JobDetails> getJobDetailsByOrgId(Long id) {
		return jobDetailsDao.getJobDetailsByOrgId(id);
	}
	@Override
	public JobDetails updateJobStatus(JobDetails details) {
		return jobDetailsDao.updateJobStatus(details);
	}
	@Override
	public JobDetails setLoggedhours(String project, String jobs, String duration) {
		return jobDetailsDao.setLoggedhours(project, jobs, duration);
	}
	@Override
	public List<JobDetails> getActiveJobDetailsByOrgId(Long id) {
		// TODO Auto-generated method stub
		return jobDetailsDao.getActiveJobDetailsByOrgId(id);
	}
	@Override
	public List<CustomJobsDetails> getActiveJobDetailsByProjectId(Long id) {
		// TODO Auto-generated method stub
		return jobDetailsDao.getActiveJobDetailsByProjectId(id);
	}
	@Override
	public JobDetails updateProjectnameinjob(String olddata, String newdata, Long id,Long project_id) {
		return jobDetailsDao.updateProjectnameinjob(olddata,newdata,id,project_id);
	}
	@Override
	public List<CustomJobsDetails> getActiveJobDetailsByOrgId_new(Long id) {
		// TODO Auto-generated method stub
		return jobDetailsDao.getActiveJobDetailsByOrgId_new(id);
	}
	@Override
	public JobDetails getJobByJobNameAndProjectName(Long orgId, String project, String job) {
		// TODO Auto-generated method stub
		return jobDetailsDao.getJobByJobNameAndProjectName(orgId, project , job);
	}
	@Override
	public List<JobDetails> getActiveJobsDetailsByProjectId(Long id) {
		// TODO Auto-generated method stub
		return jobDetailsDao.getActiveJobsDetailsByProjectId(id);
	}
	@Override
	public List<JobDetails> getInactiveJobsDetailsByProjectId(Long id) {
		// TODO Auto-generated method stub
		return jobDetailsDao.getInactiveJobsDetailsByProjectId(id);
	}
	@Override
	public JobDetails updateJobDetailWithZone(JobDetails jobDetails, String zone) {
		// TODO Auto-generated method stub
		return jobDetailsDao.updateJobDetailWithZone(jobDetails,zone);
	}
	@Override
	public boolean jobHardDeleteById(Long id) {
		// TODO Auto-generated method stub
		return jobDetailsDao.jobHardDeleteById(id);
	}
	@Override
	public boolean JobsBulkHardDelete(Long id) {
		// TODO Auto-generated method stub
		return jobDetailsDao.JobsBulkHardDelete(id);
	}
	
	@Override
	public int enableAssigneeInJob(String id) {
		return jobDetailsDao.enableAssigneeInJob(id);
	}
	
	@Override
	public int  enableBulkAssigneeInJob(JSONArray ids) {
		return jobDetailsDao.enableBulkAssigneeInJob(ids);
	}
	

	@Override
	public int removeJobAssigneeByEmployeeId(String id) {
		return jobDetailsDao.removeJobAssigneeByEmployeeId(id);
	}
	
	@Override
	public int removeBulkJobAssigneeByEmployeeId(JSONArray ids) {
		return jobDetailsDao.removeBulkJobAssigneeByEmployeeId(ids);
	}
	@Override
	public List<CustomJobsDetails> getActiveJobNameListWithProjectByOrgId(Long id) {
		return jobDetailsDao.getActiveJobNameListWithProjectByOrgId(id);
	}
	@Override
	public List<CustomJobsDetails> getAllJobsByStatusByOrgId(Long org_Id, String status, boolean is_Activated) {
		return jobDetailsDao.getAllJobsByStatusByOrgId(org_Id,status,is_Activated);
	}
}
