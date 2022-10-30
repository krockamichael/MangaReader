package com.mangareader.view;

import com.mangareader.entity.MangaEntity;
import com.mangareader.view.views.MainView;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;

@PageTitle("MangaReader")
public class MyAppLayout extends AppLayout {

  private H1 mangaTitle = new H1("");

  public MyAppLayout() {
    getStyle().set("background-color", "#00000030");
    addToNavbar(getTitle());
  }

  @Override
  protected void afterNavigation() {
    super.afterNavigation();
    createMangaTitle();
  }

  private Button getTitle() {
    Button title = new Button("MangaReader");
    title.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    title.addClickListener(e -> UI.getCurrent().navigate(MainView.class));

    return title;
  }

  private void createMangaTitle() {
    MangaEntity entity = ComponentUtil.getData(UI.getCurrent(), MangaEntity.class);

    if (entity != null) {
      mangaTitle = new H1(entity.getName() + " " + entity.getNumOfChapters());
      setStyleNames(mangaTitle.getStyle(), "160px");
      addToNavbar(mangaTitle);
    } else {
      remove(mangaTitle);
      mangaTitle = null;
    }
  }

  private void setStyleNames(Style style, String leftMargin) {
    style.set("font-size", "var(--lumo-font-size-l)")
        .set("left", "var(--lumo-space-l)")
        .set("margin-right", "0")
        .set("margin-top", "8px")
        .set("margin-bottom", "0")
        .set("margin-left", leftMargin)
        .set("position", "absolute");
  }
}