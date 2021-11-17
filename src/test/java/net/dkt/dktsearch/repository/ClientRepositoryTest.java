package net.dkt.dktsearch.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import net.dkt.dktsearch.model.Client;

@DataJpaTest
public class ClientRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@BeforeEach
	public void setUp() {
		
		Client client = new Client();
		client.setClientName("テストクライアント");
		entityManager.persist(client);
		
		Client client2 = new Client();
		client2.setClientName("サンプルクライアント");
		entityManager.persist(client2);
	}
	
	@Test
	public void testFindByClientName() {
		
		List<Client> clients = clientRepository.findByClientNameContains("サンプル");
		assertThat(clients.size()).isEqualTo(1);		
	}
}
