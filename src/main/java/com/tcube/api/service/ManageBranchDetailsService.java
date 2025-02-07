package com.tcube.api.service;

import java.util.List;

import com.tcube.api.model.ManageBranchDetails;

public interface ManageBranchDetailsService {

	public ManageBranchDetails create(ManageBranchDetails data); 
	
	public List<ManageBranchDetails> getAll();
	
	public List<ManageBranchDetails> getBranchesByOrgId(Long id);
	
	public ManageBranchDetails getBranchById(Long id);

	public ManageBranchDetails getBranchByName(String empBranch, Long orgId);

	public int deleteBranchbynameandorgid(String branchName, long id);
	
	public ManageBranchDetails updateBranchDetails(ManageBranchDetails details);
}
