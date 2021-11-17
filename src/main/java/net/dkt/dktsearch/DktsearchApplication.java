package net.dkt.dktsearch;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DktsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(DktsearchApplication.class, args);
	}
	
	@Bean
	public AppConfig appConfig() {
		
		//画像ファイル読み込み用
		File imageDir = new File("src/main/resources/static/img");	//フォルダ名を指定
		imageDir = imageDir.getAbsoluteFile();	//該当フォルダまでの絶対パスを取得
		
		AppConfig appConfig = new AppConfig();
		appConfig.setImageDir(imageDir);
		
		return appConfig;
	}
}
