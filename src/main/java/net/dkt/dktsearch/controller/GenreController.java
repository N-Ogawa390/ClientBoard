package net.dkt.dktsearch.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.Genre;
import net.dkt.dktsearch.model.GenreCategory;
import net.dkt.dktsearch.service.GenreCategoryService;
import net.dkt.dktsearch.service.GenreService;

/*
//ジャンルトップ画面表示
	@GetMapping("")	public String genre
//ジャンル別スクール検索
	@GetMapping("/search")	public String searchGenre
//ジャンル作成画面を表示
	@GetMapping("/create")	public String genreCreate
//ジャンル作成
	@PostMapping("/create")	public String genreCreate
//ジャンル編集画面を表示
	@GetMapping("/edit")	public String genreEdit
//編集ボタンの押されたジャンルを編集可に切り替え
	@GetMapping("/{genreName}/edit")	public String genreEdit
//ジャンル編集
	@Transactional	@PostMapping("/edit")	public String genreEdit
//ジャンル削除
	@Transactional	@GetMapping("/{genreName}/delete")	public String genreDelete
 */

@Controller
@RequestMapping("/genre")
public class GenreController {
	
	@Autowired
	private GenreService genreService;
	
	@Autowired
	private GenreCategoryService genreCategoryService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	//ジャンルトップ画面表示
	@GetMapping("")
	public String genre() {
		
		return "genre/index";
	}
	
	//ジャンル別スクール検索
	@GetMapping("/search")
	public String searchGenre(Model model, @RequestParam(name="genreNames", required = false) String[] genreNames) {
		
		if (genreNames == null) {
			return "genre/index";
		}
		
		//Map<String ジャンル名, List<クライアントobj>
		Map<String, List<Client>> genreNamesWithClients = new HashMap<>();
		
		for(String genreName : genreNames) {
			List<Genre> genres = genreService.getGenresFromGenreName(genreName);	//該当ジャンル名を持つジャンルobjリストを取得
			
			List<Client> clients = genres.stream().map(g -> g.getClient()).collect(Collectors.toList());	//該当ジャンルを持つクライアントobjのリストに変換
			clients.removeAll(Collections.singleton(null));	//リストからnull削除
			
			genreNamesWithClients.put(genreName, clients);
		}
		
		model.addAttribute("genreNamesWithClients", genreNamesWithClients);
		
		return "genre/index";
	}
	
	//ジャンル作成画面を表示
	@GetMapping("/create")
	public String genreCreate(Genre genre, GenreCategory genreCategory) {
		return "genre/form";
	}
	
	//ジャンル作成
	@PostMapping("/create")
	public String genreCreate(
			@Valid Genre genre, BindingResult bindingResult,
			@RequestParam(name="genreCategoryName") String genreCategoryName
			) {
		
		if (bindingResult.hasErrors()) {
			return "genre/form";
		}
		
		//ジャンルカテゴリを作成
		GenreCategory genreCategory = new GenreCategory();
		genreCategory.setGenreCategoryName(genreCategoryName);
		genreCategory.setGenreName(genre.getGenreName());
		genreCategoryService.createGenreCategory(genreCategory);
		
		//ジャンルを作成
		genreService.saveGenre(genre);
		
		return "redirect:/genre/";
	}
	
	//ジャンル編集画面を表示
	@GetMapping("/edit")
	public String genreEdit() {
		return "genre/edit";
	}
	
	//編集ボタンの押されたジャンルを編集可に切り替え
	@GetMapping("/{genreName}/edit")
	public String genreEdit(@PathVariable("genreName") String genreName, Model model) {
		
		String categoryName ="";
		Map<String, Map<String, List<Client>>> categoriesGenresClientsMap = 
				(Map<String, Map<String, List<Client>>>) model.getAttribute("categoriesGenresClientsMap");
		
		//カテゴリ名を取得
		for (Map.Entry<String, Map<String, List<Client>>> entry : categoriesGenresClientsMap.entrySet()) {
			for(Map.Entry<String, List<Client>> entry2 : entry.getValue().entrySet()) {
				if (entry2.getKey().equals(genreName)) {
					categoryName = entry.getKey();
				}
			}
		}
		
		//ジャンル名とカテゴリ名をモデルにセット
		model.addAttribute("categoryNameEdit", categoryName);
		model.addAttribute("genreNameEdit", genreName);
		
		return "genre/edit";
	}
	
	//ジャンル編集
	@Transactional
	@PostMapping("/edit")
	public String genreEdit(@RequestParam("newGenreName") String newGenreName,
			@RequestParam("newCategoryName") String newCategoryName,
			@RequestParam("oldGenreName") String oldGenreName
	) {
		
		String sql = "UPDATE genre SET genre_name = '" + newGenreName + "' WHERE genre_name = '" + oldGenreName + "';";
		jdbcTemplate.update(sql);
		
		GenreCategory genreCategory = genreService.getGenreCategoryFromGenreName(oldGenreName);
		genreCategory.setGenreName(newGenreName);
		genreCategory.setGenreCategoryName(newCategoryName);
		genreService.saveGenreCategory(genreCategory);
		
		return "redirect:/genre/edit";
	}
	
	//ジャンル削除
	@Transactional
	@GetMapping("/{genreName}/delete")
	public String genreDelete(@PathVariable("genreName") String genreName) {
		
		genreService.deleteGenreByGenreName(genreName);
		genreService.deleteGenreCategoryByGenreName(genreName);
		
		return "redirect:/genre/edit";
	}
}
