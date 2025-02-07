package com.tcube.api.dao;

import java.text.DateFormat;
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
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.ReminderDetails;

@Component
public class ReminderDetailsDaoImpl implements ReminderDetailsDao {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(ReminderDetailsDaoImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public ReminderDetails createReminderDetails(ReminderDetails reminderDetails , String zone) {
		logger.info("ReminderDetailsDaoImpl(createReminderDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			reminderDetails.setCreated_time(new Date());
			reminderDetails.setModified_time(new Date());
			
			if(reminderDetails.getReminder_date() != null) {
				Date date = reminderDetails.getReminder_date();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				// Use india time zone to format the date in
				df.setTimeZone(TimeZone.getTimeZone(zone));

				reminderDetails.setReminder_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			}
			
			session.save(reminderDetails);
			if (reminderDetails.getId() == 0) {
				entityManager.persist(reminderDetails);
				return reminderDetails;
			} else {
				entityManager.merge(reminderDetails);
				return reminderDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("ReminderDetailsDaoImpl(createReminderDetails) >> Exit ");
		return reminderDetails;
	}

	@Override
	public ReminderDetails getById(Long id) {
		logger.info("ReminderDetailsDaoImpl(getById) >> Entry Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final ReminderDetails details = (ReminderDetails) session.get(ReminderDetails.class, id);
		logger.info("ReminderDetailsDaoImpl(getById) >> Exit");
		return details;
	}

	@Override
	public ReminderDetails updateReminderDetails(ReminderDetails newDetails,String zone) {
		logger.info("ReminderDetailsDaoImpl(updateReminderDetails) >> Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	newDetails.setModified_time(new Date());
	    	if(newDetails.getReminder_date() != null) {
	    		Date date = newDetails.getReminder_date();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				// Use india time zone to format the date in
				df.setTimeZone(TimeZone.getTimeZone(zone));

				newDetails.setReminder_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
	    	}
			
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
	    logger.info("ReminderDetailsDaoImpl(updateReminderDetails) >> Exit");
		return newDetails;
	}

	@Override
	public List<ReminderDetails> getAllRemindersByOrgId(Long id) {
		logger.info("ReminderDetailsDaoImpl(getAllRemindersByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ReminderDetails where orgDetails.id=:id");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<ReminderDetails> details = query.getResultList();
		logger.info("ReminderDetailsDaoImpl(getAllRemindersByOrgId) >> Exit");
		return details;
	}

	@Override
	public List<ReminderDetails> getReminderByOrgIdAndModule(ReminderDetails newDetails) {
		logger.info("ReminderDetailsDaoImpl(getReminderByOrgIdAndModule) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ReminderDetails where orgDetails.org_id=:id and is_deleted=:a and module_name=:b ");
		query.setParameter("id", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("a", newDetails.getIs_deleted());
		query.setParameter("b", newDetails.getModule_name());
		@SuppressWarnings("unchecked")
		final List<ReminderDetails> details = query.getResultList();
		logger.info("ReminderDetailsDaoImpl(getReminderByOrgIdAndModule) >> Exit");
		return details;
	}

	@Override
	public ReminderDetails updateReminderStatus(ReminderDetails newDetails) {
		logger.info("ReminderDetailsDaoImpl(updateReminderDetails) >> Entry");
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
	    logger.info("ReminderDetailsDaoImpl(updateReminderDetails) >> Exit");
		return newDetails;
	}

	@Override
	public ReminderDetails createReminderWithoutZone(ReminderDetails reminderDetails) {
		logger.info("ReminderDetailsDaoImpl(createReminderWithoutZone) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			reminderDetails.setCreated_time(new Date());
			reminderDetails.setModified_time(new Date());
			
			session.save(reminderDetails);
			if (reminderDetails.getId() == 0) {
				entityManager.persist(reminderDetails);
				return reminderDetails;
			} else {
				entityManager.merge(reminderDetails);
				return reminderDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("ReminderDetailsDaoImpl(createReminderWithoutZone) >> Entry ");
		return reminderDetails;
	}

	@Override
	public List<ReminderDetails> TodayUserListAccessUpdate(Long id) {
		// TODO Auto-generated method stub
		logger.info("ReminderDetailsDaoImpl(TodayLeaveUserListAccessUpdate) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ReminderDetails where orgDetails.id=:id and is_active =:a and is_deleted =:b order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("a", true);
		query.setParameter("b", false);
		@SuppressWarnings("unchecked")
		final List<ReminderDetails> details = query.getResultList();
		logger.info("ReminderDetailsDaoImpl(TodayLeaveUserListAccessUpdate) >> Exit");
		return details;
	}

}
