package com.course.search.load;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

public class MovieDataLoaderTest {

	MovieDataLoader loader = new MovieDataLoader();

	@Test
	public void loadMovieFromFile() throws IOException {
		assertEquals(getMovies(loader).size() > 0, true);
	}

	@Test
	public void extractMovie(){
		String movie="112749,http://www.imdb.com/title/tt112749,\"Cry, the Beloved Country (1995)\",6.9,Drama|Thriller,\"https://images-na.ssl-images-amazon.com/images/M/MV5BMTcwMDU1OTEwOF5BMl5BanBnXkFtZTcwMTg5NjEyMQ@@._V1_UY268_CR3,0,182,268_AL_.jpg\"";
		String[] split = loader.split(movie);
		assertEquals(split[0],"112749");
		assertEquals(split[1],"http://www.imdb.com/title/tt112749");
		assertEquals(split[2],"Cry, the Beloved Country (1995)");
		assertEquals(split[3],"6.9");
		assertEquals(split[4],"Drama|Thriller");
		assertEquals(split[5],"https://images-na.ssl-images-amazon.com/images/M/MV5BMTcwMDU1OTEwOF5BMl5BanBnXkFtZTcwMTg5NjEyMQ@@._V1_UY268_CR3,0,182,268_AL_.jpg");
	}

	public List<Movie> getMovies(MovieDataLoader loader) throws IOException {
		String file = "movie_genre_poster.csv";
		loader.setMovieFile(file);
		return loader.getMovies();
	}

	@Test
	public void movieListTest() {
		String allMovies = "114709,http://www.imdb.com/title/tt114709,Toy Story (1995),8.3,Animation|Adventure|Comedy,\"https://images-na.ssl-images-amazon.com/images/M/MV5BMDU2ZWJlMjktMTRhMy00ZTA5LWEzNDgtYmNmZTEwZTViZWJkXkEyXkFqcGdeQXVyNDQ2OTk4MzI@._V1_UX182_CR0,0,182,268_AL_.jpg\"\n" +
				"113497,http://www.imdb.com/title/tt113497,Jumanji (1995),6.9,Action|Adventure|Family,\"https://images-na.ssl-images-amazon.com/images/M/MV5BZTk2ZmUwYmEtNTcwZS00YmMyLWFkYjMtNTRmZDA3YWExMjc2XkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_UY268_CR10,0,182,268_AL_.jpg\"";
		List<Movie> movies = loader.loadMovie(allMovies);
		assertEquals(movies.get(0).getId(), 114709);
		assertEquals(movies.get(0).getName(), "Toy Story");
		assertEquals(movies.get(0).getReleasedDate(), "1995");
		assertEquals(movies.get(0).getGenres(), Arrays.asList("Animation","Adventure","Comedy"));

		assertEquals(movies.get(1).getId(), 113497);
		assertEquals(movies.get(1).getName(), "Jumanji");
		assertEquals(movies.get(1).getReleasedDate(), "1995");
		assertEquals(movies.get(1).getGenres(), Arrays.asList("Action","Adventure","Family"));
	}

	@Test
	public void movieTest() {
		String movieString="112749,http://www.imdb.com/title/tt112749,\"Cry, the Beloved Country (1995)\",6.9,Drama|Thriller,\"http_link\"";
		Movie movie = loader.extractMovie(movieString);
		assertEquals(movie.getId(), 112749);
		assertEquals(movie.getName(), "Cry, the Beloved Country");
		assertEquals(movie.getReleasedDate(), "1995");
		assertEquals(movie.getGenres(), Arrays.asList("Drama", "Thriller"));
		assertEquals(movie.getPoster(),"http_link");
	}
}
