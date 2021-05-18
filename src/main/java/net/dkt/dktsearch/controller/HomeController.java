package net.dkt.dktsearch.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	private HomeService homeService;
	
	@Autowired
	private ClientService clientService;
		
	@GetMapping("/")
	public String home(Model model) {
		
		List<Client> clients = homeService.getClientAll();
		model.addAttribute("clients", clients);
		return "index";
	}
	
	//管理画面表示
	@GetMapping("/manage")
	public String manage() {
		
		return "client/manage";
	}
	
	//スクール検索画面表示
	@GetMapping("/search")
	public String search(Model model) {
		
//		List<Client> targetClients = new ArrayList<>();
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
		
		List<Client> targetClients = clientService.getClientAll();
		
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
		
		//エリア検索します
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
		
		model.addAttribute("targetClients", targetClients);
		
		return "search";
	}
	
	//ログイン画面を表示
	@GetMapping("/login")
	public String login() {
		
		return "login";
	}
}
