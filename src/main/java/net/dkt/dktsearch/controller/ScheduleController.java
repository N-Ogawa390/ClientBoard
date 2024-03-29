package net.dkt.dktsearch.controller;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.Schedule;
import net.dkt.dktsearch.service.FloorService;
import net.dkt.dktsearch.service.GenreService;
import net.dkt.dktsearch.service.ScheduleService;

@Controller
@RequestMapping("/manage/client")
public class ScheduleController {
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private GenreService genreService;
	
	@Autowired
	private FloorService floorService;
	
	//スケジュール作成画面表示
	@GetMapping("/{clientId}/schedule")
	public String schedule(
			@PathVariable("clientId") Client client,
			Schedule schedule,
			Model model
			) {
		
		model.addAttribute("client", client);
		model.addAttribute("schedule", schedule);
		return "manage/client/schedule/form";
	}
	
	//スケジュール作成
	@PostMapping("/{clientId}/schedule")
	public String createSchedule(
			@Valid Schedule schedule, BindingResult bindingResult,
			@PathVariable("clientId") Client client,
			@RequestParam(name = "dayOfWeek") String dayOfWeek,
			@RequestParam(name = "genreName", required = false) String genreName,
			@RequestParam(name = "floorName", required = false) String floorName,
			Model model
			) {
		
		if(bindingResult.hasErrors()) {
			
			model.addAttribute("client", client);
			return "manage/client/schedule/form";
		} else if(genreName == "") {
			
			model.addAttribute("client", client);
			return "manage/client/schedule/form";
		} else if(!checkTime(schedule.getStartTime(), schedule.getEndTime())) {
			
			model.addAttribute("timeError", "※終了時刻は開始時刻より後に設定してください");
			model.addAttribute("client", client);
			return "manage/client/schedule/form";
		}
		
		schedule.setGenreName(genreName);
		schedule.setFloor(floorService.getFloorByFloorName(floorName));
		schedule.setDayOfWeek(dayOfWeek);
		schedule.setClient(client);
		
		scheduleService.saveSchedule(schedule);
		
		return "redirect:/manage/client/" + client.getId() + "/schedule";
	}
	
	//スケジュール編集画面表示
	@GetMapping("/{clientId}/schedule/{scheduleId}/edit")
	public String editSchedule(
			@PathVariable("clientId") Client client,
			@PathVariable("scheduleId") Schedule schedule,
			Model model) {
		
		model.addAttribute("client", client);
		model.addAttribute("schedule", schedule);
		
		return "manage/client/schedule/edit";
	}
	
	//スケジュール編集
	@PostMapping("/{clientId}/schedule/{scheduleId}/edit")
	public String editSchedule(
			@Valid Schedule schedule, BindingResult bindingResult,
			@PathVariable("clientId") Client client,
			@RequestParam(name = "dayOfWeek") String dayOfWeek,
			@RequestParam(name = "genreName", required = false) String genreName,
			@RequestParam(name = "floorName", required = false) String floorName,
			Model model
			) {
		
		boolean bindingResultHasError = bindingResult.hasErrors();
		boolean checkTimeHasError = !checkTime(schedule.getStartTime(), schedule.getEndTime());
		
		if(bindingResultHasError || checkTimeHasError) {
			
			System.out.println("error");
			
			if(checkTimeHasError) {
				model.addAttribute("timeError", "※終了時刻は開始時刻より後に設定してください");
			}
			
			schedule.setGenreName(genreName);
			schedule.setFloor(floorService.getFloorByFloorName(floorName));
			
			model.addAttribute("client", client);
			model.addAttribute("schedule", schedule);
			return "manage/client/schedule/edit";
		}
		
		schedule.setGenreName(genreName);
		schedule.setFloor(floorService.getFloorByFloorName(floorName));
		schedule.setDayOfWeek(dayOfWeek);
		schedule.setClient(client);
		
		scheduleService.saveSchedule(schedule);
		
		return "redirect:/manage/client/{clientId}/schedule";
	}
	
	//スケジュール削除
	@GetMapping("/{clientId}/schedule/{scheduleId}/delete")
	public String deleteSchedule(
			@PathVariable("scheduleId") Integer scheduleId) {
		
		scheduleService.deleteSchedule(scheduleId);
		return "redirect:/manage/client/{clientId}/schedule";
	}
	
	//レッスン開始時刻が終了時刻より後になっている場合はエラー
	boolean checkTime(String startTime, String endTime) {
		
		Integer stTime = Integer.parseInt(startTime.replace(":", ""));
		Integer enTime = Integer.parseInt(endTime.replace(":", ""));
		
		if(enTime - stTime > 0) {
			return true;
		}
		
		return false;
	}
}
