package com.mangareader.view.views;

import com.mangareader.components.TextFieldEx;
import com.mangareader.data.MangaDataProvider;
import com.mangareader.entity.MangaEntity;
import com.mangareader.service.crawler.ReaperScansCrawler;
import com.mangareader.view.MyAppLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;
import java.util.concurrent.Executors;

import static com.mangareader.constants.StringConstants.*;

@Route(value = "", layout = MyAppLayout.class)
public class MainView extends AbstractVerticalLayout implements BeforeEnterObserver {

  private static final MangaDataProvider dataProvider = new MangaDataProvider();
  private final transient ReaperScansCrawler rsCrawler = new ReaperScansCrawler();

  public MainView() {
    super();
    setupGrid();
  }

  private void setupGrid() {
    Grid<MangaEntity> grid = new Grid<>();
    grid.addComponentColumn(this::getIconColumn).setHeader("Icon").setWidth("100px");
    Grid.Column<MangaEntity> mangaEntityColumn = grid.addComponentColumn(this::getMainColumn)
        .setHeader("Name").setAutoWidth(true)
        .setComparator(c -> c.getLatestChapterNumber() > c.getCurrentChapterNumber());

    grid.getDataCommunicator().enablePushUpdates(Executors.newCachedThreadPool());
    grid.sort(List.of(new GridSortOrder<>(mangaEntityColumn, SortDirection.DESCENDING)));

    VerticalLayout vl = new VerticalLayout(createSearch(grid), grid);
    vl.setWidth(40f, Unit.PERCENTAGE);
    setFlexGrow(0.9d, vl);

    add(vl);
  }

  private Image getIconColumn(MangaEntity entity) {
    Image image = new Image("", "");
    image.setHeight("150px");
    image.setWidth("100px");

    UI ui = UI.getCurrent();
    new Thread(() -> rsCrawler.asyncLoadIconTimed(entity)
        .addCallback(
            result -> ui.access(() -> image.setSrc(result)),
            err -> ui.access(() -> Notification.show("Failed to parse icon for " + entity.getName()))
        )).start();

    return image;
  }

  private Component getMainColumn(MangaEntity entity) {
    H3 name = new H3(entity.getName());
    name.getStyle().set(MARGIN_TOP, "5px").set(MARGIN_BOTTOM, ZERO);

    Component currentChapterLink = createChapterLink(entity, entity.getCurrentChapterNumber(), CURRENT);
    Component latestChapterLink = createChapterLink(entity, entity.getLatestChapterNumber(), LATEST);

    VerticalLayout vl = new VerticalLayout(name, currentChapterLink, latestChapterLink);
    vl.setPadding(false);

    return vl;
  }

  private Component createChapterLink(MangaEntity entity, Integer chapterNumber, String caption) {
    H4 chapterCaption = new H4(caption.concat(":"));
    chapterCaption.getStyle().set(MARGIN_TOP, ZERO);

    Button chapterLink = new Button(CHAPTER_WITH.formatted(chapterNumber), e -> {
      entity.setCurrentChapterNumber(chapterNumber);
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

  private TextFieldEx createSearch(Grid<MangaEntity> grid) {
    GridListDataView<MangaEntity> dataView = grid.setItems(dataProvider.getMangaEntities());

    TextFieldEx searchField = new TextFieldEx()
        .withPlaceholder("Search")
        .withWidth("50%")
        .withStyle(MARGIN, AUTO)
        .withPrefixComponent(new Icon(VaadinIcon.SEARCH))
        .withValueChangeMode(ValueChangeMode.EAGER)
        .withValueChangeListener(e -> dataView.refreshAll());

    dataView.addFilter(mangaEntity -> {
      String searchTerm = searchField.getValue().trim();

      if (searchTerm.isEmpty())
        return true;

      return matchesTerm(mangaEntity.getName(), searchTerm);
    });

    return searchField;
  }

  private boolean matchesTerm(String value, String searchTerm) {
    return value.toLowerCase().contains(searchTerm.toLowerCase());
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    VaadinSession.getCurrent().setAttribute(MangaEntity.class, null);
  }
}