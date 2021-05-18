package net.dkt.dktsearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan
@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Autowired
	private AppConfig appConfig;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		String imageDirUri = appConfig.getImageDir().toURI().toString();
		registry.addResourceHandler("/images/**").addResourceLocations(imageDirUri);
		//aws設定用
		registry.addResourceHandler("/static/**")
        .addResourceLocations("classpath:/static/");
		//aws設定用ここまで
	}
	
	//aws設定用............................................................
	   @Override
	   public void configureMessageConverters(List converters) {
	       converters.add(new BufferedImageHttpMessageConverter());
	       converters.add(byteArrayHttpMessageConverter());
	       converters.add(new StringHttpMessageConverter());
	       converters.add(new MappingJackson2HttpMessageConverter());
	   }

	   @Bean
	   public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
	       ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
	       arrayHttpMessageConverter.setSupportedMediaTypes(getSupportedMediaTypes());
	       return arrayHttpMessageConverter;
	   }

	   private List getSupportedMediaTypes() {
	       List list = new ArrayList();
	       list.add(MediaType.IMAGE_JPEG);
	       list.add(MediaType.IMAGE_PNG);
	       list.add(MediaType.APPLICATION_OCTET_STREAM);
	       return list;
	   }
	   //aws設定用ここまで

}
