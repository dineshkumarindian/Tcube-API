package com.tcube.api.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.GitlabIntegrationDetails;
import com.tcube.api.model.JiraIntegrationDetails;

@Component
public class GitlabIntegrationDetailsDaoImpl implements GitlabIntegrationDetailsDao {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(JiraIntegrationDetailsDaoImpl.class);

	@Override
	public GitlabIntegrationDetails createGitlabCredentials(GitlabIntegrationDetails details) {
		logger.info("GitlabIntegrationDetailsDaoImpl(createGitlabCredentials) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			logger.debug("GitlabIntegrationDetailsDaoImpl obj:" + new Gson().toJson(details));
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
		logger.info("GitlabIntegrationDetailsDaoImpl(createGitlabCredentials) >> Exit ");
		return details;
	}

	@Override
	public GitlabIntegrationDetails getGitlabDetailsByOrgid(Long id) {
		logger.info("GitlabIntegrationDetailsDaoImpl(getGitlabDetailsByOrgid) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from GitlabIntegrationDetails where is_deleted=:i and org_id=:o");
		query.setParameter("i", false);
		query.setParameter("o", id);
		GitlabIntegrationDetails details = (GitlabIntegrationDetails) query.getSingleResult();
		try {
			if (details != null) {
				logger.info("GitlabIntegrationDetailsDaoImpl(getGitlabDetailsByOrgid) Exit>>-> ");
				return details;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("GitlabIntegrationDetailsDaoImpl(getGitlabDetailsByOrgid) Exit>>-> ");
		return null;
	}

	@Override
	public GitlabIntegrationDetails getGitLabDetailsByid(Long id) {
		logger.info("GitlabIntegrationDetailsDaoImpl(getGitLabDetailsByid) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from GitlabIntegrationDetails where is_deleted=:i and id=:id");
		query.setParameter("i", false);
		query.setParameter("id", id);
		GitlabIntegrationDetails details = (GitlabIntegrationDetails) query.getSingleResult();
		try {
			if (details != null) {
				logger.info("GitlabIntegrationDetailsDaoImpl(getGitLabDetailsByid) ->Exit");
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("GitlabIntegrationDetailsDaoImpl(getGitLabDetailsByid) ->Exit");
		return null;
	}

	@Override
	public GitlabIntegrationDetails updateGitLabDetails(GitlabIntegrationDetails details) {
		logger.info("GitlabIntegrationDetailsDaoImpl(updateGitLabDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		details.setModified_time(new Date());
		try {
			logger.debug("GitlabIntegrationDetailsDaoImpl obj:" + new Gson().toJson(details));
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
		logger.info("GitlabIntegrationDetailsDaoImpl(updateGitLabDetails) >> Exit ");
		return details;
	}
}