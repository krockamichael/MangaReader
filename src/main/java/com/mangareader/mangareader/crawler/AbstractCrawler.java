package com.mangareader.mangareader.crawler;

import com.mangareader.mangareader.entity.MangaEntity;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public abstract class AbstractCrawler {

    protected static final Logger LOGGER = LogManager.getLogger(AbstractCrawler.class.getName());
    protected final Set<MangaEntity> mangaNames = new HashSet<>();

    protected String toUrl(String baseUrl, String input) {
        return Strings.concat(baseUrl, input);
    }
}
