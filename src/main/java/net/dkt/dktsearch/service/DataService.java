package net.dkt.dktsearch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.Plan;

/*
//プランの平均金額を取得
	public Integer getAveOfPlanPrice
 */

@Service
public class DataService {
	
	//プランの平均金額を取得
	public Integer getAveOfPlanPrice(String ageGroupName, String targetPlanName, List<Client> clientAll) {
		
		Integer sumNumPlan = 0;	//該当するプラン数を初期化
		Integer sumPrice = 0;	//合計料金を初期化
		
		for (Client client : clientAll) {
			List<Plan> plans = client.getPlans();
			for (Plan plan : plans) {
				if (
						plan.getPlanType().getPlanTypeName().equals(targetPlanName) &&
						plan.getAgeGroup().equals(ageGroupName)
						) {
					sumNumPlan ++;
					sumPrice += plan.getPrice();
				}
			}
		}
		
		if (sumNumPlan == 0) {
			return 0;
		}
		
		return sumPrice / sumNumPlan;
	}
}
