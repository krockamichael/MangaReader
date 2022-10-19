package com.mangareader.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

public class Navigation extends AppLayout {

  public Navigation() {
    H1 title = new H1("MangaReader");
    title.getStyle().set("font-size", "var(--lumo-font-size-l)")
        .set("left", "var(--lumo-space-l)").set("margin", "0")
        .set("position", "absolute");

    addToNavbar(title, getTabs());
  }

  private Tabs getTabs() {
    Tabs tabs = new Tabs();
    tabs.getStyle().set("margin", "auto");
    tabs.add(createTab("Home"), createTab("Bookmarks"));
    return tabs;
  }

  private Tab createTab(String viewName) {
    RouterLink link = new RouterLink();
    link.add(viewName);

    link.setRoute(MainView.class);
    link.setTabIndex(-1);

    return new Tab(link);
  }

}