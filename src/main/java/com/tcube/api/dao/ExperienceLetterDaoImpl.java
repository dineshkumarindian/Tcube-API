package com.tcube.api.dao;
import java.text.SimpleDateFormat;
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

import com.google.gson.Gson;
import com.tcube.api.model.ExperienceLetterDetails;
import com.tcube.api.model.BusinessLetterDetails;

@Component
public class ExperienceLetterDaoImpl implements ExperienceLetterDao{
	
	@PersistenceContext
	private EntityManager entityManager;

	private static Logger logger = (Logger)LogManager.getLogger(ExperienceLetterDaoImpl.class);

	@Override
	public ExperienceLetterDetails createExperienceLetter(ExperienceLetterDetails experienceLetterDetails) {
		logger.info("ExperienceLetterDaoImpl(createExperienceLetter) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			experienceLetterDetails.setCreated_time(new Date());
			experienceLetterDetails.setModified_time(new Date());
			logger.debug("ExperienceLetterDetailsDaoImpl obj:"+new Gson().toJson(experienceLetterDetails));
			session.save(experienceLetterDetails);
			if(experienceLetterDetails.getId() != null) {
				entityManager.persist(experienceLetterDetails);
				return experienceLetterDetails;
			} else {
				entityManager.merge(experienceLetterDetails);
				return experienceLetterDetails;
			}
		// TODO Auto-generated method stub

		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.info("ExperienceLetterDaoImpl(createInternshipLetter)>> exit");	
		return experienceLetterDetails;
	}

	@Override
	public List<ExperienceLetterDetails> getActiveOrgIdInternsipDetail(Long id) {
		// TODO Auto-generated method stub
		logger.info("getActiveOrgIdExperienceDetails>>Entey");
		final Session session = entityManager.unwrap(Session.class);
		System.out.println(id);
		final Query query = session.createQuery("from ExperienceLetterDetails where orgDetails.org_id=:id and is_deleted =:k order by timestamp(modified_time) desc");
			query.setParameter("i", id);
			query.setParameter("k", false);
			@SuppressWarnings("unchecked")
			final List<ExperienceLetterDetails> details = query.getResultList();
			
			logger.info("experienceLetterDetailsDaoImpl(getExperienceDetailsByOrgId) Exit>>");
		return details;
	}

	@Override
	public ExperienceLetterDetails getById(Long id) {
		// TODO Auto-generated method stub
		logger.info("ExperienceLetterDetailsDao(getById)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		final ExperienceLetterDetails details = (ExperienceLetterDetails)session.get(ExperienceLetterDetails.class, id);
		logger.info("ExperienceLetterDetailsDao(getById)<<exit");
		return details;
		
	}

	@Override
	public ExperienceLetterDetails deleteExperienceDetails(ExperienceLetterDetails oldExperienceDetails) {
		logger.info("deleteExperienceDetailDao(deleteById)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		// TODO Auto-generated method stub
		try {
			oldExperienceDetails.setModified_time(new Date());
			logger.debug("the obj:"+new Gson().toJson(oldExperienceDetails));
			session.update(oldExperienceDetails);
			if(oldExperienceDetails.getId()== 0) {
				entityManager.persist(oldExperienceDetails);
				return oldExperienceDetails;
			} else {
				entityManager.merge(oldExperienceDetails);
				return oldExperienceDetails;
			}
			} catch(Exception e) {
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			logger.info("deleteExperienceDetailImpl(deleteIntenshipDetails) exit-->");
			return oldExperienceDetails;
	}

	@Override
	public ExperienceLetterDetails updateExperienceDetails(ExperienceLetterDetails oldExperienceDeatils) {
		// TODO Auto-generated method stub
		logger.info("ExperienceLetterDaoImpl(updateinternshipDetail)<< Entry");
		final Session session = entityManager.unwrap(Session.class);
		try{
			oldExperienceDeatils.setModified_time(new Date());
			logger.debug("appInfo Obj"+new Gson().toJson(oldExperienceDeatils));
//			session.update(internshipLetterDetails);
			if(oldExperienceDeatils.getId() == 0) {
				entityManager.persist(oldExperienceDeatils);
			} else {
				entityManager.merge(oldExperienceDeatils);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.info("ExperienceDetailDaoImpl(updateinternshipDetail)<<exit");
		return oldExperienceDeatils;
	}

	@Override
	public ExperienceLetterDetails deleteAllExperienceDetails(ExperienceLetterDetails experienceLetterDetails) {
		logger.info("ExperinceletterDaoImpl(deleteAllDetailsDaoImpl<<Entry)");// TODO Auto-generated method stub
		final Session session=entityManager.unwrap(Session.class);
		try {
			experienceLetterDetails.setModified_time(new Date());
			session.update(experienceLetterDetails);
			if(experienceLetterDetails.getId() == 0) {
				entityManager.persist(experienceLetterDetails);
				return experienceLetterDetails;
			} else {
				entityManager.merge(experienceLetterDetails);
				return experienceLetterDetails;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.info("ExperienceDetailsDaoImpl(deleteProjectDetails) Exit>>-> ");
		return experienceLetterDetails;
	}
	
	

}
