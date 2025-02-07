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
import com.tcube.api.model.CustomInternshipDetails;
import com.tcube.api.model.DashboardBusinessLetter;
import com.tcube.api.model.DashboardOfferLetter;
import com.tcube.api.model.OfferLetterDetails;
import com.tcube.api.model.BusinessLetterDetails;


@Component
public  class BusinessLetterDaoImpl implements  BusinessLetterDao{
	@PersistenceContext
	private EntityManager entityManager;
	
	private static Logger logger = (Logger)LogManager.getLogger(BusinessLetterDaoImpl.class);

	@Override
	public BusinessLetterDetails createBusinessLetter(BusinessLetterDetails businessLetterDetails) {
		logger.info("BusinessLetterDaoImpl(createBusinessLetter) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			businessLetterDetails.setCreated_time(new Date());
			businessLetterDetails.setModified_time(new Date());
			logger.debug("businessLetterDetailsDaoImpl obj:"+new Gson().toJson(businessLetterDetails));
			session.save(businessLetterDetails);
			if(businessLetterDetails.getId() != null)     {
				entityManager.persist(businessLetterDetails);
				return businessLetterDetails;
			} else {
				entityManager.merge(businessLetterDetails);
				return businessLetterDetails;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.info("BusinessLetterDaoImpl(createBusinessLetter)>> exit");	
		// TODO Auto-generated method stub
		return businessLetterDetails;
	}

	@Override
	public List<BusinessLetterDetails> getAllBusinessDetails(Long id) {
		// TODO Auto-generated method stub
		logger.info("BusinessLetterDaoImpl(getAllBusinessDetails)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from BusinessLetterDetails where orgDetails.org_id=:id order by timestamp(modified_time) desc");
		query.setParameter("id",id);	
		@SuppressWarnings("unchecked")
		List<BusinessLetterDetails>  details= query.getResultList();
		logger.info("BusinessLetterDaoImpl(getAllBusinessDetails)>>Entry");
		return details;
	}

	@Override
	public BusinessLetterDetails getById(Long id) {
		// TODO Auto-generated method stub
		logger.info("BusinessLetterDaoImpl(getById)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		final BusinessLetterDetails details = (BusinessLetterDetails) session.get(BusinessLetterDetails.class, id);
		logger.info("BusinessLetterDaoImpl(getById)<<exit");
		return details;
	}

	@Override
	public BusinessLetterDetails deleteBusinessDetails(BusinessLetterDetails oldbusinessLetterDetails) {
		logger.info("BusinessLetterDaoImpl(deleteBusinessDetails)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
		oldbusinessLetterDetails.setModified_time(new Date());
		logger.debug("the obj:"+new Gson().toJson(oldbusinessLetterDetails));
		session.update(oldbusinessLetterDetails);
		if(oldbusinessLetterDetails.getId()== 0) {
			entityManager.persist(oldbusinessLetterDetails);
			return oldbusinessLetterDetails;
		} else {
			entityManager.merge(oldbusinessLetterDetails);
			return oldbusinessLetterDetails;
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		logger.info("BusinessLetterDaoImpl(deleteBusinessDetails) exit-->");
		return oldbusinessLetterDetails;
	}

	@Override
	public BusinessLetterDetails updateBusinessDetails(BusinessLetterDetails businessLetterDetails) {
		// TODO Auto-generated method stub
		logger.info("BusinessLetterDaoImpl(updateBusinessDetail)<< Entry");
		final Session session = entityManager.unwrap(Session.class);
		try{
			businessLetterDetails.setModified_time(new Date());
			logger.debug("appInfo Obj"+new Gson().toJson(businessLetterDetails));
//			session.update(businessLetterDetails);
			if(businessLetterDetails.getId() == 0) {
				entityManager.persist(businessLetterDetails);
			} else {
				entityManager.merge(businessLetterDetails);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.info("BusinessLetterDaoImpl(updateBusinessDetail)<<exit");
		return businessLetterDetails;
	}

	@Override
	public List<BusinessLetterDetails> getActiveOrgIdBusinessDetails(Long id) {
		// TODO Auto-generated method stub
		logger.info("BusinessLetterDaoImpl(getActiveOrgIdBusinessDetails)>>Entey");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from BusinessLetterDetails where orgDetails.org_id=:id and is_deleted =:k order by timestamp(modified_time) desc");
			query.setParameter("id", id);
			query.setParameter("k", false);
			@SuppressWarnings("unchecked")
			final List<BusinessLetterDetails> details = query.getResultList();
			logger.info("BusinessLetterDaoImpl(getActiveOrgIdBusinessDetails) >>Exit");
			return details;
	}

	@Override
	public BusinessLetterDetails deleteAllBusinessDetails(BusinessLetterDetails businessLetterDetails) {
		logger.info("BusinessLetterDaoImpl(deleteAllbusinessDaoImpl)>> Entry");// TODO Auto-generated method stub
		final Session session= entityManager.unwrap(Session.class);
		try {
			businessLetterDetails.setModified_time(new Date());
			logger.debug("appInfo obj:" + new Gson().toJson(businessLetterDetails));
			session.update(businessLetterDetails);
			if(businessLetterDetails.getId() == 0) {
				entityManager.persist(businessLetterDetails);
				return businessLetterDetails;
			} else {
				entityManager.merge(businessLetterDetails);
				return businessLetterDetails;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.info("BusinessLetterDaoImpl(deleteAllBusinessDetails) >>  Exit ");
		return businessLetterDetails;
	}

	@Override
	public List<CustomInternshipDetails> getActiveBusinessDetailsByOrgId_new(Long id) {
		logger .info("BusinessLetterDaoImpl(getActiveBusinessDetailsByOrgId_new) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from BusinessLetterDetails where orgDetails.org_id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked")
		List<BusinessLetterDetails> details = query.getResultList();
		List<CustomInternshipDetails> newData = new ArrayList<CustomInternshipDetails>();
		if(details.size()!=0) {
			for(int i=0;i<details.size();i++) {
				CustomInternshipDetails customData = new CustomInternshipDetails();
				customData.setId(details.get(i).getId());
				customData.setOrgId(details.get(i).getOrgDetails().getOrg_id());
				customData.setName(details.get(i).getName());
				customData.setDoj(details.get(i).getDoj());
				customData.setAddress(details.get(i).getAddress());
				customData.setProgram_title(details.get(i).getProgram_title());
				customData.setPdfFileLink(details.get(i).getPdfFileLink());
				customData.setInternPdfFormat(details.get(i).getInternPdfFormat());
				customData.setTodayDate(details.get(i).getToday_Date());
				customData.setModified_time(details.get(i).getModified_time());
				customData.setCreated_time(details.get(i).getCreated_time());
				newData.add(customData);
				
			}
			
		}
		logger.info("BusinessDetailsDaoImpl(getActiveBusinessDetailsByOrgId_new) >> Exit");
		
		// TODO Auto-generated method stub
		return newData;
	}
	@Override
	public List<BusinessLetterDetails>  getBusinessLetterCount(Long id) {		
		logger.info("BusinessLetterDaoImpl(getBusinessLetterCount)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"select count(*) from BusinessLetterDetails  where orgDetails.id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("k", false);
//		System.out.print("query "+query);
		@SuppressWarnings("unchecked")
		final List<BusinessLetterDetails> details = query.getResultList();
		logger.info("BusinessLetterDaoImpl(getBusinessLetterCount)>> Exit");
		return details;
	}
	
	@Override
	public List<DashboardBusinessLetter>  getAddedBusinessLetter(Long id) {			
		logger.info("BusinessLetterDaoImpl (getAddedBusinessLetter)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from BusinessLetterDetails where orgDetails.org_id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setMaxResults(3);
		query.setParameter("id", id);
		query.setParameter("k", false);		
		@SuppressWarnings("unchecked")
		final List<BusinessLetterDetails> details = query.getResultList();
		final List<DashboardBusinessLetter> data  = new ArrayList<>();
		 for(int i=0;i<details.size();i++) {
			 DashboardBusinessLetter temp = new DashboardBusinessLetter();
		    	temp.setName(details.get(i).getName());
		    	temp.setProgram_title(details.get(i).getProgram_title());
		    	data.add(temp);
		    }
		logger.info("BusinessLetterDaoImpl(getAddedBusinessLetter) >> Exit");
		return data;
	}

	
}
