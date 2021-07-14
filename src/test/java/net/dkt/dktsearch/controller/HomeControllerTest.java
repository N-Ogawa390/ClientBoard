package net.dkt.dktsearch.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.dkt.dktsearch.service.AccountService;
import net.dkt.dktsearch.service.AccountServiceTest;
import net.dkt.dktsearch.service.AccountServiceTestConfig;
import net.dkt.dktsearch.service.HomeService;

//@Import(AccountServiceTestConfig.class)
@WebMvcTest(HomeController.class)
public class HomeControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private HomeService homeService;
	
	@MockBean
	private AccountService accountService;
	
	@MockBean
	private UserDetailsManager userDetailsManager;
	
	@BeforeEach
	public void setUp() {
		
	}
	
	@TestConfiguration
	static class Config implements WebMvcConfigurer {
		
		@Override
		public void addFormatters(FormatterRegistry registry) {
			
		}
	}
	
	@Test
	public void testShowIndex() throws Exception {
		
		mockMvc.perform(get("/manage"))
			.andExpect(status().isOk())
			.andExpect(view().name("manage"))
			.andReturn();
			
	}
	

}
