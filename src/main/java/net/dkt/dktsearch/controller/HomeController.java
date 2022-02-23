package net.dkt.dktsearch.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.dkt.dktsearch.S3DownloadHelper;
import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.Genre;
import net.dkt.dktsearch.service.ClientService;
import net.dkt.dktsearch.service.HomeService;

/*
//管理画面表示
	@GetMapping("/manage")	public String manage
//スクール検索画面表示
	@GetMapping("/search")	public String search
//スクール検索
	@GetMapping("/search/searchSchool")	public String searchSchool
//ログイン画面を表示
	@GetMapping("/login")	public String login
 */

@Controller
public class HomeController {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private S3DownloadHelper s3DownloadHelper;
		
	@GetMapping("/")
	public String home(Model model) {
		
		return "index";
	}
	
	//ログイン画面を表示
	@GetMapping("/login")
	public String login() {
		
		return "login";
	}
	
	//クライアント管理画面表示
	@GetMapping("/manage")
	public String manage() {
		
		return "manage/client/manage";
	}
	
	//管理者画面表示
	@GetMapping("/adminMenu")
	public String adminMenu() {
		
		return "admin";
	}
	
	//スクール検索画面表示
	@GetMapping("/search")
	public String search(Model model) {
		
		model.addAttribute("targetClients", null);
		return "search";
	}
	
	//スクール検索
	@GetMapping("/search/searchSchool")
	public String searchSchool(Model model,
			@RequestParam(name="genreNames", required = false) String[] genreNames,
			@RequestParam(name="word") String word,
			@RequestParam(name="areaName") String areaName
			) {
		
		List<Client> targetClients = clientService.getActiveClients();
		
		//スクール名検索
		if (word != null) {
			
			List<Client> targetClientsWithWord = new ArrayList<>();
			
			for(Client client: targetClients) {
				if (client.getClientName().contains(word)) {
					targetClientsWithWord.add(client);
				}
			}
			
			targetClients = targetClientsWithWord;
		}
		
		//エリア検索
		if(!areaName.equals("選択してください")) {
			
			List<Client> targetClientsWithAreaName = new ArrayList<>();
			
			for(Client client : targetClients) {
				if (client.getArea().getAreaName().equals(areaName)) {
					targetClientsWithAreaName.add(client);
				}
			}
			
			targetClients = targetClientsWithAreaName;
		}
		
		//ジャンル検索
		if (genreNames != null) {
			
			List<Client> targetClientsWithGenreName = new ArrayList<>();
			
			for(String genreName : genreNames) {
				
				for(Client client : targetClients) {
					
					List<Genre> genres = client.getGenres();
					List<String> targetGenreNames = genres.stream().map(g -> g.getGenreName()).collect(Collectors.toList());
					
					if (targetGenreNames.contains(genreName)) {
						targetClientsWithGenreName.add(client);
					}
				}
			}
			
			targetClients = targetClientsWithGenreName;
		}
		
		//郵便番号でソート
		targetClients.sort((a, b)->a.getZipCode().compareTo(b.getZipCode()));
		
		model.addAttribute("budGetWithClientId", clientService.getMapBudgetWithClientId(targetClients, "ALL"));
			//クライアントIDと月4予算のマップ
		model.addAttribute("butGetWithClientIdKids", clientService.getMapBudgetWithClientId(targetClients, "キッズ"));
			//クライアントIDと月4予算(キッズ)のマップ
		model.addAttribute("topImagesWithClientId", s3DownloadHelper.getTopImagesWithClientId(targetClients));
			//クライアントIDとトップ画像MediaFormatのマップ
		model.addAttribute("targetClients", targetClients);
		
		return "search";
	}
	
}
