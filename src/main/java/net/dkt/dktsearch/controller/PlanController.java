package net.dkt.dktsearch.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.Plan;
import net.dkt.dktsearch.model.PlanType;
import net.dkt.dktsearch.service.ClientService;
import net.dkt.dktsearch.service.PlanService;
import net.dkt.dktsearch.service.PlanTypeService;

@Controller
public class PlanController {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private PlanService planService;
	
	@Autowired
	private PlanTypeService planTypeService;
	
	//クライアントに紐づくプラン作成画面表示
	@GetMapping("/client/{clientId}/plan/create")
	public String plan(@PathVariable("clientId") Client client,
			Plan plan,
			Model model) {
		
		clientService.checkClientOwner(client, model);

		model.addAttribute("client", client);	//クライアント名呼び出しのため
		model.addAttribute("plan", plan);
		
		model.addAttribute("planTypeForSelected", null);	//編集時selected用フィールドの初期化(新規作成時は意味はない)
		
		return "client/plan/form";
	}
	
	//クライアントに紐づくプラン作成
	@PostMapping("/client/{clientId}/plan/create")
	public String createPlan(
			@Valid Plan plan, BindingResult bindingResult,
			@PathVariable("clientId") Client client,
			@RequestParam(name="planTypeName") String planTypeName,
			Model model
			) {
		
		if (bindingResult.hasErrors()) {
			
			model.addAttribute("client", client);	//クライアント名呼び出しのため
			return "client/plan/form";
		}
		
		//プランタイプ(クラス・チケット・フリー等)を作成し、プランをセット
		PlanType planType = new PlanType();
		planType.setPlan(plan);
		planType.setPlanTypeName(planTypeName);
		
		//プランにクライアント・プランタイプをセット
		plan.setClient(client);
		plan.setPlanType(planType);
		
		//プランとプランタイプを保存
		plan = planService.savePlan(plan);
		planType = planTypeService.savePlanType(planType);
		
		//クライアントに予算をセット
//		client.setBudget(planService.getBudget(client));
		
		clientService.saveClient(client);
		
		return "redirect:/client/" + client.getId() + "/edit";
	}
	
	//クライアントに紐づくプラン編集画面表示
	@GetMapping("/client/{clientId}/plan/{planId}/edit")
	public String editPlanWithClientGet(
			@PathVariable("clientId") Client client,
			@PathVariable("planId") Plan plan,
			Model model) {
		
		clientService.checkClientOwner(client, model);
		
		model.addAttribute("client", client);
		model.addAttribute("plan", plan);
		model.addAttribute("planTypeForSelected", plan.getPlanType().getPlanTypeName());	//プランタイプselected用フィールド
		
		return "client/plan/form";
	}
	
	//クライアントに紐づくプラン編集
	@PostMapping("/client/{clientId}/plan/{planId}/edit")
	public String editPlanWithClientPost(
			@RequestParam(name="planTypeName") String planTypeName,
			@PathVariable("clientId") Client client,
			@Valid Plan plan, BindingResult bindingResult,
			Model model
			) {
		
		if (bindingResult.hasErrors()) {
			
			model.addAttribute("client", client);
			
			return "client/plan/form";
		}
		
		PlanType planType = planTypeService.getPlanTypeFromPlanId(plan.getId());
		planType.setPlanTypeName(planTypeName);
		planType = planTypeService.savePlanType(planType);
		
		plan.setPlanType(planType);
		plan = planService.savePlan(plan);
		
//		client.setBudget(planService.getBudget(plan.getClient()));
		
		clientService.saveClient(client);
		
		return "redirect:/client/" + client.getId() + "/edit";
	}
	
	//クライアントに紐づくプラン削除
	@GetMapping("/client/{clientId}/plan/{planId}/delete")
	public String deletePlanWithClient(@PathVariable("planId") Plan plan,
			@PathVariable("clientId") Client client,	//checkClientOwnerのため
			Model model	//checkClientOwnerのため
			) {
		
		clientService.checkClientOwner(client, model);
		
		planService.deletePlan(plan);
		
		return "redirect:/";
	}
}
