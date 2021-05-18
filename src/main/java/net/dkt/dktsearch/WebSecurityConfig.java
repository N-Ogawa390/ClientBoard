package net.dkt.dktsearch;


import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/css/**", "/html/**", "/images/**");
//		web.ignoring().antMatchers("/html/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
//		http.authorizeRequests().antMatchers("/**").permitAll();
		
		http.authorizeRequests()
			.antMatchers("/client/create").hasAnyRole("ADMINISTRATOR", "CLIENT")
			.antMatchers("/client/**/edit").hasAnyRole("ADMINISTRATOR", "CLIENT")
			.antMatchers("/client/**/upload").hasAnyRole("ADMINISTRATOR", "CLIENT")
			.antMatchers("/account/admin").hasRole("ADMINISTRATOR")
			.antMatchers("/genre/edit/**").hasRole("ADMINISTRATOR")
			.antMatchers("/genre/**/edit").hasRole("ADMINISTRATOR")
			.antMatchers("/upload").permitAll()
		.and()
			.formLogin().loginPage("/login").permitAll()
			.defaultSuccessUrl("/")
		.and()
			.logout()
			.logoutSuccessUrl("/")
		;
	}
	
	@Bean
	public UserDetailsManager userDetailsManager(DataSource dataSource) {
		
		return new JdbcUserDetailsManager(dataSource);
	}
	
	//エンコーダを指定
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}

}
