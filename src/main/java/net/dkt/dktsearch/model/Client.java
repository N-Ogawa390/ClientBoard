package net.dkt.dktsearch.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Entity
@Data
public class Client {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	//スクール名
	@NotBlank(message="※スクール名を入力してください")
	private String clientName;
	
	//校舎名
	private String clientSubName;
	
	//サイトURL
	private String siteURL;
	
	//取り扱いレッスンの難易度表記
	private String lessonLevels;
	
	//1レッスンあたりの所要時間
	private String duration;
	
	//対応時間帯
	private String supporteTime;
	
	//アクセス
	private String access;
	
	//スクール正式名称
	private String formalName;
	
	//発表会の有無
	private String presentation;
	
	//特徴
	private String appeal;
	
	//運営会社
	private String company;
	
	//本社所在地
	private String headOffice;
	
	//設立
	private String establishment;
	
	//資本金
	private Integer capital;
	
	//閉店した場合はfalse
	private Boolean active;
	
	//月4予算(内部用)
	private String budget;
	
	//作成日
	private LocalDateTime created;
	
	//最終更新日
	private LocalDateTime lastModified;
	
	@ManyToOne
	private Account account;
	
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
	private List<Plan> plans;
	
	@OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
	private Area area;
	
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
	private List<Genre> genres;
	
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
	private List<ClientMedia> clientMedias;
}