package com.mangareader.view.views.main;

import com.mangareader.components.ButtonEx;
import com.mangareader.components.TextFieldEx;
import com.mangareader.data.MangaDataProvider;
import com.mangareader.entity.MangaEntity;
import com.mangareader.service.crawler.ReaperScansCrawler;
import com.mangareader.view.MyAppLayout;
import com.mangareader.view.views.AbstractVerticalLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import static com.mangareader.constants.StringConstants.AUTO;
import static com.mangareader.constants.StringConstants.MARGIN;

@Route(value = "", layout = MyAppLayout.class)
public class MainView extends AbstractVerticalLayout implements BeforeEnterObserver {

  private static final MangaDataProvider dataProvider = new MangaDataProvider();
  private final transient ReaperScansCrawler rsCrawler = new ReaperScansCrawler();

  public MainView() {
    super();
    setupContent();
  }

  private void setupContent() {
    Grid<MangaEntity> grid = new MangaGrid();
    TextFieldEx search = createSearch(grid);
    ButtonEx updateBtn = createUpdateButton(grid);
    ButtonEx addBtn = createAddButton(grid);

    HorizontalLayout hl = new HorizontalLayout(updateBtn, addBtn, search);
    hl.setFlexGrow(1f, search);
    hl.setWidthFull();

    VerticalLayout vl = new VerticalLayout(hl, grid);
    vl.setHeightFull();
    vl.setWidth(40f, Unit.PERCENTAGE);
    setFlexGrow(0.9d, vl);

    add(vl);
  }


  private TextFieldEx createSearch(Grid<MangaEntity> grid) {
    GridListDataView<MangaEntity> dataView = grid.setItems(dataProvider.getMangaEntities());

    TextFieldEx searchField = new TextFieldEx()
        .withPlaceholder("Search")
        .withWidth("50%")
        .withStyle(MARGIN, AUTO)
        .withPrefixComponent(new Icon(VaadinIcon.SEARCH))
        .withValueChangeMode(ValueChangeMode.LAZY)
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

  private ButtonEx createUpdateButton(Grid<MangaEntity> grid) {
    UI ui = UI.getCurrent();
    return new ButtonEx("Update")
        .withClickListener(e -> {
          for (MangaEntity entity : dataProvider.getMangaEntities()) {
            new Thread(() -> rsCrawler.fetchLatestChapterNumber(entity)
                .addCallback(
                    result -> {
                      entity.setLatestChNum(result);
                      ui.access(() -> grid.getDataProvider().refreshItem(entity));
                    },
                    err -> ui.access(() -> Notification.show("Failed to parse latest chapter for " + entity.getName()))
                )).start();
          }
        });
  }

  private ButtonEx createAddButton(Grid<MangaEntity> grid) {
    return new ButtonEx("Add")
        .withClickListener(e -> new AddMangaDialog(grid, dataProvider.getMangaEntities()).open());
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    VaadinSession.getCurrent().setAttribute(MangaEntity.class, null);
  }
}