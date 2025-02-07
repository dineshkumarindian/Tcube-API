package com.tcube.api.service;

import java.util.*;
import com.tcube.api.model.BusinessLetterDetails;
import com.tcube.api.model.CustomInternshipDetails;
import com.tcube.api.model.DashboardBusinessLetter;

public interface BusinessLetterService{
	public BusinessLetterDetails createBusinessLetter(BusinessLetterDetails internshipDetails);
	
	public List<BusinessLetterDetails> getAllBusinessDetails(Long id);
	
	public BusinessLetterDetails getById(Long id);
	
	public BusinessLetterDetails getDeleteBusinessDetail(BusinessLetterDetails oldInternshipDetails);
	
	public BusinessLetterDetails updateBusinessDetail(BusinessLetterDetails internshipDetail);
	
	public List<BusinessLetterDetails> getActiveOrgIdBusinessDetail(Long id);
	 
	public BusinessLetterDetails deleteAllBusinessDetails(BusinessLetterDetails oldInternDetails);
	
	public List<CustomInternshipDetails> getActiveOrgIdBusinessDetailNew(Long id);
	
	public List<BusinessLetterDetails> getBusinessLetterCount(Long id);
	
	
	public List<DashboardBusinessLetter> getAddedBusinessLetter(Long id);
}
