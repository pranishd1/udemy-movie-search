
import React, { useState, useEffect } from "react";

import { Card, CardHeader, CardBody, CardFooter, Image, Stack, Heading, Text } from '@chakra-ui/react';
import { Wrap, WrapItem } from '@chakra-ui/react';
import { Input, Button } from '@chakra-ui/react';
import { Grid, GridItem } from '@chakra-ui/react';
import { Checkbox, CheckboxGroup } from '@chakra-ui/react';

export default function MovieComponent() {

  const DATA_URL = 'http://127.0.0.1:8080/api/v1/search/lucene'


  const [searchTerm, setSearchTerm] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [genreItems, setGenreItems] = React.useState([]);
  const [movieData, setMovieData] = React.useState({
    "result": [],
    "facets": {
      "labelValues": []
    }
  });
  const [labels, setLabels] = React.useState([]);

  const setMovies = async () => {
    let movieQuery = {};
    movieQuery["find"] = searchTerm;
    movieQuery["genres"] = genreItems;
    console.log("Movie Query: " + JSON.stringify(movieQuery));
    const response = await fetch(
      DATA_URL,
      {
        method: "POST",
        body: JSON.stringify(movieQuery),
        headers: {
          "Content-Type": "application/json",
        }
      }
    );
    let movieResponse = await response.json();
    setMovieData(movieResponse);
    setLabels(movieResponse['facets']['labelValues']);
  };


  useEffect(() => {
    setMovies();
  }, [searchTerm, genreItems]);

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setSearchTerm(searchQuery);
    setMovies();
  };


  const labelOperate = (label) => {
    const index = genreItems.indexOf(label);
    if (index > -1) {
      setGenreItems([
        ...genreItems.slice(0, index),
        ...genreItems.slice(index + 1)
      ]);
    } else {
      let items = [
        ...genreItems,
        label
      ];
      setGenreItems(items);
    }
  }

  const isLabelChecked = (label) => {
    return genreItems.includes(label);
  }

  return (
    <Grid
      templateAreas={`"header header"
                            "nav main"
                            "nav main"`}
      gridTemplateRows={'50px 1fr 30px'}
      gridTemplateColumns={'150px 1fr'}
      h='200px'
      gap='1'
      color='blackAlpha.700'
      fontWeight='bold'
    >
      <GridItem pl='2' area={'header'}> </GridItem>

      <GridItem pl='2' area={'nav'}>
        <div className='nav-padding'>
          <Stack>
            <h2>Genres</h2>
            {labels.map((label, i) => (
              <Genre key={i} label={label} labelOperate={labelOperate} isLabelChecked={isLabelChecked}></Genre>
            ))}
          </Stack>
        </div>
      </GridItem>

      <GridItem pl='2' area={'main'}>
        <div className="main">
          <h1>Movie Search</h1>
          <Wrap>
            <Input placeholder='search' size='md' width='auto'
              value={searchQuery}
              onChange={handleSearchChange}
            />
            <Button onClick={handleSearchSubmit} colorScheme='blue' size='md'>Search</Button>
          </Wrap>
          <div className="cards">

            {movieData['result'].map((movie, i) => (
              <MovieCard movie={movie} key={i} />
            ))}

          </div>
        </div>
      </GridItem>
    </Grid>


  );

}

export function Genre({ label, labelOperate, isLabelChecked }) {

  const [genre, setGenre] = useState("");
  const [value, setValue] = useState(0);

  useEffect(() => {

    setGenre(label.label);
    setValue(label.value);

  }, [label, genre]);

  return (
    <Checkbox
      onChange={event => {
        event.stopPropagation();
        labelOperate(genre);
      }}
      isChecked={isLabelChecked(genre)}
    >{genre} ({value})</Checkbox>
  );
}


export function MovieCard(props) {

  const { movie } = props;

  return (
    
    <Card maxW='sm' width='75%'>
      <CardBody>
        <Image
          padding-left='25px'
          width='200px'
          height='300px'
          padding-top='10px'
          src={movie.poster}
          alt={movie.name}
          borderRadius='lg'
        />
        <Stack mt='6' spacing='3'>
          <Heading size='md'>{movie.name} ({movie.releasedDate})</Heading>
          <Wrap>
            {movie.genres.map((g, i) => (
              <WrapItem key={i}><Text>{g}</Text></WrapItem>
            ))}
          </Wrap>
        </Stack>
      </CardBody>
    </Card>

  );

}






