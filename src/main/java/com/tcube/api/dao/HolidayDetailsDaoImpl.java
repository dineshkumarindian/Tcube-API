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
import com.tcube.api.model.ClientDetails;
import com.tcube.api.model.HolidayDetails;
import com.tcube.api.model.ManageLeaveTypes;

@Component
public class HolidayDetailsDaoImpl implements HolidayDetailsDao{

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(HolidayDetailsDaoImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public HolidayDetails createHoliday(HolidayDetails details, String zone) {
		logger.info("HolidayDetailsDaoImpl(createHoliday) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setCreated_time(new Date());
			details.setModified_time(new Date());
			
			Date date = details.getStart_date();
			Date date2 = details.getEnd_date();
//			Date date3 = details.getLeave_date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));

			details.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			details.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
//			details.setLeave_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date3)));
			logger.debug("HolidayDetailsDaoImpl obj:" + new Gson().toJson(details));
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
		logger.info("HolidayDetailsDaoImpl(createHoliday) >> Exit");

		return details;
	}

	@Override
	public HolidayDetails getById(long id) {
		logger.info("HolidayDetailsDaoImpl(getById) Entry>> Request");
		final Session session = entityManager.unwrap(Session.class);
		final HolidayDetails details = (HolidayDetails) session.get(HolidayDetails.class, id);
		logger.info("HolidayDetailsDaoImpl(getById) Exit>>-> ");
		return details;
	}

	@Override
	public HolidayDetails updateHoliday(HolidayDetails newDetails, String zone) {
		logger.info("HolidayDetailsDaoImpl(updateHoliday) >> Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	newDetails.setModified_time(new Date());
	    	
			Date date = newDetails.getStart_date();
			Date date2 = newDetails.getEnd_date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));

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
	    logger.info("HolidayDetailsDaoImpl(updateHoliday) >> Exit");
		return newDetails;
	}

	@Override
	public HolidayDetails updateHolidayWithoutZone(HolidayDetails newDetails) {
		logger.info("HolidayDetailsDaoImpl(updateHolidayWithoutZone) >> Entry");
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
	    logger.info("HolidayDetailsDaoImpl(updateHolidayWithoutZone) >> Exit");
		return newDetails;
	}

	@Override
	public List<HolidayDetails> getAllHolidays() {
		logger.info("HolidayDetailsDaoImpl (getAllHolidays) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from HolidayDetails");
		List<HolidayDetails> details = query.getResultList();
		logger.info("HolidayDetailsDaoImpl (getAllHolidays) >> Exit");
		return details;
	}

	@Override
	public List<HolidayDetails> getAllHolidaysByOrgId(Long id) {
		logger.info("HolidayDetailsDaoImpl(getAllHolidaysByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from HolidayDetails where orgDetails.org_id=:id order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<HolidayDetails> details = query.getResultList();
		logger.info("HolidayDetailsDaoImpl(getAllHolidaysByOrgId) >> Exit");
		return details;
	}

	@Override
	public List<HolidayDetails> getActiveHolidaysByOrgId(Long id) {
		logger.info("HolidayDetailsDaoImpl(getActiveHolidaysByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from HolidayDetails where orgDetails.org_id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked")
		final List<HolidayDetails> details = query.getResultList();
		logger.info("HolidayDetailsDaoImpl(getActiveHolidaysByOrgId) >> Exit");
		return details;
	}
	
	@Override
	public List<HolidayDetails> getActiveHolidaysByOrgIdAndDates(Long orgId,String startDate,String endDate, String zone) {
		logger.info("HolidayDetailsDaoImpl(getActiveHolidaysByOrgId) >> Entry");
//		Date date = newDetails.getStart_date();
//		Date date2 = newDetails.getEnd_date();
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//		// Use india time zone to format the date in
//		df.setTimeZone(TimeZone.getTimeZone(zone));

//		try {
//			newDetails.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
//			newDetails.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
//			System.out.println(newDetails.getStart_date());
//			System.out.println(newDetails.getEnd_date());
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}

		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from HolidayDetails where orgDetails.org_id=:org and is_deleted=:a and leave_date_str>=:b and leave_date_str<=:c order by timestamp(leave_date_str) asc");
//		final Query query = session.createQuery(
//				"from HolidayDetails where orgDetails.org_id=:org and is_deleted=:a order by timestamp(modified_time) desc");
		query.setParameter("org",orgId);
		query.setParameter("a", false);
		query.setParameter("b",startDate);
		query.setParameter("c",endDate);
		@SuppressWarnings("unchecked")
		List<HolidayDetails> details = query.getResultList();
		logger.info("HolidayDetailsDaoImpl(getActiveHolidaysByOrgIdAndDates) >> Exit");
		return details;
	}
//	@Override
//	public List<HolidayDetails> getActiveHolidaysByOrgIdAndDates1(HolidayDetails newDetails , String zone) {
//		logger.info("HolidayDetailsDaoImpl(getActiveHolidaysByOrgId) Entry>>");
//		Date date = newDetails.getStart_date();
//		Date date2 = newDetails.getEnd_date();
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//		// Use india time zone to format the date in
//		df.setTimeZone(TimeZone.getTimeZone(zone));
//
//		try {
//			newDetails.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
//			newDetails.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
//			System.out.println(newDetails.getStart_date());
//			System.out.println(newDetails.getEnd_date());
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//		final Session session = entityManager.unwrap(Session.class);
//		final Query query = session.createQuery(
//				"from HolidayDetails where orgDetails.org_id=:org and is_deleted=:a and leave_date_str>=:b and leave_date_str<=:c order by timestamp(leave_date_str) desc");
////		final Query query = session.createQuery(
////				"from HolidayDetails where orgDetails.org_id=:org and is_deleted=:a order by timestamp(modified_time) desc");
//		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
//		query.setParameter("a", false);
//		query.setParameter("b", newDetails.getStart_date());
//		query.setParameter("c", newDetails.getEnd_date());
//		@SuppressWarnings("unchecked")
//		List<HolidayDetails> details = query.getResultList();
//		logger.info("HolidayDetailsDaoImpl(getActiveHolidaysByOrgIdAndDates) Exit");
//		return details;
//	}

	@Override
	public int bulkDelete(JSONArray ids) {
		logger.info("HolidayDetailsDaoImpl(bulkDelete) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}
		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session.createNativeQuery("update holiday_details set is_deleted=:i where id in (" + id_sb + ")");
		query.setParameter("i", true);
		int details = query.executeUpdate();
		logger.info("HolidayDetailsDaoImpl(bulkDelete) >> Exit");
		return details;
		
		// TODO Auto-generated method stub

	}

	@Override
	public List<HolidayDetails> getTodayHolidayDetails(Long orgId, String Date) {
		// TODO Auto-generated method stub
		logger.info("HolidayDetailsDaoImpl(getTodayHolidayDetails) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		System.out.println("orgId.."+orgId +"Date..."+Date);
		final Query query = session.createQuery(
				"from HolidayDetails where orgDetails.org_id=:org and is_deleted=:a and leave_date_str=:b");
		query.setParameter("org",orgId);
		query.setParameter("a", false);
		query.setParameter("b", Date);
		@SuppressWarnings("unchecked")
		List<HolidayDetails> details = query.getResultList();
		
		logger.info("HolidayDetailsDaoImpl(getTodayHolidayDetails) >> Exit");
		return details;
	}
	
	
}
