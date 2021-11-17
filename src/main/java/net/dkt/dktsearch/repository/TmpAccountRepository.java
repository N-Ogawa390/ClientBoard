package net.dkt.dktsearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dkt.dktsearch.model.TmpAccount;

public interface TmpAccountRepository extends JpaRepository<TmpAccount, String> {
	
	TmpAccount findByTmpURL(String tmpURL);

}
