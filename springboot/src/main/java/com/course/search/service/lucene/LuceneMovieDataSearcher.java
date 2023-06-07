package com.course.search.service.lucene;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.IntFunction;

import com.course.search.model.MovieQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.facetset.ExactFacetSetMatcher;
import org.apache.lucene.facet.facetset.FacetSet;
import org.apache.lucene.facet.facetset.FacetSetDecoder;
import org.apache.lucene.facet.facetset.MatchingFacetSetsCounts;
import org.apache.lucene.facet.sortedset.DefaultSortedSetDocValuesReaderState;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetCounts;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesReaderState;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.course.search.load.Movie;

public class LuceneMovieDataSearcher {
	public static final String RESULT = "result";
	public static final String FACETS = "facets";
	private Directory dir;
	private IndexSearcher searcher;
	private int limit;

	private int threshold;

	public LuceneMovieDataSearcher(String path, int limit, int threshold) throws IOException {
		dir = FSDirectory.open(Paths.get(path));
		DirectoryReader indexReader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(indexReader);
		this.limit = limit;
		this.threshold = threshold;
	}

	/***
	 * Breaks query into small tokens
	 * Eg: Search Query = 'Harry Potter' -> 'Harry','Potter'
	 * @param analyzer
	 * @param query
	 * @return
	 */
	private List<String> tokenizeQuery(Analyzer analyzer, String query) {
		List<String> result = new ArrayList<>();
		try {
			TokenStream stream  = analyzer.tokenStream(null, new StringReader(query));
			stream.reset();
			while (stream.incrementToken()) {
				result.add(stream.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public Object search(MovieQuery movieQuery) throws IOException {
		Query query = getQuery(movieQuery);

		TopDocs result = searcher.search(query,limit);

		if (result.totalHits.value == 0) {
			return new ArrayList<>();
		}

		return getMovies(result);
	}

	private List<Movie> getMovies(TopDocs result) throws IOException {
		List<Movie> collection = new ArrayList<>();
		StoredFields fields = searcher.storedFields();
		for (ScoreDoc doc : result.scoreDocs) {
			Document d = fields.document(doc.doc);
			collection.add(getMovie(d));
		}
		return collection;
	}


	public Object searchDocumentsWithFacets(MovieQuery movieQuery) throws IOException {
		BooleanQuery.Builder buildQuery = buildQuery(movieQuery);

		if(movieQuery.getGenres()!=null && !movieQuery.getGenres().isEmpty()){
			DrillDownMustQuery drillDownQuery = new DrillDownMustQuery(LuceneMovieDataIndexer.facetsConfig);
			for(String g:movieQuery.getGenres()){
				drillDownQuery.add(LuceneMovieDataIndexer.GENRES,g);
			}
			buildQuery.add(drillDownQuery,Occur.FILTER);
		}


		Query query =  buildQuery.build();

		FacetsCollector facetsCollector = new FacetsCollector(true);
		TopScoreDocCollector topScoreDocCollector= TopScoreDocCollector.create(limit,threshold);
		searcher.search(query,MultiCollector.wrap(topScoreDocCollector,facetsCollector));

		TopDocs result = topScoreDocCollector.topDocs();

		if (result.totalHits.value == 0) {
			return getSearchResult();
		}

		List<Movie> collection = getMovies(result);

		SortedSetDocValuesReaderState state =
				new DefaultSortedSetDocValuesReaderState(searcher.getIndexReader(),
						LuceneMovieDataIndexer.facetsConfig);

		Facets facets = new SortedSetDocValuesFacetCounts(state, facetsCollector);

		FacetResult facetResult = facets.getAllChildren(LuceneMovieDataIndexer.GENRES);


		Map<String,Object> map = getSearchResult();
		map.put(RESULT,collection);
		map.put(FACETS,facetResult);
		return map;

	}

	/**
	 * Returns Empty Search Result
	 * @return
	 */
	private Map<String,Object> getSearchResult(){
		Map<String,Object> map = new HashMap<>();
		map.put(RESULT,new ArrayList<>());
		map.put(FACETS,new FacetResult("",new String[]{},0,new LabelAndValue[]{},0));
		return map;
	}

	private Query getQuery(MovieQuery movieQuery) {
		Query query =new MatchAllDocsQuery();;
		if(movieQuery!=null){
			return buildQuery(movieQuery).build();
		}
		return query;
	}

	private  BooleanQuery.Builder buildQuery(MovieQuery movieQuery) {
		List<String> queries = tokenizeQuery(new StandardAnalyzer(), movieQuery.getFind());
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

		if(queries.isEmpty()){
			booleanQuery.add(new MatchAllDocsQuery(),Occur.SHOULD);
		}

		for(String s:queries){
			booleanQuery
					.add(new TermQuery(new Term(LuceneMovieDataIndexer.NAME, s)), Occur.MUST);
		}
		return booleanQuery;
	}

	private Movie getMovie(Document document) {
		Movie movie = new Movie();
		movie.setId(Integer.parseInt(document.get(LuceneMovieDataIndexer.ID)));
		movie.setName(document.get(LuceneMovieDataIndexer.NAME));
		movie.setReleasedDate(document.get(LuceneMovieDataIndexer.DATE));
		movie.setGenres(Arrays.asList(document.getValues(LuceneMovieDataIndexer.GENRES)));
		movie.setImdbId(Integer.parseInt(document.get(LuceneMovieDataIndexer.IMDBID)));
		movie.setPoster(document.get(LuceneMovieDataIndexer.POSTER));
		return movie;
	}

}
