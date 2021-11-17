package net.dkt.dktsearch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dkt.dktsearch.model.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
	
	List<Client> findByClientNameContains(String word);
	
	List<Client> findByActiveFalse();

}
