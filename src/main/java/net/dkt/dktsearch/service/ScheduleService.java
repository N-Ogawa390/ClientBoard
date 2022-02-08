package net.dkt.dktsearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.Schedule;
import net.dkt.dktsearch.repository.ScheduleRepository;

@Service
public class ScheduleService {
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	private Schedule saveSchedule(Schedule schedule) {
		
		return scheduleRepository.save(schedule);
	}
}
