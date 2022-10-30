package com.mangareader.service.crawler;

import com.mangareader.entity.MangaEntity;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.mangareader.constants.StringConstants.USER_AGENT;

@Log4j2
@Service
@NoArgsConstructor
public class ReaperScansCrawler extends AbstractCrawler {

  private static final String BASE_URL = "https://reaperscans.com/comics/";

  public List<String> parseChapter(MangaEntity entity) {
    try {
      Document document = Jsoup.connect(toUrl(BASE_URL, entity.getUrlName()))
          .userAgent(USER_AGENT)
          .get();

      // FIXME: potential NPE
      String numOfChapters = getNumberOfChapters(document).toString();
      String latestChapterLink = parseLinkText(document, numOfChapters);
      entity.setNumOfChapters(Integer.parseInt(numOfChapters));

      Document latestChapter = Jsoup.connect(latestChapterLink)
          .userAgent(USER_AGENT)
          .get();

      return parseImages(latestChapter);
    } catch (IOException e) {
      log.error(e);
    }
    return Collections.emptyList();
  }

  private Integer getNumberOfChapters(Document document) {
    return document.select("div > div > h1")
        .stream()
        .skip(1)
        .findFirst()
        .map(Element::text)
        .filter(e -> !e.equals(""))
        .map(e -> e.split(" ")[0])
        .map(Integer::parseInt)
        .orElse(null);
  }

  private String parseLinkText(Document document, String chapterNumber) {
    return document.select("li > a[href]")
        .stream()
        .findFirst()
        .map(e -> e.attr("href"))
        .filter(e -> e.contains(chapterNumber))
        .orElse(null);
  }

  private List<String> parseImages(Document document) {
    return document.select("main > div > p > img[src]")
        .stream()
        .map(e -> e.attr("src"))
        .toList();
  }

  @Async
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
}
