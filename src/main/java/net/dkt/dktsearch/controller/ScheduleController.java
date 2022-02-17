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
import net.dkt.dktsearch.service.ScheduleService;

@Controller
@RequestMapping("/client")
public class ScheduleController {
	
	@Autowired
	private ScheduleService scheduleService;
	
	@GetMapping("/{clientId}/schedule")
	public String schedule(
			@PathVariable("clientId") Client client,
			Schedule schedule,
			Model model
			) {
		
		List<Schedule> schedules = scheduleService.getSchedules(client.getId());
		model.addAttribute("schedules", schedules);
		
		model.addAttribute("client", client);
		model.addAttribute("schedule", schedule);
		return "client/schedule/form";
	}
	
	@PostMapping("/{clientId}/schedule")
	public String createSchedule(
			@Valid Schedule schedule, BindingResult bindingResult,
			@PathVariable("clientId") Client client,
			@RequestParam(name = "dayOfWeek") String dayOfWeek,
			Model model
			) {
		
		if(bindingResult.hasErrors()) {
			
			model.addAttribute("client", client);
			return "client/schedule/form";
		} else if(!checkTime(schedule.getStartTime(), schedule.getEndTime())) {
			
			model.addAttribute("timeError", "※終了時刻は開始時刻より後に設定してください");
			model.addAttribute("client", client);
			return "client/schedule/form";
		}
		
		schedule.setDayOfWeek(dayOfWeek);
		schedule.setClient(client);
		
		scheduleService.saveSchedule(schedule);
		
		return "redirect:/client/" + client.getId() + "/schedule/form";
	}
	
	//スケジュール編集画面表示
	@GetMapping("/{clientId}/schedule/{scheduleId}/edit")
	public String editSchedule(
			@PathVariable("clientId") Client client,
			@PathVariable("scheduleId") Schedule schedule,
			Model model) {
		
		model.addAttribute("client", client);
		model.addAttribute("schedule", schedule);
		
		return "client/schedule/edit";
	}
	
	//スケジュール編集
	@PostMapping("/{clientId}/schedule/{scheduleId}/edit")
	public String editSchedule(
			@Valid Schedule schedule, BindingResult bindingResult,
			@PathVariable("clientId") Client client,
			@PathVariable("scheduleId") Integer scheduleId,
			@RequestParam(name = "dayOfWeek") String dayOfWeek,
			Model model
			) {
		
		if(bindingResult.hasErrors()) {
			
			model.addAttribute("client", client);
			model.addAttribute("schedule", schedule);
			return "client/schedule/edit";
		} else if(!checkTime(schedule.getStartTime(), schedule.getEndTime())) {
			
			model.addAttribute("timeError", "※終了時刻は開始時刻より後に設定してください");
			model.addAttribute("client", client);
			model.addAttribute("schedule", schedule);
			return "client/schedule/edit";
		}
		
		schedule.setDayOfWeek(dayOfWeek);
		schedule.setClient(client);
		
		scheduleService.saveSchedule(schedule);
		
		return "redirect:/client/{clientId}/schedule";
	}
	
	//スケジュール削除
	@GetMapping("/{clientId}/schedule/{scheduleId}/delete")
	public String deleteSchedule(
			@PathVariable("scheduleId") Integer scheduleId) {
		
		scheduleService.deleteSchedule(scheduleId);
		return "redirect:/client/{clientId}/schedule";
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
