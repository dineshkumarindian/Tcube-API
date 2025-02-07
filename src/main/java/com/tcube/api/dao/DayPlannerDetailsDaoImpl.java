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
import org.json.JSONArray;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.DayPlannerDetails;

@Component
public class DayPlannerDetailsDaoImpl implements DayPlannerDetailsDao{

	@PersistenceContext
	private EntityManager entityManager;

	private static Logger logger = (Logger) LogManager.getLogger(DayPlannerDetailsDaoImpl.class);

	@Override
	public DayPlannerDetails createDayTask(DayPlannerDetails details,String zone) {
		logger.info("DayPlannerDetailsDaoImpl(createDayTask) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setCreated_time(new Date());
			details.setModified_time(new Date());
			Date date = details.getCreated_time();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));
			details.setCreated_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			
			session.save(details);
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
		logger.info("DayPlannerDetailsDaoImpl(createDayTask) >> Entry ");
		return details;
	}

	@Override
	public DayPlannerDetails getById(Long id) {
		logger.info("DayPlannerDetailsDaoImpl(getById) >> Entry Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final DayPlannerDetails details = (DayPlannerDetails) session.get(DayPlannerDetails.class, id);
		logger.info("DayPlannerDetailsDaoImpl(getById) Exit ");
		return details;
	}

	@Override
	public DayPlannerDetails updateDayTask(DayPlannerDetails newDetails) {
		logger.info("DayPlannerDetailsDaoImpl(updateDayTask) >> Entry");
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
	    logger.info("DayPlannerDetailsDaoImpl(updateDayTask) >> Exit");
		return newDetails;
	}

	@Override
	public List<DayPlannerDetails> getDayTasksByEmpidAndDate(DayPlannerDetails newDetails) {
		logger.info("DayPlannerDetailsDaoImpl(getDayTasksByEmpidAndDate) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from DayPlannerDetails where orgDetails.org_id=:id and is_deleted=:a and emp_id=:emp and date=:date order by timestamp(created_time) desc");
		query.setParameter("id", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("a", false);
		query.setParameter("emp", newDetails.getEmp_id());
		query.setParameter("date", newDetails.getDate());
		@SuppressWarnings("unchecked")
		final List<DayPlannerDetails> details = query.getResultList();
		logger.info("DayPlannerDetailsDaoImpl(getDayTasksByEmpidAndDate) >> Exit");
		return details;
	}

	@Override
	public int updateTaskDates(StringBuffer id_sb , String date) {
		logger.info("DayPlannerDetailsDaoImpl(updateTaskDates) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery("update day_planner_details set date=:i , is_submitted=:a , is_updated=:a where id in (" + id_sb + ")");
		query.setParameter("i", date);
		query.setParameter("a", false);
		int details = query.executeUpdate();
		logger.info("DayPlannerDetailsDaoImpl(updateTaskDates) >> Exit");
		return details;
	}

	@Override
	public int updateDayTaskSubmitStatus(StringBuffer id_sb, Boolean status) {
		logger.info("DayPlannerDetailsDaoImpl(updateTaskDates) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery("update day_planner_details set is_submitted=:i where id in (" + id_sb + ")");
		query.setParameter("i", status);
		int details = query.executeUpdate();
		logger.info("DayPlannerDetailsDaoImpl(updateTaskDates) >> Exit");
		return details;
	}

	@Override
	public int updateDayTaskupdateStatus(StringBuffer id_sb, Boolean status) {
		logger.info("DayPlannerDetailsDaoImpl(updateTaskDates) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery("update day_planner_details set is_updated=:i where id in (" + id_sb + ")");
		query.setParameter("i", status);
		int details = query.executeUpdate();
		logger.info("DayPlannerDetailsDaoImpl(updateTaskDates) >> Exit");
		return details;
	}

	@Override
	public List<DayPlannerDetails> getDayTasksByOrgidAndDate(DayPlannerDetails newDetails) {
		logger.info("DayPlannerDetailsDaoImpl(getDayTasksByEmpidAndDate) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from DayPlannerDetails where orgDetails.org_id=:id and is_deleted=:a and date=:date order by timestamp(created_time) desc");
		query.setParameter("id", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("a", false);
		query.setParameter("date", newDetails.getDate());
		@SuppressWarnings("unchecked")
		final List<DayPlannerDetails> details = query.getResultList();
		logger.info("DayPlannerDetailsDaoImpl(getDayTasksByEmpidAndDate) >> Exit");
		return details;
	}

	@Override
	public DayPlannerDetails updateEmpImageDetails(String empId, byte[] compressBytes) {
		logger.info("DayPlannerDetailsDaoImpl(updateEmpImageDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from DayPlannerDetails where emp_id=:i");
		query.setParameter("i", empId);
		List<DayPlannerDetails> details = query.getResultList();
		for(int i=0; i<details.size();i++) {
//			details.get(i).setEmp_image(compressBytes);
			if (details.get(i).getId() == 0) {
				entityManager.persist(details.get(i));
			} else {
				entityManager.merge(details.get(i));
			}
		}
		logger.info("DayPlannerDetailsDaoImpl(updateEmpImageDetails) >> Exit ");
		return null;
	}

	@Override
	public int bulkDelete(JSONArray ids) {
		logger.info("DayPlannerDetailsDaoImpl(bulkDelete) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}

		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session
				.createNativeQuery("update day_planner_details set is_deleted=:i where id in (" + id_sb + ")");
		query.setParameter("i", true);
		int details = query.executeUpdate();
		logger.info("DayPlannerDetailsDaoImpl(bulkDelete) >> Exit");
		return details;
	}
	
}
