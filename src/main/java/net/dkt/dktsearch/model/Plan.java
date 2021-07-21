package net.dkt.dktsearch.model;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Plan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@NotBlank(message="※プラン名は必須項目です")
	private String planName;
	
	@NotBlank(message="※対象年齢は必須項目です")
	private String ageGroup;	//ALLまたはキッズ
	
	private Boolean online;
	
	@NotNull(message="※価格は必須項目です")
	private Integer price;
	
	private String planNote;
	
	private LocalDateTime created;
	
	private LocalDateTime lastModified;
	
	@ManyToOne
	private Client client;
	
	@OneToOne(mappedBy = "plan", cascade = CascadeType.ALL)
	private PlanType planType;
}
