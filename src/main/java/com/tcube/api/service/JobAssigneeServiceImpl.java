package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.JobAssigneeDao;
import com.tcube.api.model.JobAssigneeDetails;

@Service
@Transactional
public class JobAssigneeServiceImpl implements JobAssigneeService {
	
	@Autowired
	JobAssigneeDao jobAssigneeDao;
	
	@Override
	public JobAssigneeDetails setLoggedhours(String empId, Long jobId, String duration,Double hours) {
		return jobAssigneeDao.setLoggedhours(empId, jobId, duration,hours);
	}

	@Override
	public JobAssigneeDetails getJobAssigneeByEmpJobId(String empId, Long jobId) {
		return jobAssigneeDao.getJobAssigneeByEmpJobId(empId, jobId);
	}

	@Override
	public JobAssigneeDetails updatereferencId(Long Job_id) {
		return jobAssigneeDao.updatereferencId(Job_id);
	}

	@Override
	public List<JobAssigneeDetails> updateAssigneesStatusByRefid(Long Job_id) {
		return jobAssigneeDao.updateAssigneesStatusByRefid(Job_id);
	}

	@Override
	public JobAssigneeDetails updateAssigneebyProjectupdate(List<Long> job_id, List<String> resource_id) {
		return jobAssigneeDao.updateAssigneebyProjectupdate(job_id,resource_id);
	}

	@Override
	public JobAssigneeDetails updatebulkassigneeRemovalStatus(Long Job_id, List<String> assignee_list) {
		return jobAssigneeDao.updatebulkassigneeRemovalStatus(Job_id, assignee_list);
	}

	@Override
	public JobAssigneeDetails updatebulkassigneeStatusActive(Long Job_id, String assigneeid) {
		return jobAssigneeDao.updatebulkassigneeStatusActive(Job_id, assigneeid);
	}

	@Override
	public boolean  deleteDuplicateAssigneeDetails(Long Job_id) {
		return jobAssigneeDao.deleteDuplicateAssigneeDetails(Job_id);
	}

	@Override
	public boolean jobsAssigneeDisableByProjectId(Long jobs_list, long projectId, String empId) {
		// TODO Auto-generated method stub
		return jobAssigneeDao.jobsAssigneeDisableByProjectId(jobs_list,projectId,empId);
	}

}
