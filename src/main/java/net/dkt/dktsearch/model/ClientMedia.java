package net.dkt.dktsearch.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class ClientMedia {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private String mediaFileName;
	
	private Integer priority;
	
	private String mediaType;
	
	private LocalDateTime created;
	
	@ManyToOne
	private Client client;

}
