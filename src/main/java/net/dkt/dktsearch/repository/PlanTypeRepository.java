package net.dkt.dktsearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dkt.dktsearch.model.PlanType;


public interface PlanTypeRepository extends JpaRepository<PlanType, Integer>{
	
	PlanType findByPlanId(Integer planId);

}
