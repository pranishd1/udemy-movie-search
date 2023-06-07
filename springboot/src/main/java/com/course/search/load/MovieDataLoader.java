package com.course.search.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MovieDataLoader {

	private static final String NO_POSTER = "https://movienewsletters.net/photos/000000h1.jpg";
	@Value("{search.movie.list}")
	private Resource movieResource;

	private Map<Integer,Movie> movies;

	private final Pattern releasedDatePattern;

	public MovieDataLoader() {
		movies = new HashMap<>();
		releasedDatePattern = Pattern.compile("[(0-9)]{6}$");
	}

	public List<Movie> getMovies() throws IOException {
		if (movies != null && !movies.isEmpty())
			return new ArrayList<>(movies.values());
		load();
		return new ArrayList<>(movies.values());
	}

	private void load() throws IOException {
		loadMovieFromFile(movieResource.getFile());
	}

	protected void setMovieFile(String movie) {
		this.movieResource = new ClassPathResource(movie);
	}

	protected void loadMovieFromFile(File file) {
		BufferedReader reader;
		Map<Integer,Movie> newMovies = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) {
				try {
					Movie movie = extractMovie(line);
					newMovies.put(movie.getId(), movie);
					line = reader.readLine();
				} catch (Exception e) {
					e.printStackTrace();
					line = reader.readLine();
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		movies.clear();
		movies.putAll(newMovies);
	}

	protected List<Movie> loadMovie(String allMovies) {
		String[] splitMovie = allMovies.split("\n");
		movies = new HashMap<>();
		for (String s : splitMovie) {
			Movie movie = extractMovie(s);
			movies.put(movie.getId(), movie);
		}
		return new ArrayList<>(movies.values());
	}

	/**
	 * Extracts movie information from eg.<br>
	 * 114709,http://www.imdb.com/title/tt114709,Toy Story (1995),8.3,Animation|Adventure|Comedy,"https://image_link.jpg"
	 * 112749,http://www.imdb.com/title/tt112749,"Cry, the Beloved Country (1995)",6.9,Drama|Thriller,"https://image_link.jpg"
	 * @param movieString
	 * @return
	 */
	protected Movie extractMovie(String movieString) {
		String quoteReplace = "(^\")|(\"$)";
		String[] split = split(movieString);
		String[] genres = split[4].split("[|]");
		String id = split[0];
		String poster =NO_POSTER;
		if(split.length>5)
			poster = split[5];
		String releasedDate = getReleasedDate(split[2].replaceAll(quoteReplace, ""));
		String movieName = split[2].replaceAll(quoteReplace, "");
		if (!releasedDate.isEmpty()) {
			try {
				movieName = movieName.substring(0, movieName.indexOf("(" + releasedDate + ")")).trim();
			} catch (Exception e) {
				System.out.println("Could not parse: " + movieName);
			}
		}

		Movie movie = new Movie();
		movie.setId(Integer.valueOf(id));
		movie.setImdbId(movie.getId());
		movie.setGenres(Arrays.asList(genres).stream().map(s -> s.replace("\r", "")).collect(Collectors.toList()));
		movie.setReleasedDate(releasedDate);
		movie.setName(movieName);
		movie.setPoster(poster);
		return movie;
	}

	protected String[] split(String name) {
		List<String> tokens = new ArrayList<>();
		boolean quotes = false;
		StringBuilder builder = new StringBuilder();
		for (char c : name.toCharArray()) {
			switch (c) {
			case ',':
				if (quotes) {
					builder.append(c);
				} else {
					tokens.add(builder.toString());
					builder = new StringBuilder();
				}
				break;
			case '\"':
				quotes = !quotes;
				break;
			default:
				builder.append(c);
				break;
			}
		}
		if (builder.length() != 0)
			tokens.add(builder.toString());
		return tokens.toArray(new String[tokens.size()]);
	}

	/**
	 * Returns released date by extracting year from name of the movie using Regular
	 * Expression
	 * 
	 * @param movieNameWithReleasedDate
	 * @return
	 */
	private String getReleasedDate(String movieNameWithReleasedDate) {
		Matcher matcher = releasedDatePattern.matcher(movieNameWithReleasedDate);
		if (matcher.find()) {
			String matched = matcher.group();
			return matched.substring(1, matched.length() - 1);
		}
		return "";
	}
}
