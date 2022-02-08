package net.dkt.dktsearch.controller;

import java.time.DayOfWeek;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		
		model.addAttribute("client", client);
		model.addAttribute("schedule", schedule);
		return "client/schedule";
	}
	
	@PostMapping("/{clientId}/schedule")
	public String createSchedule(
			@Valid Schedule schedule, BindingResult bindingResult,
			@PathVariable("clientId") Client client,
			@RequestParam(name = "dayOfWeek") DayOfWeek dayOfWeek,
			Model model
			) {
		
		System.out.println(schedule.getStartTime().toString());
//		System.out.println(schedule.getStartTime().getClass().getSimpleName());
//		System.out.println(schedule.getEndTime().getClass().getSimpleName());
		
		if(bindingResult.hasErrors()) {
			model.addAttribute(client);
			System.out.println(bindingResult.getFieldError());
			return "client/schedule";
		}
		
		schedule.setDayOfWeek(dayOfWeek);
		schedule.setClient(client);
		
		return "redirect:client/" + client.getId() + "schedule";
	}
}
