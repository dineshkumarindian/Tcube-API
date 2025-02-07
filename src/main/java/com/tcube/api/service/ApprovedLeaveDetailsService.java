package com.tcube.api.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tcube.api.model.ApprovedLeaveDetails;

public interface ApprovedLeaveDetailsService {

	public ApprovedLeaveDetails createApprovedLeaveDetails(ApprovedLeaveDetails details1);

	public List<ApprovedLeaveDetails> getapprovedLeaveDetailsByEmpIdAndLTId(ApprovedLeaveDetails newDetails);

	public Double getapprovedLeaveCountsByEmpIdAndLTId(ApprovedLeaveDetails newDetails);

}
