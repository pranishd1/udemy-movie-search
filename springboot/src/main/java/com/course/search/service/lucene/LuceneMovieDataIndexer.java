package com.course.search.service.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.course.search.load.Movie;
import org.apache.lucene.util.BytesRef;

public class LuceneMovieDataIndexer {

	public final static String ID = "id";
	public final static String NAME = "name";
	public final static String DATE = "date";
	public final static String GENRES = "genres";

	public final static String IMDBID = "imdbid";

	public final static String POSTER ="poster";

	private final Path path;

	public static FacetsConfig facetsConfig = new FacetsConfig();

	static {
		facetsConfig.setMultiValued(LuceneMovieDataIndexer.GENRES, true);
		facetsConfig.setRequireDimCount(LuceneMovieDataIndexer.GENRES,true);
	}

	public LuceneMovieDataIndexer(String directoryPath) {
		path = Paths.get(directoryPath);
	}

	public void index(List<Movie> movies) throws IOException {

		Directory dir = FSDirectory.open(path);

		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(OpenMode.CREATE);
		config.setRAMBufferSizeMB(256);
		IndexWriter writer = new IndexWriter(dir, config);

		addDocument(movies.iterator(), writer);
		writer.commit();
		writer.close();

	}

	private void addDocument(Iterator<Movie> movies, IndexWriter writer) throws IOException {
		while (movies.hasNext()) {
			writer.addDocument(createDocument(movies.next()));
		}
	}

	private Document createDocument(Movie movie) throws IOException {

		IndexableField id = new StoredField(ID, String.valueOf(movie.getId()));
		IndexableField name = new TextField(NAME, movie.getName(), Store.YES);
		IndexableField releaseDate = new StoredField(DATE, movie.getReleasedDate());
		IndexableField imdbId = new StoredField(IMDBID, String.valueOf(movie.getImdbId()));
		IndexableField link = new StoredField(POSTER,movie.getPoster());

		Document doc = new Document();
		doc.add(id);
		doc.add(name);

		for (String genre : movie.getGenres()) {
			if(genre!=null && !genre.isEmpty()){
				IndexableField genresFacets = new SortedSetDocValuesFacetField(GENRES, genre);
				IndexableField genres = new TextField(GENRES,genre,Store.YES);
				doc.add(genresFacets);
				doc.add(genres);
			}
		}

		doc.add(releaseDate);
		doc.add(imdbId);
		doc.add(link);

		return facetsConfig.build(doc);
	}

}
