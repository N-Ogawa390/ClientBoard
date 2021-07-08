package net.dkt.dktsearch.service;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.ClientMedia;
import net.dkt.dktsearch.repository.ClientMediaRepository;

@Service
public class ClientMediaService {
	
	@Autowired
	ClientMediaRepository clientMediaRepository;
	
	//画像を追加する
	public ClientMedia saveMediaFile(String mediaFileName, Client client, String mediaType) throws IOException {
		
		List<ClientMedia> clientMedias = client.getClientMedias();	//クライアントのメディア一覧を取得
		
		//トップ画像登録の場合	※mediaTypeが"t"
		if(mediaType.equals("t")) {
			
			return saveTopMediaFile(clientMedias, mediaFileName, client);
		
		//サブ画像登録の場合	※mediaTypeが"s"
		} else if(mediaType.equals("s")) {

			return saveSubMediaFile(clientMedias, mediaFileName, client);
		} else {
			
			return null;
		}
	}
	
	//トップ画像の追加　※トップ画像は1つしか登録できないためすでに登録されている場合は既存の画像を削除
	private ClientMedia saveTopMediaFile(List<ClientMedia> clientMedias, String mediaFileName, Client client) {
		
		//トップ画像がすでに存在する場合は削除
		ClientMedia currentTopMedia = checkExistTopMedia(clientMedias);
		if(currentTopMedia != null) {
			
			deleteClientMedia(currentTopMedia);
		}
		
		ClientMedia clientMedia = new ClientMedia();
		
		clientMedia.setMediaFileName(mediaFileName);
		clientMedia.setMediaType("t");
		clientMedia.setClient(client);
		
		return clientMediaRepository.save(clientMedia);
	}
	
	//トップ画像がすでに存在するかチェック　※存在する場合はClientMediaを、存在しなければnullを返す
	private ClientMedia checkExistTopMedia(List<ClientMedia> clientMedias) {
		
		for(ClientMedia clientMedia : clientMedias) {
			
			if(clientMedia.getMediaType().equals("t")) {
				
				return clientMedia;
			}
		}
		
		return null;
	}
	
	//サブ画像の追加　※5枚まで。優先順位は最低値で設定する
	private ClientMedia saveSubMediaFile(List<ClientMedia> clientMedias, String mediaFileName, Client client) throws IOException {

		List<Integer> priorities = clientMedias.stream().map(a -> a.getPriority()).collect(Collectors.toList());	//登録されている画像の優先順位の一覧を取得
		priorities.removeAll(Collections.singleton(null));
		
		//優先順位を決定　※最低値で設定
		Optional<Integer> maxPriority = priorities.stream().max(Comparator.naturalOrder());	//優先順位の最大値を取得
		ClientMedia clientMedia = new ClientMedia();
		
		//最大値がnullで無い場合なければ最大値＋1を優先順位として登録
		if (maxPriority.isPresent()) {
			
			//すでに優先順位が5まである場合はエラー ※画像の最大登録数は5枚まで　　優先順位ではなく画像枚数でチェックした方がよさそう
			if (maxPriority.get() >= 5) {	
				throw new IOException("※サブ画像の最大登録数は5枚までです");
				
			} else {
				clientMedia.setPriority(maxPriority.get() + 1);	//最大値＋1を優先順位として登録
			}
			
		} else {
			clientMedia.setPriority(1);	//最大値がnullの場合は優先順位1で登録
		}
		
		clientMedia.setMediaFileName(mediaFileName);
		clientMedia.setMediaType("s");
		clientMedia.setClient(client);
		return clientMediaRepository.save(clientMedia);
	}
	
	//サブ画像の優先順位を上げる　※1つ優先順位の高い画像は順位を下げる
	public void raisePriority(Integer priority, Client client) {
		
		List<ClientMedia> clientMedias = client.getClientMedias();
		
		Integer upId = 0;	//引数で渡された優先順位の画像のID
		Integer downId = 0;	//引数で渡されたものより優先順位の1つ高い画像のID
		
		for(ClientMedia cm : clientMedias) {
			
			if(cm.getPriority() == priority) {
				upId = cm.getId();
				
			} else if(cm.getPriority() == priority - 1) {
				downId = cm.getId();
			}
		}
		
		for(ClientMedia cm : clientMedias) {
			
			if(cm.getId() == upId) {
				
				cm.setPriority(cm.getPriority() - 1);	//優先順位を上げる
				clientMediaRepository.save(cm);
				
			} else if(cm.getId() == downId) {
				
				cm.setPriority(cm.getPriority() + 1);	//優先順位を下げる
				clientMediaRepository.save(cm);
			}
		}
	}
	
	//サブ画像の優先順位を下げる　※1つ優先順位の低い画像は順位を上げる
	public void lowerPriority(Integer priority, Client client) {
		
		List<ClientMedia> clientMedias = client.getClientMedias();
		
		Integer downId = 0;	//引数で渡された優先順位の画像のID
		Integer upId = 0;	//引数で渡されたものより優先順位の1つ低い画像のID
		
		for(ClientMedia cm : clientMedias) {
			
			if(cm.getPriority() == priority) {
				downId = cm.getId();
				
			} else if(cm.getPriority() == priority + 1) {
				upId = cm.getId();
			}
		}
		
		for(ClientMedia cm : clientMedias) {
			
			if(cm.getId() == downId) {
				
				cm.setPriority(cm.getPriority() + 1);	//優先順位を下げる
				clientMediaRepository.save(cm);
				
			} else if(cm.getId() == upId) {
				
				cm.setPriority(cm.getPriority() - 1);	//優先順位を上げる
				clientMediaRepository.save(cm);
			}
		}
	}
	
	//画像を削除する
	@Transactional
	public void deleteClientMedia(ClientMedia clientMedia) {
		
		clientMediaRepository.delete(clientMedia);	//★消えねぇ あ、S3通してないからか…?いや、他のクラス見るとそんなこともないな…なぜ。
	}
	
	//画像の優先順位の空きを詰める　※サブ画像削除後
	public void scootClientMediaPriority(List<ClientMedia> clientMedias, Integer priority) {
		
		for(ClientMedia cm : clientMedias) {
			
			if(cm.getPriority() != null && cm.getPriority() > priority) {
				
				cm.setPriority(cm.getPriority() - 1);
				clientMediaRepository.save(cm);
			}
		}
	}
	
	
	//画像表示用関数 ※BufferedImageをbase64文字列にして返す
	public String getByteImages(BufferedImage image) {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();	//ストリームをnew
		BufferedOutputStream os = new BufferedOutputStream(bos);	//bufferedImageを書き込めるストリームに変換
		image.flush();	//？？？
		String encodedImage = null;
		try {
			ImageIO.write(image, "png", os);	//ストリーム(os)にbufferedImage(i)をpng形式で書き込み
			encodedImage = Base64.getEncoder().encodeToString(bos.toByteArray());	//base64形式に変換
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return encodedImage;
	}

}
