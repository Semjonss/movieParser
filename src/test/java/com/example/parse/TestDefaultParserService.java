package com.example.parse;

import static com.example.parse.constants.Constant.DON_T_CREATE_THIS_TASK_MASSAGE;
import static com.example.parse.constants.Constant.TASK_ALREADY_DONE_MASSAGE;
import static com.example.parse.constants.Constant.TASK_STOP_MASSAGE;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.example.parse.core.dto.Genres;
import com.example.parse.core.dto.MovieInfo;
import com.example.parse.core.dto.MoviePage;
import com.example.parse.core.dto.MovieTypeData;
import com.example.parse.core.service.MovieService;
import com.example.parse.core.service.impl.DefaultParserService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.internal.stubbing.answers.Returns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDefaultParserService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultParserService.class);
  private static final int TEST_FILM_ID = 1;
  private static final String TEST_NAME_FILM = "test";
  private static final int SLEEPY_SHORT_TIME = 10;
  private static final int SLEEPY_LONG_TIME = 100;
  private static final String TASK_PROGRESS_COMPLETE_MASSAGE = "Task progress: 3/3";
  private static final String TASK_RESULT_COMPLETE_MASSAGE = "result = 5.1";

  @MockBean
  private MovieService movieService;

  @Autowired
  private DefaultParserService parserService;

  private MoviePage moviePage1 = new MoviePage();
  private MoviePage moviePage2 = new MoviePage();
  private MoviePage moviePage3 = new MoviePage();


  @Before
  public void init() {

    moviePage1.setPage(1);
    moviePage2.setPage(2);
    moviePage3.setPage(3);

    moviePage1.setTotalPages(3);
    moviePage2.setTotalPages(3);
    moviePage3.setTotalPages(3);

    Set<Integer> releaseDate = new HashSet<>();
    releaseDate.add(1);
    List<MovieInfo> movieInfoList1 = new ArrayList<>();
    List<MovieInfo> movieInfoList2 = new ArrayList<>();
    List<MovieInfo> movieInfoList3 = new ArrayList<>();

    movieInfoList1.add(new MovieInfo(releaseDate, 1.0f));
    movieInfoList1.add(new MovieInfo(releaseDate, 2.0f));
    movieInfoList1.add(new MovieInfo(releaseDate, 3.0f));

    movieInfoList2.add(new MovieInfo(releaseDate, 4.0f));
    movieInfoList2.add(new MovieInfo(releaseDate, 5.9f));
    movieInfoList2.add(new MovieInfo(releaseDate, 6.0f));

    movieInfoList3.add(new MovieInfo(releaseDate, 7.0f));
    movieInfoList3.add(new MovieInfo(releaseDate, 8.0f));
    movieInfoList3.add(new MovieInfo(releaseDate, 9.0f));

    moviePage1.setResults(movieInfoList1);
    moviePage2.setResults(movieInfoList2);
    moviePage3.setResults(movieInfoList3);

    MovieTypeData movieTypeData = new MovieTypeData();
    List<Genres> genresList = new ArrayList<>();
    genresList.add(new Genres(TEST_FILM_ID, TEST_NAME_FILM));
    movieTypeData.setGenres(genresList);

    when(movieService.getMovieTypes()).thenReturn(movieTypeData);
  }

  /**
   * Test for good outcome, when we wait for complete task, during test i wait when progress will
   * complete.
   */
  @Test
  public void comliteTaskTest() {
    doAnswer(new AnswersWithDelay(SLEEPY_SHORT_TIME, new Returns(moviePage1)))
        .when(movieService)
        .getMovieData(1);
    doAnswer(new AnswersWithDelay(SLEEPY_SHORT_TIME, new Returns(moviePage2)))
        .when(movieService)
        .getMovieData(2);
    doAnswer(new AnswersWithDelay(SLEEPY_SHORT_TIME, new Returns(moviePage3)))
        .when(movieService)
        .getMovieData(3);

    parserService.startParsing(TEST_FILM_ID);

    String progressAnswer = parserService.getProgress(TEST_FILM_ID);

    while (!TASK_PROGRESS_COMPLETE_MASSAGE.equals(progressAnswer)) {
      progressAnswer = parserService.getProgress(TEST_FILM_ID);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        LOG.error("Thread.sleep failed", e);
      }
    }

    Assert
        .assertTrue(parserService.getResult(TEST_FILM_ID).startsWith(TASK_RESULT_COMPLETE_MASSAGE));
    Assert.assertTrue(parserService.stop(TEST_FILM_ID).startsWith(TASK_ALREADY_DONE_MASSAGE));
  }


  @Test
  public void stopTaskTest() {
    doAnswer(new AnswersWithDelay(SLEEPY_LONG_TIME, new Returns(moviePage1)))
        .when(movieService)
        .getMovieData(1);
    doAnswer(new AnswersWithDelay(SLEEPY_LONG_TIME, new Returns(moviePage2)))
        .when(movieService)
        .getMovieData(2);
    doAnswer(new AnswersWithDelay(SLEEPY_LONG_TIME, new Returns(moviePage3)))
        .when(movieService)
        .getMovieData(3);

    parserService.startParsing(TEST_FILM_ID);
    Assert.assertFalse(
        parserService.getProgress(TEST_FILM_ID).startsWith(TASK_PROGRESS_COMPLETE_MASSAGE));

    Assert.assertTrue(parserService.stop(TEST_FILM_ID).startsWith(TASK_STOP_MASSAGE));

    Assert.assertTrue(parserService.getResult(TEST_FILM_ID).startsWith(
        DON_T_CREATE_THIS_TASK_MASSAGE));

    Assert.assertTrue(parserService.stop(TEST_FILM_ID).startsWith(DON_T_CREATE_THIS_TASK_MASSAGE));
  }

}
