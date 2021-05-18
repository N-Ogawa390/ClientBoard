package net.dkt.dktsearch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.service.ClientService;
import net.dkt.dktsearch.service.DataService;


@Controller
public class DataController {
	
	@Autowired
	ClientService clientService;
	
	@Autowired
	DataService dataService;
	
	@GetMapping("/data")
	public String data(Model model) {
		
		//クライアントオブジェクト一覧取得
		List<Client> clientAll = clientService.getClientAll();
		
		//スクール数
		Integer numClient = clientAll.size();
		model.addAttribute("numClient", numClient);
		
		//料金
		//ageGroupNames = {"ALL", "キッズ"};
		//tergetPlanNames = {"チケット4", "フリーレッスン（ALL）", "クラス4"};
		
		model.addAttribute("ticketAll", dataService.getAveOfPlanPrice("ALL", "チケット4", clientAll));
		model.addAttribute("freeAll", dataService.getAveOfPlanPrice("ALL", "フリーレッスン（ALL）", clientAll));
		model.addAttribute("classAll", dataService.getAveOfPlanPrice("ALL", "クラス4", clientAll));
		model.addAttribute("ticketKids", dataService.getAveOfPlanPrice("キッズ", "チケット4", clientAll));
		model.addAttribute("freeKids", dataService.getAveOfPlanPrice("キッズ", "フリーレッスン（ALL）", clientAll));
		model.addAttribute("classKids", dataService.getAveOfPlanPrice("キッズ", "クラス4", clientAll));

		return "data";
	}

}
