package com.mangareader.view;

import com.mangareader.crawler.ReaperScansCrawler;
import com.mangareader.entity.MangaEntity;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "manga/:mangaId?")
public class ChapterView extends VerticalLayout implements BeforeEnterObserver {

  private String mangaId;
  private MangaEntity entity;

  public ChapterView() {
    add(new Navigation());

    entity = MangaEntity.builder()
        .name("Player Who Returned 10,000 Years Later")
        .urlName("2800-player-who-returned-10000-years-later")
        .build();
    entity = MangaEntity.builder()
        .name("Duke Pendragon")
        .urlName("2633-duke-pendragon")
        .build();
    add(createImageComponent());
//    setupImageContent();

    setAlignItems(Alignment.CENTER);
  }

  private void setupImageContent() {
    List<Image> imageComponents = createImageComponents();
    imageComponents.forEach(this::add);
  }

  private Image createImageComponent() {
    ReaperScansCrawler rsCrawler = new ReaperScansCrawler();

    return rsCrawler.parseChapter(entity)
        .stream()
        .map(url -> new Image(url, ""))
        .findFirst()
        .orElse(null);
  }

  private List<Image> createImageComponents() {
    ReaperScansCrawler rsCrawler = new ReaperScansCrawler();

    return rsCrawler.parseChapter(entity)
        .stream()
        .map(url -> new Image(url, ""))
        .toList();
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    mangaId = event.getRouteParameters()
        .get("mangaId")
        .orElse(null);
  }
}
