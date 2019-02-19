package com.example.parse.core.service;

public interface ParserService {

  String startParsing(int code);

  String getResult(int code);

  String getProgress(int code);

  String stop(int code);
}
