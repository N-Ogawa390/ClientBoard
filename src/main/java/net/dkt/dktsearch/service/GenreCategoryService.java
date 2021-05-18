package net.dkt.dktsearch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dkt.dktsearch.model.GenreCategory;
import net.dkt.dktsearch.repository.GenreCategoryRepository;

/*
//ジャンルカテゴリを新規作成
	public GenreCategory createGenreCategory\
//すべてのジャンルカテゴリobjを取得
	public List<GenreCategory> getAll
 */

@Service
public class GenreCategoryService {
	
	@Autowired
	GenreCategoryRepository genreCategoryRepository;
	
	//ジャンルカテゴリを新規作成
	public GenreCategory createGenreCategory(GenreCategory genreCategory) {
		return genreCategoryRepository.save(genreCategory);
	}
	
	//すべてのジャンルカテゴリobjを取得
	public List<GenreCategory> getAll() {
		return genreCategoryRepository.findAll();
	}
	
}
