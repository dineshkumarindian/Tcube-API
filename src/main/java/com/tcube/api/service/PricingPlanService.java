package com.tcube.api.service;

import java.util.List;

import org.json.JSONArray;

import com.tcube.api.model.PricingPlanDetails;

public interface PricingPlanService {

	public PricingPlanDetails create(PricingPlanDetails details);
	
	public List<PricingPlanDetails> getAllPlanDetails();
	
	public PricingPlanDetails getPlanDetailsById(Long id);
	
	public PricingPlanDetails updatePlanDetails(PricingPlanDetails details);
	
	public PricingPlanDetails deletePlanDetails(Long id);
	
	public int bulkDelete(JSONArray ids);
}
