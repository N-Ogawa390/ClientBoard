package net.dkt.dktsearch.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.Plan;
import net.dkt.dktsearch.repository.PlanRepository;

/*
//プランをsave
	public Plan savePlan
//プランを削除
	public void deletePlan
//予算を取得
	public String getBudget
*/

@Service
public class PlanService {
	
	@Autowired
	private PlanRepository planRepository;
	
	//プランをsave
	public Plan savePlan(Plan plan) {
		return planRepository.save(plan);
	}
	
	//プランを削除
	public void deletePlan(Plan plan) {
		planRepository.delete(plan);
	}
	
	//予算を取得
	public String getBudget(Client client) {
		
		String budget = null;	//予算を初期化
		List<Integer> provisional = new ArrayList<>();	//予算候補のリスト	
		List<Plan> plans = client.getPlans();
		
		//一般料金があれば最小値を予算にセット
		for(Plan plan : plans) {
//			System.out.println(plan.getPlanName());
//			System.out.println(plan.getPlanType().getPlanTypeName());
//			System.out.println(plan.getAgeGroup());
			if (
					(plan.getPlanType().getPlanTypeName().equals("チケット4") && plan.getAgeGroup().equals("ALL")) ||
					(plan.getPlanType().getPlanTypeName().equals("クラス4") && plan.getAgeGroup().equals("ALL"))
					) {
				provisional.add(plan.getPrice());
			}
		}
		
//		System.out.println("一般料金HIT件数" + provisional.size());
		
		if (provisional.size() != 0) {	//暫定リストに値があれば
			Collections.sort(provisional);	//小さい順にソート
			budget = provisional.get(0).toString();	//最初の要素を予算に格納
			return budget;
			
		} else {
			
			//一般料金が設定されていなければキッズの料金を予算にセット
			for(Plan plan : plans) {
				if (
						(plan.getPlanType().getPlanTypeName().equals("チケット4") && plan.getAgeGroup().equals("キッズ")) ||
						(plan.getPlanType().getPlanTypeName().equals("クラス4") && plan.getAgeGroup().equals("キッズ"))
						) {
					provisional.add(plan.getPrice());
				}
			}
			
//			System.out.println("キッズ料金HIT件数" + provisional.size());
			
			if (provisional.size() != 0) {	//暫定リストに値があれば
				Collections.sort(provisional);	//小さい順にソート
				budget = provisional.get(0).toString() + "(キッズのみ)";	//最初の要素を予算に格納
				return budget;
			} else {
				//一般もキッズも料金が設定されていなければそのままnullを返す
				System.out.println("budgetはnull");
				return budget;
			}
		}
	}
	
}
