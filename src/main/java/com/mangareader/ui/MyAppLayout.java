package com.mangareader.ui;

import com.mangareader.backend.entity.Manga;
import com.mangareader.ui.component.extension.ButtonEx;
import com.mangareader.ui.view.ChapterView;
import com.mangareader.ui.view.MainView;
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

import static com.mangareader.backend.data.Constants.*;

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
    Manga entity = VaadinSession.getCurrent().getAttribute(Manga.class);
    createChapterNavigation(entity);
  }

  private void createChapterNavigation(Manga entity) {
    if (entity != null) {
      ComboBox<String> comboBox = createComboBox(entity);
      Button nextChBtn = createNextChButton(entity, comboBox);
      Button prevChBtn = createPrevChButton(entity, comboBox, nextChBtn);

      buttonWrapper = new HorizontalLayout(prevChBtn, comboBox, nextChBtn);
      buttonWrapper.getStyle().set(LEFT, "40%").set(POSITION, "absolute");
      addToNavbar(buttonWrapper);
    } else {
      remove(buttonWrapper);
    }
  }

  private Button createNextChButton(Manga entity, ComboBox<String> comboBox) {
    ButtonEx nextChBtn = new ButtonEx("Next")
        .withClickListener(e -> onNextChButtonClick(entity, comboBox))
        .withThemeVariant(ButtonVariant.LUMO_TERTIARY)
        .withVisibility(isNextButtonVisible(entity));

    nextChBtn.addClickListener(e -> nextChBtn.setVisible(isNextButtonVisible(entity)));
    return nextChBtn;
  }

  private void onNextChButtonClick(Manga entity, ComboBox<String> comboBox) {
    entity.setCurrentChNum(
        entity.getCurrentChNum() + 1 > entity.getLatestChNum()
            ? entity.getLatestChNum()
            : entity.getCurrentChNum() + 1);
    comboBox.setValue(CHAPTER_WITH.formatted(entity.getCurrentChNum()));
    UI.getCurrent().navigate(ChapterView.class,
        new RouteParameters(CHAPTER_ID, entity.getCurrentChNum().toString()));
  }

  private Button createPrevChButton(Manga entity, ComboBox<String> comboBox, Button nextChButton) {
    ButtonEx prevChBtn = new ButtonEx("Previous")
        .withClickListener(e -> onPrevChButtonClick(entity, comboBox))
        .withThemeVariant(ButtonVariant.LUMO_TERTIARY);

    prevChBtn.addClickListener(e -> nextChButton.setVisible(isNextButtonVisible(entity)));
    return prevChBtn;
  }

  private void onPrevChButtonClick(Manga entity, ComboBox<String> comboBox) {
    entity.setCurrentChNum(entity.getCurrentChNum() - 1);
    comboBox.setValue(CHAPTER_WITH.formatted(entity.getCurrentChNum()));
    UI.getCurrent().navigate(ChapterView.class,
        new RouteParameters(CHAPTER_ID, entity.getCurrentChNum().toString()));
  }

  private boolean isNextButtonVisible(Manga entity) {
    return entity.getCurrentChNum() + 1 <= entity.getLatestChNum();
  }

  private ComboBox<String> createComboBox(Manga entity) {
    ComboBox<String> cb = new ComboBox<>();
    List<String> items = new ArrayList<>();
    for (int i = entity.getLatestChNum(); i >= 0; i--) {
      items.add(CHAPTER_WITH.formatted(i));
    }
    cb.setItems(items);
    cb.setPlaceholder(CHAPTER_WITH.formatted(entity.getCurrentChNum()));
    cb.addValueChangeListener(e -> {
      entity.setCurrentChNum(items.size() - items.indexOf(e.getValue()) - 1);
      UI.getCurrent().navigate(ChapterView.class,
          new RouteParameters(CHAPTER_ID, entity.getCurrentChNum().toString()));
    });
    return cb;
  }
}