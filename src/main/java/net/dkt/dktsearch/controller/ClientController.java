package net.dkt.dktsearch.controller;

import java.awt.Graphics;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import net.dkt.dktsearch.S3DownloadHelper;
import net.dkt.dktsearch.S3UploadHelper;
import net.dkt.dktsearch.model.Account;
import net.dkt.dktsearch.model.Area;
import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.ClientMedia;
import net.dkt.dktsearch.model.Floor;
import net.dkt.dktsearch.model.Genre;
import net.dkt.dktsearch.model.MediaFormat;
import net.dkt.dktsearch.model.Plan;
import net.dkt.dktsearch.model.PlanType;
import net.dkt.dktsearch.model.Schedule;
import net.dkt.dktsearch.service.AccountService;
import net.dkt.dktsearch.service.AreaService;
import net.dkt.dktsearch.service.ClientMediaService;
import net.dkt.dktsearch.service.ClientService;
import net.dkt.dktsearch.service.GenreService;
import net.dkt.dktsearch.service.PlanService;
import net.dkt.dktsearch.service.PlanTypeService;
import net.dkt.dktsearch.service.ScheduleService;
import net.dkt.dktsearch.type.DayOfWeek;

/*
//クライアント詳細画面表示
	@GetMapping("/{clientId}")	public String showClient
//クライアント作成画面表示
	@GetMapping("/create")	public String createClient
//クライアント作成
	@PostMapping("/create")	public String createClient
//クライアント編集画面表示
	@Transactional	@GetMapping("/{clientId}/edit")	public String editClient
//クライアント編集
	@PostMapping("/{clientId}/edit")	public String editClien
//クライアント削除
	@GetMapping("/{clientId}/delete")	public String deleteClient
//クライアントに紐づくプラン作成画面表示
	@GetMapping("/{clientId}/plan/create")	public String plan
//クライアントに紐づくプラン作成
	@PostMapping("/{clientId}/plan/create")	public String createPlan
//クライアントに紐づくプラン編集画面表示
	@GetMapping("/{clientId}/plan/{planId}/edit")	public String editPlanWithClientGet
//クライアント編集
	@PostMapping("/{clientId}/edit")	public String editClient
//クライアントに紐づくプラン削除
	@GetMapping("/{clientId}/plan/{planId}/delete")	public String deletePlanWithClient
//他クライアントへの不正アクセス防止用
	private void checkClientOwner
//不正アクセス時のメッセージを設定
	@ResponseStatus(HttpStatus.FORBIDDEN)	private class ForbiddenAccountAccessException extends RuntimeException
*/

@Controller
@RequestMapping("/client")
public class ClientController {
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private AreaService areaService;
	
	@Autowired
	private GenreService genreService;
	
	@Autowired
	private S3DownloadHelper s3DownloadHelper;
	
	@Autowired
	private ClientMediaService clientMediaService;
	
	@Autowired
	private ScheduleService scheduleService;
	
	@GetMapping("")
	public String client(Model model) {
		model.addAttribute("clients", clientService.getClientAll());
		return "client/index";
	}
	
	//クライアント詳細画面表示
	@GetMapping("/{clientId}")
	public String showClient(@PathVariable("clientId") Client client, Model model) {

		//画像表示
		List<MediaFormat> byteImages = s3DownloadHelper.getImage(client);

		model.addAttribute("client", client);
		model.addAttribute("topImage", clientMediaService.getTopImage(byteImages));
		model.addAttribute("subImages", clientMediaService.getSubImage(byteImages));
		
		return "client/show";
	}

	//クライアント作成画面表示
	@GetMapping("/create")
	public String createClient(Client client, Model model) {
		
		Area area = areaService.createDefaultArea();	//地域のデフォルト値をセット
		client.setArea(area);
		
		return "client/form";
	}
	
	//クライアント作成
	@PostMapping("/create")
	public String createClient(
			@Valid Client client, BindingResult bindingResult,
			@RequestParam(required = false) String[] genreNames,
			Model model
			) {
		
		if (bindingResult.hasErrors()) {
			
			Area area = areaService.createDefaultArea();	//地域のデフォルト値をセット
			client.setArea(area);
			
			return "client/form";
		}
		
		Area area = new Area();
		area.setAreaName(client.getArea().getAreaName());
		area.setClient(client);
		
		client.setAccount((Account)model.getAttribute("currentAccount"));
		client.setArea(area);
		client.setCreated(LocalDateTime.now());
		clientService.saveClient(client);
		
		if (genreNames !=null) {
			
			genreService.saveGenresWithClient(client, genreNames);	//ダンスジャンルをクライアントに紐づけて再登録
		}
		
		return "redirect:/client/" + client.getId() + "/edit";
	}
	
	//クライアント編集画面表示
	@GetMapping("/{clientId}/edit")
	public String editClient(@PathVariable("clientId") Client client, Model model) {
		
		clientService.checkClientOwner(client, model);
		
		List<Schedule> schedules = scheduleService.getSchedules(client.getId());
		model.addAttribute("schedules", schedules);
		model.addAttribute("client", client);
		
		//曜日
		model.addAttribute("dayOfWeek", DayOfWeek.values());
		
		//ジャンル情報
		List<Genre> genres = client.getGenres();	//クライアントに紐づくジャンルオブジェクトのリストを取得
		List<String> genreNamesWithClient = genreService.getGenreNamesWithClient(genres);	//ジャンル名のリストに変換
		model.addAttribute("genreNamesWithClient", genreNamesWithClient);
		
		//フロア情報
		List<Floor> floors = client.getFloors();
		model.addAttribute("floors", floors);
		
		//画像表示
		List<MediaFormat> byteImages = s3DownloadHelper.getImage(client);

		model.addAttribute("topImage", clientMediaService.getTopImage(byteImages));
		model.addAttribute("subImages", clientMediaService.getSubImage(byteImages));
		
		return "client/form";
	}
	
	//クライアント編集
	@PostMapping("/{clientId}/edit")
	public String editClient(
			@Valid Client client, BindingResult bindingResult,
			String[] genreNames,
			@PathVariable Integer clientId,
			Model model
			) {
		
		if (bindingResult.hasErrors()) {
			
			client = clientService.getClientById(clientId);
			
			//ジャンル情報
			model.addAttribute("genreNamesWithClient", genreNames);
			
			//画像表示
			List<MediaFormat> byteImages = s3DownloadHelper.getImage(client);

			model.addAttribute("topImage", clientMediaService.getTopImage(byteImages));
			model.addAttribute("subImages", clientMediaService.getSubImage(byteImages));
			
			return "client/form";
		}
		
		Area area = clientService.getClientById(clientId).getArea();
		if(area == null) {
			
			area = new Area();
			area.setClient(client);
		}
		area.setAreaName(client.getArea().getAreaName());
		
		client.setArea(area);
		client.setAccount((Account)model.getAttribute("currentAccount"));
		client.setLastModified(LocalDateTime.now());
		clientService.saveClient(client);	//クライアントをアップデート
		
		genreService.deleteGenreWithClient(clientId);	//クライアントに紐づくダンスジャンルをリセット
		
		if (genreNames !=null) {
			
			genreService.saveGenresWithClient(client, genreNames);	//ダンスジャンルをクライアントに紐づけて再登録
		}
		
		return "redirect:/client/" + client.getId() + "/edit";
	}
	
	//クライアント削除
	@GetMapping("/{clientId}/delete")
	public String deleteClient(@PathVariable("clientId") Client client) {
		
		clientService.deleteClient(client);
		return "redirect:/manage";
	}
}
