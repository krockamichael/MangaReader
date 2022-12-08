package com.mangareader.ui.component.custom;

import com.mangareader.backend.service.MangaService;
import com.mangareader.backend.service.crawler.ReaperScansCrawler;
import com.mangareader.ui.component.extension.*;
import com.mangareader.ui.component.grid.MangaGrid;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.ArrayList;

import static com.mangareader.backend.data.Constants.*;

public class MainGridComponent extends DialogEx {

  private final transient ReaperScansCrawler rsCrawler = new ReaperScansCrawler();
  private final transient MangaService mangaService;
  private final MangaGrid grid;

  public MainGridComponent(MangaService mangaService) {
    this.grid = new MangaGrid(mangaService);
    this.mangaService = mangaService;
    setupContent();
  }

  @Override
  protected void setupContent() {
    super.setupContent();
    add(new VerticalLayoutEx(grid)
        .withHeightFull()
        .withSelfFlexGrow(.9d)
        .withWidth(740, Unit.PIXELS));
  }

  @Override
  protected void setupHeader() {
    setHeaderTitle(" ");
    TextFieldEx search = createSearch();
    IconEx closeIcon = getCloseIcon().withStyle(MARGIN_LEFT, "-10px");
    getHeader().add(new DialogHeaderBar(true, createUpdateButton(), createAddButton(), createSaveButton(), search, closeIcon)
        .withFlexGrow(1d, search));
  }

  private ButtonEx createUpdateButton() {
    return new ButtonEx("Update")
        .withClickListener(e -> updateLatestChapters());
  }

  /** Updates the latest chapter number for each manga in MainGridComponent. */
  public void updateLatestChapters() {
    grid.getDataProvider()
        .getItems()
        .forEach(entity -> new Thread(() -> rsCrawler.fetchLatestChapterNumber(entity)
            .addCallback(
                result -> {
                  entity.setLatestChNum(result);
                  UI.getCurrent().access(() -> {
                    grid.getDataProvider().refreshItem(entity);
                    NotificationEx.success(entity.getName() + " updated to " + result);
                  });
                },
                err -> UI.getCurrent().access(() -> NotificationEx.error("Failed to parse latest chapter for " + entity.getName()))
            )
        ).start());
  }

  private ButtonEx createAddButton() {
    return new ButtonEx("Add")
        .withClickListener(e -> {
          AddMangaDialog dialog = new AddMangaDialog(mangaService);
          dialog.addOpenedChangeListener(e2 -> {
            if (!e2.isOpened()) {
              grid.getDataProvider().refreshAll();
            }
          });
          dialog.open();
        });
  }

  private ButtonEx createSaveButton() {
    return new ButtonEx("Save").withClickListener(e -> saveItems());
  }

  private TextFieldEx createSearch() {
    return new TextFieldEx()
        .withWidth("50%")
        .withStyle(MARGIN, AUTO)
        .withPlaceholder("Search")
        .withValueChangeMode(ValueChangeMode.LAZY)
        .withPrefixComponent(new Icon(VaadinIcon.SEARCH))
        .withValueChangeListener(e -> updateGrid(e.getValue()));
  }

  private void saveItems() {
    mangaService.saveAll(new ArrayList<>(grid.getDataProvider().getItems()));
    NotificationEx.success("Saved!");
  }

  private void updateGrid(String searchTerm) {
    grid.setItems(mangaService.findAll(searchTerm));
  }

  @Override
  protected void setOverlayProperties() {
    super.setOverlayProperties();
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, TOP, "5%");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, MARGIN, AUTO);
  }

  @Override
  public String getWidgetCaption() {
    return "Manga Grid";
  }

  @Override
  public MainGridComponent getComponent() {
    return this;
  }
}
