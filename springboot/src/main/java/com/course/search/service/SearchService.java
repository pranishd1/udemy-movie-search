package com.course.search.service;

import java.util.List;

import com.course.search.model.MovieQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

	public static final String LUCENE_SERVICE = "LuceneService";
	@Autowired
	private SearchFactory searchFactory;

	public Object searchDocumentsOnLucene(MovieQuery movieQuery) {
		Searcher searcher = searchFactory.get(LUCENE_SERVICE);
		return searcher.searchDocuments(movieQuery);
	}

	public Object searchDocumentsOnLuceneWithFacets(MovieQuery movieQuery) {
		Searcher searcher = searchFactory.get(LUCENE_SERVICE);
		return searcher.searchDocumentsWithFacets(movieQuery);
	}

}
