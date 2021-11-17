package net.dkt.dktsearch.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.dkt.dktsearch.model.Area;
import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.service.AreaService;
import net.dkt.dktsearch.service.ClientService;

/*
//エリア一覧画面を表示
	@GetMapping("")	public String area
 */

@Controller
@RequestMapping("/area")
public class AreaController {
	
	@Autowired
	AreaService areaService;
	
	@Autowired
	ClientService clientService;
	
	//エリア一覧画面を表示
	@GetMapping("")
	public String area(Model model) {
		
		Map<String, List<Client>> areaNameWithClients = new HashMap<>();	//最終生成物の Map<エリア名, List<クライアントobj>> を初期化
		
		List<Area> areas = areaService.getAreaAll();	//すべてのエリアobjを取得
		List<String> areaNamesAll = areas.stream().map(a -> a.getAreaName()).collect(Collectors.toList());	//すべてのエリア名に変更
		List<String> areaNames = new ArrayList<>(new HashSet<>(areaNamesAll));	//重複を削除
		
		for (String areaName : areaNames) {
			List<Area> areasWithAreaName = areaService.getClientsWithAreaName(areaName);	//該当エリア名を持つエリアobjリスト取得
			List<Client> clientsWithAreaName = areasWithAreaName.stream().map(a -> a.getClient()).collect(Collectors.toList());	//該当エリア名を持つクライアントobjのリストに変換
			areaNameWithClients.put(areaName, clientsWithAreaName);
		}
		
		model.addAttribute("areaNameWithClients", areaNameWithClients);
		
		return "area/index";
	}
}
