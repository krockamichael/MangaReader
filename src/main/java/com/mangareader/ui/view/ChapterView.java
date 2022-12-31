package com.mangareader.ui.view;

import com.mangareader.backend.entity.Manga;
import com.mangareader.backend.service.MangaService;
import com.mangareader.backend.service.crawler.ReaperScansCrawler;
import com.mangareader.ui.MyAppLayout;
import com.mangareader.ui.component.extension.ImageEx;
import com.mangareader.ui.component.extension.NotificationEx;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import static com.mangareader.backend.data.Constants.CHAPTER_ID;
import static com.mangareader.backend.data.Constants.MARGIN_TOP;

@Route(value = "manga/:chapterId", layout = MyAppLayout.class)
public class ChapterView extends AbstractVerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

  private final transient MangaService mangaService;
  private transient Manga manga;
  private Integer chapterId;

  public ChapterView(MangaService mangaService) {
    super();
    this.mangaService = mangaService;
    getStyle().set(MARGIN_TOP, "16px");
  }

  private void setupImageComponents() {
    removeAll();
    new ReaperScansCrawler()
        .parseChapter(manga, chapterId)
        .stream()
        .map(ImageEx::new)
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

  @Override
  public void beforeLeave(BeforeLeaveEvent event) {
    NotificationEx.success(manga.getName() + " saved.");
    mangaService.save(manga);
  }
}
