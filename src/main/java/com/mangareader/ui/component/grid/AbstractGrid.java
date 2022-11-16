package com.mangareader.ui.component.grid;

import com.mangareader.backend.entity.Manga;
import com.mangareader.backend.service.crawler.ReaperScansCrawler;
import com.mangareader.ui.component.extension.ImageEx;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.StreamResource;

import static com.mangareader.backend.data.Constants.GRID_IMG_HEIGHT;
import static com.mangareader.backend.data.Constants.GRID_IMG_WIDTH;
import static com.mangareader.backend.data.service.Utils.fileExists;

class AbstractGrid extends Grid<Manga> {

  Column<Manga> addIconColumn() {
    return addComponentColumn(this::getIcon).setClassNameGenerator(i -> "icon-img");
  }

  private ImageEx getIcon(Manga entity) {
    ImageEx image = new ImageEx().withHeightAndWidth(GRID_IMG_HEIGHT, GRID_IMG_WIDTH);

    // icon is saved locally
    if (entity.getIconPath() != null && fileExists(entity.getIconPath())) {
      image.setSrc(new StreamResource("icon.png",
          () -> getClass().getResourceAsStream("/images/" + entity.getIconPath())));
      return image;
    }

    // icon is not downloaded, download and set to entity
    UI ui = UI.getCurrent();
    new Thread(() -> new ReaperScansCrawler().asyncLoadIconTimed(entity)
        .addCallback(
            result -> ui.access(() -> image.setSrc(result)),
            err -> ui.access(() -> Notification.show("Failed to parse icon for " + entity.getName()))
        )).start();

    return image;
  }
}
