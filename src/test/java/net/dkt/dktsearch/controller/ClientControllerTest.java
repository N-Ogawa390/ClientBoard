package net.dkt.dktsearch.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import net.dkt.dktsearch.service.ClientService;

public class ClientControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ClientService clientService;

}
