package com.mangareader.view;

import com.mangareader.components.ButtonEx;
import com.mangareader.entity.MangaEntity;
import com.mangareader.view.views.ChapterView;
import com.mangareader.view.views.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;

import static com.mangareader.constants.StringConstants.*;

public class MyAppLayout extends AppLayout {

  private HorizontalLayout buttonWrapper = new HorizontalLayout();

  public MyAppLayout() {
    addToNavbar(getTitle());
  }

  private Button getTitle() {
    Button title = new Button(MANGA_READER);
    title.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    title.getStyle().set(MARGIN_LEFT, "10px");
    title.addClickListener(e -> UI.getCurrent().navigate(MainView.class));

    return title;
  }

  @Override
  protected void afterNavigation() {
    super.afterNavigation();
    setupChapterNavigation();
  }

  private void setupChapterNavigation() {
    MangaEntity entity = VaadinSession.getCurrent().getAttribute(MangaEntity.class);
    createChapterNavigation(entity);
  }

  private void createChapterNavigation(MangaEntity entity) {
    if (entity != null) {
      ComboBox<String> comboBox = createComboBox(entity);
      Button nextChBtn = createNextChButton(entity, comboBox);
      Button prevChBtn = createPrevChButton(entity, comboBox, nextChBtn);

      buttonWrapper = new HorizontalLayout(prevChBtn, comboBox, nextChBtn);
      buttonWrapper.getStyle().set("left", "40%").set("position", "absolute");
      addToNavbar(buttonWrapper);
    } else {
      remove(buttonWrapper);
    }
  }

  private Button createNextChButton(MangaEntity entity, ComboBox<String> comboBox) {
    ButtonEx nextChBtn = new ButtonEx("Next")
        .withClickListener(e -> onNextChButtonClick(entity, comboBox))
        .withThemeVariant(ButtonVariant.LUMO_TERTIARY)
        .withVisibility(isNextButtonVisible(entity));

    nextChBtn.addClickListener(e -> nextChBtn.setVisible(isNextButtonVisible(entity)));
    return nextChBtn;
  }

  private void onNextChButtonClick(MangaEntity entity, ComboBox<String> comboBox) {
    entity.setCurrentChapterNumber(
        entity.getCurrentChapterNumber() + 1 > entity.getLatestChapterNumber()
            ? entity.getLatestChapterNumber()
            : entity.getCurrentChapterNumber() + 1);
    comboBox.setValue(CHAPTER_WITH.formatted(entity.getCurrentChapterNumber()));
    UI.getCurrent().navigate(ChapterView.class,
        new RouteParameters(CHAPTER_ID, entity.getCurrentChapterNumber().toString()));
  }

  private Button createPrevChButton(MangaEntity entity, ComboBox<String> comboBox, Button nextChButton) {
    ButtonEx prevChBtn = new ButtonEx("Previous")
        .withClickListener(e -> onPrevChButtonClick(entity, comboBox))
        .withThemeVariant(ButtonVariant.LUMO_TERTIARY);

    prevChBtn.addClickListener(e -> nextChButton.setVisible(isNextButtonVisible(entity)));
    return prevChBtn;
  }

  private void onPrevChButtonClick(MangaEntity entity, ComboBox<String> comboBox) {
    entity.setCurrentChapterNumber(entity.getCurrentChapterNumber() - 1);
    comboBox.setValue(CHAPTER_WITH.formatted(entity.getCurrentChapterNumber()));
    UI.getCurrent().navigate(ChapterView.class,
        new RouteParameters(CHAPTER_ID, entity.getCurrentChapterNumber().toString()));
  }

  private boolean isNextButtonVisible(MangaEntity entity) {
    return entity.getCurrentChapterNumber() + 1 <= entity.getLatestChapterNumber();
  }

  private ComboBox<String> createComboBox(MangaEntity entity) {
    ComboBox<String> cb = new ComboBox<>();
    List<String> items = new ArrayList<>();
    for (int i = entity.getLatestChapterNumber(); i >= 0; i--) {
      items.add(CHAPTER_WITH.formatted(i));
    }
    cb.setItems(items);
    cb.setPlaceholder(CHAPTER_WITH.formatted(entity.getCurrentChapterNumber()));
    cb.addValueChangeListener(e -> {
      entity.setCurrentChapterNumber(items.size() - items.indexOf(e.getValue()) - 1);
      UI.getCurrent().navigate(ChapterView.class,
          new RouteParameters(CHAPTER_ID, entity.getCurrentChapterNumber().toString()));
    });
    return cb;
  }
}