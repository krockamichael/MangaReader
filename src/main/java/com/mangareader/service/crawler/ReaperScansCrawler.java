package com.mangareader.service.crawler;

import com.mangareader.entity.MangaEntity;
import com.vaadin.flow.internal.Pair;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.BooleanUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.mangareader.constants.StringConstants.USER_AGENT;

@Log4j2
@Service
public class ReaperScansCrawler extends AbstractCrawler {

  private static final String BASE_URL = "https://reaperscans.com/comics/";

  public ReaperScansCrawler() {
    clearFileNames();
  }

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

  private void loadAndSaveImages(String mangaName, String numOfChapters, List<String> imageUrls) {
    Map<Pair<String, String>, Future<BufferedImage>> futureMap = new HashMap<>();

    for (String imageUrl : imageUrls) {
      Pair<String, String> fileNamePair = toPngFilename(mangaName, numOfChapters, imageUrl);
      if (fileExists(fileNamePair.getSecond())) {
        continue;
      }

      futureMap.put(fileNamePair, asyncLoadImageContent(imageUrl));
    }
    getAsyncResponses(futureMap);
  }

  private Future<Boolean> writeImage(Pair<String, String> fileNamePair, Future<BufferedImage> future, boolean isIcon) {
    BufferedImage image;
    try {
      image = future.get();
    } catch (InterruptedException | ExecutionException e) {
      log.error(e);
      return null;
    }
    if (isIcon) {
      image = resize(image, 100, 150);
    }

    filenames.add(fileNamePair.getFirst());
    return asyncWriteImage(fileNamePair.getSecond(), image);
  }

  private void getAsyncResponses(Map<Pair<String, String>, Future<BufferedImage>> futureMap) {
    STOP_WATCH.start();

    while (futureMap.size() != 0) {
      Iterator<Map.Entry<Pair<String, String>, Future<BufferedImage>>> it = futureMap.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<Pair<String, String>, Future<BufferedImage>> entry = it.next();
        if (entry.getValue().isDone()) {
          writeImage(entry.getKey(), entry.getValue(), false);
          log.info("{} is finished.", entry.getKey().getFirst());
          it.remove();
        }
      }
    }
    stopStopWatch();
  }

  private boolean fileExists(String path) {
    return new File(path).exists();
  }

  public void parseAndSaveIcon(MangaEntity entity) {
    try {
      Document document = Jsoup.connect(toUrl(BASE_URL, entity.getUrlName()))
          .userAgent(USER_AGENT)
          .get();

      String iconUrl = document.select("div > img[src]")
          .stream()
          .map(e -> e.attr("src"))
          .findFirst()
          .orElse(null);

      // TODO: move to get png name method ??????????
      if (iconUrl == null) {
        return;
      }

      Pair<String, String> fileNamePair = toPngFilename(entity.getUrlName(), iconUrl);
      if (fileExists(fileNamePair.getSecond())) {
        entity.setIconPath(fileNamePair.getFirst()); // FIXME - should not be needed here
        return;
      }

      Future<BufferedImage> bufferedImageFuture = asyncLoadImageContent(iconUrl);
      while (!bufferedImageFuture.isDone()) {
      }
      if (bufferedImageFuture.isDone()) {
        Future<Boolean> z = writeImage(fileNamePair, bufferedImageFuture, true);
        try {
          assert z != null;
          Boolean bla = z.get();
          if (BooleanUtils.isTrue(bla) && z.isDone()) {
            entity.setIconPath(fileNamePair.getFirst());
          }
        } catch (InterruptedException | ExecutionException e) {
          log.error(e);
        }
      }
      // TODO: save icon
      //  set url to entity
    } catch (IOException e) {
      log.error(e);
    }
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
