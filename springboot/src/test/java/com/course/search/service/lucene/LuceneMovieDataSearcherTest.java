package com.course.search.service.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.course.search.model.MovieQuery;
import org.junit.jupiter.api.Test;

import com.course.search.load.Movie;

public class LuceneMovieDataSearcherTest {

	LuceneMovieDataSearcher searcher;
	int limit = 100;

	int threshold = 100;

	@Test
	public void search() throws IOException {
		String searchQuery = "Sabrina";
		searcher = new LuceneMovieDataSearcher(LuceneMovieDataIndexerTest.PATH, limit, threshold);
		Map<String,Object> movies = (Map<String, Object>) searcher.searchDocumentsWithFacets(new MovieQuery(searchQuery, Arrays.asList("Adventure")));
		List<Movie> movieList = (List<Movie>) movies.get(LuceneMovieDataSearcher.RESULT);
		if(movieList!=null){
			movieList.stream().forEach(System.out::println);
		}
		System.out.println(movies.get(LuceneMovieDataSearcher.FACETS));
	}

}
