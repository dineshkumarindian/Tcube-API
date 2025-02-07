package com.tcube.api.dao;

import java.util.List;

import org.json.JSONArray;

import com.tcube.api.model.DesignationDetails;

public interface DesignationDetailsDao {

	public DesignationDetails createDesignation(DesignationDetails designationDetails);

	public DesignationDetails getDesignationById(Long id);

	public DesignationDetails updateDesignation(DesignationDetails newDetails);

	public DesignationDetails deleteDesignation(DesignationDetails newDetails);

	public List<DesignationDetails> getAllDesignation();

	public List<DesignationDetails> getAllDesignationByOrgId(Long id);

	public int bulkDelete(JSONArray ids);

	public DesignationDetails getDesignationByName(String details, Long id);;
}
