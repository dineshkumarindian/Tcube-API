package com.tcube.api.dao;

import java.util.List;
import com.tcube.api.model.CustomInternshipDetails;
import com.tcube.api.model.DashboardBusinessLetter;
import com.tcube.api.model.BusinessLetterDetails;

public interface BusinessLetterDao {
	public BusinessLetterDetails createBusinessLetter(BusinessLetterDetails internshipLetterDetails);
	
	public List<BusinessLetterDetails> getAllBusinessDetails(Long id);
	
	public BusinessLetterDetails getById(Long id);
	
	 public BusinessLetterDetails deleteBusinessDetails(BusinessLetterDetails internshipLetterDetails);
	 
	 public BusinessLetterDetails updateBusinessDetails(BusinessLetterDetails internshipLetterDetails);
	 
	 public List<BusinessLetterDetails> getActiveOrgIdBusinessDetails(Long id);
	 
	 public BusinessLetterDetails deleteAllBusinessDetails(BusinessLetterDetails internshipLetterDetails);
	 
	 public List<CustomInternshipDetails> getActiveBusinessDetailsByOrgId_new(Long id);
	 
	 public List<BusinessLetterDetails> getBusinessLetterCount(Long id);
	 
	 public List<DashboardBusinessLetter> getAddedBusinessLetter(Long id);
}
