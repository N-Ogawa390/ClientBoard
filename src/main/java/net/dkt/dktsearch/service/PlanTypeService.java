package net.dkt.dktsearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.PlanType;
import net.dkt.dktsearch.repository.PlanTypeRepository;

/*
//プランタイプをsave
	public PlanType savePlanType
//プランIdからプランタイプを取得
	public PlanType getPlanTypeFromPlanId
 */

@Service
public class PlanTypeService {
	
	@Autowired
	PlanTypeRepository planTypeRepository;
	
	//プランタイプをsave
	public PlanType savePlanType(PlanType planType) {
		return planTypeRepository.save(planType);
	}
	
	//プランIdからプランタイプを取得
	public PlanType getPlanTypeFromPlanId(Integer planId) {
		return planTypeRepository.findByPlanId(planId);
	}

}
