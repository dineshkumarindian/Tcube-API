package com.tcube.api.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.ManageLeaveTypes;

@Component
public class ManageLeaveTypesDaoImpl implements ManageLeaveTypesDao{

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(ManageLeaveTypesDaoImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public ManageLeaveTypes createLeaveType(ManageLeaveTypes details , String timezone) {
		logger.info("ManageLeaveTypesDaoImpl(createLeaveType) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setCreated_time(new Date());
			details.setModified_time(new Date());
			
			Date date = details.getStart_date();
			Date date2 = details.getEnd_date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(timezone));

			details.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			details.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
			
			logger.debug("EmployeeDetailsDaoImpl obj:" + new Gson().toJson(details));
			session.save(details);
			if (details.getId() != null) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		logger.info("ManageLeaveTypesDaoImpl(createLeaveType) >> Exit ");

		return details;
	}

	@Override
	public ManageLeaveTypes getById(long id) {
		logger.info("ManageLeaveTypesDaoImpl(getById) >> Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final ManageLeaveTypes details = (ManageLeaveTypes) session.get(ManageLeaveTypes.class, id);
		logger.info("ManageLeaveTypesDaoImpl(getById) >> Exit");
		return details;
	}

	@Override
	public ManageLeaveTypes updateLeaveType(ManageLeaveTypes newDetails , String timezone) {
		logger.info("ManageLeaveTypesDaoImpl(updateLeaveType) >> Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	newDetails.setModified_time(new Date());
	    	
			Date date = newDetails.getStart_date();
			Date date2 = newDetails.getEnd_date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(timezone));

			newDetails.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			newDetails.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
			
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
	    logger.info("ManageLeaveTypesDaoImpl(updateLeaveType) >> Exit");
		return newDetails;
	}

	@Override
	public List<ManageLeaveTypes> getAllLeaveTypes() {
		logger.info("ManageLeaveTypesDaoImpl (getAllLeaveTypes) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from ManageLeaveTypes");
		List<ManageLeaveTypes> details = query.getResultList();
		logger.info("ManageLeaveTypesDaoImpl (getAllLeaveTypes) >> Exit ");
		return details;
	}

	@Override
	public List<ManageLeaveTypes> getAllLeaveTypesByOrgId(Long id) {
		logger.info("ManageLeaveTypesDaoImpl(getAllLeaveTypesByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ManageLeaveTypes where orgDetails.org_id=:id order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<ManageLeaveTypes> details = query.getResultList();
		logger.info("ManageLeaveTypesDaoImpl(getAllLeaveTypesByOrgId) >> Exit");
		return details;
	}

	@Override
	public List<ManageLeaveTypes> getActiveLeaveTypesByOrgId(Long id) {
		logger.info("ManageLeaveTypesDaoImpl(getActiveLeaveTypesByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ManageLeaveTypes where orgDetails.org_id=:id and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("j", true);
		@SuppressWarnings("unchecked")
		final List<ManageLeaveTypes> details = query.getResultList();
		logger.info("ManageLeaveTypesDaoImpl(getActiveLeaveTypesByOrgId) >> Exit");
		return details;
	}

	@Override
	public List<ManageLeaveTypes> getInactiveLeaveTypesByOrgId(Long id) {
		logger.info("ManageLeaveTypesDaoImpl(getInactiveLeaveTypesByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ManageLeaveTypes where orgDetails.org_id=:id and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("j", false);
		@SuppressWarnings("unchecked")
		final List<ManageLeaveTypes> details = query.getResultList();
		logger.info("ManageLeaveTypesDaoImpl(getInactiveLeaveTypesByOrgId) >> Exit");
		return details;
	}

	@Override
	public List<ManageLeaveTypes> getUndeletedLeaveTypesByOrgId(Long id) {
		logger.info("ManageLeaveTypesDaoImpl(getUndeletedLeaveTypesByOrgId) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ManageLeaveTypes where orgDetails.org_id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);  
		@SuppressWarnings("unchecked")
		final List<ManageLeaveTypes> details = query.getResultList();
		logger.info("ManageLeaveTypesDaoImpl(getUndeletedLeaveTypesByOrgId) Exit>>");
		return details;
	}

	@Override
	public List<ManageLeaveTypes> getActiveLeaveTypeByOrgIdAndDates(ManageLeaveTypes newDetails , String timezone ) {
		logger.info("LeaveTrackerDetailsDaoImpl(getActiveLeaveTypeByOrgIdAndDates) >> Entry");
		
		Date date = newDetails.getStart_date();
		Date date2 = newDetails.getEnd_date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// Use india time zone to format the date in
		df.setTimeZone(TimeZone.getTimeZone(timezone));
		try {
			newDetails.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			newDetails.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ManageLeaveTypes where orgDetails.org_id=:org and is_deleted=:a and is_activated =:k and start_date>=:b and end_date<=:c order by timestamp(modified_time) desc");
		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("a", false);
		query.setParameter("k", true); 
		query.setParameter("b", newDetails.getStart_date());
		query.setParameter("c", newDetails.getEnd_date());
		@SuppressWarnings("unchecked")
		List<ManageLeaveTypes> details = query.getResultList();
		logger.info("LeaveTrackerDetailsDaoImpl(getActiveLeaveTypeByOrgIdAndDates) >> Exit");
		return details;
	}

	@Override
	public ManageLeaveTypes updateLeaveTypeWithoutZone(ManageLeaveTypes newDetails) {
		logger.info("ManageLeaveTypesDaoImpl(updateLeaveTypeWithoutZone) >> EntrY");
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
	    logger.info("ManageLeaveTypesDaoImpl(updateLeaveTypeWithoutZone) >> Exit");
		return newDetails;
	}
	
	@Override
	public int bulkDelete(JSONArray ids) {
		logger.info("ManageLeaveTypesDaoImpl(bulkDelete) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}

		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session.createNativeQuery("update leave_type_details set is_deleted=:i where id in (" + id_sb + ")");
		query.setParameter("i", true);
//		query.setParameter("j", true)
		int details = query.executeUpdate();
		logger.info("ManageLeaveTypesDaoImpl(bulkDelete) >> Exit");
		return details;
	}

	
}
