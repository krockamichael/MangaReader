package com.mangareader.view;

import com.mangareader.entity.MangaEntity;
import com.mangareader.view.views.MainView;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.RouterLink;

@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover")
@BodySize
public class MyAppLayout extends AppLayout implements AppShellConfigurator {

  private H1 mangaTitle = new H1("");

  public MyAppLayout() {
    getStyle().set("background-color", "#00000030");
    addToNavbar(getTitle(), getTabs());
  }

  @Override
  protected void afterNavigation() {
    super.afterNavigation();
    createMangaTitle();
  }

  private H1 getTitle() {
    H1 title = new H1("MangaReader");
    title.addClickListener(event -> {
      UI ui = UI.getCurrent();
      ComponentUtil.setData(ui, MangaEntity.class, null);
      ui.navigate(MainView.class);
    });
    setStyleNames(title.getStyle(), "0");

    return title;
  }

  private void createMangaTitle() {
    MangaEntity entity = ComponentUtil.getData(UI.getCurrent(), MangaEntity.class);

    if (entity != null) {
      mangaTitle.setTitle(entity.getName() + " " + entity.getNumOfChapters());
      setStyleNames(mangaTitle.getStyle(), "160px");
      addToNavbar(mangaTitle);
    } else {
      mangaTitle.setTitle("");
//      remove(mangaTitle);
//      mangaTitle = null;
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

  private Tabs getTabs() {
    Tabs tabs = new Tabs();
    tabs.getStyle().set("margin", "auto");
    tabs.add(createTab("Home"), createTab("Bookmarks"));

    return tabs;
  }

  private Tab createTab(String viewName) {
    Tab tab = new Tab(new RouterLink(viewName, MainView.class));
    tab.getElement().addEventListener("click",
        e -> ComponentUtil.setData(UI.getCurrent(), MangaEntity.class, null));

    return tab;
  }
}