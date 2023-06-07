package com.course.search.service.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.course.search.model.MovieQuery;
import org.springframework.stereotype.Service;

import com.course.search.service.Searcher;

public class LuceneService implements Searcher {

	private final LuceneMovieDataSearcher searcher;

	public LuceneService(String path, int limit, int threshold) throws IOException {
		searcher = new LuceneMovieDataSearcher(path, limit, threshold);
	}


	@Override
	public Object searchDocuments(MovieQuery movieQuery) {
		try {
			return searcher.search(movieQuery);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public Object searchDocumentsWithFacets(MovieQuery movieQuery) {
		try {
			return searcher.searchDocumentsWithFacets(movieQuery);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new HashMap<>();
	}

}
