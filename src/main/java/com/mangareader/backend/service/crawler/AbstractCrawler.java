package com.mangareader.backend.service.crawler;

import com.mangareader.backend.data.entity.MangaEntity;
import com.mangareader.backend.data.service.Utils;
import lombok.extern.log4j.Log4j2;
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
import java.util.List;

import static com.mangareader.backend.data.Constants.*;

/**
 * Abstract crawler class that defines the basic process of parsing images.
 */
@Log4j2
public abstract class AbstractCrawler {

  protected String toUrl(String... input) {
    StringBuilder builder = new StringBuilder();
    for (String s : input) {
      builder.append(s);
    }
    return builder.toString();
  }

  Document getDocument(String url) {
    try {
      return Jsoup.connect(url)
          .userAgent(USER_AGENT)
          .get();
    } catch (IOException e) {
      log.error(e);
      return null;
    }
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
   * Method to parse a chapter based on manga name and chapter number. First
   * we open the manga page and parse the latest chapter number, which is used
   * if no chapter number was supplied. Then we parse link text and open the
   * chapter page. Lastly we parse image src attributes and return them.
   *
   * @param entity    the manga entity
   * @param chapterID the chapter number
   * @return list of image sources
   */
  public abstract List<String> parseChapter(MangaEntity entity, Integer chapterID);

  /**
   * On the manga overview page we parse the latest chapter number and return it.
   *
   * @param document the manga overview page
   * @return the latest chapter number
   */
  protected abstract Integer parseLatestChapterNumber(Document document);

  /**
   * The href attribute of the anchor tag that corresponds to the chapter
   * with specified chapter number.
   *
   * @param document      the manga overview page
   * @param chapterNumber the requested chapter number
   * @return the href content pointing to chapter site
   */
  protected abstract String parseLinkText(Document document, String chapterNumber);

  /**
   * Parses all list elements that contain img elements with src attributes.
   *
   * @param document the chapter page
   * @return list of all chapter src images
   */
  protected abstract List<String> parseImages(Document document);

  /**
   * Asynchronous method for parsing icon from manga overview pages.
   * Also fetches the latest chapter number and updates the entity.
   *
   * @param entity the manga entity
   * @return the img element src attribute containing the icon
   */
  @Async
  public abstract ListenableFuture<String> asyncLoadIcon(MangaEntity entity);
}
