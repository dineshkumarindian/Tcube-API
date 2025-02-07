package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.ManageBranchDetailsDao;
import com.tcube.api.model.ManageBranchDetails;

@Service
@Transactional
public class ManageBranchDetailsServiceImpl implements ManageBranchDetailsService{
	
	@Autowired
	ManageBranchDetailsDao manageBranchDetailsDao;
	
	@Override
	public ManageBranchDetails create(ManageBranchDetails data) {
		return manageBranchDetailsDao.create(data);
	}

	@Override
	public List<ManageBranchDetails> getAll() {
		return manageBranchDetailsDao.getAll();
	}

	@Override
	public List<ManageBranchDetails> getBranchesByOrgId(Long id) {
		return manageBranchDetailsDao.getBranchesByOrgId(id);
	}

	@Override
	public ManageBranchDetails getBranchById(Long id) {
		return manageBranchDetailsDao.getBranchById(id);
	}

	@Override
	public ManageBranchDetails getBranchByName(String empBranch, Long orgId) {
		return manageBranchDetailsDao.getBranchByName(empBranch,orgId);
	}

	@Override
	public int deleteBranchbynameandorgid(String branchName, long id) {
		return manageBranchDetailsDao.deleteBranchbynameandorgid(branchName,id);
	}
	
	@Override
	public ManageBranchDetails updateBranchDetails(ManageBranchDetails details) {
		return manageBranchDetailsDao.updateBranchDetails(details);
	}

}
