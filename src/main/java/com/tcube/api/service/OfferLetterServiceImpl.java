package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tcube.api.dao.OfferLetterDao;
import com.tcube.api.model.DashboardOfferLetter;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.OfferLetterDetails;

@Service
@Transactional
public class OfferLetterServiceImpl implements OfferLetterService {

	@Autowired
	OfferLetterDao offerLetterDao;

	@Override
	public OfferLetterDetails createOfferLetter(OfferLetterDetails offerLetterDetails) {
		return offerLetterDao.createOfferLetter(offerLetterDetails);
	}
	
	@Override
	public List<OfferLetterDetails> getAllOfferLetterDetails(Long id) {
		return offerLetterDao.getAllOfferLetterDetails(id);
	}

	@Override
	public List<OfferLetterDetails> getActiveEmpWithOfferByOrgId(Long id) {
		return offerLetterDao.getActiveEmpWithOfferByOrgId(id);
	}
	
	@Override
	public List<OfferLetterDetails>  getOfferLetterCount(Long id) {
		return offerLetterDao.getOfferLetterCount(id);
	}
	
	@Override
	public List<DashboardOfferLetter>  getAddedOfferLetter(Long id) {
		return offerLetterDao.getAddedOfferLetter(id);
	}
	
	@Override
	public OfferLetterDetails getById(Long id) {
		return offerLetterDao.getById(id);
	}

	@Override
	public OfferLetterDetails getDeleteOfferLetterDetails(OfferLetterDetails oldDetails) {
		return offerLetterDao.deleteOfferLetterDetails(oldDetails);
	}

	@Override
	public OfferLetterDetails updateOfferLetterDetails(OfferLetterDetails offerIdDetails) {
		return offerLetterDao.updateOfferLetterDetails(offerIdDetails);
	}

	@Override 
	public OfferLetterDetails deleteAllOfferLetterDetails(OfferLetterDetails oldOfferLetterDetails) {
		return offerLetterDao.deleteAllOfferLetterDetails(oldOfferLetterDetails);
	}
	

}

