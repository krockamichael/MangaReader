package com.mangareader.service.crawler;

import com.vaadin.flow.internal.Pair;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.scheduling.annotation.Async;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.mangareader.constants.StringConstants.TARGET_DOWNLOAD_DIR;
import static com.mangareader.constants.StringConstants.USER_AGENT;

@Log4j2
public abstract class AbstractCrawler {

  static final StopWatch STOP_WATCH = new StopWatch();
  protected final Set<String> filenames = new LinkedHashSet<>();
  private final ExecutorService executor = Executors.newFixedThreadPool(8); // newCachedThreadPool

  public static BufferedImage resize(BufferedImage img, int newW, int newH) {
    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2d = dimg.createGraphics();
    g2d.drawImage(tmp, 0, 0, null);
    g2d.dispose();

    return dimg;
  }

  void stopStopWatch() {
    STOP_WATCH.stop();
    float timeSpent = STOP_WATCH.getTime() / 1000.f;
    if (timeSpent != 0.f) {
      log.info(String.format("Downloading images took %.2f seconds.", timeSpent));
    }
    STOP_WATCH.reset();
  }

  @Async
  Future<BufferedImage> asyncLoadImageContent(String imageUrl) {
    return executor.submit(() -> getImage(imageUrl));
  }

  @Async
  Future<Boolean> asyncWriteImage(String fileName, BufferedImage image) {
    return executor.submit(() -> ImageIO.write(image, "png", new File(fileName)));
  }

  private BufferedImage getImage(String imageUrl) {
    BufferedImage image = null;
    try {
      URL url = new URL(imageUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("User-Agent", USER_AGENT);
      image = ImageIO.read(connection.getInputStream());
    } catch (IOException e) {
      log.error(e);
    }
    return image;
  }

  protected String toUrl(String baseUrl, String input) {
    return baseUrl.concat(input);
  }

  Pair<String, String> toPngFilename(String mangaName, String chapterNumber, String filename) {
    String fixedFilename = "";

    if (filename.contains("/")) {
      String[] splitFilename = filename.split("/");
      fixedFilename = splitFilename[splitFilename.length - 1];
    }
    String targetDir = mangaName
        .concat("\\")
        .concat(chapterNumber)
        .concat("\\");
    fixedFilename = targetDir.concat(fixedFilename.replace(".jpg", ".png"));
    filenames.add(fixedFilename);

    // TODO: refactor to call only once
    createDirIfNotExist(targetDir);

    return new Pair<>(fixedFilename, TARGET_DOWNLOAD_DIR.concat(fixedFilename));
  }

  Pair<String, String> toPngFilename(String mangaName, String filename) {
    String fixedFilename = "";

    if (filename.contains("/")) {
      String[] splitFilename = filename.split("/");
      fixedFilename = splitFilename[splitFilename.length - 1];
    }
    String targetDir = mangaName.concat("\\icon\\");
    fixedFilename = targetDir.concat(fixedFilename.replace(".jpg", ".png"));

    createDirIfNotExist(targetDir);

    return new Pair<>(fixedFilename, TARGET_DOWNLOAD_DIR.concat(fixedFilename));
  }

  private void createDirIfNotExist(String targetDir) {
    File mangaDir = new File(TARGET_DOWNLOAD_DIR.concat(targetDir));
    if (!mangaDir.exists()) {
      boolean result = mangaDir.mkdirs();
      if (!result) {
        log.error("The creation of {} directory failed.", targetDir);
      }
    }
  }

  void clearFileNames() {
    filenames.clear();
  }
}
