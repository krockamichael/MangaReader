package com.mangareader.backend.data.service;

import lombok.extern.log4j.Log4j2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.mangareader.backend.data.Constants.ABS_TARGET_DOWNLOAD_DIR;

@Log4j2
public class Utils {

  private Utils() {
  }

  public static boolean fileExists(String iconPath) {
    return new File(ABS_TARGET_DOWNLOAD_DIR.concat(iconPath)).isFile();
  }

  public static String getDownloadPath(String mangaName) {
    String path = ABS_TARGET_DOWNLOAD_DIR.concat(mangaName);
    if (!new File(path).mkdirs())
      log.warn("Directory %s already exists.".formatted(path));
    return path.concat("\\icon.png");
  }

  public static String getRelativePath(String path) {
    return path.replace(ABS_TARGET_DOWNLOAD_DIR, "");
  }

  public static BufferedImage resize(BufferedImage img, int newH, int newW) {
    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    BufferedImage dImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2d = dImg.createGraphics();
    g2d.drawImage(tmp, 0, 0, null);
    g2d.dispose();

    return dImg;
  }
}
