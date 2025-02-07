package com.tcube.api.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.ProjectDetailsDao;
import com.tcube.api.model.CustomDetailsForProjectWithReferenceId;
import com.tcube.api.model.CustomProjectDetails;
import com.tcube.api.model.CustomProjectName;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.ProjectDetails;

@Service
@Transactional
public class ProjectDetailsServiceImpl implements ProjectDetailsService{

	@Autowired
	ProjectDetailsDao projectDetailsDao;

	@Override
	public ProjectDetails createProject(ProjectDetails details , String zone) {
		return projectDetailsDao.createProject(details,zone);
	}

	@Override
	public ProjectDetails getProjectById(Long id) {
		return projectDetailsDao.getProjectById(id);
	}

	@Override
	public ProjectDetails updateProject(ProjectDetails projectDetails) {
		return projectDetailsDao.updateProject(projectDetails);
	}

	@Override
	public ProjectDetails deleteProjectDetails(ProjectDetails oldDetails) {
		return projectDetailsDao.deleteProjectDetails(oldDetails);
	}

	@Override
	public List<ProjectDetails> getAllProjectDetails() {
		return projectDetailsDao.getAllProjectDetails();
	}

	@Override
	public List<ProjectDetails> getProjectDetailsByOrgId(Long id) {
		return projectDetailsDao.getProjectDetailsByOrgId(id);
	}

	@Override
	public ProjectDetails updateProjectDetails(ProjectDetails details) {
		return projectDetailsDao.updateProjectDetails(details);
	}

	@Override
	public List<ProjectDetails> getActiveProjectDetailsByClientId(Long id) {
		// TODO Auto-generated method stub
		return projectDetailsDao.getActiveProjectDetailsByClientId(id);
	}
	
	@Override
	public List<ProjectDetails> getInactiveProjectDetailsByClientId(Long id) {
		// TODO Auto-generated method stub
		return projectDetailsDao.getInactiveProjectDetailsByClientId(id);
	}

	@Override
	public ProjectDetails getProjectByProjectNameAndOrgId(String pName , Long orgId) {
		// TODO Auto-generated method stub
		return projectDetailsDao.getProjectByProjectNameAndOrgId(pName , orgId);
	}

	@Override
	public boolean deleteDuplicateProjectResources() {
		// TODO Auto-generated method stub
		return projectDetailsDao.deleteDuplicateProjectResources();
	}

	@Override
	public ProjectDetails updateProjectWithZone(ProjectDetails projectDetails, String zone) {
		// TODO Auto-generated method stub
		return projectDetailsDao.updateProjectWithZone(projectDetails,zone);
	}

	@Override
	public List<ProjectDetails> getActiveProjecttNameListByOrgId(Long id) {
		// TODO Auto-generated method stub
		return projectDetailsDao.getActiveProjecttNameListByOrgId(id);
	}

	@Override
	public boolean removeProjectUser(Long projectId,String empId) {
		// TODO Auto-generated method stub
		return projectDetailsDao.removeProjectUser(projectId,empId);
	}
	
	@Override
    public List<ProjectDetails> getActiveProjectDetailsByOrgIdForFilter(Long id) {
        // TODO Auto-generated method stub
        return projectDetailsDao.getActiveProjectDetailsByOrgIdForFilter(id);
    }
	
	@Override
	public List<CustomProjectDetails> disableUserAfterDeactivate(Long id, Long projId) {
		return projectDetailsDao.disableUserAfterDeactivate(id,projId);
	}
	 
	 @Override
	 public int  enableUserInProject(String id) {
		 return projectDetailsDao.enableUserInProject(id);
	 }
	 
	 @Override
	 public int  enableBulkUsersInProject(JSONArray ids) {
		 return projectDetailsDao.enableBulkUsersInProject(ids);
	 }
	 
	 @Override
	 public int removeProjectUserByEmployeeId(String id) {
		 return projectDetailsDao.removeProjectUserByEmployeeId(id);
	 }
	 
	 @Override
	 public int removeBulkProjectUserByEmployeeId(JSONArray ids) {
		 return projectDetailsDao.removeBulkProjectUserByEmployeeId(ids);
	 }
	 
	 @Override
	 public List<CustomDetailsForProjectWithReferenceId>  getProjectByReferenceId(Long projectId) {
		 return projectDetailsDao.getProjectByReferenceId(projectId);
	 }
	 
	 @Override
	 public List<CustomDetailsForProjectWithReferenceId> getProjectDetailsByOrgIdWithRefProjectId(Long id) {
		 return projectDetailsDao.getProjectDetailsByOrgIdWithRefProjectId(id);
	 }
	 
	 @Override
	 public List<CustomProjectName> getProjectNameAndId(Long id) {
		 return projectDetailsDao.getProjectNameAndId(id);
	 }
	 
	 @Override
	 public  CustomProjectName  getProjectStatusById(Long id) {
		 return projectDetailsDao.getProjectStatusById(id);
	 }

	@Override
	public List<CustomDetailsForProjectWithReferenceId> getAllProjectsByOrgIdStatusCustomData(Long org_id,
			String status, boolean is_activated) {
		return projectDetailsDao.getAllProjectsByOrgIdStatusCustomData(org_id,status,is_activated);
	}

	@Override
	public ProjectDetails setLoggedhours(String project, Long orgId, String duration) {
		// TODO Auto-generated method stub
		return projectDetailsDao.setLoggedhours(project, orgId,duration);
	}
}
