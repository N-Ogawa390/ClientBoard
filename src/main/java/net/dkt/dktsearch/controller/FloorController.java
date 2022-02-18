package net.dkt.dktsearch.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.Floor;
import net.dkt.dktsearch.model.Schedule;
import net.dkt.dktsearch.service.FloorService;

@Controller
@RequestMapping("/client")
public class FloorController {
	
	@Autowired
	private FloorService floorService;
	
	//フロア作成画面表示
	@GetMapping("/{clientId}/floor")
	public String createFloor(
			@PathVariable("clientId") Client client,
			Floor floor,
			Model model) {
		
		model.addAttribute("floor", floor);
		model.addAttribute("client", client);
		
		return "client/floor/form";
	}
	
	//フロア作成
	@PostMapping("/{clientId}/floor")
	public String createFloor(
			@PathVariable("clientId") Client client,
			Floor floor) {
		
		floor.setClient(client);
		floorService.saveFloor(floor);
		
		return "redirect:/client/{clientId}/floor";
	}
	
	//フロア編集画面表示
	@GetMapping("/{clientId}/floor/{floorId}/edit")
	public String editFloor(
			@PathVariable("clientId") Client client,
			@PathVariable("floorId") Floor floor,
			Model model) {
		
		model.addAttribute("client", client);
		model.addAttribute("floor", floor);
		
		return "/client/floor/edit";
	}
	
	//フロア編集
	@PostMapping("/{clientId}/floor/{floorId}/edit")
	public String editFloor(
			@Valid Floor floor, BindingResult bindingResult,
			@PathVariable("clientId") Client client,
			Model model) {
		
		if(bindingResult.hasErrors()) {
			
			model.addAttribute("client", client);
			model.addAttribute("Floor", floor);
			
			return "client/schedule/edit";
		}
		
		floor.setClient(client);
		floorService.saveFloor(floor);
		
		return "redirect:/client/{clientId}/floor";
	}
	
	//フロア削除
	@GetMapping("/{clientId}/floor/{floorId}/delete")
	public String deleteFloor(
			@PathVariable("floorId") Floor floor) {
		
		floorService.deleteFloor(floor.getId());
		
		return "redirect:/client/{clientId}/floor";
	}
}
