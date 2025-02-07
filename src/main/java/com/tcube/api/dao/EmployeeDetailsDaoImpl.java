package com.tcube.api.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.AttendanceDetails;
import com.tcube.api.model.ClientDetails;
import com.tcube.api.model.CustomEmployeeDetails;
import com.tcube.api.model.CustomEmployeeDetails2;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.JobAssigneeDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.LeaveTrackerDetails;
import com.tcube.api.model.ProjectDetails;
import com.tcube.api.model.ProjectResourceDetails;
import com.tcube.api.model.RoleDetails;
import com.tcube.api.utils.EncryptorUtil;
import com.tcube.api.utils.ImageProcessor;

@Component
public class EmployeeDetailsDaoImpl implements EmployeeDetailsDao {

	@PersistenceContext
	private EntityManager entityManager;


	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(EmployeeDetailsDaoImpl.class);


	@Override
	public EmployeeDetails createEmployeeDetails(EmployeeDetails employeedetails) {
		logger.info("EmployeeDetailsDaoImpl(createEmployeeDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			employeedetails.setCreated_time(new Date());
			employeedetails.setModified_time(new Date());
//			employeedetails.setId(null);
			employeedetails.setPassword(EncryptorUtil.encryptPropertyValue(employeedetails.getPassword()));
			logger.debug("EmployeeDetailsDaoImpl obj:" + new Gson().toJson(employeedetails));
			session.save(employeedetails);
			if (employeedetails.getId() != null) {
				entityManager.persist(employeedetails);
				return employeedetails;
			} else {
				entityManager.merge(employeedetails);
				return employeedetails;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		logger.info("EmployeeDetailsDaoImpl(createEmployeeDetails) >> Exit ");

		return employeedetails;
	}


	@Override
	public EmployeeDetails createEmployeeDetailsWithTimeZone(EmployeeDetails employeedetails,String zone) {
		logger.info("EmployeeDetailsDaoImpl(createEmployeeDetailsWithTimeZone) >> Entry ");
		Date date = employeedetails.getDate_of_joining();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// Use india time zone to format the date in
		df.setTimeZone(TimeZone.getTimeZone(zone));
		try {
			employeedetails.setDate_of_joining(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(df.format(date)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Session session = entityManager.unwrap(Session.class);
		entityManager.clear();
		try {
			employeedetails.setCreated_time(new Date());
			employeedetails.setModified_time(new Date());
//			employeedetails.setId(null);
			employeedetails.setPassword(EncryptorUtil.encryptPropertyValue(employeedetails.getPassword()));
			logger.debug("EmployeeDetailsDaoImpl obj:" + new Gson().toJson(employeedetails));
			session.clear();
			session.save(employeedetails);		
			if (employeedetails.getId() != null) {
				entityManager.persist(employeedetails);
				return employeedetails;
			} else {
				entityManager.merge(employeedetails);
				return employeedetails;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		logger.info("EmployeeDetailsDaoImpl(createEmployeeDetailsWithTimeZone) >> Exit ");

		return employeedetails;
	}

	@Override
	public List<EmployeeDetails> getAllEmployeeDetails() {
		logger.info("EmployeeDetailsDaoImpl (getAllEmployeeDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked") final Query query = session.createQuery("from EmployeeDetails");
		List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl (getAllEmployeeDetails) >> Exit ");
		return details;

	}


	@Override
	public EmployeeDetails getAllEmployeeDetailsByID(String id) {
		logger.info("EmployeeDetailsDaoImpl(getAllEmployeeDetailsByID) Entry>> Request");
		final Session session = entityManager.unwrap(Session.class);
		final EmployeeDetails employeeDetails = (EmployeeDetails) session.get(EmployeeDetails.class, id);
		logger.info("EmployeeDetailsDaoImpl (getAllEmployeeDetailsByEmpID) >> Exit ");
		return employeeDetails;
	}

	@Override
	public List<EmployeeDetails> getAllEmployeeDetailsByEmail() {
		logger.info("EmployeeDetailsDaoImpl (getAllEmployeeDetailsByEmail) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked") final Query query = session.createQuery("select email from EmployeeDetails where is_deleted=:j");
		query.setParameter("j", false);
		List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl (getAllEmployeeDetailsByEmail) >> Exit ");
		return details;

	}

	@Override
	public EmployeeDetails updateEmployeeDetails(EmployeeDetails details) {
		
		final Session session = entityManager.unwrap(Session.class);
		entityManager.clear();
		logger.info("EmployeeDetailsDaoImpl(updateEmployeeDetails)  Entry>> Request");
		try {
			EmployeeDetails oldEmpDetails = (EmployeeDetails) session.get(EmployeeDetails.class, details.getId());
			String oldPassword = oldEmpDetails.getPassword();
			if(oldEmpDetails.getPassword()!= null && (EncryptorUtil.decryptPropertyValue(oldEmpDetails.getPassword()).equals(details.getPassword())) || details.getPassword()==null ) {
				details.setPassword(oldPassword);
			}else {
				details.setPassword(EncryptorUtil.encryptPropertyValue(details.getPassword()));
			}
			oldEmpDetails = new EmployeeDetails();
			details.setModified_time(new Date());
			session.clear();
			session.update(details);
			if (details.getId() != null) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("EmployeeDetailsDaoImpl(updateEmployeeDetails)>> Exit" );
		return details;
	}


	@Override
	public EmployeeDetails updateEmployeeDetailsWithZone(EmployeeDetails details,String zone) {
		entityManager.clear();
		Date date = details.getDate_of_joining();
		Date date2 = details.getDate_of_birth();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// Use india time zone to format the date in
		df.setTimeZone(TimeZone.getTimeZone(zone));
		try {
			details.setDate_of_joining(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(df.format(date)));
			if(details.getDate_of_birth() != null) {
				details.setDate_of_birth(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(df.format(date2)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Session session= entityManager.unwrap(Session.class);
		logger.info("EmployeeDetailsDaoImpl(updateEmployeeDetails) Entry>> Request");
		try {
			EmployeeDetails oldEmpDetails = (EmployeeDetails) session.get(EmployeeDetails.class, details.getId());
			String oldPassword = oldEmpDetails.getPassword();
			if(oldEmpDetails.getPassword()!= null && (EncryptorUtil.decryptPropertyValue(oldEmpDetails.getPassword())).equals(details.getPassword())) {
				details.setPassword(oldPassword);
			}else {
				details.setPassword(EncryptorUtil.encryptPropertyValue(details.getPassword()));
			}
			details.setModified_time(new Date());
//			details.setPassword(EncryptorUtil.encryptPropertyValue(details.getPassword()));
			oldEmpDetails = new EmployeeDetails();
			session.clear();
			session.update(details);
//				This section for to update other table's emp details while updating emp detail on manage user 

			String emp_fullname=details.getFirstname()+" "+ details.getLastname();
			String emp_firstname=details.getFirstname();
			String emp_id = details.getId();

			final Query query1 = session.createNativeQuery("update approved_leave_details as A set A.emp_firstname =:i where A.emp_id=:j");
			query1.setParameter("i", emp_firstname);
			query1.setParameter("j", emp_id);
			int approved_Details = query1.executeUpdate();

			final Query query2 = session.createNativeQuery("update day_planner_details as B set B.emp_name = :i where B.emp_id=:j");
			query2.setParameter("i", emp_fullname);
			query2.setParameter("j", emp_id);
			int dayplanner_Details = query2.executeUpdate();

			final Query query3 = session.createNativeQuery("update leave_tracker_details as C set C.emp_name = :i where C.emp_id= :j");
			query3.setParameter("i", emp_firstname);
			query3.setParameter("j", emp_id);
			int leavetracker_Details = query3.executeUpdate();

			final Query query4 = session.createNativeQuery("update timesheet_approval_details as D set D.emp_name = :i where D.emp_id=:j");
			final Query query5 = session.createNativeQuery("update timesheet_approval_details as D set D.reporter_name = :i where D.reporter=:j");
			query4.setParameter("i", emp_fullname);
			query4.setParameter("j", emp_id);
			query5.setParameter("i", emp_fullname);
			query5.setParameter("j", emp_id);
			int timesheet_Details = query4.executeUpdate();
			int approveTimesheet_Details = query5.executeUpdate();

			if (details.getId() != null) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("EmployeeDetailsDaoImpl(updateEmployeeDetails)>> Exit");
		return details;
	}


	@Override
	public EmployeeDetails deleteEmployeeDetails(EmployeeDetails details) {
		logger.info("EmployeeDetailsDaoImpl(deleteEmployeeDetails)>> Entry" );
		final Session session= entityManager.unwrap(Session.class);
		try {

			details.setModified_time(new Date());
			session.update(details);
			if (details.getId() != null) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("EmployeeDetailsDaoImpl(deleteEmployeeDetails)>> Exit" );
		return details;

	}


	@Override
	public int bulkUserdelete(JSONArray ids) {
		logger.info("EmployeeDetailsDaoImpl(bulkUserdelete) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}

		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session.createNativeQuery("update employee_details set is_deleted=:i where id in (" + id_sb + ")");
		query.setParameter("i", true);
//		query.setParameter("j", true)
		int details = query.executeUpdate();
		logger.info("EmployeeDetailsDaoImpl(bulkUserdelete) Exit>>");
		return details;
	}
//
//	@Override
//	public EmployeeDetails authenticateEmployee(EmployeeDetails employeedetails) {
//		logger.info("EmployeeDetailsDaoImpl(authenticateEmployee)>> Entry");
//		final Session session = entityManager.unwrap(Session.class);
//		final Query query = session.createQuery("from EmployeeDetails where email=:e and is_deleted=:i and is_activated=:a");
//        query.setParameter("e", employeedetails.getEmail());
//        query.setParameter("i", false);
//        query.setParameter("a", true);
//        try {
//        	final EmployeeDetails employeedetails1 = (EmployeeDetails) query.getSingleResult();
//    		try {
//    			if (EncryptorUtil.decryptPropertyValue(employeedetails1.getPassword())
//    					.equals(employeedetails.getPassword()) && employeedetails1.getIs_deleted().equals(false) && employeedetails1.getIs_activated().equals(true)) {
//    					return employeedetails1;
//    			}
//                else if(!EncryptorUtil.decryptPropertyValue(employeedetails1.getPassword())
//                        .equals(employeedetails.getPassword())) {
//                    return null;
//                }
//    		} catch (Exception e) {
//    		}
//        }
//        catch (NoResultException nre){
//			return null;
//		}
//        
//		logger.info("EmployeeDetailsDaoImpl(authenticateEmployee)>> Exit");
//		return null;
//	}
@Override
public EmployeeDetails authenticateEmployee(EmployeeDetails employeedetails) {
	logger.info("EmployeeDetailsDaoImpl(authenticateEmployee)>> Entry");
	final Session session = entityManager.unwrap(Session.class);
	final Query query = session.createQuery("from EmployeeDetails where email=:e and is_deleted=:i");
        query.setParameter("e", employeedetails.getEmail());
        query.setParameter("i", false);
	try {
		final EmployeeDetails employeedetails1 = (EmployeeDetails) query.getSingleResult();
		logger.info("EmployeeDetailsDaoImpl(authenticateEmployee)>> Exit");
		return employeedetails1;
	} catch (Exception e) {
		logger.info("EmployeeDetailsDaoImpl(authenticateEmployee)>> Exit");
		return null;
	}

}


	@Override
	public long getMaxSequenceId() {
		logger.info("EmployeeDetailsDaoImpl(getMaxSequenceId)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		Criteria c = session.createCriteria(EmployeeDetails.class);
		c.addOrder(Order.desc("seq"));
		c.setMaxResults(1);
		EmployeeDetails details = (EmployeeDetails) c.uniqueResult();
		if (details != null) {
			logger.info("EmployeeDetailsDaoImpl(getMaxSequenceId) >> Exit");
			return details.getSeq();
		} else {
			logger.info("EmployeeDetailsDaoImpl(getMaxSequenceId) >> Exit");
			return 0;
		}
	}

	@Override
	public List<EmployeeDetails> getAllEmployeeDetailsByOrgID(Long org_id) {
		logger.info("EmployeeDetailsDaoImpl(getAllEmployeeDetailsByOrgID)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id");
		query.setParameter("id", org_id);
		@SuppressWarnings("unchecked") final List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getAllEmployeeDetailsByOrgID)>> Exit");
		return details;
	}


	@Override
	public List<EmployeeDetails> getEmployeeDetailsByOrgID(Long id) {
		logger.info("EmployeeDetailsDaoImpl(getEmployeeDetailsByOrgID)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id and is_activated =:j and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("j", true);
		query.setParameter("k",false);
		@SuppressWarnings("unchecked") final List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getEmployeeDetailsByOrgID) >> Exit");
		return details;
	}


	@Override
	public EmployeeDetails getEmployeeDetailsByEmail(String email) {
//		logger.info("EmployeeDetailsDaoImpl(getEmployeeDetailsByEmail) Entry>> ");
//		final Session session = entityManager.unwrap(Session.class);
//		final Query query = session
//				.createQuery("from EmployeeDetails where email=:i and is_deleted=:j");
//		query.setParameter("i", email);
//		query.setParameter("j", false);
//		@SuppressWarnings("unchecked")
//		EmployeeDetails details = (EmployeeDetails) query.getSingleResult();
//		return details;

		logger.info("EmployeeDetailsDaoImpl(getEmployeeDetailsByEmail)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from EmployeeDetails where email=:i and is_deleted=:j");
		query.setParameter("i", email);
		query.setParameter("j", false);
		query.setMaxResults(1);
		@SuppressWarnings("unchecked")
		EmployeeDetails details = new EmployeeDetails();
		try {
			details = (EmployeeDetails) query.getSingleResult();
			return details;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		logger.info("EmployeeDetailsDaoImpl(getEmployeeDetailsByEmail)>> Exit");
		return null;
	}

	@Override
	public EmployeeDetails updateEmployeePassword(EmployeeDetails details) {
		final Session session= entityManager.unwrap(Session.class);
		logger.info("EmployeeDetailsDaoImpl(updateEmployeePassword) Entry>> Request");
		try {
			details.setModified_time(new Date());
			details.setPassword(EncryptorUtil.encryptPropertyValue(details.getPassword()));
			if(details.getIsForgotPwdEnabled()) {
				details.setIsForgotPwdEnabled(false);
			}
			session.update(details);
			if (details.getId() != null) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("EmployeeDetailsDaoImpl(updateEmployeePassword)>> Exit" );
		return details;
	}


	@Override
	public List<EmployeeDetails> getAllActiveEmployeeDetails() {
		logger.info("EmployeeDetailsDaoImpl (getAllActiveEmployeeDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from EmployeeDetails where is_deleted=:j and is_activated =:k");
		query.setParameter("j", false);
		query.setParameter("k", true);
		@SuppressWarnings("unchecked")
		List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl (getAllActiveEmployeeDetails) >> Exit ");
		return details;
	}


	@Override
	public List<EmployeeDetails> getInactiveEmployeeDetailsByOrgID(Long id) {
		logger.info("EmployeeDetailsDaoImpl(getEmployeeDetailsByOrgID)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("j", false);
		@SuppressWarnings("unchecked") final List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getEmployeeDetailsByOrgID)>> Exit");
		return details;
	}


	@Override
	public List<EmployeeDetails> getAllEmployeeReportsByOrgId(Long id) {
		logger.info("EmployeeDetailsDaoImpl(getAllEmployeeReportsByOrgId)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked") final List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getAllEmployeeReportsByOrgId)>> Exit");
		return details;
	}

	@Override
	public List<EmployeeDetails> getAllActiveEmployeeReportsByOrgId(Long id) {
		logger.info("EmployeeDetailsDaoImpl(getAllActiveEmployeeReportsByOrgId) >>Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("j", true);
		@SuppressWarnings("unchecked") final List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getAllActiveEmployeeReportsByOrgId)>> Exit");
		return details;
	}

	@SuppressWarnings("unused")
	@Override
	public int bulkDeactiveEmp(JSONArray id,String action, Long orgId) {
		int userDetails = 0;
		int reporterDetails = 0;
		logger.info("EmployeeDetailsDaoImpl(bulkDeactiveEmp) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < id.length(); i++) {
			id_list += "'" + id.get(i) + "'" + ",";
		}
		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		if(action.equals("activated")) {
			final Query query = session.createNativeQuery("update employee_details set is_activated=:i and is_deleted =:k where id in (" + id_sb + ")");
			query.setParameter("i", false);
			query.setParameter("k",false);
			userDetails = query.executeUpdate();
			// To set org admin as reporting manager if these deactivated users were reporting manager for any users
			final Query query1 = session.createQuery("from EmployeeDetails where reporting_manager in (" + id_sb + ") and is_deleted=:i");
			query1.setParameter("i",false);
			@SuppressWarnings("unchecked")
			List<EmployeeDetails> details =  query1.getResultList();
			if(details.size() !=0){
				final Query query2 = session.createQuery("from EmployeeDetails where orgDetails.id=:orgId and user_login_type = 'OrgAdmin' and is_deleted=:i and is_activated=:j");
				query2.setParameter("orgId", orgId);
				query2.setParameter("i", false);
				query2.setParameter("j", true);
				EmployeeDetails details1 =  (EmployeeDetails) query2.getSingleResult();
				for(int i=0;i<details.size();i++) {
					final Query query3 = session.createNativeQuery("update employee_details set reporting_manager=:i where reporting_manager=:j ");
					query3.setParameter("i",details1.getId());
					query3.setParameter("j",details.get(i).getReporting_manager());
//		query3.setParameter("j", false);
					reporterDetails = query3.executeUpdate();
					return reporterDetails;
				}
			}
			//To set status inactive for users in project resource details table to reduce bulk deactivate loading time
			final Query query2 = session.createQuery("from ProjectResourceDetails where emp_id in (" + id_sb + ") and is_deleted=:j and status=:k");
//		query2.setParameter("i",id);
			query2.setParameter("j",false);
			query2.setParameter("k","Active");
			@SuppressWarnings("unchecked")
			List<ProjectResourceDetails> details2 =  query2.getResultList();
			if(details2.size() != 0) {
				for(int k=0;k<details2.size();k++) {
					final Query query3 = session.createNativeQuery("update project_resource_details set status=:i where emp_id=:j and is_deleted=:k");
					query3.setParameter("i","Inactive");
					query3.setParameter("j",details2.get(k).getEmployeeDetails().getId());
					query3.setParameter("k",false);
					@SuppressWarnings("unchecked")
					int details3 = query3.executeUpdate();
				}
			}

			//To set status inactive for user in job assignee details table to reduce bulk deactivate loading time
			final Query query4 = session.createQuery("from JobAssigneeDetails where emp_id in (" + id_sb + ") and is_deleted=:j and status=:k");
//			query4.setParameter("i",id);
			query4.setParameter("j",false);
			query4.setParameter("k","Active");
			@SuppressWarnings("unchecked")
			List<JobAssigneeDetails> details4 =  query4.getResultList();
			if(details4.size() != 0) {
				for(int j=0;j<details4.size();j++) {
					final Query query5 = session.createNativeQuery("update job_assignee_details set status=:i where emp_id=:j and is_deleted=:k");
					query5.setParameter("i","Inactive");
					query5.setParameter("j",details4.get(j).getEmployeeDetails().getId());
					query5.setParameter("k",false);
					int details5 = query5.executeUpdate();
				}
			}
			logger.info("EmployeeDetailsDaoImpl(bulkDeactiveEmp) >> Exit");
		}
		return userDetails;
	}

	@Override
	public int bulkActivate(JSONArray id,String action) {
		int userDetails = 0;
		int notificationDetails = 0;
		logger.info("EmployeeDetailsDaoImpl(bulkActivate) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < id.length(); i++) {
			id_list += "'" + id.get(i) + "'" + ",";
		}
		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		if(action.equals("deactivated")) {
			final Query query = session.createNativeQuery("update employee_details set is_activated=:i and is_deleted =:k where id in (" + id_sb + ")");
			query.setParameter("i", true);
			query.setParameter("k",false);
			userDetails = query.executeUpdate();
			final Query query1 = session.createNativeQuery("update notification_details set is_deleted = true where to_notify_id in (" + id_sb +") and sub_module_name =:j");
			query1.setParameter("j", "Deactivated-User");
			notificationDetails = query1.executeUpdate();
			//To set status active for users in project resource details table to reduce activate loading time
			final Query query2 = session.createQuery("from ProjectResourceDetails where emp_id in (" + id_sb + ") and is_deleted=:j and status=:k");
//				query2.setParameter("i",id);
			query2.setParameter("j",false);
			query2.setParameter("k","Inactive");
			@SuppressWarnings("unchecked")
			List<ProjectResourceDetails> details2 =  query2.getResultList();
			if(details2.size() != 0) {
				for(int k=0;k<details2.size();k++) {
					final Query query3 = session.createNativeQuery("update project_resource_details set status=:i where emp_id=:j and is_deleted=:k");
					query3.setParameter("i","Active");
					query3.setParameter("j",details2.get(k).getEmployeeDetails().getId());
					query3.setParameter("k",false);
					@SuppressWarnings("unchecked")
					int details3 = query3.executeUpdate();
				}
			}
			//To set status active for user in job assignee details table to reduce activate loading time
			final Query query4 = session.createQuery("from JobAssigneeDetails where emp_id in (" + id_sb + ") and is_deleted=:j and status=:k");
//					query4.setParameter("i",id);
			query4.setParameter("j",false);
			query4.setParameter("k","Inactive");
			@SuppressWarnings("unchecked")
			List<JobAssigneeDetails> details4 =  query4.getResultList();
			if(details4.size() != 0) {
				for(int j=0;j<details4.size();j++) {
					final Query query5 = session.createNativeQuery("update job_assignee_details set status=:i where emp_id=:j and is_deleted=:k");
					query5.setParameter("i","Active");
					query5.setParameter("j",details4.get(j).getEmployeeDetails().getId());
					query5.setParameter("k",false);
					int details5 = query5.executeUpdate();
				}
			}
			logger.info("EmployeeDetailsDaoImpl(bulkActivate) >> Exit");
		}
		return userDetails;
	}


	@Override
	public List<EmployeeDetails> getOrgUsers(Long orgId) {
		// TODO Auto-generated method stub
		logger.info("EmployeeDetailsDaoImpl(getOrgUsers)>> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:orgId and is_activated=:i and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("orgId", orgId);
		query.setParameter("i", true);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked") final List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getOrgUsers)>> Exit");
		return details;
	}

	@Override
	public List<EmployeeDetails> getinactiveOrgUsers(Long orgId) {
		// TODO Auto-generated method stub
		logger.info("EmployeeDetailsDaoImpl(getOrgUsers)>> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:orgId and is_activated=:i and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("orgId", orgId);
		query.setParameter("i", false);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked") final List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getOrgUsers)>> Exit");
		return details;
	}

	@Override
	public EmployeeDetails createUserFromExcelFile(EmployeeDetails employeedetails,String zone) {
		logger.info("EmployeeDetailsDaoImpl(createUserFromExcelFile) >> Entry ");
		Date date = employeedetails.getDate_of_joining();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

		// Use india time zone to format the date in
		df.setTimeZone(TimeZone.getTimeZone(zone));

		try {
			employeedetails.setDate_of_joining(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(df.format(date)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final Session session = entityManager.unwrap(Session.class);
		try {
			employeedetails.setCreated_time(new Date());
			employeedetails.setModified_time(new Date());
//			employeedetails.setId(null);
			employeedetails.setPassword(EncryptorUtil.encryptPropertyValue(employeedetails.getPassword()));
			logger.debug("EmployeeDetailsDaoImpl obj:" + new Gson().toJson(employeedetails));
			session.save(employeedetails);
			if (employeedetails.getId() != null) {
				entityManager.persist(employeedetails);
				return employeedetails;
			} else {
				entityManager.merge(employeedetails);
				return employeedetails;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		logger.info("EmployeeDetailsDaoImpl(createUserFromExcelFile) >> Exit ");

		return employeedetails;
	}


	@Override
	public EmployeeDetails updateSkippedTimeEmployeeDetails(EmployeeDetails details) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public EmployeeDetails updateSkippedTimeEmployeeDetails(EmployeeDetails details,String zone,String skippedTime,Boolean isSkipped) {
		final Session session= entityManager.unwrap(Session.class);
		logger.info("EmployeeDetailsDaoImpl(updateSkippedTimeEmployeeDetails)>> Entry" );
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone(zone));
			Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(skippedTime);
			details.setModified_time(new Date());
			details.setIs_skipped(isSkipped);
			details.setSkipped_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			session.update(details);
			if (details.getId() != null) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("EmployeeDetailsDaoImpl(updateSkippedTimeEmployeeDetails)>> Exit" );
		return details;
		// TODO Auto-generated method stub
	}


	@Override
	public List<EmployeeDetails> getActiveEmpDetailsByOrgId(Long id) {
		logger.info("EmployeeDetailsDaoImpl(getActiveEmpDetailsByOrgId)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("j", true);
		@SuppressWarnings("unchecked")
		List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getActiveEmpDetailsByOrgId)>> Exit");
		return details;
	}

	@Override
	public List<EmployeeDetails> getActiveEmpByRoleAndOrgId(Long orgId, Long roleId) {
		logger.info("EmployeeDetailsDaoImpl(getActiveEmpByRoleAndOrgId)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id and role_id=:i and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", orgId);
		query.setParameter("i", roleId);
		query.setParameter("k", false);
		query.setParameter("j", true);
		@SuppressWarnings("unchecked")
		List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getActiveEmpByRoleAndOrgId)>> Exit");
		return details;
	}

	@Override
	public List<EmployeeDetails> getInactiveEmpByRoleAndOrgId(Long orgId, Long roleId) {
		logger.info("EmployeeDetailsDaoImpl(getInactiveEmpByRoleAndOrgId)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id and role_id=:i and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", orgId);
		query.setParameter("i", roleId);
		query.setParameter("k", false);
		query.setParameter("j", false);
		@SuppressWarnings("unchecked")
		List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getInactiveEmpByRoleAndOrgId)>> Exit");
		return details;
	}

	@Override
	public EmployeeDetails updateSkippedLeaveById(EmployeeDetails employeedetails) {
		// TODO Auto-generated method stub
		logger.info("EmployeeDetailsDaoImpl(updateSkippedLeaveById)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			employeedetails.setSkipped_leave(true);
			session.update(employeedetails);
			if (employeedetails.getId() != null) {
				entityManager.persist(employeedetails);
				return employeedetails;
			} else {
				entityManager.merge(employeedetails);
				return employeedetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("EmployeeDetailsDaoImpl(updateSkippedLeaveById) >> exit ");
		return employeedetails;
	}


	@Override
	public List<EmployeeDetails> getAllSkippedLeaveEmployeeDetails() {
		// TODO Auto-generated method stub
		logger.info("EmployeeDetailsDaoImpl(getAllSkippedLeaveEmployeeDetails)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from EmployeeDetails where skipped_leave =:i");
		query.setParameter("i",true);
		@SuppressWarnings("unchecked")
		List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getAllSkippedLeaveEmployeeDetails)>> Exit");
		return details;
	}


	@Override
	public int updateDailyOnceDisplayTodayLeave(StringBuffer id_sb) {
		// TODO Auto-generated method stub
		logger.info("EmployeeDetailsDaoImpl(updateDailyOnceDisplayTodayLeave)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery("update employee_details set skipped_leave=:i where id in (" + id_sb + ")");
		query.setParameter("i", false);
		int details = query.executeUpdate();
		logger.info("updateDailyOnceDisplayTodayLeave(updateDailyOnceDisplayTodayLeave)>> Exit");
		return details;
	}


	@Override
	public List<CustomEmployeeDetails2> getCustomActiveEmpDetailsByOrgID(Long id) {
		logger.info("EmployeeDetailsDaoImpl(getCustomAllEmpDetailsByOrgID)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("j", true);
		@SuppressWarnings("unchecked") final List<EmployeeDetails> details = query.getResultList();
		List<CustomEmployeeDetails2> customDetails = new ArrayList<CustomEmployeeDetails2>();
		for(EmployeeDetails i : details) {
			CustomEmployeeDetails2 tempCustom = new CustomEmployeeDetails2();
			tempCustom.setId(i.getId());
			tempCustom.setFirstname(i.getFirstname());
			tempCustom.setLastname(i.getLastname());
			tempCustom.setOrg_id(i.getOrgDetails().getOrg_id());
			tempCustom.setEmail(i.getEmail());
			tempCustom.setIs_activated(i.getIs_activated());
//			tempCustom.setIsReportingManagerAvail(i.getIsReportingManagerAvail()); // IsReportingManagerAvail is used for displaying the update button if there is no reporting manager

			if(i.getDesignationDetails() != null) {
				tempCustom.setDesignation(i.getDesignationDetails().getDesignation());
				tempCustom.setIs_designation_deleted(i.getDesignationDetails().getIs_deleted());
				tempCustom.setDesignation_id(i.getDesignationDetails().getId());
			}

			if(i.getRoleDetails() != null) {
				tempCustom.setRole(i.getRoleDetails().getRole());
				tempCustom.setIs_role_deleted(i.getRoleDetails().getIs_deleted());
				tempCustom.setRole_id(i.getRoleDetails().getId());
				tempCustom.setAccess_to(i.getRoleDetails().getAccess_to());
			}

			if(i.getReporting_manager() != null ) {
				EmployeeDetails reporterDetails = getAllEmployeeDetailsByID(i.getReporting_manager());
				tempCustom.setReporting_manager(reporterDetails.getFirstname()+ " " + reporterDetails.getLastname());
				tempCustom.setReporting_manager_id(i.getReporting_manager());
			}
			if(i.getBranchDetails() != null ) {
				tempCustom.setBranch(i.getBranchDetails().getBranch());
				tempCustom.setIs_branch_deleted(i.getBranchDetails().getIs_deleted());
				tempCustom.setBranch_id(i.getBranchDetails().getId());
			}
			tempCustom.setIs_deleted(i.getIs_deleted());
			customDetails.add(tempCustom);
		}
		logger.info("EmployeeDetailsDaoImpl(getCustomAllEmpDetailsByOrgID) >> Exit");
		return customDetails;
	}


	@Override
	public List<CustomEmployeeDetails2> getCustomInactiveEmpDetailsByOrgID(Long id) {
		logger.info("EmployeeDetailsDaoImpl(getCustomInactiveEmpDetailsByOrgID)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("j", false);
		@SuppressWarnings("unchecked") final List<EmployeeDetails> details = query.getResultList();
		List<CustomEmployeeDetails2> customDetails = new ArrayList<CustomEmployeeDetails2>();
		for(EmployeeDetails i : details) {
			CustomEmployeeDetails2 tempCustom = new CustomEmployeeDetails2();
			tempCustom.setId(i.getId());
			tempCustom.setFirstname(i.getFirstname());
			tempCustom.setLastname(i.getLastname());
			tempCustom.setOrg_id(i.getOrgDetails().getOrg_id());
			tempCustom.setEmail(i.getEmail());
			tempCustom.setIs_activated(i.getIs_activated());

			if(i.getDesignationDetails() != null) {
				tempCustom.setDesignation(i.getDesignationDetails().getDesignation());
				tempCustom.setIs_designation_deleted(i.getDesignationDetails().getIs_deleted());
				tempCustom.setDesignation_id(i.getDesignationDetails().getId());
			}

			if(i.getRoleDetails() != null) {
				tempCustom.setRole(i.getRoleDetails().getRole());
				tempCustom.setIs_role_deleted(i.getRoleDetails().getIs_deleted());
				tempCustom.setRole_id(i.getRoleDetails().getId());
				tempCustom.setAccess_to(i.getRoleDetails().getAccess_to());
			}

			if(i.getReporting_manager() != null) {
				EmployeeDetails reporterDetails = getAllEmployeeDetailsByID(i.getReporting_manager());
				tempCustom.setReporting_manager(reporterDetails.getFirstname()+ " " + reporterDetails.getLastname());
				tempCustom.setReporting_manager_id(i.getReporting_manager());
			}
			if(i.getBranchDetails() != null ) {
				tempCustom.setBranch(i.getBranchDetails().getBranch());
				tempCustom.setIs_branch_deleted(i.getBranchDetails().getIs_deleted());
				tempCustom.setBranch_id(i.getBranchDetails().getId());
			}
			tempCustom.setIs_deleted(i.getIs_deleted());
			customDetails.add(tempCustom);
		}
		logger.info("EmployeeDetailsDaoImpl(getCustomInactiveEmpDetailsByOrgID) >> Exit");
		return customDetails;
	}


//	@Override
//	public List<EmployeeDetails> updateOnceADayDisplayLeaveDeatils() {
//		
//		// TODO Auto-generated method stub
//		logger.info("EmployeeDetailsDaoImpl(updateOnceADayDisplayLeaveDeatils) Entry>>");
//		final Session session = entityManager.unwrap(Session.class);
//		final Query query = session.createNativeQuery("update employee_details set skipped_leave=:i ");
//		query.setParameter("i", true);
//		userDetails = query.executeUpdate();
//		@SuppressWarnings("unchecked")
//		EmployeeDetails details = query.getResultList();
//		logger.info("EmployeeDetailsDaoImpl(getActiveEmpDetailsByOrgId) Exit>>");
//		return details;
//	}

	// To authenticate the user after activate
	@Override
	public int activate(String id) {
		logger.info("EmployeeDetailsDaoImpl(activate) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery("update notification_details set is_deleted = true where to_notify_id =:i and sub_module_name =:j");
		query.setParameter("i", id);
		query.setParameter("j","Deactivated-User");
		@SuppressWarnings("unchecked")
		int details = query.executeUpdate();
		logger.info("EmployeeDetailsDaoImpl(activate) Exit>>");
		return details;
	}

	// To set org admin as reporting manager if deactivated user were reporting manager for any users
	@Override
	public int deactivateEmp(String id, Long orgId) {
		logger.info("EmployeeDetailsDaoImpl(deactivateEmp) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		int updateReportingManager = 0;
		int details5 = 0;
		String id_list = new String();
		for (int k = 0; k < id.length(); k++) {
//				id_list += "'" + id.get(i) + "'" + ",";
			id_list += "'" + id + "'" + ",";
		}
		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		//To update reporting manager as org admin if this id is assigned as reporting manager for any users
		final Query query = session.createQuery("from EmployeeDetails where orgDetails.id=:orgId and user_login_type = 'OrgAdmin'  and is_deleted=:i and is_activated=:j");
		query.setParameter("orgId", orgId);
		query.setParameter("i", false);
		query.setParameter("j", true);
		EmployeeDetails details =  (EmployeeDetails) query.getSingleResult();
		final Query query_manager = session.createQuery("from EmployeeDetails where reporting_manager in  (" + id_sb + ") and is_deleted=:j");
		query_manager.setParameter("j",false);
		@SuppressWarnings("unchecked") final List<EmployeeDetails> reportingmanagerList =  query_manager.getResultList();
		if(reportingmanagerList.size() != 0 ) {
			for (int i=0;i< reportingmanagerList.size();i++) {
				final Query query1 = session.createNativeQuery(
						"update employee_details set reporting_manager=:i where reporting_manager=:k");
				query1.setParameter("i",details.getId());
//			query1.setParameter("j", false);
				query1.setParameter("k",reportingmanagerList.get(i).getReporting_manager());
				updateReportingManager = query1.executeUpdate();
			}
		}
		//To set status inactive for user in project resource details table to reduce deactivate loading time
		final Query query2 = session.createQuery("from ProjectResourceDetails where emp_id=:i and is_deleted=:j and status=:k");
		query2.setParameter("i",id);

		query2.setParameter("j",false);
		query2.setParameter("k","Active");
		@SuppressWarnings("unchecked")
		List<ProjectResourceDetails> details2 =  query2.getResultList();
		if(details2.size() != 0) {
			for(int k=0;k<details2.size();k++) {
				final Query query3 = session.createNativeQuery("update project_resource_details set status=:i where emp_id=:j and is_deleted=:k");
				query3.setParameter("i","Inactive");
				query3.setParameter("j",details2.get(k).getEmployeeDetails().getId());
				query3.setParameter("k",false);
				@SuppressWarnings("unchecked")
				int details3 = query3.executeUpdate();
			}
		}
		//To set status inactive for user in job assignee details table to reduce deactivate loading time
		final Query query4 = session.createQuery("from JobAssigneeDetails where emp_id=:i and is_deleted=:j and status=:k");
		query4.setParameter("i",id);
		query4.setParameter("j",false);
		query4.setParameter("k","Active");
		@SuppressWarnings("unchecked")
		List<JobAssigneeDetails> details4 =  query4.getResultList();
		if(details4.size() != 0) {
			for(int j=0;j<details4.size();j++) {
				final Query query5 = session.createNativeQuery("update job_assignee_details set status=:i where emp_id=:j and is_deleted=:k");
				query5.setParameter("i","Inactive");
				query5.setParameter("j",details4.get(j).getEmployeeDetails().getId());
				query5.setParameter("k",false);
				details5 = query5.executeUpdate();
			}
		}
		logger.info("EmployeeDetailsDaoImpl(deactivateEmp) Exit>>");
		return details5;
	}


	@Override
	public Boolean getNewReleaseByEmpId(String empid) {
		logger.info("EmployeeDetailsDaoImpl(getNewReleaseByEmpId) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("select new_release from EmployeeDetails where id=:id");
		query.setParameter("id", empid);
		@SuppressWarnings("unchecked")
		Boolean newRelease = (Boolean) query.getSingleResult();
		logger.info("EmployeeDetailsDaoImpl(getNewReleaseByEmpId) Exit>>");
		return newRelease;
	}

	@Override
	public EmployeeDetails getReportingManagerByName(String details,Long id) {
		logger.info("EmployeeDetailsDaoImpl(getReportingManagerByName) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from  EmployeeDetails where orgDetails.id=:id and id=:j and  is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("j", details);
		query.setParameter("k", false);
		EmployeeDetails details1 = new EmployeeDetails();
		try {
			details1 = (EmployeeDetails) query.getSingleResult();
			return  details1;

		} catch (Exception e) {
		}
		logger.info("EmployeeDetailsDaoImpl(getReportingManagerByName) >> Exit");
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EmployeeDetails> getEmployeeDetailsByName(Long orgId) {
		logger.info("EmployeeDetailsDaoImpl(getEmployeeDetailsByName) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from  EmployeeDetails where orgDetails.id=:id and  is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", orgId);
		query.setParameter("k", false);
			List<EmployeeDetails> details1 = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getEmployeeDetailsByName) >> Exit");
		return details1;
	}
	
	@Override
	public List<EmployeeDetails> getActiveEmpByBranchAndOrgId(Long orgId, Long branchId) {
		logger.info("EmployeeDetailsDaoImpl(getActiveEmpByBranchAndOrgId)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id and branch_id=:i and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", orgId);
		query.setParameter("i", branchId);
		query.setParameter("k", false);
		query.setParameter("j", true);
		@SuppressWarnings("unchecked")
		List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getActiveEmpByBranchAndOrgId)>> Exit");
		return details;
	}
	
	@Override
	public List<EmployeeDetails> getInactiveEmpByBranchAndOrgId(Long orgId, Long branchId) {
		logger.info("EmployeeDetailsDaoImpl(getInactiveEmpByBranchAndOrgId)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from EmployeeDetails where orgDetails.id=:id and branch_id=:i and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", orgId);
		query.setParameter("i", branchId);
		query.setParameter("k", false);
		query.setParameter("j", false);
		@SuppressWarnings("unchecked")
		List<EmployeeDetails> details = query.getResultList();
		logger.info("EmployeeDetailsDaoImpl(getInactiveEmpByBranchAndOrgId)>> Exit");
		return details;
	}

	@Override
	public JSONObject getEmpImagesByIds(JSONArray empIds) {
		logger.info("EmployeeDetailsDaoImpl(getEmpImagesByIds) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		JSONObject result = new JSONObject();
		for(int i=0 ; i < empIds.length() ; i++) {
			try {
				final Query query = session.createQuery("select profile_image from EmployeeDetails where id=:id");
				query.setParameter("id", empIds.get(i));
				byte[] img = (byte[]) query.getSingleResult();
				if(img != null) {
					img = ImageProcessor.decompressBytes(img);
					result.put(empIds.getString(i), img);
				}else {
					result.put(empIds.getString(i), "");
				}
			} catch (Exception e) {
				result.put(empIds.getString(i), "");
			}
		}
		logger.info("EmployeeDetailsDaoImpl(getEmpImagesByIds) >> Exit");
		return result;
	}

	@Override
	public Boolean checkIsDetailsUpdatedColumnStatus(String empid) {
		logger.info("EmployeeDetailsDaoImpl(checkIsDetailsUpdatedColumnStatus)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("select is_details_updated from EmployeeDetails where id=:id");
		query.setParameter("id", empid);
		@SuppressWarnings("unchecked")
		Boolean data = (Boolean) query.getSingleResult();
		logger.info("EmployeeDetailsDaoImpl(checkIsDetailsUpdatedColumnStatus)>> Exit");
		return data;
	}


	@Override
	public int updateFalseInEmpDetailsUpdated(String empid) {
		logger.info("EmployeeDetailsDaoImpl(updateFalseInEmpDetailsUpdated)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery("update employee_details set is_details_updated=:i where id=:id");
		query.setParameter("id", empid);
		query.setParameter("i", false);
		int details = query.executeUpdate();
		logger.info("updateDailyOnceDisplayTodayLeave(updateFalseInEmpDetailsUpdated)>> Exit");
		return details;
	}


}
