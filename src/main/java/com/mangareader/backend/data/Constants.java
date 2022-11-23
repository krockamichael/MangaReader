package com.mangareader.backend.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class Constants {

  // title
  public static final String MANGA_READER = "MangaReader";

  // downloads
  public static final String ABS_TARGET_DOWNLOAD_DIR = Paths.get("target/classes/images/").toAbsolutePath().toString().concat("\\");

  // http
  public static final String USER_AGENT = "Chrome";

  // chapters
  public static final String CURRENT = "Current";
  public static final String LATEST = "Latest";
  public static final String CHAPTER_WITH = "Chapter %d";
  public static final String CHAPTER_ID = "chapterId";

  // style
  public static final String POSITION = "position";
  public static final String TOP = "top";
  public static final String BOTTOM = "bottom";
  public static final String RIGHT = "right";
  public static final String LEFT = "left";
  public static final String MARGIN = "margin";
  public static final String MARGIN_TOP = "margin-top";
  public static final String MARGIN_BOTTOM = "margin-bottom";
  public static final String MARGIN_LEFT = "margin-left";
  public static final String MARGIN_RIGHT = "margin-right";
  public static final String PADDING = "padding";
  public static final String HEIGHT = "height";
  public static final String WIDTH = "width";
  public static final String AUTO = "auto";
  public static final String ZERO = "0";
  public static final String SET_PROPERTY_IN_OVERLAY_JS = "this.$.overlay.$.overlay.style[$0]=$1";

  // image sizes
  public static final int INT_GRID_IMG_HEIGHT = 150;
  public static final int INT_GRID_IMG_WIDTH = 100;
  public static final String GRID_IMG_HEIGHT = INT_GRID_IMG_HEIGHT + "px";
  public static final String GRID_IMG_WIDTH = INT_GRID_IMG_WIDTH + "px";
  public static final String SEARCH_IMG_HEIGHT = "100px";
  public static final String SEARCH_IMG_WIDTH = "70px";
  public static final String SEARCH_HL_HEIGHT = "90px";
}
