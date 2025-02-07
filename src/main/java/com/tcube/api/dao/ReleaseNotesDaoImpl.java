package com.tcube.api.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.CustomInternshipDetails;
import com.tcube.api.model.CustomReleaseNotes;
import com.tcube.api.model.CustomReleaseNotesGetAllTable;
import com.tcube.api.model.DashboardReleaseNotes;
import com.tcube.api.model.ReleaseNotesDetails;

@Component
public class ReleaseNotesDaoImpl implements ReleaseNotesDao{

    @PersistenceContext
    private EntityManager entityManager;
    
    private static Logger logger = (Logger) LogManager.getLogger(ReleaseNotesDaoImpl.class);
    
    @Override
    public ReleaseNotesDetails createReleaseNotes(ReleaseNotesDetails releaseNotesDetails,String zone) {
        logger.info("ReleaseNotesDaoImpl(createReleaseNotes) >> Entry");
        final Session session = entityManager.unwrap(Session.class);
        try {
            releaseNotesDetails.setCreated_time(new Date());
            releaseNotesDetails.setModified_time(new Date());
            Date date = releaseNotesDetails.getDor();
            releaseNotesDetails.setIs_publish(false);
            releaseNotesDetails.setIs_republish(false);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));

            releaseNotesDetails.setDor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
            logger.debug("ReleaseNotesDaoImpl obj:" + new Gson().toJson(releaseNotesDetails));
            session.save(releaseNotesDetails);
            if (releaseNotesDetails.getId() != null) {
                entityManager.persist(releaseNotesDetails);
                return releaseNotesDetails;
            } else {
                entityManager.merge(releaseNotesDetails);
                return releaseNotesDetails;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("ReleaseNotesDaoImpl(createReleaseNotes)>> Exit");
        return releaseNotesDetails;
    }
    
    
    @Override
    public List<ReleaseNotesDetails> getAllReleaseNotesDetails() {
        logger.info("ReleaseNotesDaoImpl (getAllReleaseNotesDetails) >> Entry");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery("from ReleaseNotesDetails where is_deleted =:k order by id desc");
        query.setParameter("k", false);
        @SuppressWarnings("unchecked")
        List<ReleaseNotesDetails> details = query.getResultList();
        logger.info("ReleaseNotesDaoImpl (getAllReleaseNotesDetails) >> Exit");
        return details;

    }

    
    @Override
    public List<CustomReleaseNotes> getEmpWithReleaseById(Long id) {
        logger.info("ReleaseNotesDaoImpl (getEmpWithReleaseById)>> Entry");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery(
                "from ReleaseNotesDetails where id=:id and is_deleted =:k and is_publish =:p and is_republish =:q");
        query.setParameter("id", id);
        query.setParameter("k", false);
        query.setParameter("p", true);
        query.setParameter("q", false);
        @SuppressWarnings("unchecked")
        final List<ReleaseNotesDetails> details = query.getResultList();
        List<CustomReleaseNotes> detailsData = new ArrayList<CustomReleaseNotes>();
        for(int i=0;i< details.size();i++) {
        	CustomReleaseNotes customData = new CustomReleaseNotes();
        	customData.setId(details.get(i).getId());
        	customData.setCreated_time(details.get(i).getCreated_time());
        	customData.setModified_time(details.get(i).getModified_time());
        	customData.setDor(details.get(i).getDor());
        	customData.setKeyNote(details.get(i).getKeyNote());
        	customData.setVersion(details.get(i).getVersion());
        	customData.setProductName(details.get(i).getProductName());
        	customData.setReleaseNotesTitle(details.get(i).getReleaseNotesTitle());
        	customData.setWhatsNew(details.get(i).getWhatsNew());
        	customData.setBugFixes(details.get(i).getBugFixes());
        	customData.setComingsoon(details.get(i).getComingsoon());
        	customData.setImprovement(details.get(i).getImprovement());
        	customData.setGeneral(details.get(i).getGeneral());
        	detailsData.add(customData);
        }
        logger.info("ReleaseNotesDaoImpl(getEmpWithReleaseById) >> Exit");
        return detailsData;
    }
    
    
    @Override
    public List<ReleaseNotesDetails>  getReleaseNotesCount(Long id) {           
        logger.info("ReleaseNotesDaoImpl (getReleaseNotesCount)>> Entry");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery(
                "select count(*) from ReleaseNotesDetails  where orgDetails.id=:id and is_deleted =:k order by timestamp(modified_time) desc");
        query.setParameter("id", id);
        query.setParameter("k", false);     
        @SuppressWarnings("unchecked")
        final List<ReleaseNotesDetails> details = query.getResultList();
        logger.info("ReleaseNotesDaoImpl(getReleaseNotesCount) >> Exit");
        return details;
    }
    
    
    @Override
    public List<DashboardReleaseNotes>  getAddedReleaseNotes(Long id) {         
        logger.info("ReleaseNotesDaoImpl (getAddedReleaseNotes)>> Entry");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery(
                "from ReleaseNotesDetails where orgDetails.id=:id and is_deleted =:k order by id desc");
        query.setMaxResults(3);
        query.setParameter("id", id);
        query.setParameter("k", false);     
        @SuppressWarnings("unchecked")
         List<ReleaseNotesDetails> details = query.getResultList();
        List<DashboardReleaseNotes> data = new ArrayList<>();
        for(int i=0;i<details.size();i++) {
            DashboardReleaseNotes temp = new DashboardReleaseNotes();
            temp.setVersion(details.get(i).getVersion());
            temp.setDor(details.get(i).getDor());
            data.add(temp);
        }
        logger.info("ReleaseNotesDaoImpl(getAddedReleaseNotes) >> Exit");
        return data;
    }
    
    
    @Override
    public ReleaseNotesDetails getById(Long id) {
        logger.info("ReleaseNotesDaoImpl(getById)>> Entry");
        final Session session = entityManager.unwrap(Session.class);
        final ReleaseNotesDetails details = (ReleaseNotesDetails) session.get(ReleaseNotesDetails.class, id);
        logger.info("ReleaseNotesDaoImpl(getById)>> Exit");
        return details;
    }
    
    
    @Override
    public ReleaseNotesDetails deleteReleaseNotesDetails(ReleaseNotesDetails oldDetails) {
        logger.info("ReleaseNotesDaoImpl(deleteReleaseNotesDetails)>> Entry");
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
        
        logger.info("ReleaseNotesDaoImpl(deleteReleaseNotesDetails) >> Exit");
        return oldDetails;
    }
    
    
    @Override
    public ReleaseNotesDetails updateReleaseNotesDetails(ReleaseNotesDetails releaseDetails,String zone) {
        final Session session = entityManager.unwrap(Session.class);
        logger.info("ReleaseNotesDaoImpl(updateReleaseNotesDetails)>> Entry");
        try {
            releaseDetails.setModified_time(new Date());
            Date date = releaseDetails.getDor();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));

