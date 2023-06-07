package com.course.search.controller;

import com.course.search.model.MovieQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.course.search.service.SearchService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(originPatterns = "*")
@RequestMapping("/api/v1/search")
public class SearchController {

	@Autowired
	private SearchService searchService;

	@PostMapping("/lucene")
	public Object findInLucene(@RequestBody MovieQuery movieQuery) {
		if(movieQuery==null)
			return new Object();
		if(movieQuery.getFind()==null)
			movieQuery.setFind("");
		return searchService.searchDocumentsOnLuceneWithFacets(movieQuery);
	}

	@GetMapping("/world")
	public Object dummy(){
		return Arrays.asList("Hello","World");
	}

}
