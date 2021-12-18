package net.dkt.dktsearch.service;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.S3UploadHelper;
import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.ClientMedia;
import net.dkt.dktsearch.model.MediaFormat;
import net.dkt.dktsearch.repository.ClientMediaRepository;

@Service
public class ClientMediaService {
	
	@Autowired
	private ClientMediaRepository clientMediaRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private S3UploadHelper s3UploadHelper;
	
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
		
		//画像の追加
		ClientMedia clientMedia = new ClientMedia();
		clientMedia.setMediaFileName(mediaFileName);
		clientMedia.setMediaType("t");
		clientMedia.setClient(client);
		clientMedia.setCreated(LocalDateTime.now());
		
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
	@Transactional
	public void raisePriority(Integer priority, Client client) {
		
		List<ClientMedia> clientMedias = client.getClientMedias();
		
		Integer upId = 0;	//引数で渡された優先順位の画像のID
		Integer downId = 0;	//引数で渡されたものより優先順位の1つ高い画像のID
		
		for(ClientMedia clientMedia : clientMedias) {
			
			Integer clientMediaPriority = clientMedia.getPriority();
			
			if(clientMediaPriority != null) {
				
				if(clientMediaPriority == priority) {
					upId = clientMedia.getId();
					
				} else if(clientMediaPriority == priority - 1) {
					downId = clientMedia.getId();
				}
			}
		}
		
		for(ClientMedia clientMedia : clientMedias) {
			
			Integer clientMediaPriority = clientMedia.getPriority();
			
			if(clientMedia.getId() == upId) {
				
				clientMedia.setPriority(clientMediaPriority - 1);	//優先順位を上げる
				clientMediaRepository.save(clientMedia);
				
			} else if(clientMedia.getId() == downId) {
				
				clientMedia.setPriority(clientMediaPriority + 1);	//優先順位を下げる
				clientMediaRepository.save(clientMedia);
			}
		}
	}
	
	//サブ画像の優先順位を下げる　※1つ優先順位の低い画像は順位を上げる
	public void lowerPriority(Integer priority, Client client) {
		
		List<ClientMedia> clientMedias = client.getClientMedias();
		
		Integer downId = 0;	//引数で渡された優先順位の画像のID
		Integer upId = 0;	//引数で渡されたものより優先順位の1つ低い画像のID
		
		for(ClientMedia clientMedia : clientMedias) {
			
			Integer clientMediaPriority = clientMedia.getPriority();
			
			if(clientMediaPriority != null) {
				
				if(clientMediaPriority == priority) {
					downId = clientMedia.getId();
					
				} else if(clientMediaPriority == priority + 1) {
					upId = clientMedia.getId();
				}
			}
		}
		
		for(ClientMedia clientMedia : clientMedias) {
			
			Integer clientMediaPriority = clientMedia.getPriority();
			
			if(clientMedia.getId() == downId) {
				
				clientMedia.setPriority(clientMediaPriority + 1);	//優先順位を下げる
				clientMediaRepository.save(clientMedia);
				
			} else if(clientMedia.getId() == upId) {
				
				clientMedia.setPriority(clientMediaPriority - 1);	//優先順位を上げる
				clientMediaRepository.save(clientMedia);
			}
		}
	}
	
	//画像を削除する
	@Transactional
	public void deleteClientMedia(ClientMedia clientMedia) {
		
//		clientMediaRepository.delete(clientMedia);	//なぜかdeleteだけJPAの挙動がおかしいのでJDBCで回避
		jdbcTemplate.update("delete from client_media where id = ?",clientMedia.getId());
		s3UploadHelper.deleteFile(clientMedia.getMediaFileName());
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
		
		String encodedImage = null;
		
		if (image == null) {
			
			//★管理者にメール連絡
		} else {
			
			image.flush();	//メモリ解放？
			
			try {
				
				ImageIO.write(image, "png", os);	//bufferedImage(i)をpng形式でストリーム(os)に書き込み
				encodedImage = Base64.getEncoder().encodeToString(bos.toByteArray());	//base64形式に変換
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		return encodedImage;
	}
	
	//トップ画像の抽出
	public MediaFormat getTopImage(List<MediaFormat> mfList) {
		
		MediaFormat mediaFormat = null;
		
		for(MediaFormat mf : mfList) {
			
			if(mf.getClientMedia().getMediaType().equals("t")) {
				
				mediaFormat = mf;
			}
		}
		return mediaFormat;
	}
	
	//サブ画像の抽出
	public List<MediaFormat> getSubImage(List<MediaFormat> mfList) {
		
		List<MediaFormat> mediaFormatList = new ArrayList<>();
		
		for(MediaFormat mf : mfList) {
			
			if(mf.getClientMedia().getMediaType().equals("s")) {
				
				mediaFormatList.add(mf);
			}
		}
		
		if(mediaFormatList.size() < 1) {
			
			return null;
		}
		
		return sortMediaFormatList(mediaFormatList);
	}
	
	//List<MediaFormat>を優先度順に並べ替え
	public List<MediaFormat> sortMediaFormatList(List<MediaFormat> mfList){
		
		mfList.sort((a, b)->a.getClientMedia().getPriority().compareTo(b.getClientMedia().getPriority()));
																								//優先度昇順に並び替え
		return mfList;
	}

}
