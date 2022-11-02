package com.mangareader.view.views;

import com.mangareader.data.MangaDataProvider;
import com.mangareader.entity.MangaEntity;
import com.mangareader.service.crawler.ReaperScansCrawler;
import com.mangareader.view.MyAppLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

import java.util.concurrent.Executors;

import static com.mangareader.constants.StringConstants.*;

@Route(value = "", layout = MyAppLayout.class)
public class MainView extends AbstractVerticalLayout {

  private static final MangaDataProvider dataProvider = new MangaDataProvider();
  private final transient ReaperScansCrawler rsCrawler = new ReaperScansCrawler();

  public MainView() {
    super();
    setupGrid();
  }

  private void setupGrid() {
    Grid<MangaEntity> grid = new Grid<>();
    grid.addComponentColumn(this::getIconColumn).setHeader("Icon").setWidth("100px");
    grid.addComponentColumn(this::getMainColumn).setHeader("Name").setAutoWidth(true);

    grid.setItems(dataProvider.getMangaEntities());
    grid.getDataCommunicator().enablePushUpdates(Executors.newCachedThreadPool());

    VerticalLayout vl = new VerticalLayout(grid);
    vl.setWidth(40f, Unit.PERCENTAGE);
    setFlexGrow(0.9d, vl);

    add(vl);
  }

  private Image getIconColumn(MangaEntity entity) {
    Image image = new Image("", "");
    image.setHeight("150px");
    image.setWidth("100px");

    UI ui = UI.getCurrent();
    new Thread(() -> rsCrawler.asyncLoadIconTimed(entity.getUrlName())
        .addCallback(
            result -> ui.access(() -> image.setSrc(result)),
            err -> ui.access(() -> Notification.show("Failed to parse icon for " + entity.getName()))
        )).start();

    return image;
  }

  private Component getMainColumn(MangaEntity entity) {
    H3 name = new H3(entity.getName());
    name.getStyle().set(MARGIN_TOP, "5px").set(MARGIN_BOTTOM, ZERO);

    Component viewedChapterLink = createChapterLink(entity, entity.getCurrentChapterNumber(), "Current:");
    Component currentChapterLink = createChapterLink(entity, entity.getCurrentChapterNumber(), "Latest:");

    VerticalLayout vl = new VerticalLayout(name, viewedChapterLink, currentChapterLink);
    vl.setPadding(false);

    return vl;
  }

  private Component createChapterLink(MangaEntity entity, Integer chapterNumber, String caption) {
    H4 chapterCaption = new H4(caption);
    chapterCaption.getStyle().set(MARGIN_TOP, ZERO);

    Button chapterLink = new Button("Chapter " + chapterNumber, event -> {
      ComponentUtil.setData(UI.getCurrent(), MangaEntity.class, entity);
      UI.getCurrent().navigate(ChapterView.class,
          new RouteParameters("chapterID", getChapterNumber(entity, caption)));
    });
    chapterLink.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    chapterLink.getStyle().set(MARGIN_TOP, "-7px").set(MARGIN_BOTTOM, ZERO);

    return new HorizontalLayout(chapterCaption, chapterLink);
  }

  private String getChapterNumber(MangaEntity entity, String caption) {
    Integer chapterNumber = caption.equals("Latest:")
        ? entity.getLatestChapterNumber()
        : entity.getCurrentChapterNumber();
    return chapterNumber.toString();
  }
}