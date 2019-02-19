package com.example.parse.core.service.impl;

import com.example.parse.core.component.MyResponseErrorHandler;
import com.example.parse.core.dto.MoviePage;
import com.example.parse.core.dto.MovieTypeData;
import com.example.parse.core.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * Service for make query for test site
 */
@Service
public class DefaultMovieService implements MovieService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultMovieService.class);
  private final RestTemplate restTemplate;
  @Value("${movie-page-url}")
  private String MOVIE_INFO;
  @Value("${movie-info-url}")
  private String MOVIE_TYPE;

  @Autowired
  public DefaultMovieService(RestTemplateBuilder restTemplateBuilder,
      MyResponseErrorHandler myResponseErrorHandler
  ) {

    this.restTemplate = restTemplateBuilder
        .errorHandler(myResponseErrorHandler)
        .build();
  }

  /**
   * get query with film information by page
   *
   * @param page - № page
   * @return MoviePage in current page
   */
  @Override
  public MoviePage getMovieData(int page) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_XML);
    HttpEntity<MoviePage> entity = new HttpEntity<>(headers);
    ResponseEntity<MoviePage> response = restTemplate.exchange(MOVIE_INFO + page,
        HttpMethod.GET, entity, MoviePage.class);
    MoviePage result = null;
    if (HttpStatus.OK.equals(response.getStatusCode())) {
      result = response.getBody();
    }
    LOG.debug("Get result: {}", result);
    return result;
  }

  /**
   * method for get film types
   *
   * @return MovieTypeData - construction containing film Types
   */
  @Override
  public MovieTypeData getMovieTypes() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_XML);
    HttpEntity<MovieTypeData> entity = new HttpEntity<>(headers);
    ResponseEntity<MovieTypeData> response = restTemplate.exchange(MOVIE_TYPE,
        HttpMethod.GET, entity, MovieTypeData.class);
    MovieTypeData result = null;
    if (HttpStatus.OK.equals(response.getStatusCode())) {
      result = response.getBody();
    }
    LOG.debug("Get result: {}", result);
    return result;
  }
}
