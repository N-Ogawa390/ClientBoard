package net.dkt.dktsearch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dkt.dktsearch.model.GenreCategory;


public interface GenreCategoryRepository extends JpaRepository<GenreCategory, Integer> {
	
	List<GenreCategory> findByGenreCategoryName(String genreCategoryName);
	
	GenreCategory findByGenreName(String genreName);
	
	void deleteByGenreName(String genreName);

}
