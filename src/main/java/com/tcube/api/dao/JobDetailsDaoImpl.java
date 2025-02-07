package com.tcube.api.dao;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.CustomJobsDetails;
import com.tcube.api.model.JobAssigneeDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.ProjectDetails;
import com.tcube.api.model.RoleDetails;
import com.tcube.api.model.TimeTrackerDetails;
import com.tcube.api.service.ProjectDetailsService;

@Component
public class JobDetailsDaoImpl implements JobDetailsDao{
	@Autowired
	ProjectDetailsService projectDetailsService;
	
	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(JobDetailsDaoImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public JobDetails createJobDetails(JobDetails jobDetails , String zone) {
		logger.info("JobDetailsDaoImpl(createJobDetails) Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			
			jobDetails.setCreated_time(new Date());
			jobDetails.setModified_time(new Date());
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));

			if(jobDetails.getStart_date() != null) {
				Date date = jobDetails.getStart_date();
				jobDetails.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			}
			
			if(jobDetails.getEnd_date() != null) {
				Date date2 = jobDetails.getEnd_date();
				jobDetails.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
			}
			
			session.save(jobDetails);
			if (jobDetails.getId() == 0) {
				entityManager.persist(jobDetails);
				logger.info("JobDetailsDaoImpl(createJobDetails) Exit>> Job created");
				return jobDetails;
			} else {
				entityManager.merge(jobDetails);
				return jobDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("JobDetailsDaoImpl(createJobDetails) Exit");
		return jobDetails;
	}

	@Override
	public JobDetails getJobById(Long id) {
		logger.info("JobDetailsDaoImpl(getJobById) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final JobDetails details = (JobDetails) session.get(JobDetails.class, id);
		logger.info("JobDetailsDaoImpl(getJobById) Exit");
		return details;
	}
	
	@Override
	public JobDetails getJobByJobNameAndProjectName(Long orgId, String project, String job) {
		logger.info("JobDetailsDaoImpl(getJobByJobNameAndProjectName) Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from JobDetails where orgDetails.id=:id and project_name=:name and job_name=:name1 and is_deleted =:d  order by timestamp(modified_time) desc");
		query.setParameter("name", project);
		query.setParameter("id", orgId);
		query.setParameter("name1", job);
		query.setParameter("d", false);
		@SuppressWarnings("unchecked")
		final JobDetails details = (JobDetails) query.getSingleResult();
		logger.info("JobDetailsDaoImpl(getJobByJobNameAndProjectName) Exit");
		return details;
	}

	@Override
	public JobDetails updateJobDetailWithZone(JobDetails jobDetails, String zone) {
		logger.info("JobDetailsDaoImpl(updateJobDetail) Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	jobDetails.setModified_time(new Date());
	    	
	    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));

			if(jobDetails.getStart_date() != null) {
				Date date = jobDetails.getStart_date();
				jobDetails.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			}
			
			if(jobDetails.getEnd_date() != null) {
				Date date2 = jobDetails.getEnd_date();
				jobDetails.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
			}
//	    	logger.debug("appInfo obj:" + new Gson().toJson(jobDetails));
			session.update(jobDetails);
			if (jobDetails.getId() == 0) {
				entityManager.persist(jobDetails);
				return jobDetails;
			} else {
				entityManager.merge(jobDetails);
				return jobDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    logger.info("JobDetailsDaoImpl(updateJobDetail) Exit");
		return jobDetails;
	}
	
	@Override
	public JobDetails updateJobDetail(JobDetails jobDetails) {
		logger.info("JobDetailsDaoImpl(updateJobDetail) Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	jobDetails.setModified_time(new Date());
	    	logger.debug("appInfo obj:" + new Gson().toJson(jobDetails));
			session.update(jobDetails);
			if (jobDetails.getId() == 0) {
				entityManager.persist(jobDetails);
				return jobDetails;
			} else {
				entityManager.merge(jobDetails);
				return jobDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    logger.info("JobDetailsDaoImpl(updateJobDetail) Exit");
		return jobDetails;
	}

	@Override
	public JobDetails deleteJobDetails(JobDetails oldDetails) {
		logger.info("JobDetailsDaoImpl(deleteJobDetails) Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	oldDetails.setModified_time(new Date());
	    	logger.debug("appInfo obj:" + new Gson().toJson(oldDetails));
			session.update(oldDetails);
			if (oldDetails.getId() == 0) {
				entityManager.persist(oldDetails);
				return oldDetails;
			} else {
				entityManager.merge(oldDetails);
				return oldDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    logger.info("JobDetailsDaoImpl(deleteJobDetails) Exit");
		return oldDetails;
	}

	@Override
	public List<JobDetails> getAllJobDetails() {
		logger.info("JobDetailsDaoImpl (getAllJobDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from JobDetails");
		List<JobDetails> details = query.getResultList();
		logger.info("JobDetailsDaoImpl (getAllJobDetails) >> Exit ");
		return details;
	}

	@Override
	public List<JobDetails> getJobDetailsByOrgId(Long id) {
		logger.info("JobDetailsDaoImpl(getJobDetailsByOrgId) Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from JobDetails where orgDetails.id=:id order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<JobDetails> details = query.getResultList();
		logger.info("JobDetailsDaoImpl(getJobDetailsByOrgId) Exit");
		return details;
	}
	
	@Override
	public List<JobDetails> getActiveJobDetailsByOrgId(Long id) {
		logger.info("JobDetailsDaoImpl(getActiveJobDetailsByOrgId) Entry>> OrgId :" + id);
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from JobDetails where orgDetails.id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked")
		 List<JobDetails> details = query.getResultList();
		logger.info("JobDetailsDaoImpl(getActiveJobDetailsByOrgId) Exit");
		return details;
	}
	

	@Override
	public List<CustomJobsDetails> getActiveJobDetailsByOrgId_new(Long id) {
		logger.info("JobDetailsDaoImpl(getActiveJobDetailsByOrgId_new) Entry>> OrgId :" + id);
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from JobDetails where orgDetails.id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked")
		 List<JobDetails> details = query.getResultList();
		List<CustomJobsDetails> data = new ArrayList<CustomJobsDetails>();
		if(details.size()!=0) {
			for(int i=0;i<details.size();i++) {
				CustomJobsDetails value = new CustomJobsDetails();
				value.setId(details.get(i).getId());
				value.setOrgId(details.get(i).getOrgDetails().getOrg_id());
				value.setProject_id(details.get(i).getProject_id());
				value.setProject_name(details.get(i).getProject_name());
				value.setJob_name(details.get(i).getJob_name());
				value.setJob_cost(details.get(i).getJob_cost());
				final ProjectDetails pjtDetails = projectDetailsService.getProjectById(details.get(i).getProject_id());
				value.setIs_activated_project(pjtDetails.getIs_activated());
				//query to get the active assignees
				final Query query_active = session.createQuery("from JobAssigneeDetails where ref_jobid=:i and (status=:j or status=:k) and is_deleted=:l");
				query_active.setParameter("i", details.get(i).getId());
				query_active.setParameter("j", "Active");
				query_active.setParameter("k", "Inactive");
				query_active.setParameter("l", false);
				List<JobAssigneeDetails> details_active = query_active.getResultList();
				value.setIs_activated(details.get(i).getIs_activated());
				List<JSONObject> jobassignees = new ArrayList<JSONObject>();
				int count_active= 0;
				for(int j=0; j<details_active.size() ;j++) {
				    if(details_active.get(j).getStatus().equals("Active")) {
                        count_active+= 1;
                    }
					JSONObject object  = new JSONObject(); 
					object.put("id", details_active.get(j).getEmployeeDetails().getId());
//					object.put("profile_image", details_active.get(j).getEmployeeDetails().getProfile_image());
					object.put("name", details_active.get(j).getEmployeeDetails().getFirstname() + " " + details_active.get(j).getEmployeeDetails().getLastname());
					object.put("rate_per_hour", details_active.get(j).getRate_per_hour());
					object.put("logged_hours", details_active.get(j).getLogged_hours());
					object.put("assignee_cost", details_active.get(j).getAssignee_cost());
					object.put("assignee_hours", details_active.get(j).getAssignee_hours());
					object.put("status", details_active.get(j).getStatus());
					jobassignees.add(object);
				}
				
				//query to get the inactive assignees
//				final Query query_inactive = session.createQuery("from JobAssigneeDetails where ref_jobid=:i and status=:j");
//				query_inactive.setParameter("i", details.get(i).getId());
//				query_inactive.setParameter("j", "Inactive");
//				List<JobAssigneeDetails> details_inactive = query_inactive.getResultList();
//				for(int k=0; k<details_inactive.size();k++) {
//					JSONObject object  = new JSONObject(); 
//					object.put("id", details_inactive.get(k).getEmployeeDetails().getId());
//					object.put("profile_image", details_inactive.get(k).getEmployeeDetails().getProfile_image());
//					object.put("name", details_inactive.get(k).getEmployeeDetails().getFirstname() + " " + details_inactive.get(k).getEmployeeDetails().getLastname());
//					object.put("rate_per_hour", details_inactive.get(k).getRate_per_hour());
//					object.put("logged_hours", details_inactive.get(k).getLogged_hours());
//					object.put("assignee_cost", details_inactive.get(k).getAssignee_cost());
//					object.put("assignee_hours", details_inactive.get(k).getAssignee_hours());
//					object.put("status", details_inactive.get(k).getStatus());
//					jobassignees.add(object);
//				}
				value.setJobAssigneeDetails(jobassignees);
				value.setStart_date(details.get(i).getStart_date());
				value.setEnd_date(details.get(i).getEnd_date());
				value.setHours(details.get(i).getHours());
				value.setRate_per_hour(details.get(i).getRate_per_hour());
				value.setBill(details.get(i).getBill());
				value.setIs_deleted(details.get(i).getIs_deleted());
				value.setLogged_hours(details.get(i).getLogged_hours());
				value.setDescription(details.get(i).getDescription());
				value.setCount_active(count_active);
				value.setJob_status(details.get(i).getJob_status());
				data.add(value);
			}
		}
		logger.info("JobDetailsDaoImpl(getActiveJobDetailsByOrgId_new) Exit");
		return data;
	}
	
	@Override
	public JobDetails updateJobStatus(JobDetails details) {
		logger.info("JobDetailsDaoImpl(updateJobDetail) Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	details.setModified_time(new Date());
	    	logger.debug("appInfo obj:" + new Gson().toJson(details));
			session.update(details);
			if (details.getId() == 0) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    logger.info("JobDetailsDaoImpl(updateJobDetail) Exit");
		return details;
	}

	@Override
	public JobDetails setLoggedhours(String project, String jobs, String duration) {
		logger.info("JobDetailsDaoImpl(setLoggedhours) Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from JobDetails where project_name=:project and job_name=:job");
		query.setParameter("project", project);
		query.setParameter("job", jobs);
		@SuppressWarnings("unchecked")
		final List<JobDetails> details = query.getResultList();
		JobDetails data = details.get(0);
		data.setLogged_hours(duration);
		data.setModified_time(new Date());
		session.update(data);
		if (data.getId() == 0) {
			entityManager.persist(data);
		} else {
			entityManager.merge(data);
		}
		logger.info("JobDetailsDaoImpl(setLoggedhours) Exit");
	   return data;
	}

	@Override
	public List<CustomJobsDetails> getActiveJobDetailsByProjectId(Long id) {
		logger.info("JobDetailsDaoImpl(getActiveJobDetailsByProjectId) Entry");
		final Session session = entityManager.unwrap(Session.class);
//		final Query query = session.createQuery(
//				"from JobDetails where projectDetails.id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		final Query query = session.createQuery(
				"from JobDetails where project_id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked")
		final List<JobDetails> details = query.getResultList();
		List<CustomJobsDetails> data = new ArrayList<CustomJobsDetails>();		
		if(details.size()!=0) {
			for(int i=0;i<details.size();i++) {
				CustomJobsDetails value = new CustomJobsDetails();
				value.setId(details.get(i).getId());
				value.setOrgId(details.get(i).getOrgDetails().getOrg_id());
				value.setProject_id(details.get(i).getProject_id());
				value.setProject_name(details.get(i).getProject_name());
				value.setJob_name(details.get(i).getJob_name());
				value.setLogged_hours(details.get(i).getLogged_hours());
				value.setHours(details.get(i).getHours());
				
				//query to get the active assignees
				final Query query_active = session.createQuery("from JobAssigneeDetails where ref_jobid=:i and (status=:j or status=:k)");
				query_active.setParameter("i", details.get(i).getId());
				query_active.setParameter("j", "Active");
				query_active.setParameter("k", "Inactive");
				List<JobAssigneeDetails> details_active = query_active.getResultList();
				
				List<JSONObject> jobassignees = new ArrayList<JSONObject>();
				int count_active= 0;
				for(int j=0; j<details_active.size() ;j++) {
				    if(details_active.get(j).getStatus() == "Active") {
	                    count_active+= 1;
				    }
					JSONObject object  = new JSONObject(); 
					object.put("id", details_active.get(j).getEmployeeDetails().getId());
//					object.put("profile_image", );
					object.put("name", details_active.get(j).getEmployeeDetails().getFirstname() + " " + details_active.get(j).getEmployeeDetails().getLastname());
					object.put("rate_per_hour", details_active.get(j).getRate_per_hour());
					object.put("logged_hours", details_active.get(j).getLogged_hours());
					object.put("assignee_cost", details_active.get(j).getAssignee_cost());
					object.put("assignee_hours", details_active.get(j).getAssignee_hours());
					object.put("status", details_active.get(j).getStatus());
					jobassignees.add(object);
				}
				
				//query to get the inactive assignees
//				final Query query_inactive = session.createQuery("from JobAssigneeDetails where ref_jobid=:i and status=:j");
//				query_inactive.setParameter("i", details.get(i).getId());
//				query_inactive.setParameter("j", "Inactive");
//				List<JobAssigneeDetails> details_inactive = query_inactive.getResultList();
//				for(int k=0; k<details_inactive.size();k++) {
//					JSONObject object  = new JSONObject(); 
//					object.put("id", details_inactive.get(k).getEmployeeDetails().getId());
//					object.put("profile_image", details_inactive.get(k).getEmployeeDetails().getProfile_image());
//					object.put("name", details_inactive.get(k).getEmployeeDetails().getFirstname() + " " + details_inactive.get(k).getEmployeeDetails().getLastname());
//					object.put("rate_per_hour", details_inactive.get(k).getRate_per_hour());
//					object.put("logged_hours", details_inactive.get(k).getLogged_hours());
//					object.put("assignee_cost", details_inactive.get(k).getAssignee_cost());
//					object.put("assignee_hours", details_inactive.get(k).getAssignee_hours());
//					object.put("status", details_inactive.get(k).getStatus());
//					jobassignees.add(object);
//				}
				
				value.setJobAssigneeDetails(jobassignees);
				value.setCount_active(count_active);
				data.add(value);
			}
		}
		logger.info("JobDetailsDaoImpl(getActiveJobDetailsByProjectId) Exit");
		return data;
	}

	@Override
	public JobDetails updateProjectnameinjob(String olddata, String newdata, Long id,Long project_id) {
		logger.info("JobDetailsDaoImpl(updateProjectname) Entry");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from JobDetails where project_name=:j and org_id=:k and project_id=:i");
		query.setParameter("i", project_id);
		query.setParameter("j", olddata);
		query.setParameter("k", id);
		List<JobDetails> details = query.getResultList();
		for(int i=0; i<details.size();i++) {
			details.get(i).setProject_name(newdata);
			if (details.get(i).getId() == 0) {
				entityManager.persist(details.get(i));
			} else {
				entityManager.merge(details.get(i));
			}
		}
		logger.info("JobDetailsDaoImpl(updateProjectname) Exit");
		return null;
	}

	@Override
	public List<JobDetails> getActiveJobsDetailsByProjectId(Long id) {
		// TODO Auto-generated method stub
		logger.info("JobDetailsDaoImpl(getActiveJobDetailsByProjectId) Entry");
		final Session session = entityManager.unwrap(Session.class);
//		final Query query = session.createQuery(
//				"from JobDetails where projectDetails.id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		final Query query = session.createQuery(
				"from JobDetails where project_id=:id and is_deleted=:k and is_activated=:a order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("a", true);
		@SuppressWarnings("unchecked")
		final List<JobDetails> details = query.getResultList();
		logger.info("JobDetailsDaoImpl(getActiveJobDetailsByProjectId) Exit");
		return details;
	}

	@Override
	public List<JobDetails> getInactiveJobsDetailsByProjectId(Long id) {
		// TODO Auto-generated method stub
		logger.info("JobDetailsDaoImpl(getInactiveJobsDetailsByProjectId) Entry");
		final Session session = entityManager.unwrap(Session.class);
//		final Query query = session.createQuery(
//				"from JobDetails where projectDetails.id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		final Query query = session.createQuery(
				"from JobDetails where project_id=:id and is_deleted =:k and is_activated =:a order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("a", false);
		@SuppressWarnings("unchecked")
		final List<JobDetails> details = query.getResultList();
		logger.info("JobDetailsDaoImpl(getInactiveJobDetailsByProjectId) Exit");
		return details;
	}

//	for job hard delete
	@Override
	public boolean jobHardDeleteById(Long id) {
		boolean isDel;
		logger.info("JobDetailsDaoImpl(jobHardDeleteById) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final Query query1 = session.createQuery("delete from JobAssigneeDetails where reference_jobid=:job_id");
		final Query query = session.createQuery("delete from JobDetails where id=:job_id");
		query1.setParameter("job_id", id);
		query.setParameter("job_id", id);
		query1.executeUpdate();
		int result = query.executeUpdate();
		if(result != 1) {
			isDel = false;
		} else {
			isDel =true;
		}
		logger.info("JobDetailsDaoImpl(jobHardDeleteById) exit>>");
		return isDel;
	}
//	Jobs bulk hard delete
	@Override
	public boolean JobsBulkHardDelete(Long id) {
		boolean isDel;
		logger.info("JobDetailsDaoImpl(jobsBulkHardDelete) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final Query query1 = session.createQuery("delete from JobAssigneeDetails where reference_jobid=:job_id");
		final Query query = session.createQuery("delete from JobDetails where id=:job_id");
		query1.setParameter("job_id", id);
		query.setParameter("job_id", id);
		query1.executeUpdate();
		int result = query.executeUpdate();
		if(result != 1) {
			isDel = false;
		} else {
			isDel =true;
		}
		logger.info("JobDetailsDaoImpl(jobsBulkHardDelete) exit>>");
		return isDel;
	}
	
	//To show the assignee in active  after activated in manage user
	@Override
	public int enableAssigneeInJob(String id) {
				logger.info("JobDetailsDaoImpl(getEmployeeIdToEnableUserInAllJobs) >> Entry");
				final Session session = entityManager.unwrap(Session.class);
				final Query query = session.createNativeQuery("update job_assignee_details set status=:j where emp_id =:k");
				query.setParameter("j", "Active");
				query.setParameter("k", id);
				@SuppressWarnings("unchecked")
				int details = query.executeUpdate();
				logger.info("JobDetailsDaoImpl(getEmployeeIdToEnableUserInAllJobs) >>Exit");
				return details;
	}
	
	// To show the assignee in active after bulk activate in view assignee
	 		@Override
	 		public int  enableBulkAssigneeInJob(JSONArray ids) {
	 				logger.info("ProjectDetailsDaoImpl(enableBulkAssigneeInJob) >> Entry");
	 				final Session session = entityManager.unwrap(Session.class);
	 	 			String id_list = new String();
	 	 			for (int i = 0; i < ids.length(); i++) {
	 	 				id_list += "'" + ids.get(i) + "'" + ",";
	 	 			}
	 	 			StringBuffer id_sb = new StringBuffer(id_list);
	 	 			id_sb.deleteCharAt(id_sb.length() - 1);
	 				final Query query = session.createNativeQuery("update job_assignee_details set status=:j where emp_id in (" + id_sb + ")");
	 				query.setParameter("j", "Active");
	 				@SuppressWarnings("unchecked")
	 				int details = query.executeUpdate();
	 				logger.info("ProjectDetailsDaoImpl(enableBulkAssigneeInJob) >>Exit");
	 				return details;
	 		}

// To remove the disabled job assignee after delete those assignees/user in manage user
	@Override
	public int removeJobAssigneeByEmployeeId(String id) {
		logger.info("JobDetailsDaoImpl(removeJobAssigneeByEmployeeId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery("update job_assignee_details set is_deleted=:j where emp_id =:k");
		query.setParameter("j", true);
		query.setParameter("k", id);
		@SuppressWarnings("unchecked")
		int details = query.executeUpdate();
		logger.info("JobDetailsDaoImpl(removeJobAssigneeByEmployeeId) >>Exit");
		return details;
		
	}
	
	//To remove bulk disabled job assignees after bulk delete those assignees/user in manage user
	@Override
	public int removeBulkJobAssigneeByEmployeeId(JSONArray ids) {
		logger.info("JobDetailsDaoImpl(removeBulkJobAssigneeByEmployeeId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}

		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session.createNativeQuery("update job_assignee_details set is_deleted=:j where  emp_id in (" + id_sb + ")");
		query.setParameter("j", true);
//		query.setParameter("k", id);
		@SuppressWarnings("unchecked")
		int details = query.executeUpdate();
		logger.info("JobDetailsDaoImpl(removeBulkJobAssigneeByEmployeeId) >>Exit");
		return details;
		
	}

	@Override
	public List<CustomJobsDetails> getActiveJobNameListWithProjectByOrgId(Long id) {
		logger.info("JobDetailsDaoImpl(getActiveJobNameListWithProjectByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from JobDetails where orgDetails.org_id=:id and is_deleted =:v");
		query.setParameter("id", id);
		query.setParameter("v", false);
//		query.setParameter("a", true);
		@SuppressWarnings("unchecked")
		final List<JobDetails> details = query.getResultList();
		List<CustomJobsDetails> data = new ArrayList<CustomJobsDetails>();
		for(int i=0;i<details.size();i++) {
			CustomJobsDetails value = new CustomJobsDetails();
			value.setOrgId(details.get(i).getOrgDetails().getOrg_id());
			value.setProject_id(details.get(i).getProject_id());
			value.setProject_name(details.get(i).getProject_name());
			value.setJob_name(details.get(i).getJob_name());
			value.setId(details.get(i).getId());
			data.add(value);
		}
		logger.info("JobDetailsDaoImpl(getActiveJobNameListWithProjectByOrgId) >> Exit");
		return data;
	}

	@Override
	public List<CustomJobsDetails> getAllJobsByStatusByOrgId(Long org_Id, String status, boolean is_Activated) {
		logger.info("JobDetailsDaoImpl(getAllJobsByStatusByOrgId) Entry>> OrgId :" + org_Id);
		StringBuffer job_status = new StringBuffer(status);
		System.out.println(job_status);
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from JobDetails where org_id=:i and job_status in (" + job_status + ") and is_deleted=false and is_activated=:k order by timestamp(modified_time) desc");
		query.setParameter("i", org_Id);
 		query.setParameter("k", is_Activated);
// 		query.setParameter("c", "Completed");
		@SuppressWarnings("unchecked")
		 List<JobDetails> details = query.getResultList();
		List<CustomJobsDetails> data = new ArrayList<CustomJobsDetails>();
		if(details.size()!=0) {
			for(int i=0;i<details.size();i++) {
				CustomJobsDetails value = new CustomJobsDetails();
				value.setId(details.get(i).getId());
				value.setOrgId(details.get(i).getOrgDetails().getOrg_id());
				value.setProject_id(details.get(i).getProject_id());
				value.setProject_name(details.get(i).getProject_name());
				value.setJob_name(details.get(i).getJob_name());
				value.setJob_cost(details.get(i).getJob_cost());
				final ProjectDetails pjtDetails = projectDetailsService.getProjectById(details.get(i).getProject_id());
				value.setIs_activated_project(pjtDetails.getIs_activated());
				//query to get the active assignees
				final Query query_active = session.createQuery("from JobAssigneeDetails where ref_jobid=:i and (status=:j or status=:k) and is_deleted=:l");
				query_active.setParameter("i", details.get(i).getId());
				query_active.setParameter("j", "Active");
				query_active.setParameter("k", "Inactive");
				query_active.setParameter("l", false);
				List<JobAssigneeDetails> details_active = query_active.getResultList();
				value.setIs_activated(details.get(i).getIs_activated());
				List<JSONObject> jobassignees = new ArrayList<JSONObject>();
				int count_active= 0;
				for(int j=0; j<details_active.size() ;j++) {
				    if(details_active.get(j).getStatus().equals("Active")) {
                        count_active+= 1;
                    }
					JSONObject object  = new JSONObject(); 
					object.put("id", details_active.get(j).getEmployeeDetails().getId());
//					object.put("profile_image", details_active.get(j).getEmployeeDetails().getProfile_image());
					object.put("name", details_active.get(j).getEmployeeDetails().getFirstname() + " " + details_active.get(j).getEmployeeDetails().getLastname());
					object.put("rate_per_hour", details_active.get(j).getRate_per_hour());
					object.put("logged_hours", details_active.get(j).getLogged_hours());
					object.put("assignee_cost", details_active.get(j).getAssignee_cost());
					object.put("assignee_hours", details_active.get(j).getAssignee_hours());
					object.put("status", details_active.get(j).getStatus());
					jobassignees.add(object);
				}
				value.setJobAssigneeDetails(jobassignees);
				value.setStart_date(details.get(i).getStart_date());
				value.setEnd_date(details.get(i).getEnd_date());
				value.setHours(details.get(i).getHours());
				value.setRate_per_hour(details.get(i).getRate_per_hour());
				value.setBill(details.get(i).getBill());
				value.setIs_deleted(details.get(i).getIs_deleted());
				value.setLogged_hours(details.get(i).getLogged_hours());
				value.setDescription(details.get(i).getDescription());
				value.setCount_active(count_active);
				value.setJob_status(details.get(i).getJob_status());
				data.add(value);
			}
		}
		logger.info("JobDetailsDaoImpl(getAllJobsByStatusByOrgId) Exit");
		return data;
	}
	
}
