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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.CustomDetailsForProjectWithReferenceId;
import com.tcube.api.model.CustomProjectDetails;
import com.tcube.api.model.CustomProjectName;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.ProjectDetails;
import com.tcube.api.model.ProjectResourceDetails;
import com.tcube.api.model.RoleDetails;
import com.tcube.api.utils.ImageProcessor;

@Component
public class ProjectDetailsDaoImpl implements ProjectDetailsDao{

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(ProjectDetailsDaoImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public ProjectDetails createProject(ProjectDetails details, String zone) {
		logger.info("ProjectDetailsDaoImpl(createProject) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setCreated_time(new Date());
			details.setModified_time(new Date());
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));

			if(details.getStart_date() != null) {
				Date date = details.getStart_date();
				details.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			}
			
			if(details.getEnd_date() != null) {
				Date date2 = details.getEnd_date();
				details.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
			}
			session.save(details);
			if (details.getId() == 0) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("ProjectDetailsDaoImpl(createProject) >> Entry ");
		return details;
	}

	@Override
	public ProjectDetails getProjectById(Long id) {
		logger.info("ProjectDetailsDaoImpl(getProjectById) >> Entry Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final ProjectDetails details = (ProjectDetails) session.get(ProjectDetails.class, id);
		logger.info("ProjectDetailsDaoImpl(getProjectById) >> Exit-> ");
		return details;
	}

	@Override
	public ProjectDetails updateProject(ProjectDetails projectDetails) {
		logger.info("ProjectDetailsDaoImpl(updateProject) >> Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	projectDetails.setModified_time(new Date());
	    	logger.debug("appInfo obj:" + new Gson().toJson(projectDetails));
			session.update(projectDetails);
			if (projectDetails.getId() == 0) {
				entityManager.persist(projectDetails);
				return projectDetails;
			} else {
				entityManager.merge(projectDetails);
				return projectDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    logger.info("ProjectDetailsDaoImpl(updateProject) >> Exit");
		return projectDetails;
	}

	// update the project with the time zone
	@Override
	public ProjectDetails updateProjectWithZone(ProjectDetails projectDetails, String zone) {
		logger.info("ProjectDetailsDaoImpl(updateProject) >> Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	projectDetails.setModified_time(new Date());
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));

			if(projectDetails.getStart_date() != null) {
				Date date = projectDetails.getStart_date();
				projectDetails.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			}
			
			if(projectDetails.getEnd_date() != null) {
				Date date2 = projectDetails.getEnd_date();
				projectDetails.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
			}
			
	    	logger.debug("appInfo obj:" + new Gson().toJson(projectDetails));
			session.update(projectDetails);
			if (projectDetails.getId() == 0) {
				entityManager.persist(projectDetails);
				return projectDetails;
			} else {
				entityManager.merge(projectDetails);
				return projectDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    logger.info("ProjectDetailsDaoImpl(updateProject) >> Exit");
		return projectDetails;
	}
	
	@Override
	public ProjectDetails deleteProjectDetails(ProjectDetails oldDetails) {
		logger.info("ProjectDetailsDaoImpl(deleteProjectDetails) Entry-> ");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	oldDetails.setModified_time(new Date());
	    	logger.debug("appInfo obj:" + new Gson().toJson(oldDetails));
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
	    logger.info("ProjectDetailsDaoImpl(deleteProjectDetails) >> Exit");
		return oldDetails;
	}

	@Override
	public List<ProjectDetails> getAllProjectDetails() {
		logger.info("ProjectDetailsDaoImpl (getAllProjectDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from ProjectDetails");
		List<ProjectDetails> details = query.getResultList();
		logger.info("ProjectDetailsDaoImpl (getAllProjectDetails) >> Exit ");
		return details;
	}

	@Override
	public List<ProjectDetails> getProjectDetailsByOrgId(Long id) {
		logger.info("ProjectDetailsDaoImpl(getProjectDetailsByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ProjectDetails where orgDetails.id=:id order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<ProjectDetails> details = query.getResultList();
		logger.info("ProjectDetailsDaoImpl(getProjectDetailsByOrgId) >> Exit");
		return details;
	}

	
	
	@Override
	public ProjectDetails updateProjectDetails(ProjectDetails details) {
		logger.info("ProjectDetailsDaoImpl(updateProject) >> Entry");
		final Session session= entityManager.unwrap(Session.class);
	    try {
	    	
	    	details.setModified_time(new Date());
	    	logger.debug("appInfo obj:" + new Gson().toJson(details));
			session.update(details);
			if (details.getId() == 0) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    logger.info("ProjectDetailsDaoImpl(updateProject) >> Exit");
		return details;
	}

	@Override
	public List<ProjectDetails> getActiveProjectDetailsByClientId(Long id) {
		logger.info("ProjectDetailsDaoImpl(getActiveProjectDetailsByClientId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ProjectDetails where clientDetails.id=:id and is_deleted =:v and is_activated =:a order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("v", false);
		query.setParameter("a", true);
		@SuppressWarnings("unchecked")
		final List<ProjectDetails> details = query.getResultList();
		logger.info("ProjectDetailsDaoImpl(getActiveProjectDetailsByClientId) >> Exit");
		return details;
	}

	@Override
	public ProjectDetails getProjectByProjectNameAndOrgId(String pName, Long orgId) {
		logger.info("JobDetailsDaoImpl(getJobByJobNameAndProjectName) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ProjectDetails where project_name=:name and orgDetails.org_id=:orgId and is_deleted =:d and is_activated =:a  order by timestamp(modified_time) desc");
		query.setParameter("name", pName);
		query.setParameter("orgId", orgId);
		query.setParameter("d", false);
		query.setParameter("a", true);
		@SuppressWarnings("unchecked")
		final ProjectDetails details = (ProjectDetails) query.getSingleResult();
		logger.info("JobDetailsDaoImpl(getJobByJobNameAndProjectName) >> Exit");
		return details;
	}

	@Override
	public List<ProjectDetails> getInactiveProjectDetailsByClientId(Long id) {
		// TODO Auto-generated method stub
		logger.info("ProjectDetailsDaoImpl(getInactiveProjectDetailsByClientId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from ProjectDetails where clientDetails.id=:id and is_deleted =:v and is_activated =:a order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("v", false);
		query.setParameter("a", false);
		@SuppressWarnings("unchecked")
		final List<ProjectDetails> details = query.getResultList();
		logger.info("ProjectDetailsDaoImpl(getInactiveProjectDetailsByClientId) >> Exit");
		return details;
	}

	@Override
	public boolean deleteDuplicateProjectResources() {
		logger.info("ProjectDetailsDaoImpl (deleteDuplicateProjectResources) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createNativeQuery(
				"delete from project_resource_details where project_id is null");
		int count = query.executeUpdate();
		if(count>0) {
			return true;
		}
		logger.info("ProjectDetailsDaoImpl (deleteDuplicateProjectResources) >> Exit ");
		return false;
	}

	@Override
	public List<ProjectDetails> getActiveProjecttNameListByOrgId(Long id) {
		logger.info("ProjectDetailsDaoImpl(getActiveProjecttNameListByOrgId) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"select project_name from ProjectDetails where orgDetails.org_id=:id and is_deleted =:v");
		query.setParameter("id", id);
		query.setParameter("v", false);
//		query.setParameter("a", true);
		@SuppressWarnings("unchecked")
		final List<ProjectDetails> details = query.getResultList();
		logger.info("ProjectDetailsDaoImpl(getActiveProjecttNameListByOrgId) >> Exit");
		return details;
	}
	
// remove user on project and disable that user on jobs under this project	

	@Override
	public boolean removeProjectUser(Long projectId,String empId) {
		// TODO Auto-generated method stub
		logger.info("ProjectDetailsDaoImpl(removeProjectUser) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createNativeQuery("delete from project_resource_details where emp_id =:j and project_id=:i and designation ='team_members'");
		query.setParameter("i", projectId);
		query.setParameter("j", empId);
		int count = query.executeUpdate();
//		final ProjectDetails details = (ProjectDetails) query.getResultList();
//		for(int i=0; i<details.size();i++) {
//			details.get(i).setProject_name(newdata);
//			if (details.get(i).getId() == 0) {
//				entityManager.persist(details.get(i));
//			} else {
//				entityManager.merge(details.get(i));
//			}
//		}
		if(count>0) {
			return true;
		}
		logger.info("ProjectDetailsDaoImpl(removeProjectUser) >> Exit");
		return false;
	}
	
//  To get projectname,id,client_id and status based on org_id
    @Override
    public List<ProjectDetails> getActiveProjectDetailsByOrgIdForFilter(Long id) {
        logger.info("ProjectDetailsDaoImpl(getActiveProjectDetailsByOrgIdForFilter) >> Entry");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery(
                "select id, project_name,project_status from ProjectDetails where org_id=:org_id and is_activated=true and is_deleted=false and project_status='Inprogress'");
        query.setParameter("org_id", id);
        @SuppressWarnings("unchecked")
        final List<ProjectDetails> details = query.getResultList();
        ArrayList<JSONObject> project = new ArrayList<JSONObject>();
        JSONObject project_data = new JSONObject();
//        for(int i=0;i<details.size();i++) {
//        	for(int j=0; j<=i;j++) {
//        		List<ProjectDetails> project_data1 = details.get(i);
//        		project_data.put("project_id", details.get(i).getId());
//				project_data.put("project_name", details.get(i).getProject_name());
//				project_data.put("project_status", details.get(i).getProject_status());
//					project.add(project_data);
//        	}
//				
//		}
        logger.info("ProjectDetailsDaoImpl(getActiveProjectDetailsByOrgIdForFilter) >> Exit");
        return details;
    }
    
 // To display the user in disabled manner in view team members after deactivated in manage user
 	@Override
 	public List<CustomProjectDetails>  disableUserAfterDeactivate(Long id, Long projectId) {
 			logger.info("ProjectDetailsDaoImpl(disableUserAfterDeactivate) >> Entry Request -> " + id);
 			final Session session = entityManager.unwrap(Session.class);
 			String check = "";
 			final Query query = session.createQuery
 			("from ProjectDetails where orgDetails.id=:id and is_deleted =:k and id=:i order by timestamp(modified_time) desc");
 			query.setParameter("id", id);
 			query.setParameter("k", false);
 			query.setParameter("i", projectId);
 			@SuppressWarnings("unchecked")
 			List<ProjectDetails> details = query.getResultList();
 			List<CustomProjectDetails> data = new ArrayList<CustomProjectDetails>();
 			if(details.size()!=0) {
 				for(int i=0;i<details.size();i++) {
 					
 					CustomProjectDetails value = new CustomProjectDetails();
 					value.setId(details.get(i).getId());
 					value.setOrgId(details.get(i).getOrgDetails().getOrg_id());
 					value.setProject_name(details.get(i).getProject_name());
 					value.setIs_activated(details.get(i).getIs_activated());
 					value.setProject_status(details.get(i).getProject_status());
 					final Query query_active = session.createQuery("from ProjectResourceDetails where ref_projectid=:i and (status=:j or status=:k) and is_deleted=:l");
 					query_active.setParameter("i",  details.get(i).getId());
 					query_active.setParameter("j", "Active");
 					query_active.setParameter("k", "Inactive");
 					query_active.setParameter("l", false);
 					List<ProjectResourceDetails>  details_active = query_active.getResultList();
 					List<JSONObject> projectResource = new ArrayList<JSONObject>();
 					for(int j=0; j<details_active.size() ;j++) {
 						JSONObject object  = new JSONObject();
 						if(details_active.get(j).getEmployeeDetails().getProfile_image() != null) {
 						object.put("profile_image",ImageProcessor.decompressBytes(details_active.get(j).getEmployeeDetails().getProfile_image()));
 						object.put("id", details_active.get(j).getEmployeeDetails().getId());
 						object.put("name", details_active.get(j).getEmployeeDetails().getFirstname() + " " + details_active.get(j).getEmployeeDetails().getLastname());
 						object.put("status", details_active.get(j).getStatus());
 						object.put("designation",details_active.get(j).getDesignation());
 						object.put("role",details_active.get(j).getEmployeeDetails().getRoleDetails().getRole());
 						projectResource.add(object);
 						} else {
 							object.put("id", details_active.get(j).getEmployeeDetails().getId());
 							object.put("name", details_active.get(j).getEmployeeDetails().getFirstname() + " " + details_active.get(j).getEmployeeDetails().getLastname());
 							object.put("status", details_active.get(j).getStatus());
 							object.put("designation",details_active.get(j).getDesignation());
 							object.put("profile_image", details_active.get(j).getEmployeeDetails().getProfile_image());
 							object.put("role",details_active.get(j).getEmployeeDetails().getRoleDetails().getRole());
 							projectResource.add(object);
 						}
 					}
 					value.setResourceDetails(projectResource);
// 					value.setProject_status(details.get(i).getResourceDetails().get(i).getStatus());
 					data.add(value);
 				}
 			}
 			logger.info("ProjectDetailsDaoImpl(disableUserAfterDeactivate) >> Exit-> ");
 			return data;
 	}
 
 	
 	
 	//To show the user in active in view team members after activated in manage user
 		@Override
 		public int  enableUserInProject(String id) {
 				logger.info("ProjectDetailsDaoImpl(enableUserInProject) >> Entry");
 				final Session session = entityManager.unwrap(Session.class);
 				final Query query = session.createNativeQuery("update project_resource_details set status=:j where emp_id =:k");
 				query.setParameter("j", "Active");
 				query.setParameter("k", id);
 				@SuppressWarnings("unchecked")
 				int details = query.executeUpdate();
 				logger.info("ProjectDetailsDaoImpl(enableUserInProject) >>Exit");
 				return details;
 		}
 		
 		// To show the user in active after bulk activate in view team members
 		@Override
 		public int  enableBulkUsersInProject(JSONArray ids) {
 				logger.info("ProjectDetailsDaoImpl(enableBulkUsersInProject) >> Entry");
 				final Session session = entityManager.unwrap(Session.class);
 	 			String id_list = new String();
 	 			for (int i = 0; i < ids.length(); i++) {
 	 				id_list += "'" + ids.get(i) + "'" + ",";
 	 			}
 	 			StringBuffer id_sb = new StringBuffer(id_list);
 	 			id_sb.deleteCharAt(id_sb.length() - 1);
 				final Query query = session.createNativeQuery("update project_resource_details set status=:j where emp_id in (" + id_sb + ")");
 				query.setParameter("j", "Active");
 				@SuppressWarnings("unchecked")
 				int details = query.executeUpdate();
 				logger.info("ProjectDetailsDaoImpl(enableBulkUsersInProject) >>Exit");
 				return details;
 		}
 		
 		//To remove the disabled user in view team memebers after single delete that user in inactive user table
 			@Override
 				 public int removeProjectUserByEmployeeId(String id) {
 					logger.info("ProjectDetailsDaoImpl(removeProjectUserByEmployeeId) >> Entry");
 					final Session session = entityManager.unwrap(Session.class);
 					final Query query = session.createNativeQuery("update project_resource_details set is_deleted =:j where emp_id =:k");
 					query.setParameter("j", true);
 					query.setParameter("k", id);
 					@SuppressWarnings("unchecked")
 					int details = query.executeUpdate();
 					logger.info("ProjectDetailsDaoImpl(removeProjectUserByEmployeeId) >>Exit");
 					return details;
 				}
 			
 	//To remove bulk disabled users in view team memebers after bulk delete that user in inactive user table
 		@Override
 		 public int removeBulkProjectUserByEmployeeId(JSONArray ids) {
 			logger.info("ProjectDetailsDaoImpl(removeBulkProjectUserByEmployeeId) >> Entry");
 			final Session session = entityManager.unwrap(Session.class);
 			String id_list = new String();
 			for (int i = 0; i < ids.length(); i++) {
 				id_list += "'" + ids.get(i) + "'" + ",";
 			}

 			StringBuffer id_sb = new StringBuffer(id_list);
 			id_sb.deleteCharAt(id_sb.length() - 1);
 			final Query query = session.createNativeQuery("update project_resource_details set is_deleted =:j where emp_id in (" + id_sb + ")");
 			query.setParameter("j", true);
// 			query.setParameter("k", id);
 			@SuppressWarnings("unchecked")
 			int details = query.executeUpdate();
 			logger.info("ProjectDetailsDaoImpl(removeBulkProjectUserByEmployeeId) >>Exit");
 			return details;
 		}		
 				
 	// To display the resource details in update project page with reference id
 	@Override
 	public List<CustomDetailsForProjectWithReferenceId>  getProjectByReferenceId(Long projectId) {
 			logger.info("ProjectDetailsDaoImpl(getProjectByReferenceId) >> Entry ");
 			final Session session = entityManager.unwrap(Session.class);
 			final Query query = session.createQuery
 			("from ProjectDetails where is_deleted =:k and id=:i order by timestamp(modified_time) desc");
 			query.setParameter("k", false);
 			query.setParameter("i", projectId);
 			@SuppressWarnings("unchecked")
 			ProjectDetails details = (ProjectDetails) query.getSingleResult();
 			List<CustomDetailsForProjectWithReferenceId> data = new ArrayList<CustomDetailsForProjectWithReferenceId>();
 			if(details != null) {
 				CustomDetailsForProjectWithReferenceId value = new CustomDetailsForProjectWithReferenceId();
 				value.setId(details.getId());
 				value.setOrgId(details.getOrgDetails().getOrg_id());
 				value.setProject_name(details.getProject_name());
 				value.setProject_status(details.getProject_status());
 				value.setClientId(details.getClientDetails().getId());
 				value.setClientName(details.getClientDetails().getClient_name());
// 				value.setClientDetails(details.getClientDetails());
 				value.setStart_date(details.getStart_date());
 				value.setEnd_date(details.getEnd_date());
 				value.setDescription(details.getDescription());
 				value.setProject_cost(details.getProject_cost());
 				value.setStatus_comment(details.getStatus_comment());
 				final Query query_active = session.createQuery("from ProjectResourceDetails where ref_projectid=:i and status=:j and is_deleted=:k");
 				query_active.setParameter("i",details.getId());
 				query_active.setParameter("j","Active");
 				query_active.setParameter("k",false);
 				@SuppressWarnings("unchecked")
 				List<ProjectResourceDetails>  details_active = query_active.getResultList();
 				List<JSONObject> projectResource = new ArrayList<JSONObject>();
 				for(int j=0; j<details_active.size() ;j++) {
 					JSONObject object  = new JSONObject();
 					object.put("id", details_active.get(j).getEmployeeDetails().getId());
 					object.put("name", details_active.get(j).getEmployeeDetails().getFirstname() + " " + details_active.get(j).getEmployeeDetails().getLastname());
 					object.put("status", details_active.get(j).getStatus());
 					object.put("designation",details_active.get(j).getDesignation());
 					object.put("profile_image", details_active.get(j).getEmployeeDetails().getProfile_image());
 					object.put("rate_per_hour", details_active.get(j).getRate_per_hour());
 					projectResource.add(object);					
 				}
 				value.setResourceDetails(projectResource);
 				data.add(value);
 			}
 			logger.info("ProjectDetailsDaoImpl(getProjectByReferenceId) >> Exit-> ");
 			return data;
 	}
 	
 // To display the project details by org Id and with reference project id for project table and update project
 	@Override
 	public List<CustomDetailsForProjectWithReferenceId> getProjectDetailsByOrgIdWithRefProjectId(Long id) {
 		logger.info("ProjectDetailsDaoImpl(getProjectDetailsByOrgIdWithRefProjectId) >> Entry");
 		final Session session = entityManager.unwrap(Session.class);
 		final Query query = session.createQuery(
 				"from ProjectDetails where orgDetails.id=:id and is_deleted=false and is_activated=true order by timestamp(modified_time) desc");
 		query.setParameter("id", id);
 		@SuppressWarnings("unchecked")
 		final List<ProjectDetails> details = query.getResultList();
 		List<CustomDetailsForProjectWithReferenceId> data = new ArrayList<CustomDetailsForProjectWithReferenceId>();
 		if(details.size()!=0) {
 			// Used Custom file for getting project details
 				for(int i=0;i<details.size();i++) {
 					CustomDetailsForProjectWithReferenceId value = new CustomDetailsForProjectWithReferenceId();
 					value.setId(details.get(i).getId());
 					value.setProject_name(details.get(i).getProject_name());
 					value.setProject_status(details.get(i).getProject_status());
 					value.setProject_cost(details.get(i).getProject_cost());
 					value.setClientId(details.get(i).getClientDetails().getId());
 					value.setClientName(details.get(i).getClientDetails().getClient_name());
 					value.setDescription(details.get(i).getDescription());
 					value.setStart_date(details.get(i).getStart_date());
 					value.setEnd_date(details.get(i).getEnd_date());
 					value.setIs_activated(details.get(i).getIs_activated());
 					value.setTotal_jobs(details.get(i).getTotal_jobs());
 					final Query query_active = session.createQuery("from ProjectResourceDetails where ref_projectid=:i and is_deleted =: j");
 					query_active.setParameter("i", details.get(i).getId());
 					query_active.setParameter("j", false);
 					List<ProjectResourceDetails>  details_active = query_active.getResultList();
 					List<JSONObject> projectResource = new ArrayList<JSONObject>();
 					for(int j=0;j<details_active.size(); j++) {
 						JSONObject object  = new JSONObject();
 						object.put("id", details_active.get(j).getEmployeeDetails().getId());
 							object.put("name", details_active.get(j).getEmployeeDetails().getFirstname() + " " + details_active.get(j).getEmployeeDetails().getLastname());
 							object.put("status", details_active.get(j).getStatus());
 							object.put("designation",details_active.get(j).getDesignation());
 							object.put("profile_image", details_active.get(j).getEmployeeDetails().getProfile_image());
 							object.put("role",details_active.get(j).getEmployeeDetails().getRoleDetails().getRole());
 							object.put("rate_per_hour",details_active.get(j).getRate_per_hour());
// 							object.put("is_deleted", details_active.get(j).getIs_deleted());
// 							object.put("referenceId", details_active.get(j).getRef_projectid());
 							projectResource.add(object);
 					}
 					value.setResourceDetails(projectResource);
  					data.add(value);
// 				}
 		}
 				}
 		logger.info("ProjectDetailsDaoImpl(getProjectDetailsByOrgIdWithRefProjectId) >> Exit");
 		return data;
 	}

 	// To display the project name in dropdown in add job
 		@Override
 		public List<CustomProjectName> getProjectNameAndId(Long id) {
 			logger.info("ProjectDetailsDaoImpl(getProjectNameAndId) >> Entry");
 	 		final Session session = entityManager.unwrap(Session.class);
 	 		final Query query = session.createQuery("from ProjectDetails where orgDetails.id=:id and is_deleted=false and is_activated=true order by timestamp(modified_time) desc");
 	 		query.setParameter("id", id);
 	 		@SuppressWarnings("unchecked")
 	 		final List<ProjectDetails> details = query.getResultList();
 	 		// Custom project file for getting only project name,id and status
 	 		List<CustomProjectName> data = new ArrayList<CustomProjectName>();
 	 		if(details.size()!=0) {
 	 				for(int i=0;i<details.size();i++) {
 	 					CustomProjectName value = new CustomProjectName();
 	 					value.setId(details.get(i).getId());
 	 					value.setProject_name(details.get(i).getProject_name());
 	 					value.setProject_status(details.get(i).getProject_status());
 	 					value.setStatus_comment(details.get(i).getStatus_comment());
 	 					data.add(value);
 	 				}
 	 		}
 		logger.info("ProjectDetailsDaoImpl(getProjectNameAndId) >> Exit");
 		return data;
 		}
 		
 		@Override 
 		public CustomProjectName getProjectStatusById(Long id) {
 			logger.info("ProjectDetailsDaoImpl(getProjectStatusById) >> Entry Request -> " + id);
 			final Session session = entityManager.unwrap(Session.class);
 			final Query query = session.createQuery("from ProjectDetails where id=:i and is_deleted=false and is_activated=true order by timestamp(modified_time) desc");
 	 		query.setParameter("i", id);
 	 		@SuppressWarnings("unchecked")
 	 		final ProjectDetails details = (ProjectDetails) query.getSingleResult();
 	 		// Custom project file for getting only project name,id and status
 	 		CustomProjectName value = new CustomProjectName();
 	 		if(details !=null) {
// 	 				for(int i=0;i<details.size();i++) { 	 					
 	 					value.setId(details.getId());
 	 					value.setProject_name(details.getProject_name());
 	 					value.setProject_status(details.getProject_status());
 	 					value.setStatus_comment(details.getStatus_comment());
// 	 					data.add(value);
// 	 				}
 	 		}
 		logger.info("ProjectDetailsDaoImpl(getProjectStatusById) >> Exit");
 		return value;
 		}

		@Override
		public List<CustomDetailsForProjectWithReferenceId> getAllProjectsByOrgIdStatusCustomData(Long org_id,String status, boolean is_activated) {
			logger.info("ProjectDetailsDaoImpl(getAllProjectsByOrgIdStatusCustomData) >> Entry");
			StringBuffer pj_status = new StringBuffer(status);
			final Session session = entityManager.unwrap(Session.class);
	 		final Query query = session.createQuery(
	 				"from ProjectDetails where orgDetails.id=:i and project_status in (" + pj_status + ") and is_deleted=false and is_activated=:k order by timestamp(modified_time) desc");
	 		query.setParameter("i", org_id);
	 		query.setParameter("k", is_activated);
	 		final List<ProjectDetails> details = query.getResultList();
	 		List<CustomDetailsForProjectWithReferenceId> data = new ArrayList<CustomDetailsForProjectWithReferenceId>();
	 		
	 		if(details.size()!=0) {
	 			// Used Custom file for getting project details
	 				for(int i=0;i<details.size();i++) {
	 					CustomDetailsForProjectWithReferenceId value = new CustomDetailsForProjectWithReferenceId();
	 					value.setId(details.get(i).getId());
	 					value.setProject_name(details.get(i).getProject_name());
	 					value.setProject_status(details.get(i).getProject_status());
	 					value.setProject_cost(details.get(i).getProject_cost());
	 					value.setClientId(details.get(i).getClientDetails().getId());
	 					value.setClientName(details.get(i).getClientDetails().getClient_name());
	 					value.setDescription(details.get(i).getDescription());
	 					value.setStart_date(details.get(i).getStart_date());
	 					value.setEnd_date(details.get(i).getEnd_date());
	 					value.setIs_activated(details.get(i).getIs_activated());
	 					value.setTotal_jobs(details.get(i).getTotal_jobs());
	 					value.setLogged_hours(details.get(i).getLogged_hours());
	 					
	 					//to get the resource details of the project
	 					final Query query_active = session.createQuery("from ProjectResourceDetails where ref_projectid=:i and is_deleted=false");
	 					query_active.setParameter("i", details.get(i).getId());
	 					List<ProjectResourceDetails>  details_active = query_active.getResultList();
	 					List<JSONObject> projectResource = new ArrayList<JSONObject>();
	 					for(int j=0;j<details_active.size(); j++) {
	 						JSONObject object  = new JSONObject();
	 						object.put("id", details_active.get(j).getEmployeeDetails().getId());
	 							object.put("name", details_active.get(j).getEmployeeDetails().getFirstname() + " " + details_active.get(j).getEmployeeDetails().getLastname());
	 							object.put("status", details_active.get(j).getStatus());
	 							object.put("designation",details_active.get(j).getDesignation());
	 							object.put("role",details_active.get(j).getEmployeeDetails().getRoleDetails().getRole());
	 							object.put("rate_per_hour",details_active.get(j).getRate_per_hour());
	 							projectResource.add(object);
	 					}
	 					value.setResourceDetails(projectResource);
	  					data.add(value);
	 				}
	 				return data;
	 		}
			logger.info("ProjectDetailsDaoImpl(getAllProjectsByOrgIdStatusCustomData) >> Exit");
			return data;
		}

		@Override
		public ProjectDetails setLoggedhours(String project, Long orgId, String duration) {
			logger.info("ProjectDetailsDaoImpl(setLoggedhours) Entry");
			final Session session = entityManager.unwrap(Session.class);
			final Query query = session.createQuery(
					"from ProjectDetails where project_name=:project and org_id=:org_id");
			query.setParameter("project", project);
			query.setParameter("org_id", orgId);
			@SuppressWarnings("unchecked")
			final List<ProjectDetails> details = query.getResultList();
			ProjectDetails data = details.get(0);
			data.setLogged_hours(duration);
			data.setModified_time(new Date());
			session.update(data);
			if (data.getId() == 0) {
				entityManager.persist(data);
			} else {
				entityManager.merge(data);
			}
			logger.info("ProjectDetailsDaoImpl(setLoggedhours) Exit");
		   return data;
		}
}
