package com.example.parse.core.data;

import java.util.concurrent.Future;
import lombok.Data;

@Data
public class ParserData {
  private TaskStatus status;
  private Future<Double> future;
}
