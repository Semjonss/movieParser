package com.example.parse.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieInfo {
  @JsonProperty("release_date")
  private Set<Integer> releaseDate;
  @JsonProperty("vote_average")
  private float voteAverage;
}
