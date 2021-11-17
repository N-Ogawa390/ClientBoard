package net.dkt.dktsearch.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import net.dkt.dktsearch.model.Account;
import net.dkt.dktsearch.repository.AccountRepository;
import net.dkt.dktsearch.repository.TmpAccountRepository;

//@Import(AccountServiceTestConfig.class)
@SpringJUnitConfig
public class AccountServiceTest {
	
	@TestConfiguration
	static class Config {
		
		@Bean
		public AccountService accountService() {
			
			return new AccountService();
		}
	}
	
	@Autowired
	private AccountService accountService;
	
	@MockBean
	private JdbcTemplate jdbcTemplate;
	
	@MockBean
	private SpringUserService springUserService;
	
	@MockBean
	private AccountRepository accountRepository;
	
	@MockBean
	private TmpAccountRepository tmpAccountRepository;
	
	@Test
	public void testFind() {
		
		Account account = new Account();
		
		when(accountRepository.findById("test")).thenReturn(Optional.of(account));
		
		Account a = accountService.find("test");
		verify(accountRepository).findById("test");
	}
	
	@Test
	public void testGetAccountsOfRoleClient() {
		
		List<Account> accountList = new ArrayList<>();
		
		when(accountRepository.findByType("client")).thenReturn(accountList);
		
		List<Account> aList = accountService.getAccountsOfRoleClient();
		verify(accountRepository).findByType("client");
	}

}
