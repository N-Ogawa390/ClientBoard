package net.dkt.dktsearch.model;

import lombok.Data;

@Data
public class MediaFormat {
	
	private ClientMedia clientMedia;
	private String base64;
	
	public MediaFormat(ClientMedia cm, String b64) {
		clientMedia = cm;
		base64 = b64;
	}
	
}
