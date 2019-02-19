package com.example.parse.core.service;

import com.example.parse.core.dto.MoviePage;
import com.example.parse.core.dto.MovieTypeData;

public interface MovieService {

    MoviePage getMovieData(int page);

    MovieTypeData getMovieTypes();
}
