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
import com.tcube.api.model.ManageIntegration;

@Component
public class ManageIntegrationDaoImpl implements ManageIntegrationDao{

	
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(ManageIntegrationDaoImpl.class);


	@Override
	public ManageIntegration createManageIntegration(ManageIntegration ManageIntegration) {
		logger.info("ManageIntegrationDaoImpl(createManageIntegration) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			ManageIntegration.setCreatedTime(new Date());
			ManageIntegration.setModifiedTime(new Date());
			session.save(ManageIntegration);
			if (ManageIntegration.getId() == 0) {
				entityManager.persist(ManageIntegration);
			} else {
				entityManager.merge(ManageIntegration);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("ManageIntegrationDaoImpl(createManageIntegration) >> Exit " +e);
		}
		logger.info("ManageIntegrationDaoImpl(createManageIntegration) >> Exit ");
		return ManageIntegration;
	}


	@Override
	public ManageIntegration getManageIntegrationById(Long id) {
		logger.info("ManageIntegrationDaoImpl(getManageIntegrationById) >> Entry Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final ManageIntegration details = (ManageIntegration) session.get(ManageIntegration.class, id);
		logger.info("ManageIntegrationDaoImpl(getManageIntegrationById) >> Exit");
		return details;
	}


	@Override
	public ManageIntegration updateManageIntegration(ManageIntegration newDetails) {
		logger.info("ManageIntegrationDaoImpl(updateManageIntegration) >> Entry");
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
			logger.info("ManageIntegrationDaoImpl(updateManageIntegration) >> Exit "+ e);
			e.printStackTrace();
		}
	    logger.info("ManageIntegrationDaoImpl(updateManageIntegration) Exception  >> Exit");
		return newDetails;
	}


	@Override
	public ManageIntegration deleteManageIntegration(ManageIntegration newDetails) {
		logger.info("ManageIntegrationDaoImpl(deleteManageIntegration) >> Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	newDetails.setDelete(false);
	    	newDetails.setModifiedTime(new Date());
			session.update(newDetails);
			if (newDetails.getId() == 0) {
				entityManager.persist(newDetails);
			} else {
				entityManager.merge(newDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
			 logger.info("ManageIntegrationDaoImpl(deleteManageIntegration) exception >> Exit " + e);
		}
	    logger.info("ManageIntegrationDaoImpl(deleteManageIntegration) >> Exit");
		return newDetails;
	}



	@Override
	public List<ManageIntegration> getManageIntegrationByOrgId(Long id) {
		logger.info("ManageIntegrationDaoImpl(getManageIntegrationByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ManageIntegration where org_id=:id order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<ManageIntegration> details = query.getResultList();
		logger.info("ManageIntegrationDaoImpl(getManageIntegrationByOrgId) >> Exit");
		return details;
	}


	@Override
	public ManageIntegration getOrgAMdetails(Long org_id, String app, String module) {
		logger.info("ManageIntegrationDaoImpl(getOrgAMdetails) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ManageIntegration where org_id=:org_id and module=:module and app=:app order by timestamp(modified_time) desc");
		query.setParameter("org_id", org_id);
		query.setParameter("app", app);
		query.setParameter("module", module);		
		@SuppressWarnings("unchecked")
		List<ManageIntegration> details = query.getResultList();
		logger.info("ManageIntegrationDaoImpl(getOrgAMdetails) >> Exit");
		if(details.size()>0) {
			return details.get(0);
		}
		else {
			return null;
		}
		
	}

//	final Query query = session.createNativeQuery("update leave_type_details set is_deleted=:i where id in (" + id_sb + ")");
	@Override
	public int updateAllSlackIntegration(Long org_id, String app, String module) {
		// TODO Auto-generated method stub
		logger.info("ManageIntegrationDaoImpl(updateAllSlackIntegration) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery("update manage_integrations set is_active=:i where app =:j and module=:k and org_id=:org");
		query.setParameter("i", false);
		query.setParameter("j", app);
		query.setParameter("k", module);
		query.setParameter("org", org_id);
//		query.setParameter("j", true)
		int details = query.executeUpdate();
//		System.out.println(details);
		logger.info("ManageLeaveTypesDaoImpl(updateAllSlackIntegration) >> Exit");
		return details;
	}
	



	
}
