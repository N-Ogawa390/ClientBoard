package net.dkt.dktsearch.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class SpringUserServiceTest {
	
	@TestConfiguration
	static class Config {
		
		@Bean
		public UserDetailsManager userDetailsManager() {
			
			return new InMemoryUserDetailsManager();
		}
		
		@Bean
		public SpringUserService springUserService() {
			
			return new SpringUserService();
		}
	}
	
	@Autowired
	private UserDetailsManager userDetailsManager;
	
	@Autowired
	private SpringUserService springUserService;
	
	@Test
	public void testCreateSpringUser() {
		
		springUserService.createSpringUser("admin1", "admin1", "ADMINISTRTOR", true);
		
		UserDetails userDetails = userDetailsManager.loadUserByUsername("admin1");
		assertThat(userDetails).isNotNull();
		assertThat(userDetails.isEnabled()).isEqualTo(true);
		
		for(GrantedAuthority auth : userDetails.getAuthorities()) {
			
			assertThat(auth.getAuthority()).isEqualTo("ROLE_ADMINISTRATOR");
		}
	}

}
