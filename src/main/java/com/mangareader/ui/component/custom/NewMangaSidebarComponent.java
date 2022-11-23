package com.mangareader.ui.component.custom;

import com.mangareader.backend.service.MangaService;
import com.mangareader.ui.component.extension.DialogEx;
import com.mangareader.ui.component.extension.VerticalLayoutEx;
import com.mangareader.ui.component.grid.NewMangaGrid;
import com.vaadin.flow.component.Unit;

import static com.mangareader.backend.data.Constants.*;

public class NewMangaSidebarComponent extends DialogEx {

  private final NewMangaGrid grid;

  public NewMangaSidebarComponent(MangaService mangaService) {
    grid = new NewMangaGrid(mangaService.findAll());
    setupContent();
  }

  @Override
  protected void setupContent() {
    super.setupContent();
    add(new VerticalLayoutEx(grid)
        .withSpacing(false)
        .withWidth(69f, Unit.PERCENTAGE));
  }

  @Override
  protected void setupHeader() {
    setHeaderTitle("New");
    getHeader().add(new DialogHeaderBar(false, getCloseIcon()));
  }

  @Override
  protected void setOverlayProperties() {
    super.setOverlayProperties();
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, WIDTH, "130px");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, RIGHT, "75%");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, TOP, "13.1%");
  }

  @Override
  public String getWidgetCaption() {
    return "New Manga Sidebar";
  }

  @Override
  public NewMangaSidebarComponent getComponent() {
    return this;
  }
}
