package com.mangareader.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class StringConstants {

  // http
  public static final String USER_AGENT = "Chrome";

  // chapters
  public static final String CURRENT = "Current";
  public static final String LATEST = "Latest";
  public static final String CHAPTER_WITH = "Chapter %d";
  public static final String CHAPTER_ID = "chapterId";

  // style
  public static final String MARGIN_TOP = "margin-top";
  public static final String MARGIN_BOTTOM = "margin-bottom";
  public static final String MARGIN_LEFT = "margin-left";
  public static final String ZERO = "0";
}
