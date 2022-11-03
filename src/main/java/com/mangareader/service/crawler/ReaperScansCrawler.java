package com.mangareader.service.crawler;

import com.mangareader.entity.MangaEntity;
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
import static java.lang.Math.ceil;

/**
 * Crawler implementation for ReaperScans website.
 */
@Log4j2
@Service
@NoArgsConstructor
public class ReaperScansCrawler extends AbstractCrawler {

  private static final String BASE_URL = "https://reaperscans.com/comics/";
  private static final Integer CH_PER_PAGE = 32;

  @Override
  public List<String> parseChapter(MangaEntity entity, Integer chapterID) {
    try {
      double pageNum = ceil(((double) (entity.getLatestChapterNumber() - chapterID - 1) / CH_PER_PAGE));

      Document document;
      if (pageNum <= 1) {
        document = Jsoup.connect(toUrl(BASE_URL, entity.getUrlName()))
            .userAgent(USER_AGENT)
            .get();
      } else {
        document = Jsoup.connect(toUrl(BASE_URL, entity.getUrlName(), "?page=", Double.toString(pageNum)))
            .userAgent(USER_AGENT)
            .get();
      }

      entity.setLatestChapterNumber(parseLatestChapterNumber(document));
      String chapterLink = parseLinkText(document, chapterID.toString());

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
  protected Integer parseLatestChapterNumber(Document document) {
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
  public ListenableFuture<String> asyncLoadIcon(MangaEntity entity) {
    try {
      Document document = Jsoup.connect(toUrl(BASE_URL, entity.getUrlName()))
          .userAgent(USER_AGENT)
          .get();

      entity.setLatestChapterNumber(parseLatestChapterNumber(document));

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

  public ListenableFuture<String> asyncLoadIconTimed(MangaEntity entity) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    ListenableFuture<String> result = asyncLoadIcon(entity);

    stopWatch.stop();
    log.info("Icon loaded for %s in %d ms".formatted(entity.getUrlName(), stopWatch.getLastTaskTimeMillis()));

    return result;
  }
}
