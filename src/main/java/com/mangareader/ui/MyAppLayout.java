package com.mangareader.ui;

import com.mangareader.backend.entity.Manga;
import com.mangareader.ui.component.extension.ButtonEx;
import com.mangareader.ui.component.extension.HorizontalLayoutEx;
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
  private Button nextChBtn;
  private Button prevChBtn;

  public MyAppLayout() {
    addToNavbar(getTitle());
  }

  private Button getTitle() {
    return new ButtonEx(MANGA_READER)
        .withStyle(MARGIN_LEFT, "10px")
        .withThemeVariant(ButtonVariant.LUMO_TERTIARY)
        .withClickListener(e -> UI.getCurrent().navigate(MainView.class));
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
      createNextChButton(entity, comboBox);
      createPrevChButton(entity, comboBox);
      setupButtonVisibility(entity);

      buttonWrapper = new HorizontalLayoutEx(prevChBtn, comboBox, nextChBtn)
          .withStyle(LEFT, "40%")
          .withStyle(POSITION, "absolute");
      addToNavbar(buttonWrapper);
    } else {
      remove(buttonWrapper);
    }
  }

  private void createNextChButton(Manga entity, ComboBox<String> comboBox) {
    if (nextChBtn == null) {
      nextChBtn = new ButtonEx("Next")
          .withVisibility(isNextButtonVisible(entity))
          .withThemeVariant(ButtonVariant.LUMO_TERTIARY)
          .withClickListener(e -> {
            onNextChButtonClick(entity, comboBox);
            setupButtonVisibility(entity);
          });
    }
  }

  private void onNextChButtonClick(Manga entity, ComboBox<String> comboBox) {
    entity.setCurrentChNum(
        entity.getCurrentChNum() + 1 > entity.getLatestChNum()
            ? entity.getLatestChNum()
            : entity.getCurrentChNum() + 1);
    comboBox.setValue(CHAPTER_WITH.formatted(entity.getCurrentChNum()));  // also navigates
  }

  private void createPrevChButton(Manga entity, ComboBox<String> comboBox) {
    if (prevChBtn == null) {
      prevChBtn = new ButtonEx("Previous")
          .withThemeVariant(ButtonVariant.LUMO_TERTIARY)
          .withVisibility(isPreviousButtonVisible(entity))
          .withClickListener(e -> {
            onPrevChButtonClick(entity, comboBox);
            setupButtonVisibility(entity);
          });
    }
  }

  private void onPrevChButtonClick(Manga entity, ComboBox<String> comboBox) {
    entity.setCurrentChNum(entity.getCurrentChNum() - 1);
    comboBox.setValue(CHAPTER_WITH.formatted(entity.getCurrentChNum()));  // also navigates
  }

  private boolean isPreviousButtonVisible(Manga entity) {
    return entity.getCurrentChNum() > 1;
  }

  private boolean isNextButtonVisible(Manga entity) {
    return entity.getCurrentChNum() + 1 <= entity.getLatestChNum();
  }

  private void setupButtonVisibility(Manga entity) {
    nextChBtn.setVisible(isNextButtonVisible(entity));
    prevChBtn.setVisible(isPreviousButtonVisible(entity));
  }

  private ComboBox<String> createComboBox(Manga entity) {
    ComboBox<String> cb = new ComboBox<>();
    List<String> items = new ArrayList<>();

    for (int i = entity.getLatestChNum(); i > 0; i--) {
      items.add(CHAPTER_WITH.formatted(i));
    }
    cb.setItems(items);
    cb.setPlaceholder(CHAPTER_WITH.formatted(entity.getCurrentChNum()));
    cb.addValueChangeListener(e -> {
      entity.setCurrentChNum(items.size() - items.indexOf(e.getValue()));
      setupButtonVisibility(entity);
      UI.getCurrent().navigate(ChapterView.class, new RouteParameters(CHAPTER_ID, entity.getCurrentChNum().toString()));
    });
    return cb;
  }
}