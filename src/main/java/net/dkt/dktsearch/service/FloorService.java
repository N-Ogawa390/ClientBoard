package net.dkt.dktsearch.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.Floor;
import net.dkt.dktsearch.repository.FloorRepository;

@Service
public class FloorService {
	
	@Autowired
	private FloorRepository floorRepository;
	
	@Transactional
	public Floor saveFloor(Floor floor) {
		
		return floorRepository.save(floor);
	}
	
	@Transactional
	public void deleteFloor(Integer floorId) {
		
		floorRepository.deleteById(floorId);
	}
	
	public Floor getFloorByBloorName(String floorName) {
		
		return floorRepository.findByFloorName(floorName);
	}
}
