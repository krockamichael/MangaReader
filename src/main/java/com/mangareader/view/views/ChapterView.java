package com.mangareader.view.views;

import com.mangareader.crawler.ReaperScansCrawler;
import com.mangareader.entity.MangaEntity;
import com.mangareader.view.MyAppLayout;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

@PreserveOnRefresh
@Route(value = "manga", layout = MyAppLayout.class)
public class ChapterView extends AbstractVerticalLayout {

  private final transient MangaEntity mangaEntity;

  public ChapterView() {
    super();
    mangaEntity = ComponentUtil.getData(UI.getCurrent(), MangaEntity.class);
    if (mangaEntity != null) {
      setupImageComponents();
    }
  }

  private void setupImageComponents() {
    ReaperScansCrawler rsCrawler = new ReaperScansCrawler();
    rsCrawler.parseChapter(mangaEntity)
        .stream()
        .map(url -> new Image(new StreamResource(getImageName(url),
            () -> getClass().getResourceAsStream("/images/" + url)), ""))
        .forEach(this::add);
  }
}
