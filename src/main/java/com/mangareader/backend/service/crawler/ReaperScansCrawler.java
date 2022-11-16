package com.mangareader.backend.service.crawler;

import com.mangareader.backend.dto.SearchResultDto;
import com.mangareader.backend.entity.Manga;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static com.mangareader.backend.data.service.Utils.getDownloadPath;
import static com.mangareader.backend.data.service.Utils.getRelativePath;
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
  public List<String> parseChapter(Manga entity, Integer chapterID) {
    double pageNum = ceil(((double) (entity.getLatestChNum() - chapterID - 1) / CH_PER_PAGE));

    Document document = pageNum <= 1
        ? getDocument(toUrl(BASE_URL, entity.getUrlName()))
        : getDocument(toUrl(BASE_URL, entity.getUrlName(), "?page=", Double.toString(pageNum)));

    entity.setLatestChNum(parseLatestChapterNumber(document));
    String chapterLink = parseLinkText(document, chapterID.toString());
    Document latestChapter = getDocument(chapterLink);

    return parseImages(latestChapter);
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

  //TODO: can this be implemented in abstract crawler and the asyncLoadIcon be overridden in subclasses?
  public ListenableFuture<String> asyncLoadIconTimed(Manga entity) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    ListenableFuture<String> result = asyncLoadIcon(entity);

    stopWatch.stop();
    log.info("Icon loaded for %s in %d ms".formatted(entity.getName(), stopWatch.getLastTaskTimeMillis()));
    return result;
  }

  /**
   * Icon is not downloaded, download it in background, show icon from website
   *
   * @param entity the manga entity
   * @return the icon url
   */
  @Async
  @Override
  public ListenableFuture<String> asyncLoadIcon(Manga entity) {
    Document document = getDocument(toUrl(BASE_URL, entity.getUrlName()));
    entity.setLatestChNum(parseLatestChapterNumber(document));
    String iconUrl = document.select("div > img[src]")
        .stream()
        .map(e -> e.attr("src"))
        .findFirst()
        .orElse(null);

    new Thread(() -> asyncDownloadIcon(entity, iconUrl)).start();

    assert iconUrl != null;
    return AsyncResult.forValue(iconUrl);
  }

  //TODO: is this not also a super method?
  @Async
  private void asyncDownloadIcon(Manga entity, String iconUrl) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    log.info("Started download for %s".formatted(entity.getName()));

    String newIconPath = getDownloadPath(entity.getUrlName());
    writeImage(newIconPath, getImage(iconUrl));
    entity.setIconPath(getRelativePath(newIconPath));

    stopWatch.stop();
    log.info("Finished download for %s icon in %d ms.".formatted(entity.getName(), stopWatch.getLastTaskTimeMillis()));
  }

  @Async
  public ListenableFuture<Integer> fetchLatestChapterNumber(Manga entity) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    Document document = getDocument(toUrl(BASE_URL, entity.getUrlName()));

    stopWatch.stop();
    log.info("Latest chapter update for %s in %d ms".formatted(entity.getName(), stopWatch.getLastTaskTimeMillis()));

    assert document != null;
    return AsyncResult.forValue(parseLatestChapterNumber(document));
  }

  // https://curlconverter.com/java/
  public List<SearchResultDto> getMangaUrl(String value, PageRequest pageRequest) {
    if (value == null)
      return Collections.emptyList();

    try {
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();

      HttpURLConnection httpConn = (HttpURLConnection) new URL("https://reaperscans.com/livewire/message/frontend.dtddzhx-ghvjlgrpt").openConnection();
      httpConn.setRequestMethod("POST");

      httpConn.setRequestProperty("content-type", "application/json");
      httpConn.setRequestProperty("referer", "https://reaperscans.com/");
      httpConn.setRequestProperty("user-agent", "Chrome");
      httpConn.setRequestProperty("x-livewire", "true");

      httpConn.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
      writer.write("{\"fingerprint\":{\"id\":\"olJuYSFBkawm5K7qqSJk\",\"name\":\"frontend.dtddzhx-ghvjlgrpt\",\"locale\":\"en\",\"path\":\"/\",\"method\":\"GET\",\"v\":\"acj\"},\"serverMemo\":{\"children\":[],\"errors\":[],\"htmlHash\":\"5a182466\",\"data\":{\"query\":\"\",\"comics\":[],\"novels\":[]},\"dataMeta\":[],\"checksum\":\"ecf28746c7fd2589eebf011c40e8c26656c8e73c52c47714f79d35e433a3b834\"},\"updates\":[{\"type\":\"syncInput\",\"payload\":{\"id\":\"enwxj\",\"name\":\"query\"," +
          "\"value\":\"%s\"}}]}".formatted(value));
      writer.flush();
      writer.close();
      httpConn.getOutputStream().close();

      InputStream responseStream = httpConn.getResponseCode() / 100 == 2
          ? httpConn.getInputStream()
          : httpConn.getErrorStream();
      Scanner s = new Scanner(responseStream).useDelimiter("\\A");
      String response = s.hasNext() ? s.next() : "";

      Document document = Jsoup.parse(response, "UTF-8");
      List<String> urls = parseResponseLinks(document);
      List<String> names = parseResponseNames(document);
      List<String> icons = parsResponseIcons(document);
      List<Integer> latestChapters = parseResponseLatestChapters(document);

      List<SearchResultDto> resultDtos = new ArrayList<>();
      for (int i = 0; i < urls.size(); i++) {
        resultDtos.add(new SearchResultDto(names.get(i), urls.get(i), icons.get(i), latestChapters.get(i)));
      }

      stopWatch.stop();
      log.info("Found %d search results for \"%s\" loaded in %d ms"
          .formatted(resultDtos.size(), value, stopWatch.getLastTaskTimeMillis()));

      return resultDtos;
    } catch (IOException e) {
      log.error(e);
      return Collections.emptyList();
    }
  }

  private List<String> parseResponseLinks(Document document) {
    return document
        .select("a[href*=comics]")
        .stream()
        .map(e -> e.attr("href"))
        .distinct()
        .map(e -> e.split("/")[4].replace("\\\"", ""))
        .toList();
  }

  private List<String> parseResponseNames(Document document) {
    return document
        .select("a[href*=comics] > div > div > p[text-neutral-200]")
        .stream()
        .map(Element::text)
        .map(e -> e.trim().split("\\\\n ")[1])
        .toList();
  }

  private List<String> parsResponseIcons(Document document) {
    return document
        .select("a[href*=comics] > div > img[src]")
        .stream()
        .map(e -> e.attr("src"))
        .map(e -> e.replace("\\\"", "").replace("/", ""))
        .toList();
  }

  private List<Integer> parseResponseLatestChapters(Document document) {
    return document
        .select("a[href*=comics] > div > div > p > span > span > i > span > i")
        .stream()
        .map(Element::text)
        .map(e -> e.split(" ")[1])
        .map(Integer::parseInt)
        .toList();
  }
}
