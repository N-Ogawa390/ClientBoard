package net.dkt.dktsearch;

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
	
	public List<MediaFormat> getImage(Client client) {	//BufferdImage:画像ファイルをクラスとして扱う
		
		List<MediaFormat> mediaList = new ArrayList<>();
		
		BufferedImage image;
		
		for(ClientMedia clientMedia : client.getClientMedias()) {
			
			Resource resource = resourceLoader.getResource(	//URLを指定してResourceインスタンスを取得…①
					new StringBuilder()
					.append(S3_BUCKET_PREFIX)
					.append(bucketName)
					.append(DIRECTORY_DELIMITER)
					.append(clientMedia.getMediaFileName())
					.toString()
					);
			
			try(InputStream inputStream = resource.getInputStream()) {	//①で取得したインスタンスをストリーム(バイト列)に変換
				image = ImageIO.read(inputStream);	//ストリームをBufferdImage(Javaで画像を扱うときの基本型)に変換

				mediaList.add(new MediaFormat(clientMedia, clientMediaService.getByteImages(image)));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(System.getenv("BUCKET_NAME"));
		return mediaList;
	}
}
