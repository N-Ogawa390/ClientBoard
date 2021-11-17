package net.dkt.dktsearch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import net.dkt.dktsearch.model.Client;
import net.dkt.dktsearch.service.ClientMediaService;
import net.dkt.dktsearch.service.ClientService;

@Component
public class S3UploadHelper {
	
	private static final String S3_BUCKET_PREFIX = "s3://";
	private static final String DIRECTORY_DELIMITER = "/";
	
	@Value("${aws.s3.bucketName}")
	private String bucketName;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	ResourcePatternResolver resourcePatternResolver;
	
	@Autowired
	AmazonS3 amazonS3;
	
	@Autowired
	ClientMediaService clientMediaService;
	
	@Autowired
	private BasicAWSCredentials basicAWSCredentials;
	
	//S3アクセス用オブジェクトキーの生成
	private String getObjectKey (String mediaFileName) {
		
		String objectKey = new StringBuilder()	//URIを指定
				.append(S3_BUCKET_PREFIX)
				.append(bucketName)
				.append(DIRECTORY_DELIMITER)
				.append(mediaFileName)
				.toString();
		
		return objectKey;
	}
	
	//AWSクライアント生成
	private AmazonS3 getAwsS3Client(String bucketName) {
		
		AWSCredentials credentials = basicAWSCredentials;
		
		AmazonS3 s3Client = AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
		
		return s3Client;
	}
	
	//ファイルアップロード
	public String saveFile(MultipartFile multipartFile, Client client, String mediaType) throws IOException {
		
		//ファイル名を作成 ※クライアント名+乱数_ファイル名
		String mediaFileName = client.getClientName() + "_" + client.getId() + "_" + new Random().nextInt(1000000) + "_" + multipartFile.getOriginalFilename();
		
		//Model ClientMediaに情報を保存
		try {
			clientMediaService.saveMediaFile(mediaFileName, client, mediaType);
			
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
		
		//S3アクセス用　※パス+メディアファイル名
		String objectKey = getObjectKey(mediaFileName);
		
		WritableResource writableResource = (WritableResource)resourceLoader.getResource(objectKey);	//S3の書き込み可能なリソースに、objectKeyを指定してアクセス

		try(
			InputStream inputStream = multipartFile.getInputStream();	//postされたファイルをストリームに変換 ※直接outputstreamには変換できない
			OutputStream outputStream = writableResource.getOutputStream()	//アクセス中のS3リソースを書き込み用ストリームに変換
			) {
			
			IOUtils.copy(inputStream, outputStream);	//取り込んだinputstreamをoutputstreamにコピー ※これでS3に保存される
			
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		return objectKey;
	}
	
	//ファイル削除
	public void deleteFile(String mediaFileName) {
		
		String objectKey = getObjectKey(mediaFileName);
		
		getAwsS3Client(objectKey).deleteObject(bucketName, mediaFileName);
	}
	
	public boolean existsDirectory(String directoryPath) {
		
		try {
			List resourceList = Arrays.asList(
					resourcePatternResolver.getResources(directoryPath + "/**"));
			if(resourceList.size() == 0) {
				return false;
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void createDirectory(String directoryPath) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		
		try(InputStream emptyContent = new ByteArrayInputStream(new byte[0]);) {
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, directoryPath, emptyContent, objectMetadata);
			amazonS3.putObject(putObjectRequest);
			
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
