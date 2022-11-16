package com.mangareader.ui.view;

import com.mangareader.backend.entity.Manga;
import com.mangareader.backend.service.crawler.ReaperScansCrawler;
import com.mangareader.ui.MyAppLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import static com.mangareader.backend.data.Constants.CHAPTER_ID;
import static com.mangareader.backend.data.Constants.MARGIN_TOP;

@Route(value = "manga/:chapterId", layout = MyAppLayout.class)
public class ChapterView extends AbstractVerticalLayout implements BeforeEnterObserver {

  private transient Manga manga;
  private Integer chapterId;

  public ChapterView() {
    super();
    getStyle().set(MARGIN_TOP, "16px");
  }

  private void setupImageComponents() {
    removeAll();
    new ReaperScansCrawler()
        .parseChapter(manga, chapterId)
        .stream()
        .map(url -> new Image(url, ""))
        .forEach(this::add);
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    event.getRouteParameters()
        .getInteger(CHAPTER_ID)
        .ifPresent(value -> chapterId = value);

    manga = VaadinSession.getCurrent().getAttribute(Manga.class);
    setupImageComponents();
  }
}
