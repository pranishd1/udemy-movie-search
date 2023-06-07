package com.course.search.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.course.search.service.lucene.LuceneService;

@Component
public class SearchFactory {

	@Value("${lucene.dir.path}")
	private String path;
	@Value("${lucene.result.limit}")
	private Integer limit;

	@Value("${lucene.result.threshold}")
	private Integer threshold;

	private final Map<String, Searcher> searches;

	public SearchFactory(@Value("${lucene.dir.path}") String path, @Value("${lucene.result.limit}") int limit,
						 @Value("${lucene.result.threshold}") int threshold)
			throws IOException {
		searches = new HashMap<>();
		searches.put(LuceneService.class.getSimpleName(), new LuceneService(path, limit, threshold));
	}

	public boolean add(String key, Searcher search) {
		if (searches.containsKey(key))
			return false;
		searches.put(key, search);
		return true;
	}

	public Searcher get(String key) {
		if (!searches.containsKey(key))
			throw new RuntimeException("Key not found");
		return searches.get(key);
	}

}
