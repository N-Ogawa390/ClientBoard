package net.dkt.dktsearch.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import net.dkt.dktsearch.S3DownloadHelper;
import net.dkt.dktsearch.S3UploadHelper;
import net.dkt.dktsearch.model.Account;
import net.dkt.dktsearch.model.Area;
import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.ClientMedia;
import net.dkt.dktsearch.model.Genre;
import net.dkt.dktsearch.model.MediaFormat;
import net.dkt.dktsearch.model.Plan;
import net.dkt.dktsearch.model.PlanType;
import net.dkt.dktsearch.service.AccountService;
import net.dkt.dktsearch.service.AreaService;
import net.dkt.dktsearch.service.ClientMediaService;
import net.dkt.dktsearch.service.ClientService;
import net.dkt.dktsearch.service.GenreService;
import net.dkt.dktsearch.service.PlanService;
import net.dkt.dktsearch.service.PlanTypeService;

/*
//使ってないかも
	@GetMapping("")	client
//クライアント詳細画面表示
	@GetMapping("/{clientId}")	public String showClient
//クライアント作成画面表示
	@GetMapping("/create")	public String createClient
//クライアント作成
	@PostMapping("/create")	public String createClient
//クライアント編集画面表示
	@Transactional	@GetMapping("/{clientId}/edit")	public String editClient
//クライアント編集
	@PostMapping("/{clientId}/edit")	public String editClien
//クライアント削除
	@GetMapping("/{clientId}/delete")	public String deleteClient
//クライアントに紐づくプラン作成画面表示
	@GetMapping("/{clientId}/plan/create")	public String plan
//クライアントに紐づくプラン作成
	@PostMapping("/{clientId}/plan/create")	public String createPlan
//クライアントに紐づくプラン編集画面表示
	@GetMapping("/{clientId}/plan/{planId}/edit")	public String editPlanWithClientGet
//クライアント編集
	@PostMapping("/{clientId}/edit")	public String editClient
//クライアントに紐づくプラン削除
	@GetMapping("/{clientId}/plan/{planId}/delete")	public String deletePlanWithClient
//他クライアントへの不正アクセス防止用
	private void checkClientOwner
//不正アクセス時のメッセージを設定
	@ResponseStatus(HttpStatus.FORBIDDEN)	private class ForbiddenAccountAccessException extends RuntimeException
*/

@Controller
@RequestMapping("/client")
public class ClientController {
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private AreaService areaService;
	
	@Autowired
	private GenreService genreService;
	
	@Autowired
	private PlanService planService;
	
	@Autowired
	private PlanTypeService planTypeService;
	
	@Autowired
	private S3UploadHelper s3UploadHelper;
	
	@Autowired
	private S3DownloadHelper s3DownloadHelper;
	
	@Autowired
	private ClientMediaService clientMediaService;
	
	@GetMapping("")
	public String client(Model model) {
		model.addAttribute("clients", clientService.getClientAll());
		return "client/index";
	}
	
	//クライアント詳細画面表示
	@GetMapping("/{clientId}")
	public String showClient(@PathVariable("clientId") Client client, Model model) {

		//画像表示
		List<MediaFormat> byteImages = s3DownloadHelper.getImage(client);

		model.addAttribute("client", client);
		model.addAttribute("topImages", getTopImage(byteImages));
		model.addAttribute("subImages", getSubImage(byteImages));
		
		return "client/show";
	}

	//クライアント作成画面表示
	@GetMapping("/create")
	public String createClient(Client client, Model model) {
		
		Area area = areaService.createDefaultArea();	//地域のデフォルト値をセット
		client.setArea(area);
		
		return "client/form";
	}
	
	//クライアント作成
	@PostMapping("/create")
	public String createClient(@Valid Client client, BindingResult bindingResult,
			@RequestParam(required = false) String[] areaNames,
			@RequestParam(required = false) String[] genreNames,
			Model model
			) {
		
		if (bindingResult.hasErrors()) {
			return "client/form";
		}
		
		client.setAccount((Account)model.getAttribute("currentAccount"));
		clientService.saveClient(client);
		
		if (areaNames != null) {
			areaService.createAreaWithClient(client, areaNames);	//エリアをクライアントに紐づけて再設定
		}
		
		if (genreNames !=null) {
			genreService.saveGenresWithClient(client, genreNames);	//ダンスジャンルをクライアントに紐づけて再登録
		}
		
		return "redirect:/";
	}
	
