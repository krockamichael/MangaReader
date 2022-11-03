package com.mangareader.view.views;

import com.mangareader.entity.MangaEntity;
import com.mangareader.service.crawler.ReaperScansCrawler;
import com.mangareader.view.MyAppLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import static com.mangareader.constants.StringConstants.CHAPTER_ID;
import static com.mangareader.constants.StringConstants.MARGIN_TOP;

@Route(value = "manga/:chapterId", layout = MyAppLayout.class)
public class ChapterView extends AbstractVerticalLayout implements BeforeEnterObserver {

  private transient MangaEntity mangaEntity;
  private Integer chapterId;

  public ChapterView() {
    super();
    getStyle().set(MARGIN_TOP, "16px");
  }

  private void setupImageComponents() {
    removeAll();
    new ReaperScansCrawler()
        .parseChapter(mangaEntity, chapterId)
        .stream()
        .map(url -> new Image(url, ""))
        .forEach(this::add);
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    event.getRouteParameters()
        .getInteger(CHAPTER_ID)
        .ifPresent(value -> chapterId = value);

    mangaEntity = VaadinSession.getCurrent().getAttribute(MangaEntity.class);
    setupImageComponents();
  }
}
