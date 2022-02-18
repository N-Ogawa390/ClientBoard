package net.dkt.dktsearch.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Entity
@Data
public class Floor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@NotBlank(message = "※フロア名を入力してください")
	private String floorName;
	
	@OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
	private List<Schedule> schedules;
	
	@ManyToOne
	private Client client;
}
