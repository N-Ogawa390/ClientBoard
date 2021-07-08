package net.dkt.dktsearch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.Account;
import net.dkt.dktsearch.model.TmpAccount;
import net.dkt.dktsearch.repository.AccountRepository;
import net.dkt.dktsearch.repository.TmpAccountRepository;

/*
//ユーザ名からアカウントobjを取得
	public Account find
//アカウントobj一覧を取得
	public List<Account> getAccountsOfRoleClient
//アカウント作成仮登録　※認証メール送信
	@Transactional	public void createTmpAccount
//認証メールがクリックされた際に、該当のtmpURLを使用しているtmpAccountオブジェクトを取得
	public TmpAccount getTmpAccount
//アカウントがすでに存在するかチェック
	public boolean accountExist
//アカウントのemailがすでに存在するかチェック
	public boolean accountEmailExist
//アカウント本登録 ※最初の管理者登録の際のみADMINISTRATOR、その他はCLIENT
	@Transactional	public Account createAccount
//アカウント更新
	@Transactional	public void updateAccount
//アカウント削除
	@Transactional	public void deleteAccount
//Emailからアカウントobjを取得 ※パスワードリセットのため
	@Transactional	public Account getAccountFromEmail
//パスワードリセット
	public void passwordReset
 */

@Service
public class AccountService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
//	@Autowired
//	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private SpringUserService springUserService;
	
	@Autowired
	private TmpAccountRepository tmpAccountRepository;
	
	//ユーザ名からアカウントobjを取得
	public Account find(String username) {
		
		return accountRepository.findById(username).get();
	}
	
	//アカウントobj一覧を取得
	public List<Account> getAccountsOfRoleClient() {
		
		return accountRepository.findByType("client");
	}
	
	//認証メールがクリックされた際に、該当のtmpURLを使用しているtmpAccountオブジェクトを取得
	public TmpAccount getTmpAccount(String tmpURL) {
		
		TmpAccount tmpAccount = tmpAccountRepository.findByTmpURL(tmpURL);
		return tmpAccount;
	}
	
	//アカウントがすでに存在するかチェック
	public boolean accountExist(String username) {
		
		Optional<Account> accountCheck = accountRepository.findById(username);
		
		if (accountCheck.isPresent() == true) {
			return true;
		} else {
			return false;
		}
	}
	
	//アカウントのemailがすでに存在するかチェック
	public boolean accountEmailExist(String email) {
		
		Account accountCheck = accountRepository.findByEmail(email);
		
		if (accountCheck != null) {
			return true;
		} else {
			return false;
		}
	}
	
	//アカウント本登録 ※最初の管理者登録の際のみADMINISTRATOR、その他はCLIENT
	@Transactional
	public Account createAccount(String username, String password, String email, String site) {
		
		Account account = new Account();
		account.setUsername(username);
		account.setType("client");
		account.setEmail(email);
		account.setSite(site);
		account.setActive(true);
		accountRepository.save(account);
		
		springUserService.createSpringUser(username, password, "CLIENT", true);
		
		return account;
	}
	
	//アカウント更新
	@Transactional
	public void updateAccount(String username, String password, String email, String site, boolean active) {

		springUserService.updateSpringUser(username, password, active);
		
		Account account = accountRepository.findById(username).get();
		account.setEmail(email);
		account.setSite(site);
		account.setActive(active);
		accountRepository.save(account);
	}
	
	//アカウント削除
	@Transactional
	public void deleteAccount(Account account) {
		
		jdbcTemplate.update("DELETE FROM USERS WHERE username = ?", account.getUsername());
				//account作成時に@Transactionalを用いてaccountテーブルとspringuserを更新している
				//そのため、accountテーブルとusersテーブルの間に参照整合性制約を設定できないので個別にdeleteする
		accountRepository.delete(account);
	}
	
	//Emailからアカウントobjを取得 ※パスワードリセットのため
	@Transactional
	public Account getAccountFromEmail(String email) {
		
		return accountRepository.findByEmail(email);
	}
}
