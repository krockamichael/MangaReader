package com.mangareader.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Manga Entity containing basic information.
 */
@Getter
@Setter
@JsonPropertyOrder({"name", "urlName", "scansName", "scansUrlName", "latestChapterNumber", "currentChapterNumber", "iconPath"})
public class MangaEntity {

  private String name;
  private String urlName;
  private String scansName;
  private String scansUrlName;
  private Integer latestChapterNumber;
  private Integer currentChapterNumber;
  private String iconPath;
}
