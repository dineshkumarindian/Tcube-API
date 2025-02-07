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
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.RoleDetails;
import org.json.JSONArray;

@Component
public class RoleDetailsDaoImpl implements RoleDetailsDao {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(RoleDetailsDaoImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public RoleDetails createRoleDetails(RoleDetails roledetails) {
		logger.info("RoleDetailsDaoImpl(createRoleDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			roledetails.setCreated_time(new Date());
			roledetails.setModified_time(new Date());
			session.save(roledetails);
			if (roledetails.getId() == 0) {
				entityManager.persist(roledetails);
				return roledetails;
			} else {
				entityManager.merge(roledetails);
				return roledetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("RoleDetailsDaoImpl(createRoleDetails) >> Entry ");
		return roledetails;
	}

	@Override
	public RoleDetails getRoleDetailsById(Long id) {
		logger.info("RoleDetailsDaoImpl(getRoleDetailsById) >> Entry Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final RoleDetails details = (RoleDetails) session.get(RoleDetails.class, id);
		logger.info("RoleDetailsDaoImpl(getRoleDetailsById) >> Exit");
		return details;
	}

	@Override
	public RoleDetails updateRoleDetails(RoleDetails newDetails) {
		logger.info("RoleDetailsDaoImpl(updateRoleDetails) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
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
		logger.info("RoleDetailsDaoImpl(updateRoleDetails) >> Exit");
		return newDetails;
	}

	@Override
	public RoleDetails deleteRoleDetails(RoleDetails newRoleetails) {
		logger.info("RoleDetailsDaoImpl(deleteRoleDetails) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {

			newRoleetails.setModified_time(new Date());
			logger.debug("appInfo obj:" + new Gson().toJson(newRoleetails));
			session.update(newRoleetails);
			if (newRoleetails.getId() == 0) {
				entityManager.persist(newRoleetails);
				return newRoleetails;
			} else {
				entityManager.merge(newRoleetails);
				return newRoleetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("RoleDetailsDaoImpl(deleteRoleDetails) >> Exit");
		return newRoleetails;
	}

	@Override
	public List<RoleDetails> getAllRoleDetails() {
		logger.info("RoleDetailsDaoImpl (getAllRoleDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from RoleDetails");
		List<RoleDetails> details = query.getResultList();
		logger.info("RoleDetailsDaoImpl (getAllRoleDetails) >> Exit ");
		return details;
	}

	@Override
	public List<RoleDetails> getRoleDetailsByOrgId(Long id) {
		logger.info("RoleDetailsDaoImpl(getRoleDetailsByClientId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from RoleDetails where orgDetails.id=:id order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<RoleDetails> details = query.getResultList();
		logger.info("RoleDetailsDaoImpl(getRoleDetailsByClientId) >> Exit");
		return details;
	}

	public RoleDetails getRoleByName(String details, Long id) {
		logger.info("RoleDetailsDaoImpl(getRoleByName) >> Entry");
		final Session session = entityManager.unwrap(Session.class);

		final Query query = session.createQuery(
				"from RoleDetails where orgDetails.id=:i and role =: j and  is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("i", id);
		query.setParameter("j", details);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked")
		RoleDetails details1 = new RoleDetails();
		try {
		details1 = (RoleDetails) query.getSingleResult();
		return  details1;
		
		} catch (Exception e) {
		}
		logger.info("RoleDetailsDaoImpl(getRoleByName) >> Exit");
		return null;
	}

	@Override
	public RoleDetails getRoleDetailsByOrgidAndRoleId(Long orgid, Long roleid) {
		logger.info("RoleDetailsDaoImpl(getRoleDetailsByOrgidAndRoleId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from RoleDetails where org_id=:i and id=:j");
		query.setParameter("i", orgid);
		query.setParameter("j", roleid);
		List<RoleDetails> details = query.getResultList();
		RoleDetails role = details.get(0);
		logger.info("RoleDetailsDaoImpl(getRoleDetailsByOrgidAndRoleId) Exit");
		return role;
	}

	@Override
	public RoleDetails upgradeRoledetailsForOrgadmin(String roles, Long org_id) {
		logger.info("RoleDetailsDaoImpl(getRoleDetailsByOrgidAndRoleId) Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from RoleDetails where org_id=:i and role=:j");
		query.setParameter("i", org_id);
		query.setParameter("j", "OrgAdmin");
		List<RoleDetails> details = query.getResultList();
		RoleDetails newDetails = details.get(0);
		try {
			newDetails.setAccess_to(roles);
			newDetails.setModified_time(new Date());
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
		logger.info("RoleDetailsDaoImpl(upgradeRoledetailsForEmployees) Exit");
		return newDetails;
	}

	@Override
	public RoleDetails upgradeRoledetailsForEmployees(Long id, String roles) {
		logger.info("RoleDetailsDaoImpl(upgradeRoledetailsForEmployees) Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from RoleDetails where id=:i and is_deleted=:j");
		query.setParameter("i", id);
		query.setParameter("j", false);
		List<RoleDetails> details = query.getResultList();
		RoleDetails newDetails = details.get(0);
		try {
			newDetails.setAccess_to(roles);
			newDetails.setModified_time(new Date());
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
		logger.info("RoleDetailsDaoImpl(upgradeRoledetailsForEmployees) >> Exit");
		return newDetails;
	}

	// Role bulk delete
	@Override
	public int bulkDelete(JSONArray ids) {
		logger.info("RoleDetailsDaoImpl(bulkDelete) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}

		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session
				.createNativeQuery("update role_details set is_deleted=:i where id in (" + id_sb + ")");
		query.setParameter("i", true);
//		query.setParameter("j", true)
		int details = query.executeUpdate();
		logger.info("RoleDetailsDaoImpl(bulkDelete) >> Exit");
		return details;
	}

}
