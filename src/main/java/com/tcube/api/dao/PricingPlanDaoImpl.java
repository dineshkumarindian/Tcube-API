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
import com.tcube.api.model.PricingPlanDetails;
import com.tcube.api.model.TimeTrackerDetails;

@Component
public class PricingPlanDaoImpl implements PricingPlanDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(PricingPlanDaoImpl.class);
	
	@Override
	public PricingPlanDetails create(PricingPlanDetails details) {
		logger.info("PricingPlanDaoImpl(create) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setCreated_time(new Date());
			details.setModified_time(new Date());
			
			logger.debug("PricingPlanDaoImpl obj:" + new Gson().toJson(details));
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
		logger.info("PricingPlanDaoImpl(create) >> Exit ");
		return details;
	}

	@Override
	public List<PricingPlanDetails> getAllPlanDetails() {
		logger.info("PricingPlanDaoImpl (getAllPlanDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session
				.createQuery("from PricingPlanDetails where is_deleted=:i");
		query.setParameter("i", false);
		List<PricingPlanDetails> details = query.getResultList();
		logger.info("PricingPlanDaoImpl (getAllPlanDetails) >> Exit ");
		return details;
	}

	@Override
	public PricingPlanDetails getPlanDetailsById(Long id) {
		logger.info("PricingPlanDaoImpl(getPlanDetailsById) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final PricingPlanDetails details = (PricingPlanDetails) session.get(PricingPlanDetails.class, id);
		logger.info("PricingPlanDaoImpl(getPlanDetailsById) Exit>>-> ");
		return details;
	}

	@Override
	public PricingPlanDetails updatePlanDetails(PricingPlanDetails details) {
		logger.info("PricingPlanDaoImpl(updatePlanDetails) Entry>> Request -> " + details.getId());
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setModified_time(new Date());
			session.update(details);
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
		logger.info("PricingPlanDaoImpl(updatePlanDetails) Entry>> Request -> " + details.getId());
		return null;
	}

	@Override
	public PricingPlanDetails deletePlanDetails(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int bulkDelete(JSONArray ids) {
		logger.info("PricingPlanDaoImpl(bulkDelete) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}

		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session.createNativeQuery("update pricing_plan_details set is_deleted=:i where id in (" + id_sb + ")");
		query.setParameter("i", true);
		int details = query.executeUpdate();
		logger.info("PricingPlanDaoImpl(bulkDelete) Exit>>");
		return details;
	}

}
