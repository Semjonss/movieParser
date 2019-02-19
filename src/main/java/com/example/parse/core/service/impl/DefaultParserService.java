package com.example.parse.core.service.impl;

import static com.example.parse.constants.Constant.*;

import com.example.parse.core.data.ParserData;
import com.example.parse.core.data.TaskStatus;
import com.example.parse.core.service.MovieService;
import com.example.parse.core.service.ParserService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for support and manage all task I want to make it more accurate, add enums for status and
 * support good practice with answer
 */
@Service
public class DefaultParserService implements ParserService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultParserService.class);
  private ConcurrentMap<Integer, ParserData> concurrentMap = new ConcurrentHashMap<>();
  private Lock lock = new ReentrantLock();

  @Autowired
  private AsyncParser asyncParser;

  @Autowired
  private MovieService movieService;

  /**
   * method for crate task
   */
  @Override
  public String startParsing(int code) {
    if (isInvalidCode(code)) {
      LOG.error("Invalid code {}", code);
      return INVALID_CODE_MASSAGE;
    } else {

      lock.lock();
      try {
        ParserData parserData = new ParserData();
        TaskStatus status = new TaskStatus();
        parserData.setStatus(status);
        Future<Double> future = asyncParser.getAverageRating(code, status);
        parserData.setFuture(future);
        concurrentMap.putIfAbsent(code, parserData);

      } finally {
        lock.unlock();
      }
      LOG.debug("create task with code: {} ", code);
      return START_MASSAGE;
    }
  }

  @Override
  public String getProgress(int code) {
    ParserData parserData = concurrentMap.get(code);
    if (parserData == null) {
      return DON_T_CREATE_THIS_TASK_MASSAGE;
    } else {
      TaskStatus status = parserData.getStatus();

      if (0 == status.getAllListCount().get()) {
        return TASK_JUST_CREATE_MASSAGE;
      } else {
        return TASK_PROGRESS_MASSAGE + status.getCompleteListCount().get() + "/" + status
            .getAllListCount().get();
      }

    }
  }

  @Override
  public String getResult(int code) {
    ParserData parserData = concurrentMap.get(code);
    if (parserData == null) {
      return DON_T_CREATE_THIS_TASK_MASSAGE;
    } else {
      if (isTaskComplite(parserData)) {
        Double result;
        try {
          result = parserData.getFuture().get();
        } catch (InterruptedException | ExecutionException e) {
          LOG.error("something go wrong", e);
          return TASK_FAILED_MASSAGE;
        }
        return "result = " + result;
      } else {
        return TASK_DON_T_COMPLETE_MASSAGE;
      }
    }
  }

  @Override
  public String stop(int code) {
    ParserData parserData = concurrentMap.get(code);
    if (parserData == null) {
      return DON_T_CREATE_THIS_TASK_MASSAGE;
    } else {
      if (isTaskComplite(parserData)) {
        return TASK_ALREADY_DONE_MASSAGE;
      } else {
        lock.lock();
        try {
          parserData.getFuture().cancel(true);
          concurrentMap.remove(code);
        } finally {
          lock.unlock();
        }
        LOG.debug("we stop task {}", code);
        return TASK_STOP_MASSAGE;
      }
    }
  }

  private boolean isInvalidCode(final int code) {
    return movieService.getMovieTypes().getGenres().stream()
        .noneMatch(p -> p.getId() == code);
  }

  private boolean isTaskComplite(ParserData parserData) {
    return parserData.getStatus().getCompleteListCount().get() == (parserData.getStatus()
        .getAllListCount().get()) && (parserData.getStatus().getCompleteListCount().get() != 0);
  }
}
