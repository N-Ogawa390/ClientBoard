package net.dkt.dktsearch.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.dkt.dktsearch.S3DownloadHelper;
import net.dkt.dktsearch.S3UploadHelper;
import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.ClientMedia;
import net.dkt.dktsearch.model.MediaFormat;
import net.dkt.dktsearch.service.ClientMediaService;

@Controller
@RequestMapping("/manage/client")
public class ClietnMediaController {
	
	@Autowired
	private ClientMediaService clientMediaService;
	
	@Autowired
	private S3UploadHelper s3UploadHelper;
	
	@Autowired
	private S3DownloadHelper s3DownloadHelper;
	
	//画像編集画面表示
	@GetMapping("/{clientId}/medias")
	public String medias(@PathVariable("clientId") Client client, Model model) {
		
		model.addAttribute("client", client);
		
		//画像表示
		List<MediaFormat> byteImages = s3DownloadHelper.getImage(client);
		model.addAttribute("topImage", clientMediaService.getTopImage(byteImages));
		model.addAttribute("subImages", clientMediaService.getSubImage(byteImages));
		
		return "manage/client/medias";
	}
	
	//画像の追加
	@PostMapping("/{clientId}/upload")
	public String upload(
			@RequestParam MultipartFile file,
			@RequestParam String mediaType,
			@PathVariable("clientId") Client client,
			HttpServletResponse httpServletResponse,
			Model model) {
		
		if(file.getSize() >= 1000000) {
			if(mediaType.equals("t")) {
				
				model.addAttribute("mediaUploadTopMaxSizeError", "※ファイルサイズが大きすぎます（最大10MB）");
			} else {
				
				model.addAttribute("mediaUploadSubMaxSizeError", "※ファイルサイズが大きすぎます（最大10MB）");
			}
				
			model.addAttribute("client", client);
				
			//画像表示
			List<MediaFormat> byteImages = s3DownloadHelper.getImage(client);
			model.addAttribute("topImage", clientMediaService.getTopImage(byteImages));
			model.addAttribute("subImages", clientMediaService.getSubImage(byteImages));
				
			return "manage/client/medias";				
		}
		try {

			s3UploadHelper.saveFile(file, client, mediaType);
				//AWSストレージに保存するためServiceに投げるのではなくS3UploadHelperを経由
				//Service→S3UploadHelperの方がわかりやすいかも…
			
		} catch (IOException e) {

			model.addAttribute("client", client);
			
			//画像表示
			List<MediaFormat> byteImages = s3DownloadHelper.getImage(client);
			model.addAttribute("topImage", clientMediaService.getTopImage(byteImages));
			model.addAttribute("subImages", clientMediaService.getSubImage(byteImages));
			
			model.addAttribute("mediaUploadMaxNumError", e.getMessage());	//エラーメッセージを表示
			
			return "manage/client/medias";
		}
		
		return "redirect:/manage/client/{clientId}/medias";
	}
	
	//画像の優先度を上げる・下げる
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
		
		return "redirect:/manage/client/{clientId}/medias";
	}
	
	//クライアント画像削除
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
		model.addAttribute("topImages", clientMediaService.getTopImage(byteImages));
		model.addAttribute("subImages", clientMediaService.getSubImage(byteImages));
		
		return "redirect:/manage/client/{clientId}/medias";
	}
}
