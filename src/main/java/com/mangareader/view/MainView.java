package com.mangareader.view;

import com.mangareader.entity.MangaEntity;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Home")
@Route(value = "")
public class MainView extends VerticalLayout {

  //TODO: init manga collection
  //  create entity links
  //  in ChapterView select entity from session based on :mangaId?

  public MainView() {
    add(new Navigation());

    // FIXME: could be collection wrapper for multiple entities
    // FIXME: manga entities initializer in specific class, read from CSV?
    //  Use DB (for 2 strings and an integer)?
    VaadinSession.getCurrent().setAttribute(MangaEntity.class, MangaEntity.builder().name("Player Who Returned 10,000 Years Later").urlName("2800-player-who-returned-10000-years-later").build());
    routerLink();

    setMargin(true);
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);
  }

  private void routerLink() {
    NativeButton linkButton = new NativeButton("some manga");

    linkButton.addClickListener(e ->
        linkButton.getUI().ifPresent(ui -> ui.navigate(
            ChapterView.class,
            new RouteParameters("mangaId", "2633-duke-pendragon"))));
    add(linkButton);
  }
}