package net.dkt.dktsearch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dkt.dktsearch.model.Account;

public interface AccountRepository extends JpaRepository<Account, String>{
	
	List<Account> findByType(String roleName);
	
	Account findByEmail(String email);

}
