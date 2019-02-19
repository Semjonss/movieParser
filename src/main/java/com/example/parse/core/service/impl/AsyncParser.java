package com.example.parse.core.service.impl;

import com.example.parse.core.data.TaskStatus;
import com.example.parse.core.dto.MovieInfo;
import com.example.parse.core.dto.MoviePage;
import com.example.parse.core.service.MovieService;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class AsyncParser {

  @Autowired
  private MovieService filmService;

  @Async
  public Future<Double> getAverageRating(int code, TaskStatus taskStatus) {
    AtomicInteger completeListCount = taskStatus.getCompleteListCount();
    AtomicInteger allListCount = taskStatus.getAllListCount();
    int filmCount = 0;
    float ratingSum = 0;

    MoviePage moviePage = filmService.getMovieData(1);
    allListCount.set(moviePage.getTotalPages());
    List<MovieInfo> movieInfoList = moviePage.getResults();
    if (movieInfoList == null) {
      return new AsyncResult<>(0d);
    }
    for (MovieInfo movieInfo : movieInfoList) {
      if (movieInfo.getReleaseDate().contains(code)) {
        filmCount++;
        ratingSum += movieInfo.getVoteAverage();
      }
    }

    completeListCount.set(1);
    while (!Thread.currentThread().isInterrupted() && isNotComplete(taskStatus)) {
      moviePage = filmService.getMovieData(completeListCount.get() + 1);
      if (moviePage != null && moviePage.getResults() != null) {
        movieInfoList = moviePage.getResults();
        for (MovieInfo movieInfo : movieInfoList) {
          if (movieInfo.getReleaseDate() != null && movieInfo.getReleaseDate().contains(code)) {
            filmCount++;
            ratingSum += movieInfo.getVoteAverage();
          }
        }
      }
      completeListCount.incrementAndGet();
    }
    double result = filmCount == 0 ? 0d : ratingSum / filmCount;
    return new AsyncResult<>(result);
  }

  private boolean isNotComplete(TaskStatus taskStatus) {
    return taskStatus.getAllListCount().get() > taskStatus.getCompleteListCount().get();
  }
}
