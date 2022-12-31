package com.mangareader.backend.service.crawler;

import com.mangareader.backend.data.service.Utils;
import com.mangareader.backend.entity.Manga;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.mangareader.backend.data.Constants.*;

/**
 * Abstract crawler class that defines the basic process of parsing images.
 */
@Log4j2
public abstract class Crawler {

  private static final String GOOGLE_SEARCH_URL = "http://www.google.com/search?q=";

  protected String toUrl(String... input) {
    StringBuilder builder = new StringBuilder();
    for (String s : input) {
      builder.append(s);
    }
    return builder.toString();
  }

  Document getDocument(@NotNull String url) {
    try {
      return Jsoup.connect(url)
          .userAgent(USER_AGENT)
          .get();
    } catch (IOException e) {
      log.error(e);
      return null;
    }
  }

  Document getDocumentWithNumberOfRetries(@NotNull String url, int numberOfRetries) {
    for (int i = 0; i < numberOfRetries; i++) {
      Document document = getDocument(url);
      if (document != null) {
        log.info("Retrieving document took {} tries", i + 1);
        return document;
      }
    }

    return null;
  }

  void writeImage(String fileName, BufferedImage image) {
    try {
      image = Utils.resize(image, INT_GRID_IMG_HEIGHT, INT_GRID_IMG_WIDTH);
      ImageIO.write(image, "png", new File(fileName));
    } catch (IOException e) {
      log.error(e);
    }
  }

  protected BufferedImage getImage(String imageUrl) {
    try {
      URL url = new URL(imageUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("User-Agent", USER_AGENT);
      return ImageIO.read(connection.getInputStream());
    } catch (IOException e) {
      log.error(e);
      return null;
    }
  }

  /**
   * Create Google search result URL from manga name and chapter number. URL is encoded to UTF-8.
   * @param mangaName non-null manga name
   * @param chapterID non-null chapter number
   * @return the String URL
   */
  String getGoogleSearchUrlForChapter(@NotNull String siteName, @NotNull String mangaName, @NotNull Integer chapterID) {
    String search = "site:" + siteName + " " + mangaName + " chapter " + chapterID;
    return toUrl(GOOGLE_SEARCH_URL, URLEncoder.encode(search, StandardCharsets.UTF_8), "&num=5");
  }

  /**
   * Gets the base URL for some scans' website.
   * @return the base URL
   */
  protected abstract String getBaseUrl();

  /**
   * Method to parse a chapter based on manga name and chapter number. First
   * we open the manga page and parse the latest chapter number, which is used
   * if no chapter number was supplied. Then we parse link text and open the
   * chapter page. Lastly we parse image src attributes and return them.
   * @param entity    the manga entity
   * @param chapterID the chapter number
   * @return list of image sources
   */
  public abstract List<String> parseChapter(Manga entity, Integer chapterID);

  /**
   * On the manga overview page we parse the latest chapter number and return it.
   * @param document the manga overview page
   * @return the latest chapter number
   */
  protected abstract Integer parseLatestChapterNumber(Document document);

  /**
   * Parses a chapter URL from Google search result page.
   * @param document the Google search result page
   * @return the href content (URL) pointing to chapter site
   */
  protected abstract String parseChapterURL(Document document);

  /**
   * Parses all list elements that contain img elements with src attributes.
   * @param document the chapter page
   * @return list of all chapter src images
   */
  protected abstract List<String> parseImages(Document document);

  /**
   * Asynchronous method for parsing icon from manga overview pages.
   * Also fetches the latest chapter number and updates the entity.
   * @param entity the manga entity
   * @return the img element src attribute containing the icon
   */
  @Async
  public abstract ListenableFuture<String> asyncLoadIcon(Manga entity);
}
