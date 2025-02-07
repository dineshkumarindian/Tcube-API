package com.tcube.api.dao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.ClientDetails;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.ProjectDetails;
import com.tcube.api.model.RoleDetails;
import com.tcube.api.model.SuperAdminDashboard;
import com.tcube.api.service.JobDetailsService;
import com.tcube.api.service.ProjectDetailsService;
import com.tcube.api.utils.EncryptorUtil;

@Component
public class ClientDetailsDaoImpl implements ClientDetailsDao{

	@PersistenceContext
	private EntityManager entityManager;
 
	@Autowired
	ProjectDetailsService projectDetailsService;
	
	@Autowired
	JobDetailsService jobDetailsService;

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(ClientDetailsDaoImpl.class);


	@Override
	public ClientDetails createClientDetails(ClientDetails clientdetails) {
		logger.info("ClientDetailsDaoImpl(createClientDetails) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			clientdetails.setCreated_time(new Date());
			clientdetails.setModified_time(new Date());
			session.save(clientdetails);
			if (clientdetails.getId() == 0) {
				entityManager.persist(clientdetails);
				return clientdetails;
			} else {
				entityManager.merge(clientdetails);
				return clientdetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("ClientDetailsDaoImpl(createClientDetails) >> Exit ");
		return clientdetails;
	}


	@Override
	public ClientDetails getClientById(Long id) {
		logger.info("ClientDetailsDaoImpl(getClientById) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final ClientDetails details = (ClientDetails) session.get(ClientDetails.class, id);
		logger.info("ClientDetailsDaoImpl(getClientById) >> Exit");
		return details;
	}


	@Override
	public ClientDetails updateClientDetails(ClientDetails clientDetails) {
		logger.info("ClientDetailsDaoImpl(updateClientDetails) >> Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	clientDetails.setModified_time(new Date());
	    	logger.debug("appInfo obj:" + new Gson().toJson(clientDetails));
			session.update(clientDetails);
			if (clientDetails.getId() == 0) {
				entityManager.persist(clientDetails);
				return clientDetails;
			} else {
				entityManager.merge(clientDetails);
				return clientDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    logger.info("ClientDetailsDaoImpl(updateClientDetails) >> Exit");
		return clientDetails;
	}


	@Override
	public ClientDetails deleteClientDetails(ClientDetails oldDetails) {
		 logger.info("ClientDetailsDaoImpl(deleteClientDetails) >> Entry");
			final Session session= entityManager.unwrap(Session.class);
		    try {
		    	
		    	oldDetails.setModified_time(new Date());
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
		    logger.info("ClientDetailsDaoImpl(deleteClientDetails) >> Exit");
			return oldDetails;
	}


	@Override
	public List<ClientDetails> getAllClientDetails() {
		logger.info("ClientDetailsDaoImpl (getAllClientDetails) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from ClientDetails");
		List<ClientDetails> details = query.getResultList();
		logger.info("ClientDetailsDaoImpl (getAllClientDetails) >> Exit");
		return details;
	}


	@Override
	public List<ClientDetails> getClientDetailsByOrgId(Long id) {
		logger.info("ClientDetailsDaoImpl(getClientDetailsByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ClientDetails where orgDetails.id=:id order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<ClientDetails> details = query.getResultList();
		logger.info("ClientDetailsDaoImpl(getClientDetailsByOrgId) >> Exit");
		return details;
	}
	
	@Override
	public List<ClientDetails> getAllActiveClientReportsByOrgId(Long id) {
		logger.info("ClientDetailsDaoImpl(getAllActiveClientReportsByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ClientDetails where orgDetails.org_id=:id and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("j", true);
		@SuppressWarnings("unchecked")
		final List<ClientDetails> details = query.getResultList();
		logger.info("ClientDetailsDaoImpl(getAllActiveClientReportsByOrgId) >> Exit");
		return details;
	}
	
	@Override
	public List<ClientDetails> getInactiveClientDetailsByOrgID(Long id) {
		logger.info("ClientDetailsDaoImpl(getInactiveClientDetailsByOrgID) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ClientDetails where orgDetails.id=:id and is_deleted =:k and is_activated =:j order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("j", false);
		@SuppressWarnings("unchecked")
		final List<ClientDetails> details = query.getResultList();
		logger.info("ClientDetailsDaoImpl(getInactiveClientDetailsByOrgID) >> Exit");
		return details;
	}
	@Override
	public JSONObject getTotalCPJByOrgId(Long id) {
		logger.info("ClientDetailsDaoImpl(getTotalCPJByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ClientDetails where orgDetails.id =:id and is_deleted =:k and is_activated=:j order by timestamp(modified_time) desc");
		final Query query1 = session.createQuery(
				"from ProjectDetails where orgDetails.id =:id and is_deleted =:k and is_activated=:j and project_status=: s order by timestamp(modified_time) desc");
		final Query query2 = session.createQuery(
				"from JobDetails where orgDetails.id=:id and is_deleted =:k and is_activated=:j and job_status=: s order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		query.setParameter("j", true);
		query1.setParameter("id", id);
		query1.setParameter("k", false);
		query1.setParameter("j", true);
		query1.setParameter("s", "Inprogress");
		query2.setParameter("id", id);
		query2.setParameter("k", false);
		query2.setParameter("j", true);
		query2.setParameter("s", "Inprogress");

		@SuppressWarnings("unchecked")
		List<ClientDetails> clientCount = query.getResultList();
		@SuppressWarnings("unchecked")
		List<ProjectDetails> projCount = query1.getResultList();		
		@SuppressWarnings("unchecked")
		List<JobDetails> jobCount = query2.getResultList();
		
		int clientDashboardCount = clientCount.size();
		int projectDashboardCount = projCount.size();
		int jobDashboardCount = jobCount.size();
		
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("clientCountDetails", clientDashboardCount);
		jsonObject.put("projectCountdetails", projectDashboardCount);
		jsonObject.put("jobCountdetails", jobDashboardCount);
		logger.info("ClientDetailsDaoImpl(getTotalCPJByOrgId) >> Exit");
		return jsonObject;
	}


	@Override
	public int bulkDelete(JSONArray ids) {
		logger.info("ClientDetailsDaoImpl(bulkDelete) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}
		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session.createNativeQuery("update client_details set is_deleted=:i where id in (" + id_sb + ")");
		query.setParameter("i", true);
		int details = query.executeUpdate();
		logger.info("ClientDetailsDaoImpl(bulkDelete) >> Exit");
		return details;
		// TODO Auto-generated method stub
//		return 0;
	}


	@Override
	public int bulkDeactive(JSONArray ids, String action) {
		int clientDetails = 0;
		logger.info("ClientDetailsDaoImpl(bulkDeactive) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}
		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		if(action.equals("activated")) {
		final Query query = session.createNativeQuery("update client_details set is_activated=:i and is_deleted =:k where id in (" + id_sb + ")");
		query.setParameter("i", false);
		query.setParameter("k",false);
		clientDetails = query.executeUpdate();
		logger.info("ClientDetailsDaoImpl(bulkDeactive) >> Exit");
		} 
		return clientDetails;
		// TODO Auto-generated method stub
//		return 0;
	}


	@Override
	public int bulkActivate(JSONArray ids, String action) {
		// TODO Auto-generated method stub
		int clientDetails = 0;
		logger.info("ClientDetailsDaoImpl(bulkActivate) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}
		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		if(action.equals("deactivated")) {
			final Query query = session.createNativeQuery("update client_details set is_activated=:i and is_deleted =:k where id in (" + id_sb + ")");
			query.setParameter("i", true);
			query.setParameter("k",false);
			clientDetails = query.executeUpdate();
			logger.info("ClientDetailsDaoImpl(bulkActivate) >> Exit");
		}
		
		return clientDetails;
	}
}
