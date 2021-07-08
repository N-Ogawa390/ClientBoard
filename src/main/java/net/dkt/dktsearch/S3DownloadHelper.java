package net.dkt.dktsearch;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.model.ClientMedia;
import net.dkt.dktsearch.model.MediaFormat;
import net.dkt.dktsearch.service.ClientMediaService;

//List<MediaFormat> getImage(Client client)
//	クライアントの持っている画像の一覧をMediaFromat形式で返す
//	MediaFormat[<ClientMedia>, <String> base64]

@Component
public class S3DownloadHelper {
	
	private static final String S3_BUCKET_PREFIX = "s3://";
    private static final String DIRECTORY_DELIMITER = "/";
	
	@Value("${aws.s3.bucketName}")
	private String bucketName;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private ClientMediaService clientMediaService;
	
	//クライアントに紐づく画像一覧を取得
	public List<MediaFormat> getImage(Client client) {
		
		List<MediaFormat> mediaList = new ArrayList<>();
		BufferedImage image;
		
		for(ClientMedia clientMedia : client.getClientMedias()) {
			
			Resource resource = getResource(clientMedia);
			
			try(InputStream inputStream = resource.getInputStream()) {	//①で取得したインスタンスをストリーム(バイト列)に変換
				image = ImageIO.read(inputStream);	//ストリームをBufferdImage(Javaで画像を扱うときの基本型)に変換

				mediaList.add(new MediaFormat(clientMedia, clientMediaService.getByteImages(image)));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return mediaList;
	}
	
	//クライアント一覧をクライアントIDとトップ画像MediaFormatのマップにして返却
	//検索結果カード一覧のトップ画像表示用
	public Map<Integer, MediaFormat> getTopImagesWithClientId(List<Client> clients) {
		
		Map<Integer, MediaFormat> topImagesWithClientId = new HashMap<>();
		
		for(Client client : clients) {
			
			topImagesWithClientId.put(client.getId(), getTopImageMediaFormat(client));
		}
		
		return topImagesWithClientId;
	}
	
	public MediaFormat getTopImageMediaFormat(Client client) {
		
		BufferedImage image = null;
		ClientMedia clientMedia = null;
		
		for(ClientMedia cm : client.getClientMedias()) {
			
			if(cm.getMediaType().equals("t")) {
				
				clientMedia = cm;
			}
		}
		
		if(clientMedia != null) {
			Resource resource = getResource(clientMedia);
			
			try(InputStream inputStream = resource.getInputStream()) {	//①で取得したインスタンスをストリーム(バイト列)に変換
				image = ImageIO.read(inputStream);	//ストリームをBufferdImage(Javaで画像を扱うときの基本型)に変換
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return new MediaFormat(clientMedia, clientMediaService.getByteImages(image));
		} else {
			
			return null;
		}
	}
	
	public Resource getResource(ClientMedia clientMedia) {
		
		Resource resource = resourceLoader.getResource(	//URLを指定してResourceインスタンスを取得…①
				new StringBuilder()
				.append(S3_BUCKET_PREFIX)
				.append(bucketName)
				.append(DIRECTORY_DELIMITER)
				.append(clientMedia.getMediaFileName())
				.toString()
			);
		
		return resource;
	}
}
