package com.tcube.api.dao;

import java.util.List;

import com.tcube.api.model.SuperAdminDetails;

public interface SuperAdminDetailsDao {

	SuperAdminDetails createSuperAdminDetails(SuperAdminDetails admindetails);

	SuperAdminDetails getAdminDetailsById(Long adminId);

	SuperAdminDetails updateSuperAdminDetails(SuperAdminDetails oldAdminDetails);

	SuperAdminDetails deleteSuperAdminDetails(SuperAdminDetails oldAdminDetails);

	List<SuperAdminDetails> getAllSuperAdminDetails();
	
	public SuperAdminDetails authenticateSA(SuperAdminDetails admindetails);
	
	public SuperAdminDetails updateSuperAdminPassword(SuperAdminDetails oldSADetails);
}
