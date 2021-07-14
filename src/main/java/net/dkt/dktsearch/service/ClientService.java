package net.dkt.dktsearch.service;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.Client;
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
		client.setPresentation(true);
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
}
