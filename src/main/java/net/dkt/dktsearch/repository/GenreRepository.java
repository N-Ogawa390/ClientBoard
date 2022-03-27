package net.dkt.dktsearch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dkt.dktsearch.model.Floor;
import net.dkt.dktsearch.model.Genre;


public interface GenreRepository extends JpaRepository<Genre, Integer>{
	
	void deleteByClientId(Integer id);
	
	List<Genre> findByGenreName(String genreName);
	
	void deleteByGenreName(String genreName);
}
