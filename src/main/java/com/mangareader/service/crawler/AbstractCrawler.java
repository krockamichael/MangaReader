package com.mangareader.service.crawler;

import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

/**
 * Abstract crawler class that defines the basic process of parsing images.
 */
public abstract class AbstractCrawler {

  protected String toUrl(String baseUrl, String input) {
    return baseUrl.concat(input);
  }

  /**
   * Method to parse a chapter based on manga name and chapter number. First
   * we open the manga page and parse the latest chapter number, which is used
   * if no chapter number was supplied. Then we parse link text and open the
   * chapter page. Lastly we parse image src attributes and return them.
   *
   * @param mangaID   the url name of the manga
   * @param chapterID the chapter number
   * @return list of image sources
   */
  public abstract List<String> parseChapter(String mangaID, Integer chapterID);

  /**
   * On the manga overview page we parse the latest chapter number and return it.
   *
   * @param document the manga overview page
   * @return the latest chapter number
   */
  protected abstract String parseLatestChapterNumber(Document document);

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
   *
   * @param mangaUrlName the url name of the manga
   * @return the img element src attribute containing the icon
   */
  @Async
  public abstract ListenableFuture<String> asyncLoadIcon(String mangaUrlName);
}