	//クライアント編集画面表示
	@GetMapping("/{clientId}/edit")
	public String editClient(@PathVariable("clientId") Client client, Model model) {
		
		checkClientOwner(client, model);
		
		model.addAttribute("client", client);
		
		//ジャンル情報
		List<Genre> genres = client.getGenres();	//クライアントに紐づくジャンルオブジェクトのリストを取得
		List<String> genreNamesWithClient = genreService.getGenreNamesWithClient(genres);	//ジャンル名のリストに変換
		model.addAttribute("genreNamesWithClient", genreNamesWithClient);
		
		//画像表示
		List<MediaFormat> byteImages = s3DownloadHelper.getImage(client);


		model.addAttribute("topImages", getTopImage(byteImages));
		model.addAttribute("subImages", getSubImage(byteImages));
		
		return "client/form";
	}
	
	//クライアント編集
	@Transactional
	@PostMapping("/{clientId}/edit")
	public String editClient(@Valid Client client, BindingResult bindingResult,
			String[] areaNames,
			String[] genreNames,
			@PathVariable Integer clientId,
			Model model
			) {
		
		if (bindingResult.hasErrors()) {
			return "client/form";
		}
		
		client.setAccount((Account)model.getAttribute("currentAccount"));
		clientService.saveClient(client);	//クライアントをアップデート
		areaService.deleteAreaWithClient(clientId);	//クライアントに紐づくエリアをリセット
		genreService.deleteGenreWithClient(clientId);	//クライアントに紐づくダンスジャンルをリセット
		
		if (areaNames != null) {
			areaService.createAreaWithClient(client, areaNames);	//エリアをクライアントに紐づけて再設定
		}
		
		if (genreNames !=null) {
			
			genreService.saveGenresWithClient(client, genreNames);	//ダンスジャンルをクライアントに紐づけて再登録
			
		}
		
		return "redirect:/manage";
	}
	
	//画像編集画面表示
	@GetMapping("/{clientId}/medias")
	public String medias(@PathVariable("clientId") Client client, Model model) {
		
		model.addAttribute("client", client);
		
		//画像表示
		List<MediaFormat> byteImages = s3DownloadHelper.getImage(client);
		model.addAttribute("topImages", getTopImage(byteImages));
		model.addAttribute("subImages", getSubImage(byteImages));
		
		return "client/medias";
	}
	
	//画像の追加
	@PostMapping("{clientId}/upload")
	public String upload(
			@RequestParam MultipartFile file,
			@RequestParam String mediaType,
			@PathVariable("clientId") Client client,
			Model model) {
		
		try {

			s3UploadHelper.saveFile(file, client, mediaType);
				//awsストレージに保存するためServiceに投げるのではなくS3UploadHelperを経由
				//Service→S3UploadHelperの方がわかりやすいかも…
			
		} catch (IOException e) {

			model.addAttribute("client", client);
			
			//画像表示
			List<MediaFormat> byteImages = s3DownloadHelper.getImage(client);
			model.addAttribute("topImages", getTopImage(byteImages));
			model.addAttribute("subImages", getSubImage(byteImages));
			
			model.addAttribute("mediaUploadMaxNumError", e.getMessage());	//エラーメッセージを表示
			
			return "client/medias";
		}
		
		return "redirect:/client/{clientId}/medias";
	}
	
	//画像の優先度を上げる・下げる・削除する
	@GetMapping("/{clientId}/medias/{priority}/{done}")
	public String mediasEdit(
			@PathVariable("clientId") Client client,
			@PathVariable("priority") Integer priority,
			@PathVariable("done") String done
			) {
		
		if(done.equals("raise")) {
			clientMediaService.raisePriority(priority, client);
			
		} else if(done.equals("lower")) {
			clientMediaService.lowerPriority(priority, client);
			
		}
		
		return "redirect:/client/{clientId}/medias";
		
	}
	
	//クライアント画像削除
	@Transactional
	@GetMapping("/{clientId}/medias/delete/{mediaId}")
	public String deleteClientMedia(
			@PathVariable("clientId") Client client,
			@PathVariable("mediaId") ClientMedia clientMedia,
			Model model) {

		clientMediaService.deleteClientMedia(clientMedia);	//該当Idのメディアを削除

		//サブ画像の場合は削除後に優先順位を詰める
		if(clientMedia.getMediaType().equals("s")) {
			
			List<ClientMedia> clientMedias = client.getClientMedias();	//クライアントの画像一覧を取得
			clientMediaService.scootClientMediaPriority(clientMedias, clientMedia.getPriority());	//優先順位を詰める
		}
		
		model.addAttribute("client", client);
		
		//画像表示
		List<MediaFormat> byteImages = s3DownloadHelper.getImage(client);
		model.addAttribute("topImages", getTopImage(byteImages));
		model.addAttribute("subImages", getSubImage(byteImages));
		
		return "client/medias";
	}
	
