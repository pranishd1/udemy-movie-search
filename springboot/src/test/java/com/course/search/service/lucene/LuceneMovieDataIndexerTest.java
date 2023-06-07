package com.course.search.service.lucene;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.course.search.load.Movie;
import com.course.search.load.MovieDataLoader;
import com.course.search.load.MovieDataLoaderTest;

public class LuceneMovieDataIndexerTest {

	public final static String PATH = "E:\\Udemy\\search\\index\\";

	@Test
	public void indexTest() throws IOException {
		LuceneMovieDataIndexer indexer = new LuceneMovieDataIndexer(PATH);
		MovieDataLoader loader = new MovieDataLoader();
		MovieDataLoaderTest movieLoader = new MovieDataLoaderTest();
		List<Movie> movies = movieLoader.getMovies(loader);
		indexer.index(movies);
	}

}
