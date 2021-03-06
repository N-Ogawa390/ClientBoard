package net.dkt.dktsearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class SpringUserService {
	
	@Autowired
	private UserDetailsManager userDetailsManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//スプリングユーザを作成 ※Usersテーブル・Authoritiesテーブル更新のため
	public void createSpringUser(String username, String password, String role, boolean active) {
		
		UserBuilder builder = User.builder();
		UserDetails userDetails = 
				builder
				.username(username)
				.password(password)	//tmpAccountの時点でエンコードされているためここではエンコードしない
				.roles(role)
				.disabled(!active)
				.build();
		userDetailsManager.createUser(userDetails);
	}
	
	//スプリングユーザを更新
	public void updateSpringUser(String username, String password, boolean active) {
		
		UserDetails userDetails = userDetailsManager.loadUserByUsername(username);	//パスワードとauthoritiesを現在の情報から使用するため
		UserBuilder builder;
		
		if (!password.equals("dammypassword")) {	//パスワードがダミーでなければ
			
			builder = User.builder();
			builder.password(passwordEncoder.encode(password));	//再設定
			
		} else {	//パスワードがダミーなら
			
			builder = User.builder();
			builder.password(userDetails.getPassword());	//現在のパスワードを使用
		}
		
		UserDetails newUserDetails = 
				builder
				.username(username)
				.authorities(userDetails.getAuthorities())
				.disabled(!active)
				.build();
		
		userDetailsManager.updateUser(newUserDetails);
	}
}
