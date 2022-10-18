package com.mangareader.crawler;

import com.mangareader.entity.MangaEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ReaperScansCrawler extends AbstractCrawler {

    protected static final String BASE_URL = "https://reaperscans.com/comics/";

    public ReaperScansCrawler() {
        initMangaEntities();
    }

    private void initMangaEntities() {
        MangaEntity player = MangaEntity.builder()
                .name("Player Who Returned 10,000 Years Later")
                .urlName("2800-player-who-returned-10000-years-later")
                .build();
        mangaNames.add(player);
        player = MangaEntity.builder()
            .name("Duke Pendragon")
            .urlName("2633-duke-pendragon")
            .build();
        mangaNames.add(player);
    }

    public List<String> parseMangas() {
        List<String> images = new ArrayList<>();
        try {
            for (MangaEntity mangaEntity : mangaNames) {
                Document document = Jsoup.connect(toUrl(BASE_URL, mangaEntity.getUrlName()))
                        .userAgent("Chrome")
                        .get();

                Integer numOfChapters = getNumberOfChapters(document);

                String latestChapterLink = parseLinkText(document, numOfChapters);
                Document latestChapter = Jsoup.connect(latestChapterLink)
                        .userAgent("Chrome")
                        .get();

                images.addAll(parseImages(latestChapter));
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return images;
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

    private String parseLinkText(Document document, Integer chapterNumber) {
        return document.select("li > a[href]")
            .stream()
            .findFirst()
            .map(e -> e.attr("href"))
            .filter(e -> e.contains(chapterNumber.toString()))
            .orElse(null);
    }

    private List<String> parseImages(Document document) {
        return document.select("main > div > p > img[src]")
            .stream()
            .map(e -> e.attr("src"))
            .toList();
    }
}
