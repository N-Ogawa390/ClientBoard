package net.dkt.dktsearch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dkt.dktsearch.model.Area;


public interface AreaRepository extends JpaRepository<Area, Integer>{
	
	void deleteByClientId(Integer clientId);
	
	List<Area> findByAreaName(String areaName);
	
}
