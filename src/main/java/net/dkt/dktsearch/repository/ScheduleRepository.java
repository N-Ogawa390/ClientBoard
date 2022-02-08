package net.dkt.dktsearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dkt.dktsearch.model.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

}
