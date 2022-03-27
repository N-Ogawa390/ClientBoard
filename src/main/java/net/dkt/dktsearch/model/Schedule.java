package net.dkt.dktsearch.model;

import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Entity
@Data
public class Schedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private String genreName;
	
	@NotBlank(message = "※レッスンのレベルを入力してください")
	private String level;
	
	private String dayOfWeek;
	
	private String startTime;
	
	private String endTime;
	
	private String notes;
	
	@ManyToOne
	private Client client;
	
	@ManyToOne
	private Floor floor;
}
