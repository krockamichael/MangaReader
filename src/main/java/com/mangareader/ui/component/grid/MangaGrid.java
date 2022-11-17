package com.mangareader.ui.component.grid;

import com.mangareader.backend.entity.Manga;
import com.mangareader.backend.service.MangaService;
import com.mangareader.ui.component.extension.VerticalLayoutEx;
import com.mangareader.ui.view.ChapterView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static com.mangareader.backend.data.Constants.*;

public class MangaGrid extends AbstractGrid {

  private final MangaService mangaService;

  public MangaGrid(MangaService mangaService) {
    this.mangaService = mangaService;

    addIconColumn().setHeader("Icon").setWidth("110px").setFlexGrow(0);
    Grid.Column<Manga> mangaEntityColumn = addComponentColumn(this::getMainColumn)
        .setHeader("Name").setAutoWidth(true)
        .setComparator(c -> c.getLatestChNum() > c.getCurrentChNum());
    addComponentColumn(this::getActionsColumn).setHeader("Actions").setWidth("90px").setFlexGrow(0);

    setDetailsVisibleOnClick(false);
    setItemDetailsRenderer(createMangaDetailsRenderer());

    getDataCommunicator().enablePushUpdates(Executors.newCachedThreadPool());
    sort(List.of(new GridSortOrder<>(mangaEntityColumn, SortDirection.DESCENDING)));
    setAllRowsVisible(true);  // TODO: change in future
    setItems(mangaService.findAll());

    setClassName("main-grid");
  }

  private static ComponentRenderer<MangaDetailsFormLayout, Manga> createMangaDetailsRenderer() {
    return new ComponentRenderer<>(MangaDetailsFormLayout::new, MangaDetailsFormLayout::setManga);
  }

  private Component getMainColumn(Manga entity) {
    H3 name = new H3(entity.getName());
    name.getStyle().set(MARGIN_TOP, "5px").set(MARGIN_BOTTOM, ZERO);

    Component currentChapterLink = createChapterLink(entity, entity.getCurrentChNum(), CURRENT);
    Component latestChapterLink = createChapterLink(entity, entity.getLatestChNum(), LATEST);

    return new VerticalLayoutEx(name, currentChapterLink, latestChapterLink).withPadding(false);
  }

  private Component getActionsColumn(Manga entity) {
    Icon editIcon = new Icon(VaadinIcon.EDIT);
    editIcon.addClickListener(e -> setDetailsVisible(entity, !isDetailsVisible(entity)));

    Icon deleteIcon = new Icon(VaadinIcon.TRASH);
    deleteIcon.addClickListener(e ->
        new DeleteConfirmDialog("Confirmation", "Are you sure you want to delete this item?",
            () -> removeItem(entity)).open());

    Stream.of(editIcon, deleteIcon).forEach(icon -> {
      icon.setSize("20px");
      icon.getStyle().set(MARGIN, AUTO);
    });

    return new VerticalLayoutEx(editIcon, deleteIcon).withSizeFull();
  }

  private Component createChapterLink(Manga entity, Integer chapterNumber, String caption) {
    H4 chapterCaption = new H4(caption.concat(":"));
    chapterCaption.getStyle().set(MARGIN_TOP, ZERO);

    Button chapterLink = new Button(CHAPTER_WITH.formatted(chapterNumber), e -> {
      entity.setCurrentChNum(chapterNumber);
      VaadinSession.getCurrent().setAttribute(Manga.class, entity);
      UI.getCurrent().navigate(ChapterView.class,
          new RouteParameters(CHAPTER_ID, chapterNumber.toString()));
    });
    chapterLink.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    chapterLink.getStyle()
        .set(MARGIN_TOP, "-7px")
        .set(MARGIN_BOTTOM, ZERO)
        .set(MARGIN_LEFT, caption.equals(LATEST) ? "13px" : ZERO);

    return new HorizontalLayout(chapterCaption, chapterLink);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ListDataProvider<Manga> getDataProvider() {
    return (ListDataProvider<Manga>) super.getDataProvider();
  }

  /**
   * Add item to grid and data provider entity list.
   *
   * @param entity the entity to be added
   */
  public void addItem(Manga entity) {
    getDataProvider().getItems().add(entity);
    getDataProvider().refreshAll();
  }

  /**
   * Remove item from grid and data provider entity list.
   *
   * @param entity the entity to be removed
   */
  public void removeItem(Manga entity) {
    mangaService.delete(entity);
    getDataProvider().getItems().remove(entity);
    getDataProvider().refreshAll();
  }

  private static class DeleteConfirmDialog extends ConfirmDialog {
    DeleteConfirmDialog(String header, String text, Runnable runnable) {
      setHeader(header);
      setText(text);
      addConfirmListener(e -> runnable.run());
      setCancelable(true);
    }
  }

  private static class MangaDetailsFormLayout extends FormLayout {
    private final Binder<Manga> binder = new Binder<>();
    private final TextField nameField = new TextField("Name");
    private final TextField urlField = new TextField("URL");
    private final TextField scansNameField = new TextField("Scans name");
    private final IntegerField latestChField = new IntegerField("Latest chapter");
    private final IntegerField currentChField = new IntegerField("Current chapter");

    MangaDetailsFormLayout() {
      Stream.of(nameField, urlField, scansNameField, latestChField, currentChField)
          .forEach(this::add);

      binder.bind(nameField, Manga::getName, Manga::setName);
      binder.bind(urlField, Manga::getUrlName, Manga::setUrlName);
      binder.bind(scansNameField, Manga::getScansName, Manga::setScansName);
      binder.bind(latestChField, Manga::getLatestChNum, Manga::setLatestChNum);
      binder.bind(currentChField, Manga::getCurrentChNum, Manga::setCurrentChNum);

      setResponsiveSteps(new ResponsiveStep("0", 3));
      setColspan(nameField, 3);
      setColspan(urlField, 3);
    }

    void setManga(Manga manga) {
      nameField.setValue(manga.getName());
      urlField.setValue(manga.getUrlName());
      scansNameField.setValue(manga.getScansName());
      latestChField.setValue(manga.getLatestChNum());
      currentChField.setValue(manga.getCurrentChNum());
      binder.setBean(manga);
    }
  }
}
