package com.mangareader.ui.component.grid;

import com.mangareader.backend.data.MangaDataProvider;

public class NewMangaSidebar extends AbstractGrid {

  public NewMangaSidebar() {
    addIconColumn();
    setItems(new MangaDataProvider().getMangaEntities());
//    getStyle().set(MARGIN, ZERO);
//    getStyle().set(MARGIN_TOP, AUTO).set(MARGIN_BOTTOM, AUTO);
    setAllRowsVisible(true);
    setClassName("sidebar-grid");
  }
}
