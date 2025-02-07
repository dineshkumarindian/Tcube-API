package com.tcube.api.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.tcube.api.model.ActionCards;
import com.tcube.api.model.ManageAttendance;

@Component
public class ManageAttendanceDaoImpl implements ManageAttendanceDao {
	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(ManageAttendanceDaoImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public ManageAttendance createattendancecard(ManageAttendance details) {
		logger.info("ManageAttendanceDaoImpl(createAttendanceDetailst) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setCreated_time(new Date());
			details.setModified_time(new Date());
			details.setDelete(false);
			session.save(details);
			if (details.getId() == 0) {
				entityManager.persist(details);
			} else {
				entityManager.merge(details);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(
					"Exception occured in ManageAttendanceDaoImpl(createAttendanceDetailst) and Exception details >> "
							+ e);
		}
		logger.info("ManageAttendanceDaoImpl(createAttendanceDetailst) Exit>> ");
		return details;
	}

	@Override
	public ManageAttendance updateattendancecard(ManageAttendance details) {
		logger.info("ManageAttendanceDaoImpl(updateattendancecard) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setModified_time(new Date());
//			session.save(details);
			if (details.getId() == 0) {
				entityManager.persist(details);
			} else {
				entityManager.merge(details);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(
					"Exception occured in ManageAttendanceDaoImpl(updateattendancecard) and Exception details >> "
							+ e);
		}
		logger.info("ManageAttendanceDaoImpl(updateattendancecard) Exit>> ");
		return details;
	}

	@Override
	public ManageAttendance getattendancecardById(Long id) {
		logger.info("ManageAttendanceDaoImpl(getattendancecardById) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		ManageAttendance details = new ManageAttendance();
		try {
			final Query query = session.createQuery("from ManageAttendance where isDelete=:i and id=:j");
			query.setParameter("i", false);
			query.setParameter("j", id);

			@SuppressWarnings("unchecked")
			List<ManageAttendance> datalist = query.getResultList();
			if(datalist.size()>0) {
			    details = datalist.get(0); 
			    return details;
			}
			else {
			    return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(
					"Exception occured in ManageAttendanceDaoImpl(getattendancecardById) and Exception details >> "
							+ e);
		}
		logger.info("ManageAttendanceDaoImpl(getattendancecardById) Exit>> ");
		return null;
	}

	@SuppressWarnings("null")
	@Override
	public List<ActionCards> getAllattendancecardByOrgId(Long orgId) {
		// TODO Auto-generated method stub
		logger.info("ManageAttendanceDaoImpl(getAllattendancecardByOrgId) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from ManageAttendance where isDelete=:i and orgDetails.org_id=:orgId");
		query.setParameter("i", false);
		query.setParameter("orgId", orgId);
		@SuppressWarnings("unchecked")
		List<ManageAttendance> details = query.getResultList();
		List<ActionCards> result = new ArrayList<>();
		if(details != null) {
			for(ManageAttendance data : details) {
				ActionCards addvalue = new ActionCards();
				addvalue.setId(data.getId());
				addvalue.setAction(data.getAction());
				addvalue.setAction_image(data.getAction_image());
				addvalue.setAction_type(data.getAction_type());
				addvalue.setCurrent_section(data.getCurrent_section());
				addvalue.setNext_section(data.getNext_section());
				result.add(addvalue);
			}
		}
		
	
		logger.info("ManageAttendanceDaoImpl(getAllattendancecardByOrgId) Exit>> ");
		return result;
	}

}
