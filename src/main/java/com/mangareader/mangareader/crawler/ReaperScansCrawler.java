package com.mangareader.mangareader.crawler;

import com.mangareader.mangareader.entity.MangaEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ReaperScansCrawler extends AbstractCrawler {

    protected static final String BASE_URL = "https://reaperscans.com/comics/";

    public ReaperScansCrawler() {
        initMangaEntities();
        parseMangas();
    }

    private void initMangaEntities() {
        MangaEntity player = MangaEntity.builder()
                .name("Player Who Returned 10,000 Years Later")
                .urlName("2800-player-who-returned-10000-years-later")
                .build();
        mangaNames.add(player);
    }

    private void parseMangas() {
        try {
            for (MangaEntity mangaEntity : mangaNames) {
                Document document = Jsoup.connect(toUrl(BASE_URL, mangaEntity.getUrlName()))
                        .userAgent("Chrome")
                        .get();
                Integer numOfChapters = getNumberOfChapters(document);
                System.out.println(numOfChapters);
                System.out.println("hahaaasdasda");
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private Integer getNumberOfChapters(Document document) {
        if (document != null) {
            Element parsedElement = document.select("div > div > h1").get(1);
            if (parsedElement != null && !parsedElement.text().equals("")) {
                return Integer.valueOf(parsedElement.text().split(" ")[0]);
            }
        }
        return null;
    }
}
