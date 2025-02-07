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
import com.tcube.api.model.MailConfigDetails;

@Component
public class MailConfigDetailsDaoImpl implements MailConfigDetailsDao{

	
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(MailConfigDetailsDaoImpl.class);


	@Override
	public MailConfigDetails createMailConfig(MailConfigDetails MailConfigDetails) {
		logger.info("MailConfigDetailsDaoImpl(createMailConfig) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			MailConfigDetails.setCreatedTime(new Date());
			MailConfigDetails.setModifiedTime(new Date());
			session.save(MailConfigDetails);
			if (MailConfigDetails.getId() == 0) {
				entityManager.persist(MailConfigDetails);
			} else {
				entityManager.merge(MailConfigDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("MailConfigDetailsDaoImpl(createMailConfig) >> Exit " +e);
		}
		logger.info("MailConfigDetailsDaoImpl(createMailConfig) >> Exit ");
		return MailConfigDetails;
	}


	@Override
	public MailConfigDetails getMailConfigById(Long id) {
		logger.info("MailConfigDetailsDaoImpl(getMailConfigById) >> Entry Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final MailConfigDetails details = (MailConfigDetails) session.get(MailConfigDetails.class, id);
		logger.info("MailConfigDetailsDaoImpl(getMailConfigById) >> Exit");
		return details;
	}


	@Override
	public MailConfigDetails updateMailConfig(MailConfigDetails newDetails) {
		logger.info("MailConfigDetailsDaoImpl(updateMailConfig) >> Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	newDetails.setModifiedTime(new Date());
	    	logger.debug("appInfo obj:" + new Gson().toJson(newDetails));
			session.update(newDetails);
			if (newDetails.getId() == 0) {
				entityManager.persist(newDetails);
			} else {
				entityManager.merge(newDetails);
			}
		} catch (Exception e) {
			logger.info("MailConfigDetailsDaoImpl(updateMailConfig) >> Exit "+ e);
			e.printStackTrace();
		}
	    logger.info("MailConfigDetailsDaoImpl(updateMailConfig) Exception  >> Exit");
		return newDetails;
	}


	@Override
	public MailConfigDetails deleteMailConfig(MailConfigDetails newDetails) {
		logger.info("MailConfigDetailsDaoImpl(deleteMailConfig) >> Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	newDetails.setModifiedTime(new Date());
			session.update(newDetails);
			if (newDetails.getId() == 0) {
				entityManager.persist(newDetails);
			} else {
				entityManager.merge(newDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
			 logger.info("MailConfigDetailsDaoImpl(deleteMailConfig) exception >> Exit " + e);
		}
	    logger.info("MailConfigDetailsDaoImpl(deleteMailConfig) >> Exit");
		return newDetails;
	}



	@Override
	public MailConfigDetails getMailConfigByOrgId(Long id) {
		logger.info("MailConfigDetailsDaoImpl(getMailConfigByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		  try {
			  final Query query = session.createQuery(
						"from MailConfigDetails where org_id=:id and isDelete=:isDelete order by timestamp(modified_time) desc");
				query.setParameter("id", id);
//				query.setParameter("isActive", true);
				query.setParameter("isDelete", false);
				@SuppressWarnings("unchecked")
				final MailConfigDetails details = (MailConfigDetails) query.getSingleResult();
				logger.info("MailConfigDetailsDaoImpl(getMailConfigByOrgId) >> Exit");
				return details;
			} catch (Exception e) {
				e.printStackTrace();
				 logger.info("MailConfigDetailsDaoImpl(getMailConfigByOrgId) exception >> Exit " + e);
				 return null;
			}
	}
	



	
}
