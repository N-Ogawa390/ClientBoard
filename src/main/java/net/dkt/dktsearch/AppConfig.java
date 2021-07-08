package net.dkt.dktsearch;

import java.io.File;

import lombok.Data;

@Data
public class AppConfig {
	
	private File imageDir;
		//静的画像をアプリケーションのstaticフィールドに保存する際のパス格納用フィールド
}
