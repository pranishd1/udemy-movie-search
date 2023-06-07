package com.course.search.load;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Movie {

	private int id;
	private String releasedDate;
	private String name;
	private List<String> genres;
	private int imdbId;
	private String poster;

}
