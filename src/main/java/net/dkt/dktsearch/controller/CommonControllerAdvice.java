package net.dkt.dktsearch.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import net.dkt.dktsearch.model.Account;
import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.service.AccountService;
import net.dkt.dktsearch.service.GenreService;

/*
//common.htmlで、navのユーザ名からアカウント編集ページへ遷移させるため
	@ModelAttribute("user")	public Object account
//現在のアカウント情報を取得
	@ModelAttribute	public void currentAccount
//チェックボックスデータ登録に使用するエリア名格納用リスト
	@ModelAttribute("areaNamesWithClient")	public List<String> areaNameData
//クライアント作成フォーム・エリア名チェックボックス表示のため
	@ModelAttribute("areaNameList")	public List<String> areaNameList
//ジャンル名一覧取得 ※クライアント作成・編集時のデフォルト表示のため
	@ModelAttribute("genreNameList")	public List<String> genreNameList
//Map<カテゴリ名, Map<カテゴリに属するジャンル名, List<クライアントobj>>> をモデルに登録
	@ModelAttribute	public void categoriesGenresClientsMap
//クライアント作成フォーム・ジャンル名チェックボックス表示のため
	@ModelAttribute("genreNamesWithClient")	public List<String> genreNamesWithClient
 */

@ControllerAdvice
public class CommonControllerAdvice {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private GenreService genreService;
	
	//common.htmlで、navのユーザ名からアカウント編集ページへ遷移させるため
	@ModelAttribute("user")
	public Object account() {
		
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user;
	}
	
	//現在のアカウント情報を取得
	@ModelAttribute
	public void currentAccount(Model model, HttpServletRequest httpServletRequest) {
		
		if (httpServletRequest.getRemoteUser() != null) {
			
			String userName = httpServletRequest.getRemoteUser();
			Account currentAccount = accountService.find(userName);
			
			model.addAttribute("currentAccount", currentAccount);
		}
		
	}
	
	//チェックボックスデータ登録に使用するエリア名格納用リスト
	@ModelAttribute("areaNamesWithClient")
	public List<String> areaNameData() {
		List<String> areaNameData = new ArrayList<>();
		return areaNameData;
	}
	
	//クライアント作成フォーム・エリア名チェックボックス表示のため
	@ModelAttribute("areaNameList")
	public List<String> areaNameList() {
		final String[] areaNameList = {
			"北海道", "青森県", "岩手県", "宮城県",
			"秋田県", "山形県", "福島県", "茨城県",
			"栃木県", "群馬県", "埼玉県", "千葉県",
			"東京都", "神奈川県", "新潟県", "富山県",
			"石川県", "福井県", "山梨県", "長野県",
			"岐阜県", "静岡県", "愛知県", "三重県",
			"滋賀県", "京都府", "大阪府", "兵庫県",
			"奈良県", "和歌山県", "鳥取県", "島根県",
			"岡山県", "広島県", "山口県", "徳島県",
			"香川県", "愛媛県", "高知県", "福岡県",
			"佐賀県", "長崎県", "熊本県", "大分県",
			"宮崎県", "鹿児島県", "沖縄県" 
		};
		return Arrays.asList(areaNameList);
	}
	
	//ジャンル名一覧取得 ※クライアント作成・編集時のデフォルト表示のため
	@ModelAttribute("genreNameList")
	public List<String> genreNameList() { 
		List<String> genreNameList = genreService.getGenreNameAll();
		return genreNameList;
	}
	
	//Map<カテゴリ名, Map<カテゴリに属するジャンル名, List<クライアントobj>>> をモデルに登録
	@ModelAttribute
	public void categoriesGenresClientsMap(Model model) {
		
		Map<String, Map<String, List<Client>>> categoriesGenresClientsMap = genreService.categoriesGenresClientsMap();
		model.addAttribute("categoriesGenresClientsMap", categoriesGenresClientsMap);
	}
	
	//クライアント作成フォーム・ジャンル名チェックボックス表示のため
	@ModelAttribute("genreNamesWithClient")
	public List<String> genreNamesWithClient() {
		List<String> genreNamesWithClient = new ArrayList<>();
		return genreNamesWithClient;
	}
}
