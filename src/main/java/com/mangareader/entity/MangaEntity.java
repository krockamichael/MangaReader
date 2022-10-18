package com.mangareader.entity;

import lombok.Builder;
import lombok.Getter;

/**
 * Manga Entity containing basic information.
 */
@Getter
@Builder
public class MangaEntity {

    private String name;
    private String urlName;
    private String scansName;
    private String scansUrlName;
    private Integer numOfChapters;
    private Integer lastReadChapter;
}
