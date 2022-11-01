package com.mangareader.service.crawler;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.mangareader.constants.StringConstants.USER_AGENT;

/**
 * Crawler implementation for ReaperScans website.
 */
@Log4j2
@Service
@NoArgsConstructor
public class ReaperScansCrawler extends AbstractCrawler {

  private static final String BASE_URL = "https://reaperscans.com/comics/";

  @Override
  public List<String> parseChapter(String mangaID, Integer chapterID) {
    try {
      Document document = Jsoup.connect(toUrl(BASE_URL, mangaID))
          .userAgent(USER_AGENT)
          .get();

      String numOfChapters = parseLatestChapterNumber(document);
//      entity.setNumOfChapters(Integer.parseInt(numOfChapters));

      String chapterNumber = chapterID != null
          ? chapterID.toString()
          : numOfChapters;
      String chapterLink = parseLinkText(document, chapterNumber);

      Document latestChapter = Jsoup.connect(chapterLink)
          .userAgent(USER_AGENT)
          .get();

      return parseImages(latestChapter);
    } catch (IOException e) {
      log.error(e);
    }
    return Collections.emptyList();
  }

  @Override
  protected String parseLatestChapterNumber(Document document) {
    return document.select("div > div > h1")
        .stream()
        .skip(1)
        .findFirst()
        .map(Element::text)
        .filter(e -> !e.equals(""))
        .map(e -> e.split(" ")[0])
        .orElse(null);
  }

  @Override
  protected String parseLinkText(Document document, String chapterNumber) {
    return document.select("li > a[href]")
        .stream()
        .map(e -> e.attr("href"))
        .filter(e -> e.contains("chapter-" + chapterNumber))
        .findFirst()
        .orElse(null);
  }

  @Override
  protected List<String> parseImages(Document document) {
    return document.select("main > div > p > img[src]")
        .stream()
        .map(e -> e.attr("src"))
        .toList();
  }

  @Override
  public ListenableFuture<String> asyncLoadIcon(String mangaUrlName) {
    try {
      Document document = Jsoup.connect(toUrl(BASE_URL, mangaUrlName))
          .userAgent(USER_AGENT)
          .get();

      String iconUrl = document.select("div > img[src]")
          .stream()
          .map(e -> e.attr("src"))
          .findFirst()
          .orElse(null);

      assert iconUrl != null;
      return AsyncResult.forValue(iconUrl);
    } catch (IOException e) {
      log.error(e);
      return AsyncResult.forExecutionException(new RuntimeException("Error"));
    }
  }

  public ListenableFuture<String> asyncLoadIconTimed(String mangaUrlName) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    ListenableFuture<String> result = asyncLoadIcon(mangaUrlName);

    stopWatch.stop();
    log.info("Icon loaded for %s in %d ms".formatted(mangaUrlName, stopWatch.getLastTaskTimeMillis()));

    return result;
  }
}
