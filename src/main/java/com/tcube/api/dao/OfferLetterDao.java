package com.tcube.api.dao;

import java.util.List;

import com.tcube.api.model.DashboardOfferLetter;
import com.tcube.api.model.OfferLetterDetails;

public interface OfferLetterDao {

	public OfferLetterDetails createOfferLetter(OfferLetterDetails offerLetterDetails);

	public List<OfferLetterDetails> getActiveEmpWithOfferByOrgId(Long id);
			
	public List<OfferLetterDetails>  getOfferLetterCount(Long id);
	
	public List<DashboardOfferLetter>  getAddedOfferLetter(Long id);
	
	public OfferLetterDetails getById(Long id);
	
	public OfferLetterDetails deleteOfferLetterDetails(OfferLetterDetails oldDetails);
	
	public OfferLetterDetails updateOfferLetterDetails(OfferLetterDetails offerDetails);

	public OfferLetterDetails deleteAllOfferLetterDetails(OfferLetterDetails oldOfferLetterDetails);

	public List<OfferLetterDetails> getAllOfferLetterDetails(Long id);

}
