package net.dkt.dktsearch.service;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseStatus;

import net.dkt.dktsearch.model.Account;
import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.Plan;
import net.dkt.dktsearch.repository.ClientRepository;

/*
//デフォルト値でクライアントを作成(テスト用)
	public Client createDefaultClient
//クライアントオブジェクト一覧を取得
	public List<Client> getClientAll
//クライアントを作成・更新
	public Client saveClient
//クライアントを削除
	public void deleteClient
//クライアントIDのリストからクライアントオブジェクトのリストを取得
	public List<Client> getClientsWithClientIds
*/

@Service
public class ClientService {
	
	@Autowired
	private ClientRepository clientRepository;
	
	//デフォルト値でクライアントを作成(テスト用)
	public Client createDefaultClient() {
		
		Client client = new Client();
		client.setClientName("DANCE回転灯スクール");
		client.setAccess("吉祥寺駅徒歩5分");
		client.setPresentation("有");
		clientRepository.save(client);
		
		return client;
	}
	
	//クライアントオブジェクト一覧を取得
	public List<Client> getClientAll() {
		
		List<Client> clients = clientRepository.findAll();
		
		return clients;
	}
	
	//閉店していないクライアントオブジェクト一覧を取得
	public List<Client> getActiveClients() {
		
		List<Client> clients = clientRepository.findByActiveFalse();
		
		return clients;
	}
	
	//クライアントIDからクライアントオブジェクトを取得
	public Client getClientById(Integer id) {
		
		return clientRepository.findById(id).get();
	}

	//クライアントを作成・更新
	public Client saveClient(Client client) {
		
		return clientRepository.save(client);
	}
	
	//クライアントを削除
	public void deleteClient(Client client) {
		
		clientRepository.delete(client);
	}
	
	//クライアントIDのリストからクライアントオブジェクトのリストを取得
	public List<Client> getClientsWithClientIds(List<Integer> clientIds) {
		
		List<Client> clients = new ArrayList<>();
		
		for (Integer clientId : clientIds) {
			
			Client client = clientRepository.findById(clientId).get();
			clients.add(client);
		}
		
		return clients;
	}
	
	//クライアントオブジェクトのリストからクライアントIDと月4予算のマップを返却
	//※検索結果スクールカード予算表示用
	//プランタイプ：ALL or キッズを選択
	public Map<Integer, Integer> getMapBudgetWithClientId(List<Client> clients, String ageGroup) {
		
		Map<Integer, Integer> budgetWithClientIdMap = new HashMap<>();
		
		for(Client client : clients) {
			
			budgetWithClientIdMap.put(client.getId(), getMinBudget(client, ageGroup));
		}
		
		return budgetWithClientIdMap;
	}
	
	//クライアントの持つプランの中で月4価格の最も低いものを週出 ※プランタイプ：ALL or キッズを選択
	public Integer getMinBudget(Client client, String ageGroup) {
		
		Integer minBudget = null;
		
		for(Plan plan : client.getPlans()) {
			
			String planType = plan.getPlanType().getPlanTypeName();
			Boolean online = plan.getOnline();
			Integer price = plan.getPrice();
			
			if(
					(planType.equals("クラス4") || planType.equals("チケット4")) &&
					(online == null || online == false) &&
					plan.getAgeGroup().equals(ageGroup) &&
					(minBudget == null || minBudget > price)
					) {
				
				minBudget = price;
			}
		}
		
		return minBudget;
	}
	
	//他クライアントへの不正アクセス防止用
	//※リクエストされたクライアント(client)と現在のclient(modelから取り出す)が一致するか比較
	//クライアントを識別するGetMapメソッドに設定する
	public void checkClientOwner(Client client, Model model) {
		
		Account currentAccount = (Account)model.getAttribute("currentAccount");
		
		if (client.getAccount() != currentAccount) {
			throw new ForbiddenAccountAccessException("アクセスが許可されていません");
		}
	}
	
	//不正アクセス時のメッセージを設定
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public class ForbiddenAccountAccessException extends RuntimeException {
		
		private static final long serialVersionUID = 1L;
		
		public ForbiddenAccountAccessException(String message) {
			super(message);
		}
	}
}
