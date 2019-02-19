package com.example.parse.core.controler;


import com.example.parse.core.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Just controller for touch method I add Swagger for easy touch it
 */
@RestController
public class ParseController {

  final private ParserService service;

  @Autowired
  public ParseController(ParserService service) {
    this.service = service;
  }

  @RequestMapping(value = "/start", method = RequestMethod.GET)
  public String start(@RequestParam int code) {
    return service.startParsing(code);
  }

  @RequestMapping(value = "/progress", method = RequestMethod.GET)
  public String getProgress(@RequestParam int code) {
    return service.getProgress(code);
  }

  @RequestMapping(value = "/result", method = RequestMethod.GET)
  public String getResult(@RequestParam int code) {
    return service.getResult(code);
  }

  @RequestMapping(value = "/stop", method = RequestMethod.GET)
  public String stop(@RequestParam int code) {
    return service.stop(code);
  }
}
