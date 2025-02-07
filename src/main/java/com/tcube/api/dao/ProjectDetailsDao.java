package com.tcube.api.dao;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tcube.api.model.CustomDetailsForProjectWithReferenceId;
import com.tcube.api.model.CustomProjectDetails;
import com.tcube.api.model.CustomProjectName;
import com.tcube.api.model.JobDetails;
import com.tcube.api.model.ProjectDetails;

public interface ProjectDetailsDao {

	public ProjectDetails createProject(ProjectDetails details, String zone);

	public ProjectDetails getProjectById(Long id);

	public ProjectDetails updateProject(ProjectDetails projectDetails);

	public ProjectDetails deleteProjectDetails(ProjectDetails oldDetails);

	public List<ProjectDetails> getAllProjectDetails();

	public List<ProjectDetails> getProjectDetailsByOrgId(Long id);

	public ProjectDetails updateProjectDetails(ProjectDetails details);

	public List<ProjectDetails> getActiveProjectDetailsByClientId(Long id);
	
	public List<ProjectDetails> getInactiveProjectDetailsByClientId(Long id);

	public ProjectDetails getProjectByProjectNameAndOrgId(String pName, Long orgId);

	public boolean deleteDuplicateProjectResources();

	public ProjectDetails updateProjectWithZone(ProjectDetails projectDetails, String zone);

	public List<ProjectDetails> getActiveProjecttNameListByOrgId(Long id);

	public boolean removeProjectUser(Long projectId,String empId);
	
	public List<ProjectDetails> getActiveProjectDetailsByOrgIdForFilter(Long id);
	
	public List<CustomProjectDetails> disableUserAfterDeactivate(Long id, Long projId);
	
//	public List<CustomProjectDetails>  disableBulkUserAfterBulkDeactivate(Long id, JSONArray ids);
	
	public int enableUserInProject(String id);
	
	 public int  enableBulkUsersInProject(JSONArray ids);
	
	public int removeProjectUserByEmployeeId(String id);
	
	 public int removeBulkProjectUserByEmployeeId(JSONArray ids);
	
	public List<CustomDetailsForProjectWithReferenceId>  getProjectByReferenceId(Long projectId);
	
	public List<CustomDetailsForProjectWithReferenceId> getProjectDetailsByOrgIdWithRefProjectId(Long id);
	
	public List<CustomProjectName> getProjectNameAndId(Long id);
	
	 public  CustomProjectName  getProjectStatusById(Long id);
	 
	public List<CustomDetailsForProjectWithReferenceId> getAllProjectsByOrgIdStatusCustomData(Long org_id,String status, boolean is_activated);

	public ProjectDetails setLoggedhours(String project, Long orgId, String duration);

}
