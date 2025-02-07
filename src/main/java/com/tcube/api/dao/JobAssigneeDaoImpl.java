package com.tcube.api.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.tcube.api.model.JobAssigneeDetails;
import com.tcube.api.model.JobDetails;

@Component
public class JobAssigneeDaoImpl implements JobAssigneeDao {
    
	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(JobAssigneeDaoImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public JobAssigneeDetails setLoggedhours(String empId, Long jobId, String duration,Double hours) {
		logger.info("JobAssigneeDetails (setLoggedhours) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery(
				"from JobAssigneeDetails where employeeDetails.id=:empId and ref_jobid=:jobId");
		query.setParameter("empId", empId);
		query.setParameter("jobId", jobId);
		List<JobAssigneeDetails> details = query.getResultList();
		// System.out.println(details);
		JobAssigneeDetails data = details.get(0);
		data.setLogged_hours(duration);
		//assignee_cost calculation
		Long cost = (long) (data.getRate_per_hour() * hours);
		data.setAssignee_cost(cost);
		data.setAssignee_hours(hours);
		if (data.getId() == 0) {
			entityManager.persist(data);
		} else {
			entityManager.merge(data);
		}
		logger.info("JobAssigneeDetails (setLoggedhours) >> Exit ");
		return data;
	}

	@Override
	public JobAssigneeDetails getJobAssigneeByEmpJobId(String empId, Long jobId) {
		logger.info("JobAssigneeDetails (getJobAssigneeByEmpJobId) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery(
				"from JobAssigneeDetails where employeeDetails.id=:empId and ref_jobid=:jobId");
		query.setParameter("empId", empId);
		query.setParameter("jobId", jobId);
		List<JobAssigneeDetails> details = query.getResultList();
		JobAssigneeDetails data = new JobAssigneeDetails();
		if(details.size()!=0) {
			 data = details.get(0);
		}
		
		logger.info("JobAssigneeDetails (getJobAssigneeByEmpJobId) >> Exit ");
		return data;
	}

	@Override
	public JobAssigneeDetails updatereferencId(Long Job_id) {
		logger.info("JobAssigneeDetails (updatereferencId) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery(
				"from JobAssigneeDetails where job_id=:i");
		query.setParameter("i", Job_id);
		List<JobAssigneeDetails> details = query.getResultList();
		JobAssigneeDetails data  = new JobAssigneeDetails();
		if(details != null) {
			for(int i=0;i<details.size();i++) {
				data = details.get(i);
				 data.setRef_jobid(Job_id);
				if (data.getId() == 0) {
					entityManager.persist(data);
				} else {
					entityManager.merge(data);
				}
			}
		}
		logger.info("JobAssigneeDetails (updatereferencId) >> Exit ");
		return data;
	}

	@Override
	public List<JobAssigneeDetails> updateAssigneesStatusByRefid(Long Job_id) {
		logger.info("JobAssigneeDetails (getAssigneesByRefid) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery(
				"from JobAssigneeDetails where ref_jobid=:i and job_id=null");
		query.setParameter("i", Job_id);
		List<JobAssigneeDetails> details = query.getResultList();
		JobAssigneeDetails data  = new JobAssigneeDetails();
		
		final Query query2 = session.createQuery(
				"from JobAssigneeDetails where ref_jobid=:i and status=:j");
		query2.setParameter("i", Job_id);
		query2.setParameter("j", "Active");
		List<JobAssigneeDetails> details2 = query2.getResultList();
		 List<String> empid_list = new ArrayList<String>();
		 List<JobAssigneeDetails> empinactive = new ArrayList<JobAssigneeDetails>();
		 List<JobAssigneeDetails> empactive = new ArrayList<JobAssigneeDetails>();
		for(int i=0;i<details2.size();i++) {
			empid_list.add(details2.get(i).getEmployeeDetails().getId());
		}
		
		
		if(details != null) {
			for(int i=0;i<details.size();i++) {
				data = details.get(i);
				if(data.getStatus() == null) {
					
				}
				else {
					if(empid_list.contains(data.getEmployeeDetails().getId()) && !(data.getStatus().equals("Active") && !(data.getStatus().equals("Inactive")))) { 
						data.setStatus(null);
					}
					else {
						data.setStatus("Inactive");
					}
				}
				
				 
				if (data.getId() == 0) {
					entityManager.persist(data);
				} else {
					entityManager.merge(data);
				}
			}
		}
		details = query.getResultList();
		for(int i=0;i<details.size();i++) {
			empinactive.add(details.get(i));
		}
		details2 = query2.getResultList();
		for(int i=0;i<details2.size();i++) {
			empactive.add(details2.get(i));
//			if(details2.get(i).getEmployeeDetails().getId())
		}
		for(int k=0 ; k<empactive.size(); k++) {
			for(int j=0; j<empinactive.size(); j++ ) {
				if( empinactive.get(j).getStatus()!=null) {
					if(empactive.get(k).getEmployeeDetails().getId()==empinactive.get(j).getEmployeeDetails().getId() && empinactive.get(j).getStatus().equals("Inactive")) {
						empinactive.get(j).setStatus(null);
						if (empinactive.get(j).getId() == 0) {
							entityManager.persist(empinactive.get(j));
						} else {
							entityManager.merge(empinactive.get(j));
						}
					}
				}
				
			}
		}
		
		//to set already worked hours to the active hours
		final Query query3 = session.createQuery(
				"from JobAssigneeDetails where ref_jobid=:i and status=null");
		query3.setParameter("i", Job_id);
		List<JobAssigneeDetails> details3 = query3.getResultList();
		//to get current active assignees
		final Query query4 = session.createQuery(
				"from JobAssigneeDetails where job_id=:i and status=:j");
		query4.setParameter("i", Job_id);
		query4.setParameter("j", "Active");
		
		List<JobAssigneeDetails> details_active = query4.getResultList();
		
		 
		for(int i=0;i<details3.size();i++) {
			for(int j=0;j<details_active.size();j++) {
				if(details_active.get(j).getEmployeeDetails().getId() == details3.get(i).getEmployeeDetails().getId()){
					details_active.get(j).setLogged_hours(details3.get(i).getLogged_hours());
					if (details_active.get(j).getId() == 0) {
						entityManager.persist(details_active.get(j));
					} else {
						entityManager.merge(details_active.get(j));
					}
				}
			}
		}
		logger.info("JobAssigneeDetails (updatereferencId) >> Exit ");
		return details;
	}

	@Override
	public JobAssigneeDetails updateAssigneebyProjectupdate(List<Long> job_id, List<String> resource_id) {
		logger.info("JobAssigneeDetails (updateAssigneebyProjectupdate) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		for(int i=0;i<job_id.size();i++) {
				final Query query = session.createQuery(
						"from JobAssigneeDetails where ref_jobid=:i and status !=null");
				query.setParameter("i", job_id.get(i));
				List<JobAssigneeDetails> details = query.getResultList();
				for(int j=0;j<details.size();j++) {
					if(resource_id.contains(details.get(j).getEmployeeDetails().getId())) {
						if(details.get(j).getStatus().equals("Active")) {
							details.get(j).setStatus("Inactive");
							if (details.get(j).getId() == 0) {
								entityManager.persist(details.get(j));
							} else {
								entityManager.merge(details.get(j));
							}
						}
						else {
							details.get(j).setStatus("Active");
							if (details.get(j).getId() == 0) {
								entityManager.persist(details.get(j));
							} else {
								entityManager.merge(details.get(j));
							}
						}
					}
//					if(!(resource_id.contains(details.get(j).getEmployeeDetails().getId()))) {
//						details.get(j).setStatus("Inactive");
//						if (details.get(j).getId() == 0) {
//							entityManager.persist(details.get(j));
//						} else {
//							entityManager.merge(details.get(j));
//						}
//					}
//					else if(resource_id.contains(details.get(j).getEmployeeDetails().getId()) && details.get(j).getStatus().equals("Inactive")) {
//						details.get(j).setStatus("Active");
//						if (details.get(j).getId() == 0) {
//							entityManager.persist(details.get(j));
//						} else {
//							entityManager.merge(details.get(j));
//						}
//					}
				}
		}
		logger.info("JobAssigneeDetails (updateAssigneebyProjectupdate) >> Exit ");
		return null;
	}

	@Override
	public JobAssigneeDetails updatebulkassigneeRemovalStatus(Long Job_id, List<String> assignee_list) {
		logger.info("JobAssigneeDetails (updatebulkassigneeRemovalStatus) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		for(int i=0;i<assignee_list.size();i++) {
			final Query query = session.createQuery(
					"from JobAssigneeDetails where ref_jobid=:k and emp_id=:j and status=:l");
			query.setParameter("k", Job_id);
			query.setParameter("j", assignee_list.get(i));
			query.setParameter("l", "Active");
			List<JobAssigneeDetails> details = query.getResultList();
			if(details.size() > 0) {
				details.get(0).setStatus("Inactive");
				if (details.get(0).getId() == 0) {
					entityManager.persist(details.get(0));
				} else {
					entityManager.merge(details.get(0));
				}
			}
		}
		logger.info("JobAssigneeDetails (updatebulkassigneeRemovalStatus) >> Exit ");
		return null;
	}

	@Override
	public JobAssigneeDetails updatebulkassigneeStatusActive(Long Job_id, String assigneeid) {
		logger.info("JobAssigneeDetails (updatebulkassigneeStatusActive) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from JobAssigneeDetails where ref_jobid=:k and emp_id=:j and status=:l");
		query.setParameter("k", Job_id);
		query.setParameter("j", assigneeid);
		query.setParameter("l", "Inactive");
		List<JobAssigneeDetails> details = query.getResultList();
		if(details.size()>0) {
			details.get(0).setStatus("Active");
			if (details.get(0).getId() == 0) {
				entityManager.persist(details.get(0));
			} else {
				entityManager.merge(details.get(0));
			}
		}
		logger.info("JobAssigneeDetails (updatebulkassigneeStatusActive) >> Exit ");
		return null;
	}

	@Override
	public boolean  deleteDuplicateAssigneeDetails(Long Job_id) {
		logger.info("JobAssigneeDetails (deleteDuplicateAssigneeDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createNativeQuery(
				"delete from job_assignee_details where reference_jobid=:jobId and status is null");
		query.setParameter("jobId", Job_id);
		int count = query.executeUpdate();
		if(count>0) {
			return true;
		}
		logger.info("JobAssigneeDetails (deleteDuplicateAssigneeDetails) >> Exit ");
		return false;
	}

	@Override
	public boolean jobsAssigneeDisableByProjectId(Long jobs_list, long projectId, String empId) {
		// TODO Auto-generated method stub
		logger.info("JobAssigneeDetails (jobsAssigneeDisableByProjectId) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery(
				"update job_assignee_details set status=:k where job_id=:i and emp_id=:j");
		query.setParameter("i", jobs_list);
		query.setParameter("j", empId);
		query.setParameter("k", "Inactive");
//		List<JobAssigneeDetails> details = query.getResultList();
		int count = query.executeUpdate();
		if(count>0) {
			return true;
		}
//		if(details.size()>0) {
//			details.get(0).setStatus("Inactive");
//			if (details.get(0).getId() == 0) {
//				entityManager.persist(details.get(0));
//			} else {
//				entityManager.merge(details.get(0));
//			}
//		}
		logger.info("JobAssigneeDetails (updatebulkassigneeStatusActive) >> Exit ");
		return false;
	}

}
