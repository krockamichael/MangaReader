package com.mangareader.service.crawler;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractCrawler {

  protected String toUrl(String baseUrl, String input) {
    return baseUrl.concat(input);
  }

}
