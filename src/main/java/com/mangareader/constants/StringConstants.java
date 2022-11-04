package com.mangareader.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class StringConstants {

  // title
  public static final String MANGA_READER = "MangaReader";

  // downloads
  public static final String ABS_TARGET_DOWNLOAD_DIR = Paths.get("target/classes/images/").toAbsolutePath().toString().concat("\\");
  public static final String REL_TARGET_DOWNLOAD_DIR = Paths.get("target/classes/images/").toString().concat("\\");

  // http
  public static final String USER_AGENT = "Chrome";

  // chapters
  public static final String CURRENT = "Current";
  public static final String LATEST = "Latest";
  public static final String CHAPTER_WITH = "Chapter %d";
  public static final String CHAPTER_ID = "chapterId";

  // style
  public static final String MARGIN = "margin";
  public static final String MARGIN_TOP = "margin-top";
  public static final String MARGIN_BOTTOM = "margin-bottom";
  public static final String MARGIN_LEFT = "margin-left";
  public static final String AUTO = "auto";
  public static final String ZERO = "0";
}
