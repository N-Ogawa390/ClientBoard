package net.dkt.dktsearch.model;

import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Entity
@Data
public class Schedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@NotBlank(message = "※ジャンルを入力してください")
	private String genre;
	
	@NotBlank(message = "※レッスンのレベルを入力してください")
	private String level;
	
	private String dayOfWeek;
	
	private String startTime;
	
	private String endTime;
	
	private String notes;
	
	@ManyToOne
	private Client client;
}