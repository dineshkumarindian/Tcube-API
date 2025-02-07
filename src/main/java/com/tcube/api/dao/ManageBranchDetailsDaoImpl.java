package com.tcube.api.dao;

import java.util.List;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.google.gson.Gson;
import com.tcube.api.model.ManageBranchDetails;

@Component
public class ManageBranchDetailsDaoImpl implements ManageBranchDetailsDao{

	@PersistenceContext
	private EntityManager entityManager;
	
	private static Logger logger =  LogManager.getLogger(ManageBranchDetailsDaoImpl.class);
	
	@Override
	public ManageBranchDetails create(ManageBranchDetails data) {
		logger.info("ManageBranchDetailsDaoImpl(create) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			data.setCreatedTime(new Date());
			data.setModifiedTime(new Date());
			session.save(data);
			if (data.getId() != null) {
				entityManager.persist(data);
				return data;
			} else {
				entityManager.merge(data);
				return data;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("ManageBranchDetailsDaoImpl(create) >> Exit");
		return data;
	}

	@Override
	public List<ManageBranchDetails> getAll() {
		logger.info("ManageBranchDetailsDaoImpl (getAll) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked") 
		final Query query = session.createQuery("from ManageBranchDetails where is_deleted=:i");
		query.setParameter("i", false);
		List<ManageBranchDetails> details = query.getResultList();
		logger.info("ManageBranchDetailsDaoImpl (getAll) >> Exit ");
		return details;
	}

	@Override
	public List<ManageBranchDetails> getBranchesByOrgId(Long id) {
		logger.info("ManageBranchDetailsDaoImpl (getBranchesByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from ManageBranchDetails where is_deleted=:i and orgid=:id");
		query.setParameter("i", false);
		query.setParameter("id", id);
		List<ManageBranchDetails> details = query.getResultList();
		// System.out.println(details.size());
		if(details.size() > 0 && details != null) {
			logger.info("ManageBranchDetailsDaoImpl (getBranchesByOrgId) >> Exit");
			return details;
		}
		else {
			logger.info("ManageBranchDetailsDaoImpl (getBranchesByOrgId) >> Exit");
			return null;
		}
	}

	@Override
	public ManageBranchDetails getBranchById(Long id) {
		logger.info("ManageBranchDetailsDaoImpl(getBranchById) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final ManageBranchDetails details = (ManageBranchDetails) session.get(ManageBranchDetails.class, id);
		logger.info("ManageBranchDetailsDaoImpl(getBranchById) Exit>>-> ");
		return details;
	}

	@Override
	public ManageBranchDetails getBranchByName(String empBranch, Long orgId) {
		logger.info("ManageBranchDetailsDaoImpl (getBranchByName) >> Entry");
		final Session session = entityManager.unwrap(Session.class);

		final Query query = session.createQuery(
				"from ManageBranchDetails where orgid=:i and branch =: j and  is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("i", orgId);
		query.setParameter("j", empBranch);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked")
		ManageBranchDetails details1 = new ManageBranchDetails();
		try {
		details1 = (ManageBranchDetails) query.getSingleResult();
		return  details1;
		
		} catch (Exception e) {
		}
		logger.info("ManageBranchDetailsDaoImpl(getBranchByName) >> Exit");
		return null;
	}

	@Override
	public int deleteBranchbynameandorgid(String branchName, long id) {
		logger.info("JobDetailsDaoImpl(removeJobAssigneeByEmployeeId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery("update manage_branch_details set is_deleted=:j where branch_name =:k and org_id =:org");
		query.setParameter("j", true);
		query.setParameter("k", branchName);
		query.setParameter("org", id);
		@SuppressWarnings("unchecked")
		int details = query.executeUpdate();
		logger.info("JobDetailsDaoImpl(removeJobAssigneeByEmployeeId) >>Exit");
		return details;
	}
	
	@Override
	public ManageBranchDetails updateBranchDetails(ManageBranchDetails details) {
		final Session session = entityManager.unwrap(Session.class);
		entityManager.clear();
		logger.info("ManageBranchDetailsDaoImpl(updateBranchDetails) >> Entry");
		try {
			details.setModifiedTime(new Date());
			logger.debug("appInfo obj:" + new Gson().toJson(details));
			session.clear();
			session.update(details);
			if (details.getId() != null) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}		
	} catch(Exception e) {
		e.printStackTrace();
	}
		logger.info("ManageBranchDetailsDaoImpl(updateBranchDetails) >> Exit");
		return details;
	}

}
