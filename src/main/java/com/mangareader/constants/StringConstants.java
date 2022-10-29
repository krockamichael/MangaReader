package com.mangareader.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class StringConstants {

  // downloads
  public static final String TARGET_DOWNLOAD_DIR = Paths.get("target/classes/images/").toAbsolutePath().toString().concat("\\");

  // http
  public static final String USER_AGENT = "Chrome";

  // style
  public static final String MARGIN_TOP = "margin-top";
  public static final String MARGIN_BOTTOM = "margin-bottom";
  public static final String ZERO = "0";
}
