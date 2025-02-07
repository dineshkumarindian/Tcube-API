package com.tcube.api.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.AccessDetails;
import com.tcube.api.model.ClientDetails;

@Component
public class AccessDetailsDaoImpl implements AccessDetailsDao{

	@PersistenceContext
	private EntityManager entityManager;


	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(AccessDetailsDaoImpl.class);


	@Override
	public AccessDetails createAccess(AccessDetails accessDetails) {
		logger.info("AccessDetailsDaoImpl(createAccess) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			accessDetails.setCreated_time(new Date());
			accessDetails.setModified_time(new Date());
			session.save(accessDetails);
			if (accessDetails.getId() == 0) {
				entityManager.persist(accessDetails);
				return accessDetails;
			} else {
				entityManager.merge(accessDetails);
				return accessDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("AccessDetailsDaoImpl(createAccess) Entry>> ");
		return accessDetails;
	}


	@Override
	public AccessDetails getById(long id) {
		logger.info("AccessDetailsDaoImpl(getById) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final AccessDetails details = (AccessDetails) session.get(AccessDetails.class, id);
		logger.info("AccessDetailsDaoImpl(getById) Exit>>-> ");
		return details;
	}


	@Override
	public AccessDetails updateAccess(AccessDetails oldDetails) {
		logger.info("AccessDetailsDaoImpl(updateAccess) Entry>>-> ");
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
	    logger.info("AccessDetailsDaoImpl(updateAccess) Exit>>-> ");
		return oldDetails;
	}


	@Override
	public AccessDetails deleteAccessDetails(AccessDetails oldDetails) {
		logger.info("AccessDetailsDaoImpl(deleteAccessDetails) Entry>>-> ");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	oldDetails.setModified_time(new Date());
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
	    logger.info("AccessDetailsDaoImpl(deleteAccessDetails) Exit>>-> ");
		return oldDetails;
	}


	@Override
	public List<AccessDetails> getAllAccessDetails() {
		logger.info("AccessDetailsDaoImpl (getAllAccessDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from AccessDetails");
		List<AccessDetails> details = query.getResultList();
		logger.info("AccessDetailsDaoImpl (getAllAccessDetails) >> Exit ");
		return details;
	}


	@Override
	public List<AccessDetails> getAccessDetailsByOrgId(Long id) {
		logger.info("AccessDetailsDaoImpl(getAccessDetailsByOrgId) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from AccessDetails where orgDetails.id=:id order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<AccessDetails> details = query.getResultList();
		logger.info("AccessDetailsDaoImpl(getAccessDetailsByOrgId) Exit>>");
		return details;
	}


	@Override
	public AccessDetails getByEmpId(String id) {
		System.out.println(id);
		logger.info("AccessDetailsDaoImpl(getByEmpId) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from AccessDetails where employeeDetails.id=:id order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final AccessDetails details = (AccessDetails) query.getSingleResult();
		logger.info("AccessDetailsDaoImpl(getByEmpId) Exit>>");
		return details;
	}
	
	
}
