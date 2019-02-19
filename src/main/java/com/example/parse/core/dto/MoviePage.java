package com.example.parse.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;


@Data
public class MoviePage {
  private int page;
  @JsonProperty("total_pages")
  private int totalPages;
  private List<MovieInfo> results;
}
