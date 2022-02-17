package net.dkt.dktsearch.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.Schedule;
import net.dkt.dktsearch.repository.ScheduleRepository;

@Service
public class ScheduleService {
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	public Schedule getSchedule(Integer scheduleId) {
		
		return scheduleRepository.getOne(scheduleId);
	}
	
	@Transactional
	public Schedule saveSchedule(Schedule schedule) {
		
		return scheduleRepository.save(schedule);
	}
	
	public List<Schedule> getSchedules(Integer clientId) {
		
		return scheduleRepository.findByClientId(clientId);
	}
	
	@Transactional
	public void deleteSchedule(Integer scheduleId) {
		
		scheduleRepository.deleteById(scheduleId);
	}
}
