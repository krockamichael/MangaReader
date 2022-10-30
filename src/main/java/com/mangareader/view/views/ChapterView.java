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
@Route(value = ":mangaID/:chapterID", layout = MyAppLayout.class)
public class ChapterView extends AbstractVerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

  //  private final transient MangaEntity mangaEntity;
  private String mangaID;
  private Integer chapterID;

  public ChapterView() {
    super();
    getStyle().set(MARGIN_TOP, "16px");
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    event.getRouteParameters()
        .get("mangaID")
        .ifPresent(value -> mangaID = value);
    event.getRouteParameters()
        .getInteger("chapterID")
        .ifPresent(value -> chapterID = value);

    if (mangaID != null) {
      setupImageComponents();
      setupNavigationButtons();
    }
  }

  private void setupImageComponents() {
    new ReaperScansCrawler()
        .parseChapter(mangaID, chapterID)
        .stream()
        .map(url -> new Image(url, ""))
        .forEach(this::add);
  }

  private void setupNavigationButtons() {
    // I would need number of chapter from entity to NOT show next chapter if it does not exist
    // How do I even add items to navigation here?
  }

  @Override
  public void beforeLeave(BeforeLeaveEvent event) {
    ComponentUtil.setData(UI.getCurrent(), MangaEntity.class, null);
  }
}
