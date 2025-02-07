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
import com.tcube.api.model.AppsIntegrationDetails;

@Component
public class AppsIntegrationDetailsDaoImpl implements AppsIntegrationDetailsDao{

	@PersistenceContext
	private EntityManager entityManager;

	private static Logger logger = (Logger) LogManager.getLogger(AppsIntegrationDetailsDaoImpl.class);

	@Override
	public AppsIntegrationDetails createIntegration(AppsIntegrationDetails details) {
		logger.info("AppsIntegrationDetailsDaoImpl(createIntegration) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setCreated_time(new Date());
			details.setModified_time(new Date());
//			details.setUrl(EncryptorUtil.encryptPropertyValue(details.getUrl()));
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
		logger.info("AppsIntegrationDetailsDaoImpl(createIntegration) >> Exit ");
		return details;
	}

	@Override
	public AppsIntegrationDetails getById(Long id) {
		logger.info("AppsIntegrationDetailsDaoImpl(getById) >> Entry Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final AppsIntegrationDetails details = (AppsIntegrationDetails) session.get(AppsIntegrationDetails.class, id);
		logger.info("AppsIntegrationDetailsDaoImpl(getById) >> Exit");
		return details;
	}

	@Override
	public AppsIntegrationDetails updateIntegration(AppsIntegrationDetails newDetails) {
		logger.info("AppsIntegrationDetailsDaoImpl(updateIntegration) >> Entry");
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
	    logger.info("AppsIntegrationDetailsDaoImpl(updateIntegration) >> Exit");
		return newDetails;
	}

	@Override
	public List<AppsIntegrationDetails> getAllIntegrations() {
		logger.info("AppsIntegrationDetailsDaoImpl (getAllIntegrations) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from AppsIntegrationDetails");
		List<AppsIntegrationDetails> details = query.getResultList();
		logger.info("AppsIntegrationDetailsDaoImpl (getAllIntegrations) >> Exit ");
		return details;
	}

	@Override
	public List<AppsIntegrationDetails> getAllIntegrationByOrgId(Long id) {
		logger.info("AppsIntegrationDetailsDaoImpl(getAllIntegrationByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from AppsIntegrationDetails where orgDetails.id=:id order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<AppsIntegrationDetails> details = query.getResultList();
		logger.info("AppsIntegrationDetailsDaoImpl(getAllIntegrationByOrgId) >> Exit");
		return details;
	}

	@Override
	public List<AppsIntegrationDetails> getActiveIntegrationByOrgId(Long id) {
		logger.info("AppsIntegrationDetailsDaoImpl(getActiveIntegrationByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from AppsIntegrationDetails where orgDetails.org_id=:id and is_deleted=:a order by module_name asc");
		query.setParameter("id", id);
		query.setParameter("a", false);
		@SuppressWarnings("unchecked")
		final List<AppsIntegrationDetails> details = query.getResultList();
		logger.info("AppsIntegrationDetailsDaoImpl(getActiveIntegrationByOrgId) >> Exit");
		return details;
	}

	@Override
	public AppsIntegrationDetails getIntegrationByOrgIdAndModule(AppsIntegrationDetails newDetails) {
		logger.info("AppsIntegrationDetailsDaoImpl(getIntegrationByOrgIdAndModule) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from AppsIntegrationDetails where orgDetails.org_id=:id and is_deleted=:a and module_name=:b and reason=:c and app_name=:d");
		query.setParameter("id", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("a", false);
		query.setParameter("b", newDetails.getModule_name());
		query.setParameter("c", newDetails.getReason());
		query.setParameter("d", newDetails.getApp_name());
		@SuppressWarnings("unchecked")
		final AppsIntegrationDetails details = (AppsIntegrationDetails) query.getSingleResult();
		logger.info("AppsIntegrationDetailsDaoImpl(getIntegrationByOrgIdAndModule) >> Exit");
		return details;
	}

	@Override
	public String getSlackUrlLeaveTracker(Long id) {
		// TODO Auto-generated method stub
		logger.info("AppsIntegrationDetailsDaoImpl(getSlackUrlLeaveTracker) >> Entry");
		String url="";
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"select url from AppsIntegrationDetails where orgDetails.org_id=:id and is_deleted=:a and is_paused=:b and module_name=:c and app_name=:d");
		query.setParameter("id",id);
		query.setParameter("a", false);
		query.setParameter("b", false);
		query.setParameter("c", "leave-tracker");
		query.setParameter("d", "slack");
		@SuppressWarnings("unchecked")
		final List<String> details =  query.getResultList();
		if(details.size() != 0) {
			url = details.get(0); 
		} else {
			url = "";
		}
//		String slackUrl = details.get(0);
//		if(slackUrl.isEmpty()) {
//			url="";
//		} else {
//			url = slackUrl;
//		}
		logger.info("AppsIntegrationDetailsDaoImpl(getSlackUrlLeaveTracker) >> Exit");
		return url;
	}
	
// 	@Override
// 	public String getSlackUrlTimeTracker(Long id) {
// 		// TODO Auto-generated method stub
// 		logger.info("AppsIntegrationDetailsDaoImpl(getSlackUrlTimeTracker) >> Entry");
// 		String url="";
// 		final Session session = entityManager.unwrap(Session.class);
// 		final Query query = session.createQuery(
// 				"select url from AppsIntegrationDetails where orgDetails.org_id=:id and is_deleted=:a and is_paused=:b and module_name=:c and app_name=:d");
// 		query.setParameter("id",id);
// 		query.setParameter("a", false);
// 		query.setParameter("b", false);
// 		query.setParameter("c", "attendance");
// 		query.setParameter("d", "slack");
// 		@SuppressWarnings("unchecked")
// 		final List<String> details =  query.getResultList();
// 		if(details.size() != 0) {
// 			url = details.get(0); 
// 		} else {
// 			url = "";
// 		}
// //		String slackUrl = details.get(0);
// //		if(slackUrl.isEmpty()) {
// //			url="";
// //		} else {
// //			url = slackUrl;
// //		}
// 		logger.info("AppsIntegrationDetailsDaoImpl(getSlackUrlTimeTracker) >> Exit");
// 		return url;
// 	}

	@Override
	public List<AppsIntegrationDetails> getActiveSlackIntegrationByOrgId(Long id) {
		logger.info("AppsIntegrationDetailsDaoImpl(getActiveSlackIntegrationByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from AppsIntegrationDetails where orgDetails.org_id=:id and app_name='slack'  and is_deleted=:a order by module_name asc");
		query.setParameter("id", id);
		query.setParameter("a", false);
		@SuppressWarnings("unchecked")
		final List<AppsIntegrationDetails> details = query.getResultList();
		logger.info("AppsIntegrationDetailsDaoImpl(getActiveSlackIntegrationByOrgId) >> Exit");
		return details;
	}

	@Override
	public List<AppsIntegrationDetails> getActiveWhatsappIntegrationByOrgId(Long id) {
		logger.info("AppsIntegrationDetailsDaoImpl(getActiveWhatsappIntegrationByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from AppsIntegrationDetails where orgDetails.org_id=:id and app_name='whatsapp'  and is_deleted=:a order by module_name asc");
		query.setParameter("id", id);
		query.setParameter("a", false);
		@SuppressWarnings("unchecked")
		final List<AppsIntegrationDetails> details = query.getResultList();
		logger.info("AppsIntegrationDetailsDaoImpl(getActiveWhatsappIntegrationByOrgId) >> Exit");
		return details;

	}
	public String getSlackUrlAttendance(Long id) {
		// TODO Auto-generated method stub
		logger.info("AppsIntegrationDetailsDaoImpl(getSlackUrlTimeTracker) >> Entry");
		String url="";
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"select url from AppsIntegrationDetails where orgDetails.org_id=:id and is_deleted=:a and is_paused=:b and module_name=:c and app_name=:d");
		query.setParameter("id",id);
		query.setParameter("a", false);
		query.setParameter("b", false);
		query.setParameter("c", "attendance");
		query.setParameter("d", "slack");
		@SuppressWarnings("unchecked")
		final List<String> details =  query.getResultList();
		if(details.size() != 0) {
			url = details.get(0); 
		} else {
			url = "";
		}
//		String slackUrl = details.get(0);
//		if(slackUrl.isEmpty()) {
//			url="";
//		} else {
//			url = slackUrl;
//		}
		logger.info("AppsIntegrationDetailsDaoImpl(getSlackUrlTimeTracker) >> Exit");
		return url;
	}

}
