package com.tcube.api.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.TimeTrackerDetails;
import com.tcube.api.model.TimesheetApprovalDetails;

@Component
public class TimesheetApprovalDetailsDaoImpl implements TimesheetApprovalDetailsDao{

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(TimesheetApprovalDetailsDaoImpl.class);

	@Override
	public TimesheetApprovalDetails createTimesheet(TimesheetApprovalDetails timesheetDetails) {
		logger.info("TimesheetApprovalDetailsDaoImpl(createTimesheet) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			timesheetDetails.setCreated_time(new Date());
			timesheetDetails.setModified_time(new Date());
			session.save(timesheetDetails);
			if (timesheetDetails.getId() == 0) {
				entityManager.persist(timesheetDetails);
				return timesheetDetails;
			} else {
				entityManager.merge(timesheetDetails);
				return timesheetDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("TimesheetApprovalDetailsDaoImpl(createTimesheet) Entry>> ");
		return timesheetDetails;
	}

	@Override
	public TimesheetApprovalDetails getById(Long id) {
		logger.info("TimesheetApprovalDetailsDaoImpl(getById) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final TimesheetApprovalDetails details = (TimesheetApprovalDetails) session.get(TimesheetApprovalDetails.class, id);
		logger.info("TimesheetApprovalDetailsDaoImpl(getById) Exit>>-> ");
		return details;
	}

	@Override
	public TimesheetApprovalDetails updateTimesheetStatus(TimesheetApprovalDetails newDetails) {
		logger.info("TimesheetApprovalDetailsDaoImpl(updateTimesheetStatus) Entry>>-> ");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	newDetails.setModified_time(new Date());
	    	logger.debug("appInfo obj:" + new Gson().toJson(newDetails));
			session.update(newDetails);
			if (newDetails.getId() == 0) {
				entityManager.persist(newDetails);
				return newDetails;
			} else {
				entityManager.merge(newDetails);
				return newDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    logger.info("TimesheetApprovalDetailsDaoImpl(updateTimesheetStatus) Exit>>-> ");
		return newDetails;
	}

	@Override
	public List<TimesheetApprovalDetails> getActiveTimesheetByEmpId(String id) {
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByEmpId) Entry>>" + id);
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from TimesheetApprovalDetails where emp_id=:id and is_deleted=:d order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("d", false);
		@SuppressWarnings("unchecked")
		final List<TimesheetApprovalDetails> details = query.getResultList();
		
//		BigInteger approvedCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where emp_id="+"'"+id+"'"+" and is_deleted =false and approval_status='Approved' ")).getSingleResult());
//		System.out.println(approvedCounts);
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByEmpId) Exit");
		return details;
	}

	@Override
	public JSONObject getActiveTimesheetByApproverId(String id) {
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByApproverId) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from TimesheetApprovalDetails where reporter=:id and is_deleted=:d and approval_status!=:a order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("d", false);
		query.setParameter("a", "Updated");
		@SuppressWarnings("unchecked")
		final List<TimesheetApprovalDetails> details = query.getResultList();
		
		BigInteger totalCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status!='Updated'")).getSingleResult());
		
		BigInteger approvedCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Approved' ")).getSingleResult());
		
		BigInteger rejectedCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Rejected' ")).getSingleResult());
		
		BigInteger pendingCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Submitted' ")).getSingleResult());
		
		JSONObject allDetails = new JSONObject();
		allDetails.put("details", new Gson().toJson(details));
		allDetails.put("total_counts", totalCounts);
		allDetails.put("approved_counts", approvedCounts);
		allDetails.put("rejected_counts", rejectedCounts);
		allDetails.put("pending_counts", pendingCounts);
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByApproverId) Exit>>");
		return allDetails;
	}

