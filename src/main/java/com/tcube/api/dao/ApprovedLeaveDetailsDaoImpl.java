package com.tcube.api.dao;

import java.math.BigInteger;
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
import com.tcube.api.model.ApprovedLeaveDetails;
import com.tcube.api.model.ClientDetails;
import com.tcube.api.model.ManageLeaveTypes;
import com.tcube.api.service.ManageLeaveTypesService;

@Component
public class ApprovedLeaveDetailsDaoImpl implements ApprovedLeaveDetailsDao{

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	ManageLeaveTypesService manageLeaveTypesService; 
	
	private static Logger logger = (Logger) LogManager.getLogger(ApprovedLeaveDetailsDaoImpl.class);

	@Override
	public ApprovedLeaveDetails createApprovedLeaveDetails(ApprovedLeaveDetails details) {
		logger.info("ApprovedLeaveDetailsDaoImpl(createApprovedLeaveDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setCreated_time(new Date());
			details.setModified_time(new Date());
			logger.debug("ApprovedLeaveDetailsDaoImpl obj:" + new Gson().toJson(details));
			session.save(details);
			if (details.getId() != null) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		logger.info("ApprovedLeaveDetailsDaoImpl(createApprovedLeaveDetails) >> Exit ");

		return details;
	}

	@Override
	public List<ApprovedLeaveDetails> getapprovedLeaveDetailsByEmpIdAndLTId(ApprovedLeaveDetails newDetails) {
		logger.info("ApprovedLeaveDetailsDaoImpl(getapprovedLeaveDetailsByEmpIdAndLTId) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ApprovedLeaveDetails where orgDetails.org_id=:id and emp_id =:k and leaveTypeDetails.id =:j order by timestamp(modified_time) desc");
		query.setParameter("id", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("k", newDetails.getEmp_id());
		query.setParameter("j", newDetails.getLeaveTypeDetails().getId());
		@SuppressWarnings("unchecked")
		final List<ApprovedLeaveDetails> details = query.getResultList();
		logger.info("ApprovedLeaveDetailsDaoImpl(getapprovedLeaveDetailsByEmpIdAndLTId) Exit>>");
		return details;
	}

	@Override
	public Double getapprovedLeaveCountsByEmpIdAndLTId(ApprovedLeaveDetails newDetails) {
		logger.info("ApprovedLeaveDetailsDaoImpl(getapprovedLeaveDetailsByEmpIdAndLTId) Entry>>");
		Double Count = (double) 0;
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ApprovedLeaveDetails where orgDetails.org_id=:id and emp_id =:k and leaveTypeDetails.id =:j order by timestamp(modified_time) desc");
		query.setParameter("id", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("k", newDetails.getEmp_id());
		query.setParameter("j", newDetails.getLeaveTypeDetails().getId());
		@SuppressWarnings("unchecked")
		final List<ApprovedLeaveDetails> details = query.getResultList();
		for(ApprovedLeaveDetails i : details) {
			Count += i.getTotal_days();
		}
		System.out.println(Count);
		logger.info("ApprovedLeaveDetailsDaoImpl(getapprovedLeaveDetailsByEmpIdAndLTId) Exit>>");
		return Count;
	}
	
	
}
