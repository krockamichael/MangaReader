package com.mangareader.backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Manga Entity containing basic information.
 */
@Getter
@Setter
@JsonPropertyOrder({"name", "urlName", "scansName", "scansUrlName", "latestChNum", "currentChNum", "iconPath"})
public class MangaEntity {
  private String name;
  private String urlName;
  private String scansName;
  private String scansUrlName;
  private Integer latestChNum;
  private Integer currentChNum = 1;
  private String iconPath;
}