	@Override
	public TimesheetApprovalDetails getTimesheetByEmpidAndDate(TimesheetApprovalDetails newDetails) {
		logger.info("TimeTrackerDetailsDaoImpl (getTaskByEmpidAndDate) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session
				.createQuery("from TimesheetApprovalDetails where is_deleted=:i and date_of_request=:j and emp_id=:k");
		query.setParameter("i", false);
		query.setParameter("j", newDetails.getDate_of_request());
		query.setParameter("k", newDetails.getEmp_id());
		final List<TimesheetApprovalDetails> details = query.getResultList();
		TimesheetApprovalDetails data = new TimesheetApprovalDetails();
		if(details.size()!=0) {
			data = details.get(0);			
		}
		else {
			data= null;
		}
		return data;
	}

	@Override
	public List<TimesheetApprovalDetails> getTimesheetsByEmpidAndDate(TimesheetApprovalDetails toChecktDetails) {
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByEmpId) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from TimesheetApprovalDetails where emp_id=:id and is_deleted=:d and date_of_request=:date order by timestamp(modified_time) desc");
		query.setParameter("id", toChecktDetails.getEmp_id());
		query.setParameter("date", toChecktDetails.getDate_of_request());
		query.setParameter("d", false);
		@SuppressWarnings("unchecked")
		final List<TimesheetApprovalDetails> details = query.getResultList();
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByEmpId) Exit");
		return details;
	}

	@Override
	public TimesheetApprovalDetails getTimesheetById(Long id) {
		logger.info("TimesheetApprovalDetailsDaoImpl(getTimesheetById) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final TimesheetApprovalDetails details = (TimesheetApprovalDetails) session.get(TimesheetApprovalDetails.class, id);
		logger.info("TimesheetApprovalDetailsDaoImpl(getTimesheetById) Exit>>-> ");
		return details;
	}

	@Override
	public JSONObject getActivePendingTimesheetByApproverId(String id) {
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByApproverId) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from TimesheetApprovalDetails where reporter=:id and is_deleted=:d and approval_status='Submitted' order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("d", false);
		@SuppressWarnings("unchecked")
		final List<TimesheetApprovalDetails> details = query.getResultList();
		
		BigInteger approvedCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Approved' ")).getSingleResult());
		
		BigInteger rejectedCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Rejected' ")).getSingleResult());
		
		BigInteger pendingCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Submitted' ")).getSingleResult());
		
		JSONObject allDetails = new JSONObject();
		allDetails.put("details", new Gson().toJson(details));
		allDetails.put("approved_counts", approvedCounts);
		allDetails.put("rejected_counts", rejectedCounts);
		allDetails.put("pending_counts", pendingCounts);
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByApproverId) Exit>>");
		return allDetails;
	}

	@Override
	public JSONObject getActiveApprovedTimesheetByApproverId(String id) {
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByApproverId) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from TimesheetApprovalDetails where reporter=:id and is_deleted=:d and approval_status='Approved' order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("d", false);
		@SuppressWarnings("unchecked")
		final List<TimesheetApprovalDetails> details = query.getResultList();
		
		BigInteger approvedCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Approved' ")).getSingleResult());
		
		BigInteger rejectedCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Rejected' ")).getSingleResult());
		
		BigInteger pendingCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Submitted' ")).getSingleResult());
		
		JSONObject allDetails = new JSONObject();
		allDetails.put("details", new Gson().toJson(details));
		allDetails.put("approved_counts", approvedCounts);
		allDetails.put("rejected_counts", rejectedCounts);
		allDetails.put("pending_counts", pendingCounts);
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByApproverId) Exit>>");
		return allDetails;
	}

	@Override
	public JSONObject getActiveRejectedTimesheetByApproverId(String id) {
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByApproverId) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from TimesheetApprovalDetails where reporter=:id and is_deleted=:d and approval_status='Rejected' order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("d", false);
		@SuppressWarnings("unchecked")
		final List<TimesheetApprovalDetails> details = query.getResultList();
		
		BigInteger approvedCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Approved' ")).getSingleResult());
		
		BigInteger rejectedCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Rejected' ")).getSingleResult());
		
		BigInteger pendingCounts =((BigInteger) (session.createNativeQuery("select count(*) from timesheet_approval_details where reporter="+"'"+id+"'"+" and is_deleted =false and approval_status='Submitted' ")).getSingleResult());
		
		JSONObject allDetails = new JSONObject();
		allDetails.put("details", new Gson().toJson(details));
		allDetails.put("approved_counts", approvedCounts);
		allDetails.put("rejected_counts", rejectedCounts);
		allDetails.put("pending_counts", pendingCounts);
		logger.info("TimesheetApprovalDetailsDaoImpl(getActiveTimesheetByApproverId) Exit>>");
		return allDetails;
	}

	@Override
	public boolean updateResubmittedTimesheetStatus(Long timesheetid) {
		logger.info("TimesheetApprovalDetailsDaoImpl (updateResubmittedTimesheetStatus) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createNativeQuery(
				"update timesheet_approval_details set approval_status = 'Updated' where id="+"'" + timesheetid + "'");
		int count = query.executeUpdate();
		if(count>0) {
			return true;
		}
		logger.info("TimesheetApprovalDetailsDaoImpl (updateResubmittedTimesheetStatus) >> Exit ");
		return false;
	}

}
