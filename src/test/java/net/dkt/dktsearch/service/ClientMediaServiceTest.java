package net.dkt.dktsearch.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import net.dkt.dktsearch.model.ClientMedia;
import net.dkt.dktsearch.repository.ClientMediaRepository;

@SpringJUnitConfig
public class ClientMediaServiceTest {
	
	@TestConfiguration
	static class Config {
		
		@Bean
		public ClientMediaService clientMediaService() {
			
			return new ClientMediaService();
		}
	}
	
	@Autowired
	private ClientMediaService clientMediaService;
	
	@MockBean
	private ClientMediaRepository clientMediaRepository;
	
	@Test
	public void testDeleteClientMedia() {
		
		ClientMedia cm = new ClientMedia();
		
		clientMediaService.deleteClientMedia(cm);
		verify(clientMediaRepository).delete(cm);
	}
}
