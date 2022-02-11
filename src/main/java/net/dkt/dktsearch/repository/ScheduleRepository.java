package net.dkt.dktsearch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dkt.dktsearch.model.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
	
	public List<Schedule> findByClientId(Integer clientId);
}
