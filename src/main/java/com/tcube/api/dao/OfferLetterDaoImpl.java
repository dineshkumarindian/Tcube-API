package com.tcube.api.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.tcube.api.model.DashboardOfferLetter;
import com.tcube.api.model.OfferLetterDetails;

@Component
public class OfferLetterDaoImpl implements OfferLetterDao {

	@PersistenceContext
	private EntityManager entityManager;

	private static Logger logger = (Logger) LogManager.getLogger(OfferLetterDaoImpl.class);

	@Override
	public OfferLetterDetails createOfferLetter(OfferLetterDetails offerLetterDetails) {
		logger.info("OfferLetterDaoImpl(createOfferLetter) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			offerLetterDetails.setCreated_time(new Date());
			offerLetterDetails.setModified_time(new Date());
			logger.debug("OfferLetterDaoImpl obj:" + new Gson().toJson(offerLetterDetails));
			session.save(offerLetterDetails);
			if (offerLetterDetails.getId() != null) {
				entityManager.persist(offerLetterDetails);
				return offerLetterDetails;
			} else {
				entityManager.merge(offerLetterDetails);
				return offerLetterDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("OfferLetterDaoImpl(createOfferLetter)>> Exit");
		return offerLetterDetails;
	}

	@Override
	public List<OfferLetterDetails> getAllOfferLetterDetails(Long id) {
		logger.info("OfferLetterDaoImpl (getAllOfferLetterDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from OfferLetterDetails  where orgDetails.id=:id order by timestamp(modified_time) desc");
		query.setParameter("id",id);
		@SuppressWarnings("unchecked")
		List<OfferLetterDetails> details = query.getResultList();
		logger.info("OfferLetterDaoImpl (getAllOfferLetterDetails) >> Exit ");
		return details;

	}

	@Override
	public List<OfferLetterDetails> getActiveEmpWithOfferByOrgId(Long id) {
		logger.info("OfferLetterDaoImpl (getActiveEmpWithOfferLetter)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from OfferLetterDetails where orgDetails.id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked")
		final List<OfferLetterDetails> details = query.getResultList();
		logger.info("OfferLetterDaoImpl(getActiveEmpWithOfferLetter) Exit>>");
		return details;
	}
	
	@Override
	public List<OfferLetterDetails>  getOfferLetterCount(Long id) {			
		logger.info("OfferLetterDaoImpl (getOfferLetterCount)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"select count(*) from OfferLetterDetails  where orgDetails.id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);		
		@SuppressWarnings("unchecked")
		final List<OfferLetterDetails> details = query.getResultList();
		logger.info("OfferLetterDaoImpl(getOfferLetterCount) Exit>>");
		return details;
	}
	
	@Override
	public List<DashboardOfferLetter>  getAddedOfferLetter(Long id) {			
		logger.info("OfferLetterDaoImpl (getAddedOfferLetter)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
//		final Query query = session.createQuery("select name,dob from OfferLetterDetails where orgDetails.id=:id and is_deleted =:k order by id desc");
		final Query query = session.createQuery(
				"from OfferLetterDetails where orgDetails.id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setMaxResults(3);
		query.setParameter("id", id);
		query.setParameter("k", false);		
		@SuppressWarnings("unchecked")
		 List<OfferLetterDetails> details = query.getResultList();
	    List<DashboardOfferLetter> data = new ArrayList<>();
	    for(int i=0;i<details.size();i++) {
	    	DashboardOfferLetter temp = new DashboardOfferLetter();
	    	temp.setName(details.get(i).getName());
	    	temp.setDob(details.get(i).getDob());
	    	data.add(temp);
	    }
		logger.info("OfferLetterDaoImpl(getAddedOfferLetter) Exit>>");
		return data;
	}
	
	@Override
	public OfferLetterDetails getById(Long id) {
		logger.info("OfferLetterDaoImpl(getById)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		final OfferLetterDetails details = (OfferLetterDetails) session.get(OfferLetterDetails.class, id);
		logger.info("OfferLetterDaoImpl(getById)>>Exit");
		return details;
	}

	@Override
	public OfferLetterDetails deleteOfferLetterDetails(OfferLetterDetails oldDetails) {
		logger.info("OfferLetterDaoImpl(deleteOfferLetterDetails)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			oldDetails.setModified_time(new Date());
			logger.debug("the obj:" + new Gson().toJson(oldDetails));
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
		
		logger.info("OfferLetterDaoImpl(deleteOfferLetterDetails) Exit-->");
		return oldDetails;
	}

	@Override
	public OfferLetterDetails updateOfferLetterDetails(OfferLetterDetails offerDetails) {
		final Session session = entityManager.unwrap(Session.class);
		logger.info("OfferLetterDaoImpl(updateOfferLetterDetails)<< Entry");
		try {
			offerDetails.setModified_time(new Date());
			logger.debug("appInfo Obj" + new Gson().toJson(offerDetails));
//			session.update(offerDetails);
			if (offerDetails.getId() == 0) {
				entityManager.persist(offerDetails);
			} else {
				entityManager.merge(offerDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("OfferLetterDaoImpl(updateOfferLetterDetails)<<Exit");
		return offerDetails;
	}
	@Override
	public OfferLetterDetails deleteAllOfferLetterDetails(OfferLetterDetails oldDetails) {
		logger.info("OfferLetterDaoImpl(deleteAllOfferLetterDetails)>>Entry");
		final Session session= entityManager.unwrap(Session.class);
		try {
			oldDetails.setModified_time(new Date());
			logger.debug("appInfo obj:" + new Gson().toJson(oldDetails));
			session.update(oldDetails);
			if(oldDetails.getId() == 0) {
				entityManager.persist(oldDetails);
				return oldDetails;
			} else {
				entityManager.merge(oldDetails);
				return oldDetails;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.info("OfferLetterDaoImpl(deleteAllOfferLetterDetails) Exit>>-> ");
		return oldDetails;
	}


}