	//クライアントに紐づくプラン作成画面表示
	@GetMapping("/{clientId}/plan/create")
	public String plan(@PathVariable("clientId") Client client,
			Plan plan,
			Model model) {
		
		checkClientOwner(client, model);

		model.addAttribute("client", client);	//クライアント名呼び出しのため
		model.addAttribute("plan", plan);
		
		model.addAttribute("planTypeForSelected", null);	//編集時selected用フィールドの初期化(新規作成時は意味はない)
		
		return "client/plan/form";
	}
	
	//クライアントに紐づくプラン作成
	@PostMapping("/{clientId}/plan/create")
	public String createPlan(@Valid Plan plan, BindingResult bindingResult,
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
		client.setBudget(planService.getBudget(client));
		clientService.saveClient(client);
		
		return "redirect:/";
	}
	
	//クライアントに紐づくプラン編集画面表示
	@GetMapping("/{clientId}/plan/{planId}/edit")
	public String editPlanWithClientGet(
			@PathVariable("clientId") Client client,
			@PathVariable("planId") Plan plan,
			Model model) {
		
		checkClientOwner(client, model);
		
		model.addAttribute("client", client);
		
		plan.setClient(client);
		model.addAttribute("plan", plan);
		
		model.addAttribute("planTypeForSelected", plan.getPlanType().getPlanTypeName());	//プランタイプselected用フィールド
		
		return "client/plan/form";
	}
	
	//クライアントに紐づくプラン編集
	@PostMapping("/{clientId}/plan/{planId}/edit")
	public String editPlanWithClientPost(
			@RequestParam(name="planTypeName") String planTypeName,
			@PathVariable("clientId") Client client,
			Plan plan) {
		
		PlanType planType = planTypeService.getPlanTypeFromPlanId(plan.getId());
		planType.setPlanTypeName(planTypeName);
		planType = planTypeService.savePlanType(planType);
		
		plan.setPlanType(planType);
		plan = planService.savePlan(plan);
		
		client.setBudget(planService.getBudget(plan.getClient()));
		clientService.saveClient(client);
		
		return "redirect:/client/" + client.getId();
	}
	
	//クライアントに紐づくプラン削除
	@GetMapping("/{clientId}/plan/{planId}/delete")
	public String deletePlanWithClient(@PathVariable("planId") Plan plan,
			@PathVariable("clientId") Client client,	//checkClientOwnerのため
			Model model	//checkClientOwnerのため
			) {
		
		checkClientOwner(client, model);
		
		planService.deletePlan(plan);
		
		return "redirect:/";
	}
	
	//クライアント削除
	@GetMapping("/{clientId}/delete")
	public String deleteClient(@PathVariable("clientId") Client client) {
		clientService.deleteClient(client);
		return "redirect:/";
	}
	
	//他クライアントへの不正アクセス防止用
	//※リクエストされたクライアント(client)と現在のclient(modelから取り出す)が一致するか比較
	//クライアントを識別するGetMapメソッドに設定する
	private void checkClientOwner(Client client, Model model) {
		
		Account currentAccount = (Account)model.getAttribute("currentAccount");
		
		if (client.getAccount() != currentAccount) {
			throw new ForbiddenAccountAccessException("アクセスが許可されていません");
		}
	}
	
	//不正アクセス時のメッセージを設定
	@ResponseStatus(HttpStatus.FORBIDDEN)
	private class ForbiddenAccountAccessException extends RuntimeException {
		
		private static final long serialVersionUID = 1L;
		
		public ForbiddenAccountAccessException(String message) {
			super(message);
		}
	}
	
	//トップ画像の抽出
	private List<MediaFormat> getTopImage(List<MediaFormat> mfList) {
		
		List<MediaFormat> mediaFormatList = new ArrayList<>();
		
		for(MediaFormat mf : mfList) {
			if(mf.getClientMedia().getMediaType().equals("t")) {
				mediaFormatList.add(mf);
			}
		}
		return mediaFormatList;
	}
	
	//サブ画像の抽出
	private List<MediaFormat> getSubImage(List<MediaFormat> mfList) {
		
		List<MediaFormat> mediaFormatList = new ArrayList<>();
		
		for(MediaFormat mf : mfList) {
			if(mf.getClientMedia().getMediaType().equals("s")) {
				mediaFormatList.add(mf);
			}
		}
		return sortMediaFormatList(mediaFormatList);
	}
	
	//List<MediaFormat>を優先度順に並べ替え
	private List<MediaFormat> sortMediaFormatList(List<MediaFormat> mfList){
		
		mfList.sort((a, b)->a.getClientMedia().getPriority().compareTo(b.getClientMedia().getPriority()));
																								//優先度昇順に並び替え
		return mfList;
	}


}
