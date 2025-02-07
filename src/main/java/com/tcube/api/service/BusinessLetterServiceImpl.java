package com.tcube.api.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tcube.api.dao.BusinessLetterDao;
import com.tcube.api.model.CustomInternshipDetails;
import com.tcube.api.model.DashboardBusinessLetter;
import com.tcube.api.model.BusinessLetterDetails;

@Service 
@Transactional
public class BusinessLetterServiceImpl implements BusinessLetterService{
	
	@Autowired
	BusinessLetterDao businessLetterDao;
	
	@Override
	public BusinessLetterDetails createBusinessLetter(BusinessLetterDetails internshipDetails) {
		// TODO Auto-generated method stub
		return businessLetterDao.createBusinessLetter(internshipDetails);
	}
	
	@Override
	public  List<BusinessLetterDetails> getAllBusinessDetails(Long id) {
//		logger.info("InternshipLetterDetails(getAllInternshipDetails)<< Entry");
		
		return businessLetterDao.getAllBusinessDetails(id);
	}
	
	@Override
	public BusinessLetterDetails getById(Long id) {
		// TODO Auto-generated method stub
		
		return businessLetterDao.getById(id);
	}
	
	@Override
	public BusinessLetterDetails getDeleteBusinessDetail(BusinessLetterDetails oldInternshipDetails) {
		// TODO Auto-generated method stub
		return businessLetterDao.deleteBusinessDetails(oldInternshipDetails);
	}
	
	@Override
	public BusinessLetterDetails updateBusinessDetail(BusinessLetterDetails internshipDetail) {
		// TODO Auto-generated method stub
		return businessLetterDao.updateBusinessDetails(internshipDetail);
	}

	@Override
	public List<BusinessLetterDetails> getActiveOrgIdBusinessDetail(Long id) {
		// TODO Auto-generated method stub
		return businessLetterDao.getActiveOrgIdBusinessDetails(id);
	}

	@Override
	public BusinessLetterDetails deleteAllBusinessDetails(BusinessLetterDetails oldInternDetails) {
		// TODO Auto-generated method stub
		return businessLetterDao.deleteAllBusinessDetails(oldInternDetails);
	}

	@Override
	public List<CustomInternshipDetails> getActiveOrgIdBusinessDetailNew(Long id) {
		// TODO Auto-generated method stub
		return businessLetterDao.getActiveBusinessDetailsByOrgId_new(id);
	}
	
	@Override
	public List<BusinessLetterDetails> getBusinessLetterCount(Long id) {
		return businessLetterDao.getBusinessLetterCount(id);
	}
	

	@Override
	public List<DashboardBusinessLetter> getAddedBusinessLetter(Long id) {
		return businessLetterDao.getAddedBusinessLetter(id);
	}
}
