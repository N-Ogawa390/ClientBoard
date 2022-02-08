package net.dkt.dktsearch.model;

import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Schedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private String genre;
	
	private String level;
	
	private DayOfWeek dayOfWeek;
	
	private Time startTime;
	
	private Time endTime;
	
	@ManyToOne
	private Client client;
}
