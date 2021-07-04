package net.dkt.dktsearch.controller;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.dkt.dktsearch.MailHelper;
import net.dkt.dktsearch.model.Account;
import net.dkt.dktsearch.model.AccountForm;
import net.dkt.dktsearch.model.TmpAccount;
import net.dkt.dktsearch.service.AccountService;
import net.dkt.dktsearch.service.SpringUserService;

/*
//管理者画面を表示
	@GetMapping("/admin")	public String account
//アカウント作成仮登録画面を表示
	@GetMapping("/tmp/create")	public String testMail
//アカウント作成仮登録 ※認証メールを送信
	@PostMapping("/tmp/create")	public String testMail
//認証メールURLをクリックしたらアカウントを本登録
	@Transactional	@GetMapping("/tmp")	public String tmpUrlClicked
//アカウント編集画面を表示
	@GetMapping("/{username}/edit")	public String editAccount
//アカウント編集
	@PostMapping("/{username}/edit")	public String editAccount
//パスワード再設定画面を表示
	@GetMapping("/{username}/passchange")	public String passChange
//パスワード再設定
	@PostMapping("/{username}/passchange")	public String passChange
//アカウント削除
	@GetMapping("/{username}/delete")	public String deleteAccount
//パスワードリセット画面を表示
	@GetMapping("/passwordreset")	public String passwordReset
//パスワードリセット
	@PostMapping("/passwordreset")	public String passwordReset
 */

@Controller
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	SpringUserService springUserService;
	
	@Autowired
	private MailHelper mailHelper; 
	
	//管理者画面を表示
	@GetMapping("/admin")
	public String account(Account account, Model model) {
		
		List<Account> accountsOfRoleClient = accountService.getAccountsOfRoleClient();
		model.addAttribute("accountsOfRoleClient", accountsOfRoleClient);
		
		return "account/admin";
	}
	
	//アカウント作成仮登録画面を表示
	@GetMapping("/tmp/create")
	public String testMail(TmpAccount tmpAccount) {
		
		return "account/tmp/form";
	}
	
	//アカウント作成仮登録 ※認証メールを送信
	@PostMapping("/tmp/create")
	public String testMail(@Valid TmpAccount tmpAccount, BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			return "account/tmp/form";
			
		} else if (accountService.accountExist(tmpAccount.getUsername()) == true) {
			
			bindingResult.addError(new FieldError("accountForm", "username", "※すでに存在するユーザIDです"));
			return "account/tmp/form";
			
		} else if (accountService.accountEmailExist(tmpAccount.getEmail()) == true) {
			
			bindingResult.addError(new FieldError("accountForm", "email", "※すでに使用されているメールアドレスです"));
			return "account/tmp/form";
		}
		
		mailHelper.createTmpAccount(tmpAccount.getUsername(), tmpAccount.getPassword(), tmpAccount.getEmail(), tmpAccount.getSite());
		return "account/tmp/mailsend";
	}
	
	//認証メールURLをクリックしたらアカウントを本登録
	@Transactional
	@GetMapping("/tmp")
	public String tmpUrlClicked(@RequestParam("id") String tmpURL) {
		
		TmpAccount tmpAccount = accountService.getTmpAccount(tmpURL);
		
		String username = tmpAccount.getUsername();
		String password = tmpAccount.getPassword();
		String email = tmpAccount.getEmail();
		String site = tmpAccount.getSite();

		accountService.createAccount(username, password, email, site);

		return "account/tmp/confirmed";
	}
	
	//アカウント編集画面を表示
	@GetMapping("/{username}/edit")
	public String editAccount(@PathVariable("username") Account account, AccountForm accountForm) {
		
		accountForm.setType(account.getType());
		accountForm.setUsername(account.getUsername());
		accountForm.setEmail(account.getEmail());
		accountForm.setSite(account.getSite());
		accountForm.setActive(account.getActive());
		accountForm.setPassword("dammypassword");
		//パスワードをどうやって入手するか…　ダミーを送信して内部では更新しないように設定するのが妥当か。
		
		return "account/edit";
	}
	
	//アカウント編集
	@PostMapping("/{username}/edit")
	public String editAccount(
			@PathVariable("username") Account account,
			@Valid AccountForm accountForm,	BindingResult bindingResult,
			Model model
			) {
		
		if (bindingResult.hasErrors()) {
			
			return "account/edit";
		}
		
		//アカウントが自身のアカウント以外ですでに使用されている場合はエラー
		if (accountService.accountEmailExist(accountForm.getEmail()) == true &&
				!accountForm.getEmail().equals(accountForm.getEmail())) {
			
			bindingResult.addError(new FieldError("accountForm", "email", "※すでに使用されているメールアドレスです"));
			return "account/edit";
		}
		
		String username = accountForm.getUsername();
		String password = accountForm.getPassword();
		String email = accountForm.getEmail();
		String site = accountForm.getSite();
		boolean active;
		
		//ロールがCLIENTならactiveは編集不可なのでTRUEで固定、ADMINISRATORなら可なのでgetする
		Account currentAccount = (Account)model.getAttribute("currentAccount");
		
		if (currentAccount.getType().equals("client")) {
			
			active = true;
			
		} else {
			
			active = accountForm.getActive();
		}
		
		accountService.updateAccount(username, password, email, site, active);

		return "redirect:/";
	}
	
	//パスワード再設定画面を表示
	@GetMapping("/{username}/passchange")
	public String passChange(
			@PathVariable("username") Account account,
			AccountForm accountForm) {
		
		accountForm.setType(account.getType());
		accountForm.setUsername(account.getUsername());
		accountForm.setEmail(account.getEmail());
		accountForm.setSite(account.getSite());
		accountForm.setActive(account.getActive());
		
		return "account/passchange";
	}
	
	//パスワード再設定
	@PostMapping("/{username}/passchange")
	public String passChange(@PathVariable("username") Account account,
			@Valid AccountForm accountForm, BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult);
			return "account/passchange";
		}
		
		String username = account.getUsername();
		String password = accountForm.getPassword();
//		String email = account.getEmail();
//		String site = account.getSite();
		boolean active = true;
		
		springUserService.updateSpringUser(username, password, active);
//		accountService.updateAccount(username, password, email, site, active);
		
//		accountForm.setType(account.getType());
//		accountForm.setUsername(account.getUsername());
//		accountForm.setEmail(account.getEmail());
//		accountForm.setSite(account.getSite());
//		accountForm.setActive(account.getActive());
		
		return "redirect:/";
	}
	
	//アカウント削除
	@GetMapping("/{username}/delete")
	public String deleteAccount(@PathVariable("username") Account account) {
		
		accountService.deleteAccount(account);
		
		return "redirect:/";
	}
	
	//パスワードリセット画面を表示
	@GetMapping("/passwordreset")
	public String passwordReset() {
		
		return "account/passreset/form";
	}
	
	//パスワードリセット
	@PostMapping("/passwordreset")
	public String passwordReset(
			@RequestParam("email") String email) {
		
		System.out.println("スルー");
		
		Account account = accountService.getAccountFromEmail(email);
		
		System.out.println(account.getEmail());
		
		accountService.passwordReset(account);
		
		System.out.println("スルー2");
		
		return "account/passreset/mailsend";
	}
	
}
