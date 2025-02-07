package com.tcube.api.service;

import java.util.List;

import com.tcube.api.model.SuperAdminDetails;

public interface SuperAdminDetailsService {

	SuperAdminDetails createSuperAdminDetails(SuperAdminDetails admindetails);

	SuperAdminDetails getAdminDetailsById(Long adminId);

	SuperAdminDetails updateSuperAdminDetails(SuperAdminDetails oldAdminDetails);

	SuperAdminDetails deleteSuperAdminDetails(SuperAdminDetails oldAdminDetails);
	
	List<SuperAdminDetails> getAllSuperAdminDetails();
	
	public SuperAdminDetails authenticateSA(SuperAdminDetails admindetails);

	SuperAdminDetails updateSuperAdminPassword(SuperAdminDetails oldSADetails);
}
