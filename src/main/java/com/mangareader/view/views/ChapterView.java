package com.mangareader.view.views;

import com.mangareader.entity.MangaEntity;
import com.mangareader.service.crawler.ReaperScansCrawler;
import com.mangareader.view.MyAppLayout;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.*;

import static com.mangareader.constants.StringConstants.MARGIN_TOP;

@PreserveOnRefresh
@Route(value = ":mangaId/:chapterId", layout = MyAppLayout.class)
public class ChapterView extends AbstractVerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

  private transient MangaEntity mangaEntity;
  private String mangaId;
  private Integer chapterId;

  public ChapterView() {
    super();
    getStyle().set(MARGIN_TOP, "16px");
  }

  private void setupImageComponents() {
    new ReaperScansCrawler()
        .parseChapter(mangaId, chapterId)
        .stream()
        .map(url -> new Image(url, ""))
        .forEach(this::add);
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    event.getRouteParameters()
        .getInteger("chapterId")
        .ifPresent(value -> chapterId = value);

    mangaEntity = ComponentUtil.getData(UI.getCurrent(), MangaEntity.class);
    mangaId = mangaEntity.getUrlName();
    setupImageComponents();
  }

  @Override
  public void beforeLeave(BeforeLeaveEvent event) {
    ComponentUtil.setData(UI.getCurrent(), MangaEntity.class, null);
  }
}
