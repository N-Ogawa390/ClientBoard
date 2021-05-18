package net.dkt.dktsearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.BasicAWSCredentials;

@Configuration
public class S3Config {
	
	@Value("${cloud.aws.credentials.accessKey}")
	private String accessKey;
	
	@Value("${cloud.aws.credentials.secretKey}")
	private String secretKey;
	
	@Value("${cloud.aws.region.static}")
	private String region;
	
	@Bean
	public BasicAWSCredentials basicAWSCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}
}
