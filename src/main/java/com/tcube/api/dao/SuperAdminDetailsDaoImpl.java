package com.tcube.api.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.SuperAdminDetails;
import com.tcube.api.utils.EncryptorUtil;

@Component
public class SuperAdminDetailsDaoImpl implements SuperAdminDetailsDao{
	
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(SuperAdminDetailsDaoImpl.class);

	@Override
	public SuperAdminDetails createSuperAdminDetails(SuperAdminDetails admindetails) {
		final Session session = entityManager.unwrap(Session.class);
		try {
			admindetails.setCreated_time(new Date());
			admindetails.setModified_time(new Date());
			admindetails.setPassword(EncryptorUtil.encryptPropertyValue(admindetails.getPassword()));
			session.save(admindetails);
			if (admindetails.getId() == 0) {
				entityManager.persist(admindetails);
				return admindetails;
			} else {
				entityManager.merge(admindetails);
				return admindetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return admindetails;
	}

	@Override
	public SuperAdminDetails getAdminDetailsById(Long adminId) {
//		logger.info("ApplicationInfoDetailsDaoImpl(getApplicationDetailsById) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final SuperAdminDetails details = (SuperAdminDetails) session.get(SuperAdminDetails.class, adminId);
//		logger.info("ApplicationInfoDetailsDaoImpl(getApplicationDetailsById) Exit>>-> ");
		return details;
	}

	@Override
	public SuperAdminDetails updateSuperAdminDetails(SuperAdminDetails oldAdminDetails) {
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	oldAdminDetails.setModified_time(new Date());
	    	oldAdminDetails.setPassword(EncryptorUtil.encryptPropertyValue(oldAdminDetails.getPassword()));
//	    	logger.debug("appInfo obj:" + new Gson().toJson(oldAdminDetails));
			session.update(oldAdminDetails);
			if (oldAdminDetails.getId() == 0) {
				entityManager.persist(oldAdminDetails);
				return oldAdminDetails;
			} else {
				entityManager.merge(oldAdminDetails);
				return oldAdminDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oldAdminDetails;
	}

	@Override
	public SuperAdminDetails deleteSuperAdminDetails(SuperAdminDetails oldAdminDetails) {
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	oldAdminDetails.setModified_time(new Date());
			session.update(oldAdminDetails);
			if (oldAdminDetails.getId() == 0) {
				entityManager.persist(oldAdminDetails);
				return oldAdminDetails;
			} else {
				entityManager.merge(oldAdminDetails);
				return oldAdminDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oldAdminDetails;

	}

	@Override
	public List<SuperAdminDetails> getAllSuperAdminDetails() {
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final List<SuperAdminDetails> details = session.createCriteria(SuperAdminDetails.class).list();
		return details;
	}
	
	@Override
	public SuperAdminDetails authenticateSA(SuperAdminDetails admindetails) {
		logger.info("SuperAdminDetailsDaoImpl(authenticateSA) Entry>> Request -> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from SuperAdminDetails where email=:e and is_deleted=:d");
		query.setParameter("e", admindetails.getEmail());
		query.setParameter("d", false);
		SuperAdminDetails admindetails1 = null;
		try{
			admindetails1 = (SuperAdminDetails)query.getSingleResult();
			logger.info("SuperAdminDetailsDaoImpl(authenticateSA) Exit>> Request -> ");
				return admindetails1;
		}
		catch (NoResultException nre){
			logger.info("SuperAdminDetailsDaoImpl(authenticateSA) Exit>> Request -> ");
			return null;
		}	
	}
	
	@Override
	public SuperAdminDetails updateSuperAdminPassword(SuperAdminDetails oldSADetails) {
		final Session session= entityManager.unwrap(Session.class);
		logger.info("SuperAdminDetailsDaoImpl(updateSuperAdminPassword) Entry>> Request -> " + oldSADetails);
		 try {    
			 oldSADetails.setModified_time(new Date());
			 oldSADetails.setPassword(oldSADetails.getPassword());
			 if(oldSADetails.getIsForgotPwdEnabled()) {
				 oldSADetails.setIsForgotPwdEnabled(false);
			 }
				session.update(oldSADetails);
				if (oldSADetails.getId() != null) {
					entityManager.persist(oldSADetails);
					return oldSADetails;
				} else {
					entityManager.merge(oldSADetails);
					return oldSADetails;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		 logger.info("SuperAdminDetailsDaoImpl(updateSuperAdminPassword) Exit>>" );
			return oldSADetails;
	}
	

}