			releaseDetails.setDor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
//            if(releaseDetails)
//            releaseDetails.setIs_republish(true);
            logger.debug("appInfo Obj" + new Gson().toJson(releaseDetails));
            if (releaseDetails.getId() == 0) {
                entityManager.persist(releaseDetails);
            } else {
                entityManager.merge(releaseDetails);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("ReleaseNotesDaoImpl(updateReleaseNotesDetails)>> Exit");
        return releaseDetails;
    }
    
    
    @Override
    public ReleaseNotesDetails deleteAllReleaseNotesDetails(ReleaseNotesDetails oldDetails) {
        logger.info("ReleaseNotesDaoImpl(deleteAllReleaseNotesDaoImpl)>> Entry");
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
        logger.info("ReleaseNotesDaoImpl(deleteAllReleaseNotesDaoImpl)>> Exit");
        return oldDetails;
    }

//
	@Override
	public List<CustomReleaseNotes> getAllReleaseNoteNew(String type) {
		 logger.info("ReleaseNotesDaoImpl (getAllReleaseNoteNew)>> Entry");
	        final Session session = entityManager.unwrap(Session.class);
	        final Query query = session.createQuery(
	                "from ReleaseNotesDetails where is_deleted =:k and keyNote=:key and is_publish =:p and is_republish =:q order by id desc");
	        query.setParameter("k", false);    
	        query.setParameter("key",type);
	        query.setParameter("p", true);
	        query.setParameter("q", false);
	        @SuppressWarnings("unchecked")
	        List<ReleaseNotesDetails> details = query.getResultList();
	        List<CustomReleaseNotes> detailsData = new ArrayList<CustomReleaseNotes>();
	        for(int i=0;i< details.size();i++) {
	        	CustomReleaseNotes customData = new CustomReleaseNotes();
	        	customData.setId(details.get(i).getId());
	        	customData.setCreated_time(details.get(i).getCreated_time());
	        	customData.setModified_time(details.get(i).getModified_time());
	        	customData.setDor(details.get(i).getDor());
	        	customData.setKeyNote(details.get(i).getKeyNote());
	        	customData.setVersion(details.get(i).getVersion());
	        	customData.setProductName(details.get(i).getProductName());
	        	customData.setReleaseNotesTitle(details.get(i).getReleaseNotesTitle());
	        	customData.setWhatsNew(details.get(i).getWhatsNew());
	        	customData.setBugFixes(details.get(i).getBugFixes());
	        	customData.setComingsoon(details.get(i).getComingsoon());
	        	customData.setImprovement(details.get(i).getImprovement());
	        	customData.setGeneral(details.get(i).getGeneral());
	        	detailsData.add(customData);
	        }
	        logger.info("ReleaseNotesDaoImpl (getAllReleaseNoteNew)>> Exit");
		// TODO Auto-generated method stub
		return detailsData;
	}


