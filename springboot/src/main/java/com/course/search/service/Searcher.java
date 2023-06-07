package com.course.search.service;

import com.course.search.model.MovieQuery;

import java.util.List;

public interface Searcher {

	Object searchDocuments( MovieQuery movieQuery);
	Object searchDocumentsWithFacets(MovieQuery movieQuery) ;

}
