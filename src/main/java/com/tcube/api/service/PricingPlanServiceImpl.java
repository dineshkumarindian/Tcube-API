package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.PricingPlanDao;
import com.tcube.api.model.PricingPlanDetails;

@Service
@Transactional
public class PricingPlanServiceImpl implements PricingPlanService{
	@Autowired
	PricingPlanDao pricingPlanDao;

	@Override
	public PricingPlanDetails create(PricingPlanDetails details) {
		return pricingPlanDao.create(details);
	}

	@Override
	public List<PricingPlanDetails> getAllPlanDetails() {
		return pricingPlanDao.getAllPlanDetails();
	}

	@Override
	public PricingPlanDetails getPlanDetailsById(Long id) {
		return pricingPlanDao.getPlanDetailsById(id);
	}

	@Override
	public PricingPlanDetails updatePlanDetails(PricingPlanDetails details) {
		return pricingPlanDao.updatePlanDetails(details);
	}

	@Override
	public PricingPlanDetails deletePlanDetails(Long id) {
		return pricingPlanDao.deletePlanDetails(id);
	}

	@Override
	public int bulkDelete(JSONArray ids) {
		return pricingPlanDao.bulkDelete(ids);
	}
}