	@Override
	public ReleaseNotesDetails updatePublishReleaseNotesDetails(ReleaseNotesDetails oldDetails) {
		// TODO Auto-generated method stub
		 final Session session = entityManager.unwrap(Session.class);
	        logger.info("ReleaseNotesDaoImpl(updatePublishReleaseNotesDetails)>> Entry");
	        try {
	        	oldDetails.setModified_time(new Date());
	            logger.debug("appInfo Obj" + new Gson().toJson(oldDetails));
	            if (oldDetails.getId() == 0) {
	                entityManager.persist(oldDetails);
	            } else {
	                entityManager.merge(oldDetails);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        logger.info("ReleaseNotesDaoImpl(updatePublishReleaseNotesDetails)>> Exit");
	        return oldDetails;
	}


	@Override
	public List<CustomReleaseNotesGetAllTable> getAllReleaeNotesTable() {
		// TODO Auto-generated method stub
		 logger.info("ReleaseNotesDaoImpl (CustomReleaseNotesGetAllTable)>> Entry");
	        final Session session = entityManager.unwrap(Session.class);
	        final Query query = session.createQuery(
	                "from ReleaseNotesDetails where is_deleted =:k order by id desc");
	        query.setParameter("k", false);     
	        @SuppressWarnings("unchecked")
	        final List<ReleaseNotesDetails> details = query.getResultList();
	        final List<CustomReleaseNotesGetAllTable> data= new ArrayList<CustomReleaseNotesGetAllTable>(); 
	        for(int i=0;i< details.size();i++) {
	        	CustomReleaseNotesGetAllTable customData = new CustomReleaseNotesGetAllTable();
	        	customData.setId(details.get(i).getId());
	        	customData.setCreated_time(details.get(i).getCreated_time());
	        	customData.setModified_time(details.get(i).getModified_time());
	        	customData.setDor(details.get(i).getDor());
	        	customData.setKeyNote(details.get(i).getKeyNote());
	        	customData.setVersion(details.get(i).getVersion());
	        	customData.setProductName(details.get(i).getProductName());
	        	customData.setReleaseNotesTitle(details.get(i).getReleaseNotesTitle());
//	        	customData.setNotes_pdfFormat(details.get(i).getNotes_pdfFormat());
	        	customData.setIs_publish(details.get(i).getIs_publish());
	        	customData.setIs_republish(details.get(i).getIs_republish());
	        	data.add(customData);
	        }
	        
	        logger.info("ReleaseNotesDaoImpl(CustomReleaseNotesGetAllTable) >> Exit");
		return data;
	}


	@Override
	public List<ReleaseNotesDetails> getPdfView(Long id) {
		 logger.info("ReleaseNotesDaoImpl (getAllReleaseNoteNew)>> Entry");
		 final Session session = entityManager.unwrap(Session.class);
	        final Query query = session.createQuery("select notes_pdfFormat from ReleaseNotesDetails where id=:id and is_deleted =:k");
	        query.setParameter("k", false);     
	        query.setParameter("id", id);
	        @SuppressWarnings("unchecked")
	        List<ReleaseNotesDetails> details =  query.getResultList();
	        System.out.println(details.size());
		// TODO Auto-generated method stub
		return details;
	}


	@Override
	public int updatenew_release() {
		 logger.info("ReleaseNotesDaoImpl (updatenew_release)>> Entry");
		 final Session session = entityManager.unwrap(Session.class);
	        final Query query = session.createQuery("update EmployeeDetails set new_release =:k");
	        query.setParameter("k", true);
	        @SuppressWarnings("unchecked")
	        int details = query.executeUpdate();
		return details;
	}


	@Override
	public int updatenew_release_byEmpId(String id) {
		logger.info("ReleaseNotesDaoImpl (updatenew_release_byEmpId)>> Entry");
		 final Session session = entityManager.unwrap(Session.class);
	        final Query query = session.createQuery("update EmployeeDetails set new_release =:k where id =:id");
	        query.setParameter("k", false);
	        query.setParameter("id", id);
	        @SuppressWarnings("unchecked")
	        int details = query.executeUpdate();
		return details;
	}

}
