package com.mangareader.ui.component.grid;

import com.mangareader.backend.entity.Manga;

import java.util.List;

public class NewMangaGrid extends AbstractGrid {

  public NewMangaGrid(List<Manga> mangaList) {
    addIconColumn();
    setItems(mangaList);
    setAllRowsVisible(true);
    setClassName("sidebar-grid");
  }
}
