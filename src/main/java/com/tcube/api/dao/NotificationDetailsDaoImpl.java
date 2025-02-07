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
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.NotificationsDetails;

@Component
public class NotificationDetailsDaoImpl implements NotificationDetailsDao {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(NotificationDetailsDaoImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public NotificationsDetails addNotification(NotificationsDetails details,String zone) {
		logger.info("NotificationDetailsDaoImpl(addNotification) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setCreated_time(new Date());
			details.setModified_time(new Date());
//			details.setTimesheet_date(new Date());
			
			Date date = details.getCreated_time();
			Date date2 = details.getModified_time();
//			Date date3 = details.getTimesheet_date();
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			df.setTimeZone(TimeZone.getTimeZone(zone));
			
			details.setCreated_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			details.setModified_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
//			details.setTimesheet_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date3)));
			
			session.save(details);
			if (details.getId() == 0) {
				entityManager.persist(details);
				logger.info("NotificationDetailsDaoImpl(addNotification) Exit Job created");
				return details;
			} else {
				entityManager.merge(details);
				logger.info("NotificationDetailsDaoImpl(addNotification) >> Exit ");
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return details;
	}

	@Override
	public List<NotificationsDetails> getNotificationsEmpid(String empid) {
		logger.info("NotificationDetailsDaoImpl(getNotificationsEmpid) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from NotificationsDetails where to_notify_id=:i and is_deleted=: j");
		query.setParameter("i", empid);
		query.setParameter("j", false);
		List<NotificationsDetails> data = query.getResultList();
		logger.info("NotificationDetailsDaoImpl(getNotificationsEmpid) >> Exit ");
		return data;
	}

	@Override
	public NotificationsDetails getNotificationById(Long id) {
		logger.info("TimeTrackerDetailsDaoImpl(getById) Entry Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final NotificationsDetails details = (NotificationsDetails) session.get(NotificationsDetails.class, id);
		logger.info("TimeTrackerDetailsDaoImpl(getById) Exit-> ");
		return details;
	}
	
	@Override
	public NotificationsDetails updateNotificationMarkAsRead(NotificationsDetails details) {
		logger.info("TimeTrackerDetailsDaoImpl(updateNotificationMarkAsRead) Entry-> ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setModified_time(new Date());
			details.setIs_read(true);
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
		logger.info("TimeTrackerDetailsDaoImpl(updateNotificationMarkAsRead) Exit-> ");
		return details;
	}

	@Override
	public NotificationsDetails updateNotifierPersonalDetails(String empid, byte[] img){
		logger.info("NotificationDetailsDaoImpl(updateNotifierPersonalDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from NotificationsDetails where notifier=:i");
		query.setParameter("i", empid);
		List<NotificationsDetails> details = query.getResultList();
		for(int i=0; i<details.size();i++) {
//			details.get(i).setTo_notifier_prfl_img(img);
			if (details.get(i).getId() == 0) {
				entityManager.persist(details.get(i));
			} else {
				entityManager.merge(details.get(i));
			}
		}
		logger.info("NotificationDetailsDaoImpl(updateNotifierPersonalDetails) >> Exit ");
		return null;
	}

	@Override
	public List<NotificationsDetails> getUnreadNotificationsEmpid(String empid) {
		logger.info("NotificationDetailsDaoImpl(getUnreadNotificationsEmpid) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from NotificationsDetails where to_notify_id=:i and is_deleted=: j and is_read =:v ");
		query.setParameter("i", empid);
		query.setParameter("j", false);
		query.setParameter("v", false);
		List<NotificationsDetails> data = query.getResultList();
		logger.info("NotificationDetailsDaoImpl(getUnreadNotificationsEmpid) >> Exit ");
		return data;
	}

	@Override
	public List<NotificationsDetails> getNotificationsEmpidAndDateOfRequest(String empId, String dor , String module , long tSheetID) {
		logger.info("NotificationDetailsDaoImpl(getNotificationsEmpidAndDateOfRequest) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from NotificationsDetails where notifier=:i and is_deleted=: j and date_of_request=:k and sub_module_name =: l and timesheet_id=: tId ");
		query.setParameter("i", empId);
		query.setParameter("j", false);
		query.setParameter("k", dor);
		query.setParameter("l", module);
		query.setParameter("tId", tSheetID);
		List<NotificationsDetails> data = query.getResultList();
		logger.info("NotificationDetailsDaoImpl(getNotificationsEmpidAndDateOfRequest) >> Exit ");
		return data;
	}

	@Override
	public NotificationsDetails updateStatus(NotificationsDetails oldDetails) {
		logger.info("NotificationDetailsDaoImpl(updateStatus) >> Entry");
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
	    logger.info("NotificationDetailsDaoImpl(updateStatus) >> Exit");
		return oldDetails;
	}

	@Override
	public List<NotificationsDetails> getNotificationByEmpIdAndDate(NotificationsDetails incomingDetails, Long orgId,String zone) {
		logger.info("NotificationDetailsDaoImpl(getNotificationByEmpIdAndDate) >> Entry");
		Date date = incomingDetails.getCreated_time();
		Date date2 = incomingDetails.getModified_time();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		df.setTimeZone(TimeZone.getTimeZone(zone));
		
		try {
			incomingDetails.setCreated_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			incomingDetails.setModified_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from NotificationsDetails where org_id=:org and to_notify_id=:emp and is_deleted=: j and  created_time>=:sd and created_time <=:ed ");
		query.setParameter("org", orgId);
		query.setParameter("emp", incomingDetails.getTo_notify_id());
		query.setParameter("j", false);
		query.setParameter("sd",incomingDetails.getCreated_time());
		query.setParameter("ed",incomingDetails.getModified_time());
		List<NotificationsDetails> data = query.getResultList();
		logger.info("NotificationDetailsDaoImpl(getNotificationByEmpIdAndDate) >> Exit");
		
		return data;
		
	}

	@Override
	public boolean checkTimesheetNotSubmittedUserListNotificationCreated(Long orgid, String dateOfRequest) {
		logger.info("NotificationDetailsDaoImpl(checkTimesheetNotSubmittedUserListNotificationCreated) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		boolean val = false;
		final Query query = session.createQuery("from NotificationsDetails where org_id=:orgid and is_deleted=:i and date_of_request=:dor and message=:msg");
		query.setParameter("orgid", orgid);
		query.setParameter("i", false);
		query.setParameter("dor", dateOfRequest);
		query.setParameter("msg", "Timesheets not submitted staff");
		List<NotificationsDetails> data = query.getResultList();
		if(data.size() > 0 && data != null) {
			logger.info("NotificationDetailsDaoImpl(checkTimesheetNotSubmittedUserListNotificationCreated) >> Exit ");
			return true;
		}else {
			logger.info("NotificationDetailsDaoImpl(checkTimesheetNotSubmittedUserListNotificationCreated) >> Exit ");
			return false;
		}
	}
}
