package com.mangareader.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Manga Entity containing basic information.
 */
@Getter
@Setter
@JsonPropertyOrder({"name", "urlName", "scansName", "scansUrlName", "numOfChapters", "lastReadChapter", "iconPath"})
public class MangaEntity {

  private String name;
  private String urlName;
  private String scansName;
  private String scansUrlName;
  private Integer numOfChapters;
  private Integer lastReadChapter;
  private String iconPath;
}
