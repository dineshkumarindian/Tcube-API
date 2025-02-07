package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.ApprovedLeaveDetailsDao;
import com.tcube.api.model.ApprovedLeaveDetails;

@Service
@Transactional
public class ApprovedLeaveDetailsServiceImpl implements ApprovedLeaveDetailsService{

	@Autowired
	ApprovedLeaveDetailsDao approvedLeaveDetailsDao;
	
	@Override
	public ApprovedLeaveDetails createApprovedLeaveDetails(ApprovedLeaveDetails details1) {
		return approvedLeaveDetailsDao.createApprovedLeaveDetails(details1);
	}

	@Override
	public List<ApprovedLeaveDetails> getapprovedLeaveDetailsByEmpIdAndLTId(ApprovedLeaveDetails newDetails) {
		// TODO Auto-generated method stub
		return approvedLeaveDetailsDao.getapprovedLeaveDetailsByEmpIdAndLTId(newDetails);
	}

	@Override
	public Double getapprovedLeaveCountsByEmpIdAndLTId(ApprovedLeaveDetails newDetails) {
		// TODO Auto-generated method stub
		return approvedLeaveDetailsDao.getapprovedLeaveCountsByEmpIdAndLTId(newDetails);
	}

}
