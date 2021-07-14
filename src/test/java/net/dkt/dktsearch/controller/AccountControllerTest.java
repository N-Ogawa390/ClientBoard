package net.dkt.dktsearch.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import net.dkt.dktsearch.model.Account;
import net.dkt.dktsearch.model.AccountForm;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {
	
//	@Autowired
//	private MockMvc mockMvc;
	
//	@MockBean
//	private AccountService accountService;
	
	@MockBean
	private UserDetailsManager userDetailsManager;
	
	private static List<Account> accounts;
	private static Account account;
	
//	@BeforeEach
//	public void setUp() {
//		
//		account = new Account();
//		account.setUsername("testUsername");
//		account.setEmail("test@example.com");
//		account.setType("administrator");
//		accounts = Arrays.asList(account);
//	}
	
	@Test
	public void testIndexNotLogin() throws Exception {
		
//		mockMvc.perform(get("/account"))
//			.andExpect(status().isFound());
	}
	
//	@Test
//	@WithMockUser	//デフォルトのユーザ名とロールは？
//	public void testIndexNotLoginNoPermision() throws Exception{
//		
//		mockMvc.perform(get("/account"))
//			.andExpect(status().isForbidden());
//	}
//	
//	@Test
//	@WithMockUser(roles = "ADMINISTRATOR")
//	public void testCreateGet() throws Exception {
//		
//		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//		params.add("type", "acministrator");
//		
//		MvcResult result = mockMvc.perform(get("/account/create").params(params))
//				.andExpect(status().isOk())
//				.andExpect(view().name("account/create"))
//				.andReturn();
//		
//		AccountForm form = (AccountForm)result.getModelAndView().getModel().get("accountForm");
//		assertThat(form.getType()).isEqualTo("administrator");
//	}

}
