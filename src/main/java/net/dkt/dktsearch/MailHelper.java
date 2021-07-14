package net.dkt.dktsearch;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import net.dkt.dktsearch.model.Account;
import net.dkt.dktsearch.model.TmpAccount;
import net.dkt.dktsearch.repository.TmpAccountRepository;
import net.dkt.dktsearch.service.SpringUserService;

@Component
public class MailHelper {
	
	//Mail環境変設定
	@Value("${properties.domain}")
	public String domain;

	@Value("${spring.mail.properties.mailFrom}")
	public String mailFrom;

	@Autowired
	private TmpAccountRepository tmpAccountRepository;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SpringUserService springUserService;
	
	//アカウント作成仮登録　※認証メール送信
	//同じメールアドレスで登録が行われた場合は仮登録のデータを上書きする
	@Transactional
	public void createTmpAccount(String username, String password, String email, String site) {
	 
		String tmpURL = UUID.randomUUID().toString();

		TmpAccount tmpAccount = new TmpAccount();
		tmpAccount.setUsername(username);
		tmpAccount.setPassword(passwordEncoder.encode(password));
		tmpAccount.setEmail(email);
		tmpAccount.setSite(site);
		tmpAccount.setTmpURL(tmpURL);
		tmpAccount.setTimeStamp(LocalDateTime.now());

		tmpAccountRepository.save(tmpAccount);

		String to = email;
		String title = "アカウント確認のお願い";
		String content = username + "さん" + "\n" + "\n" + "以下のリンクにアクセスしてアカウントを認証してください" + "\n" + "http://" + domain + "/account/tmp" + "?id=" + tmpURL;

		try {
		  
		  SimpleMailMessage msg = new SimpleMailMessage();
		  msg.setFrom(mailFrom);
		  msg.setTo(to);
		  msg.setSubject(title);
		  msg.setText(content);
		  mailSender.send(msg);
		  
		} catch (MailException e) {
		  
		  e.printStackTrace();
		}
	}
	
	//パスワードリセット
	public void passwordReset(Account account) {
		
		String password = RandomStringUtils.randomAlphanumeric(12);
		
		springUserService.updateSpringUser(
				account.getUsername(),
				password,
				account.getActive());
		
		//メール送信
		String to = account.getEmail();
		String title = "パスワードリセットを行いました";
		String content = "ユーザ名：" + account.getUsername() + "さん" + "\n" + "\n" + "新しいパスワードをお知らせします。" + "\n" + "パスワード：" + password;
		
		try {
			
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setFrom(mailFrom);
			msg.setTo(to);
			msg.setSubject(title);
			msg.setText(content);
			mailSender.send(msg);
			
		} catch (MailException e) {
			
			e.printStackTrace();
		}	
	}
}
