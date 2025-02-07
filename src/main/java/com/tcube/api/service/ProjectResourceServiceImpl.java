package com.tcube.api.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.tcube.api.dao.ProjectResourceDao;
import com.tcube.api.model.ProjectResourceDetails;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@Transactional
public class ProjectResourceServiceImpl implements ProjectResourceService{ 
	
	@Autowired
	ProjectResourceDao projectResourceDao;
	
	@Override
	public ProjectResourceDetails updateProjectreferenceId(Long Proj_id) {
		return projectResourceDao.updateProjectreferenceId(Proj_id);
	}

}
