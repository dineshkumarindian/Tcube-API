package com.tcube.api.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.tcube.api.model.JobAssigneeDetails;
import com.tcube.api.model.ProjectResourceDetails;

@Component
public class ProjectResourceDaoImpl implements ProjectResourceDao {
	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(ProjectResourceDaoImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public ProjectResourceDetails updateProjectreferenceId(Long Proj_id) {
		logger.info("ProjectResourceDetails (updateProjectreferenceId) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery(
				"from ProjectResourceDetails where project_id=:i");
		query.setParameter("i", Proj_id);
		List<ProjectResourceDetails> details = query.getResultList();
		ProjectResourceDetails data  = new ProjectResourceDetails();
		if(details != null) {
			for(int i=0;i<details.size();i++) {
				data = details.get(i);
				 data.setRef_projectid(Proj_id);
				if (data.getId() == 0) {
					entityManager.persist(data);
				} else {
					entityManager.merge(data);
				}
			}
		}
		logger.info("ProjectResourceDetails (updateProjectreferenceId) >> Exit ");
		return data;
	}
	
	

}
