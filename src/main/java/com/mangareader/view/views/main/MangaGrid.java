package com.mangareader.view.views.main;

import com.mangareader.data.MangaDataProvider;
import com.mangareader.entity.MangaEntity;
import com.mangareader.service.crawler.ReaperScansCrawler;
import com.mangareader.view.views.ChapterView;
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
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static com.mangareader.constants.StringConstants.*;
import static com.mangareader.service.Utils.fileExists;

class MangaGrid extends Grid<MangaEntity> {

  private static final MangaDataProvider dataProvider = new MangaDataProvider();

  MangaGrid() {
    addComponentColumn(this::getIconColumn).setHeader("Icon").setWidth("110px").setFlexGrow(0);
    Grid.Column<MangaEntity> mangaEntityColumn = addComponentColumn(this::getMainColumn)
        .setHeader("Name").setAutoWidth(true)
        .setComparator(c -> c.getLatestChNum() > c.getCurrentChNum());
    addComponentColumn(this::getActionsColumn).setHeader("Actions").setWidth("90px").setFlexGrow(0);

    setDetailsVisibleOnClick(false);
    setItemDetailsRenderer(createMangaDetailsRenderer());

    getDataCommunicator().enablePushUpdates(Executors.newCachedThreadPool());
    sort(List.of(new GridSortOrder<>(mangaEntityColumn, SortDirection.DESCENDING)));
    setAllRowsVisible(true);
  }

  private static ComponentRenderer<MangaDetailsFormLayout, MangaEntity> createMangaDetailsRenderer() {
    return new ComponentRenderer<>(MangaDetailsFormLayout::new, MangaDetailsFormLayout::setManga);
  }

  private Image getIconColumn(MangaEntity entity) {
    Image image = new Image();

    // icon is saved locally
    if (entity.getIconPath() != null && fileExists(entity.getIconPath())) {
      image.setSrc(new StreamResource("icon.png",
          () -> getClass().getResourceAsStream("/images/" + entity.getIconPath())));
      return image;
    }

    // icon is not downloaded, download and set to entity
    UI ui = UI.getCurrent();
    new Thread(() -> new ReaperScansCrawler().asyncLoadIconTimed(entity)
        .addCallback(
            result -> ui.access(() -> image.setSrc(result)),
            err -> ui.access(() -> Notification.show("Failed to parse icon for " + entity.getName()))
        )).start();

    image.setHeight("150px");
    image.setWidth("100px");
    return image;
  }

  private Component getMainColumn(MangaEntity entity) {
    H3 name = new H3(entity.getName());
    name.getStyle().set(MARGIN_TOP, "5px").set(MARGIN_BOTTOM, ZERO);

    Component currentChapterLink = createChapterLink(entity, entity.getCurrentChNum(), CURRENT);
    Component latestChapterLink = createChapterLink(entity, entity.getLatestChNum(), LATEST);

    VerticalLayout vl = new VerticalLayout(name, currentChapterLink, latestChapterLink);
    vl.setPadding(false);

    return vl;
  }

  private Component getActionsColumn(MangaEntity entity) {
    Icon editIcon = new Icon(VaadinIcon.EDIT);
    editIcon.addClickListener(e -> setDetailsVisible(entity, !isDetailsVisible(entity)));

    Icon deleteIcon = new Icon(VaadinIcon.TRASH);
    deleteIcon.addClickListener(e ->
        new DeleteConfirmDialog("Confirmation", "Are you sure you want to delete this item?", () -> {
          dataProvider.getMangaEntities().remove(entity);
          getDataProvider().refreshAll();
        }).open());

    Stream.of(editIcon, deleteIcon).forEach(icon -> {
      icon.setSize("20px");
      icon.getStyle().set(MARGIN, AUTO);
    });
    VerticalLayout vl = new VerticalLayout(editIcon, deleteIcon);
    vl.setSizeFull();
    return vl;
  }

  private Component createChapterLink(MangaEntity entity, Integer chapterNumber, String caption) {
    H4 chapterCaption = new H4(caption.concat(":"));
    chapterCaption.getStyle().set(MARGIN_TOP, ZERO);

    Button chapterLink = new Button(CHAPTER_WITH.formatted(chapterNumber), e -> {
      entity.setCurrentChNum(chapterNumber);
      VaadinSession.getCurrent().setAttribute(MangaEntity.class, entity);
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

  private static class DeleteConfirmDialog extends ConfirmDialog {
    DeleteConfirmDialog(String header, String text, Runnable runnable) {
      setHeader(header);
      setText(text);
      addConfirmListener(e -> runnable.run());
      setCancelable(true);
    }
  }

  private static class MangaDetailsFormLayout extends FormLayout {
    private final Binder<MangaEntity> binder = new Binder<>();
    private final TextField nameField = new TextField("Name");
    private final TextField urlField = new TextField("URL");
    private final TextField scansNameField = new TextField("Scans name");
    private final IntegerField latestChField = new IntegerField("Latest chapter");
    private final IntegerField currentChField = new IntegerField("Current chapter");

    MangaDetailsFormLayout() {
      Stream.of(nameField, urlField, scansNameField, latestChField, currentChField)
          .forEach(this::add);

      binder.bind(nameField, MangaEntity::getName, MangaEntity::setName);
      binder.bind(urlField, MangaEntity::getUrlName, MangaEntity::setUrlName);
      binder.bind(scansNameField, MangaEntity::getScansName, MangaEntity::setScansName);
      binder.bind(latestChField, MangaEntity::getLatestChNum, MangaEntity::setLatestChNum);
      binder.bind(currentChField, MangaEntity::getCurrentChNum, MangaEntity::setCurrentChNum);

      setResponsiveSteps(new ResponsiveStep("0", 3));
      setColspan(nameField, 3);
      setColspan(urlField, 3);
    }

    void setManga(MangaEntity manga) {
      nameField.setValue(manga.getName());
      urlField.setValue(manga.getUrlName());
      scansNameField.setValue(manga.getScansName());
      latestChField.setValue(manga.getLatestChNum());
      currentChField.setValue(manga.getCurrentChNum());
      binder.setBean(manga);
    }
  }
}
