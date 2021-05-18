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
		File imageDir = new File("images");		//相対パス
		imageDir = imageDir.getAbsoluteFile();	//絶対パスに変換
		
		if (!imageDir.exists()) {
			
			imageDir.mkdir();
		}
		
		AppConfig appConfig = new AppConfig();
		appConfig.setImageDir(imageDir);
		
		return appConfig;
	}

}
