package com.tcube.api.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.JiraIntegrationDetails;

@Component
public class JiraIntegrationDetailsDaoImpl implements JiraIntegrationDetailsDao {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(JiraIntegrationDetailsDaoImpl.class);

	@Override
	public JiraIntegrationDetails createJiraCredentials(JiraIntegrationDetails details) {
		logger.info("JiraIntegrationDetailsDaoImpl(createJiraCredentials) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			logger.debug("JiraIntegrationDetailsDaoImpl obj:" + new Gson().toJson(details));
			session.save(details);
			if (details.getId() != null) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("JiraIntegrationDetailsDaoImpl(createJiraCredentials) >> Exit ");
		return details;
	}

	@Override
	public JiraIntegrationDetails getJiraCredByOrgid(Long id) {
		logger.info("JiraIntegrationDetailsDaoImpl(getJiraCredByOrgid) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from JiraIntegrationDetails where is_deleted=:i and org_id=:o");
		query.setParameter("i", false);
		query.setParameter("o", id);
		List<JiraIntegrationDetails> details = query.getResultList();
		try {
			if (details != null && details.size() >0) {
				logger.info("JiraIntegrationDetailsDaoImpl(getJiraCredByOrgid) Exit>>-> ");
				return details.get(0);
			}else {
				logger.info("JiraIntegrationDetailsDaoImpl(getJiraCredByOrgid) Exit>>-> ");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("JiraIntegrationDetailsDaoImpl(getJiraCredByOrgid) Exit>>-> ");
		return null;
	}

	@Override
	public JiraIntegrationDetails updateJiraCred(JiraIntegrationDetails details) {
		logger.info("JiraIntegrationDetailsDaoImpl(updateJiraCred) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		details.setModified_time(new Date());
		try {
			logger.debug("JiraIntegrationDetailsDaoImpl obj:" + new Gson().toJson(details));
			session.save(details);
			if (details.getId() != null) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("JiraIntegrationDetailsDaoImpl(updateJiraCred) >> Exit ");
		return details;
	}

	@Override
	public boolean deleteJiraCred(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JiraIntegrationDetails getJiraCredByid(Long id) {
		logger.info("JiraIntegrationDetailsDaoImpl(getJiraCredByid) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from JiraIntegrationDetails where is_deleted=:i and id=:id");
		query.setParameter("i", false);
		query.setParameter("id", id);
		JiraIntegrationDetails details = (JiraIntegrationDetails) query.getSingleResult();
		try {
			if (details != null) {
				logger.info("JiraIntegrationDetailsDaoImpl(getJiraCredByid) ->Exit");
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("JiraIntegrationDetailsDaoImpl(getJiraCredByid) ->Exit");
		return null;
	}

	@Override
	public String getJiraProjects(Long orgid) {
		logger.info("JiraIntegrationDetailsDaoImpl(getJiraProjects) Entry>> Request -> " + orgid);
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery("select projects from jira_integration_credentials where is_deleted=:i and org_id=:id");
		query.setParameter("i", false);
		query.setParameter("id", orgid);
		String details =  (String) query.getSingleResult();
		if(details != null) {
			logger.info("JiraIntegrationDetailsDaoImpl(getJiraProjects) ->Exit");
			return details;
		}
		logger.info("JiraIntegrationDetailsDaoImpl(getJiraProjects) ->Exit");
		return null;
	}

}
