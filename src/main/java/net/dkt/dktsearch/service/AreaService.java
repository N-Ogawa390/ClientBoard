package net.dkt.dktsearch.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.Area;
import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.repository.AreaRepository;

/*
//すべてのエリアobjを取得
	public List<Area> findAll
//デフォルト値でエリアobjを作成
	public Area createDefaultArea
//エリアを新規登録
	public Area createArea
//クライアントに紐づけてエリアを登録
	public void createAreaWithClient
//すべてのエリアobjを取得
	public List<Area> getAreaAll
//該当エリアを持つエリアobjリストを取得
	public List<Area> getClientsWithAreaName
//クライアントに紐づくエリア削除
	@Transactional	public void deleteAreaWithClient
 */

@Service
public class AreaService {
	
	@Autowired
	AreaRepository areaRepository;
	
	//すべてのエリアobjを取得
	public List<Area> findAll() {
		return areaRepository.findAll();
	}
	
	//デフォルト値でエリアobjを作成
	public Area createDefaultArea() {
		
		Area area = new Area();
		area.setAreaName("東京都");
		return area;
	}
	
	//エリアを新規登録
	public Area createArea(Area area) {
		return areaRepository.save(area);
	}
	
	//クライアントに紐づけてエリアを登録
	public void createAreaWithClient(Client client, String[] areaNames) {
		for (String a : areaNames) {
			Area area = new Area();
			area.setAreaName(a);
			area.setClient(client);
			createArea(area);
		}
	}
	
	//すべてのエリアobjを取得
	public List<Area> getAreaAll() {
		return areaRepository.findAll();
	}
	
	//該当エリアを持つエリアobjリストを取得
	public List<Area> getClientsWithAreaName(String areaName) {
		return areaRepository.findByAreaName(areaName);
	}
	
	//クライアントに紐づくエリア削除
	@Transactional
	public void deleteAreaWithClient(Integer id) {
		areaRepository.deleteByClientId(id);
	}
	
}
