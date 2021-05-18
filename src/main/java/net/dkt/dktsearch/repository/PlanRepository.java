package net.dkt.dktsearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dkt.dktsearch.model.Plan;


public interface PlanRepository extends JpaRepository<Plan, Integer> {

}
