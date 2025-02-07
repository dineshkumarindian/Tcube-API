package com.tcube.api.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.DesignationDetails;

@Component
public class DesignationDetailsDaoImpl implements DesignationDetailsDao{

	
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(DesignationDetailsDaoImpl.class);


	@Override
	public DesignationDetails createDesignation(DesignationDetails designationDetails) {
		logger.info("DesignationDetailsDaoImpl(createDesignation) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			designationDetails.setCreated_time(new Date());
			designationDetails.setModified_time(new Date());
			session.save(designationDetails);
			if (designationDetails.getId() == 0) {
				entityManager.persist(designationDetails);
				return designationDetails;
			} else {
				entityManager.merge(designationDetails);
				return designationDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("DesignationDetailsDaoImpl(createDesignation) >> Entry ");
		return designationDetails;
	}


	@Override
	public DesignationDetails getDesignationById(Long id) {
		logger.info("DesignationDetailsDaoImpl(getDesignationById) >> Entry Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final DesignationDetails details = (DesignationDetails) session.get(DesignationDetails.class, id);
		logger.info("DesignationDetailsDaoImpl(getDesignationById) >> Exit");
		return details;
	}


	@Override
	public DesignationDetails updateDesignation(DesignationDetails newDetails) {
		logger.info("DesignationDetailsDaoImpl(updateDesignation) >> Entry");
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
	    logger.info("DesignationDetailsDaoImpl(updateDesignation) >> Exit");
		return newDetails;
	}


	@Override
	public DesignationDetails deleteDesignation(DesignationDetails newDetails) {
		logger.info("DesignationDetailsDaoImpl(deleteDesignation) >> Entry");
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
	    logger.info("DesignationDetailsDaoImpl(deleteDesignation) >> Exit");
		return newDetails;
	}


	@Override
	public List<DesignationDetails> getAllDesignation() {
		logger.info("DesignationDetailsDaoImpl (getAllDesignation) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from DesignationDetails");
		List<DesignationDetails> details = query.getResultList();
		logger.info("DesignationDetailsDaoImpl (getAllDesignation) >>  Exit ");
		return details;
	}


	@Override
	public List<DesignationDetails> getAllDesignationByOrgId(Long id) {
		logger.info("DesignationDetailsDaoImpl(getAllDesignationByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from DesignationDetails where orgDetails.id=:id order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<DesignationDetails> details = query.getResultList();
		logger.info("DesignationDetailsDaoImpl(getAllDesignationByOrgId) >> Exit");
		return details;
	}
	
	@Override
	public DesignationDetails getDesignationByName(String details,Long id) {
		logger.info("DesignationDetailsDaoImpl(getDesignationByName) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from DesignationDetails where  designation=:k and orgDetails.id=:id and is_deleted=:j order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", details);
		query.setParameter("j", false);
		@SuppressWarnings("unchecked")
		 DesignationDetails details1 = new DesignationDetails();
		try {
			details1 = (DesignationDetails) query.getSingleResult();
		   return details1;
		}
		catch(Exception e) {
			
		}
		logger.info("DesignationDetailsDaoImpl(getDesignationByName) >> Exit");
		return null;
	}

	@Override
	public int bulkDelete(JSONArray ids) {
		logger.info("DesignationDetailsDaoImpl(bulkDelete) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}

		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session.createNativeQuery("update designation_details set is_deleted=:i where id in (" + id_sb + ")");
		query.setParameter("i", true);
//		query.setParameter("j", true)
		int details = query.executeUpdate();
		logger.info("DesignationDetailsDaoImpl(bulkDelete) >> Exit");
		return details;
	}
	
}
