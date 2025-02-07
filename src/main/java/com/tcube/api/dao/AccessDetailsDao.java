package com.tcube.api.dao;

import java.util.List;

import com.tcube.api.model.AccessDetails;

public interface AccessDetailsDao {

	public AccessDetails createAccess(AccessDetails accessDetails);

	public AccessDetails getById(long id);

	public AccessDetails updateAccess(AccessDetails oldDetails);

	public AccessDetails deleteAccessDetails(AccessDetails oldDetails);

	public List<AccessDetails> getAllAccessDetails();

	public List<AccessDetails> getAccessDetailsByOrgId(Long id);

	public AccessDetails getByEmpId(String id);

}
