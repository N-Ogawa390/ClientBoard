package net.dkt.dktsearch.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.Floor;
import net.dkt.dktsearch.model.Genre;
import net.dkt.dktsearch.model.GenreCategory;
import net.dkt.dktsearch.repository.GenreCategoryRepository;
import net.dkt.dktsearch.repository.GenreRepository;

/*
//すべてのジャンル名一覧を取得 ※重複なし
	public List<String> getGenreNameAll
//クライアントに紐づくジャンル名一覧取得
	public List<String> getGenreNamesWithClient
//ジャンルを新規作成
	public Genre saveGenre
//クライアントに紐づいたジャンルを登録
	public List<Genre> saveGenresWithClient
//クライアントに紐づいたジャンル削除
	@Transactional	public void deleteGenreWithClient
//指定のジャンル名を持つジャンルobjリストを取得
	public List<Genre> getGenresFromGenreName
//Map<カテゴリ名, Map<カテゴリに属するジャンル名, List<クライアントobj>>> を取得
	public Map<String, Map<String, List<Client>>> categoriesGenresClientsMap
//ジャンル名からカテゴリobjを取得
	public GenreCategory getGenreCategoryFromGenreName
//ジャンルカテゴリをsave
	public void saveGenreCategory
//ジャンル削除
	public void deleteGenreByGenreName
//ジャンル名をキーにジャンルカテゴリを削除
	public void deleteGenreCategoryByGenreName
 */

@Service
public class GenreService {
	
	@Autowired
	private GenreRepository genreRepository;
	
	@Autowired
	private GenreCategoryRepository genreCategoryRepository;
	
	final String[] genreCategories = {"ヒップホップ系", "ジャズ系", "その他（ストリートダンス）", "その他"};
	
	//すべてのジャンル名一覧を取得 ※重複なし
	public List<String> getGenreNameAll() {
		
		List<Genre> genreAll = genreRepository.findAll();	//全てのジャンルオブジェクトを取得
		List<String> genreNameAllDuplication = genreAll.stream().map(g -> g.getGenreName()).collect(Collectors.toList());	//すべてのジャンル名のリストに変換
		List<String> genreNameAll = new ArrayList<>(new HashSet<>(genreNameAllDuplication));	//重複を削除
		
		return genreNameAll;
	}
	
	
	//クライアント編集用............................................................
	
	//クライアントに紐づくジャンル名一覧取得
	public List<String> getGenreNamesWithClient(List<Genre> genres) {
		
		List<String> genreNamesWithClient = genres.stream().map(g -> g.getGenreName()).collect(Collectors.toList());
		return genreNamesWithClient;
	}
	
	//ジャンルを新規作成
	public Genre saveGenre(Genre genre) {
		return genreRepository.save(genre);
	}
	
	//クライアントに紐づいたジャンルを登録
	public List<Genre> saveGenresWithClient(Client client, String[] genreNames) {
		
		List<Genre> genresWithClient = new ArrayList<>();
		for (String a : genreNames) {
			Genre genre = new Genre();
			genre.setGenreName(a);
			genre.setClient(client);
			genresWithClient.add(genreRepository.save(genre));
		}
		
		return genresWithClient;
	}
	
	//クライアントに紐づいたジャンル削除
	@Transactional
	public void deleteGenreWithClient(Integer id) {
		genreRepository.deleteByClientId(id);
	}
	
	//クライアント編集用(ここまで)............................................................

	
	//ジャンル検索用............................................................
	
	//指定のジャンル名を持つジャンルobjリストを取得
	public List<Genre> getGenresFromGenreName(String genreName) {
		return genreRepository.findByGenreName(genreName);
	}
	
	//Map<カテゴリ名, Map<カテゴリに属するジャンル名, List<クライアントobj>>> を取得
	public Map<String, Map<String, List<Client>>> categoriesGenresClientsMap() {
		
		//Map<カテゴリ名, List<カテゴリに属するジャンル名>> を初期化
		Map<String, Map<String, List<Client>>> genreNamesOfCategories = new HashMap<>();
		
		for (String categoryName : genreCategories) {
			
			//該当カテゴリ名を持つカテゴリobjリストを取得 ※重複なし
			List<GenreCategory> categoriesOfCategoryName = genreCategoryRepository.findByGenreCategoryName(categoryName);
			//ジャンル名のリストに変換
			List<String> genreNamesOfCategory = categoriesOfCategoryName.stream().map(gc -> gc.getGenreName()).collect(Collectors.toList());
			
			//Map<ジャンル名, List<該当ジャンルを取り扱うクライアントobj>> を初期化
			Map<String, List<Client>> clientsOfGenreName = new HashMap<>();
			
			for(String genreName : genreNamesOfCategory) {
				
				//該当のジャンル名を持つジャンルobjのリストを取得
				List<Genre> genresOfGenreName = genreRepository.findByGenreName(genreName);
				
				//該当ジャンルを持つクライアントobjのリストに変換
				List<Client> clients = genresOfGenreName.stream().map(g -> g.getClient()).collect(Collectors.toList());
				clients.removeAll(Collections.singleton(null));	//リストからnull削除
				
				clientsOfGenreName.put(genreName, clients);	//Map<ジャンル名, List<クライアントobj>> に値を追加 
				
			}
			
			genreNamesOfCategories.put(categoryName, clientsOfGenreName);	//Map<カテゴリ名, Map<ジャンル名, List<クライアントobj>>> に値を追加
		}
		
		return genreNamesOfCategories;
	}
	
	//ジャンル検索用(ここまで)............................................................
	
	
	//ジャンル編集用............................................................
	
	//ジャンル名からカテゴリobjを取得
	public GenreCategory getGenreCategoryFromGenreName(String genreName) {
		
		return genreCategoryRepository.findByGenreName(genreName);
	}
	
	//ジャンルカテゴリをsave
	public void saveGenreCategory(GenreCategory genreCategory) {
		
		genreCategoryRepository.save(genreCategory);
	}
	
	//ジャンル編集用(ここまで)............................................................
	
	//ジャンル削除
	public void deleteGenreByGenreName(String genreName) {
		
		genreRepository.deleteByGenreName(genreName);
	}
	
	//ジャンル名をキーにジャンルカテゴリを削除
	public void deleteGenreCategoryByGenreName(String genreName) {
		
		genreCategoryRepository.deleteByGenreName(genreName);
	}
}
