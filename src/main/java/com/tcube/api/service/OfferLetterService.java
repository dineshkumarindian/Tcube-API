package com.tcube.api.service;
import com.tcube.api.model.DashboardOfferLetter;
import com.tcube.api.model.OfferLetterDetails;
import java.util.List;

public interface OfferLetterService {

	public OfferLetterDetails createOfferLetter(OfferLetterDetails offerLetterDetails);
	
	public List<OfferLetterDetails> getAllOfferLetterDetails(Long id);

	public List<OfferLetterDetails> getActiveEmpWithOfferByOrgId(Long id);
	
	public List<OfferLetterDetails>  getOfferLetterCount(Long id);
	
	public List<DashboardOfferLetter>  getAddedOfferLetter(Long id);
	
	public OfferLetterDetails getById(Long id);

	public OfferLetterDetails getDeleteOfferLetterDetails(OfferLetterDetails oldDetails);

	public OfferLetterDetails updateOfferLetterDetails(OfferLetterDetails offerIdDetails);

	public OfferLetterDetails deleteAllOfferLetterDetails(OfferLetterDetails oldOfferLetterDetails);
	
	

}
