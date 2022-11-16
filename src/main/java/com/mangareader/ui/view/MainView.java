package com.mangareader.ui.view;

import com.mangareader.backend.data.entity.MangaEntity;
import com.mangareader.backend.data.service.crawler.ReaperScansCrawler;
import com.mangareader.ui.MyAppLayout;
import com.mangareader.ui.component.AddMangaDialog;
import com.mangareader.ui.component.extension.ButtonEx;
import com.mangareader.ui.component.extension.HorizontalLayoutEx;
import com.mangareader.ui.component.extension.TextFieldEx;
import com.mangareader.ui.component.extension.VerticalLayoutEx;
import com.mangareader.ui.component.grid.MangaGrid;
import com.mangareader.ui.component.grid.NewMangaSidebar;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import static com.mangareader.backend.data.Constants.AUTO;
import static com.mangareader.backend.data.Constants.MARGIN;

@Route(value = "", layout = MyAppLayout.class)
public class MainView extends AbstractVerticalLayout implements BeforeEnterObserver {

  private final transient ReaperScansCrawler rsCrawler = new ReaperScansCrawler();

  public MainView() {
    super();
    setupContent();
  }

  private void setupContent() {
    MangaGrid grid = new MangaGrid();
    TextFieldEx search = createSearch(grid);
    ButtonEx updateBtn = createUpdateButton(grid);
    ButtonEx addBtn = createAddButton(grid);
    NewMangaSidebar sidebar = new NewMangaSidebar();

    HorizontalLayoutEx hl = new HorizontalLayoutEx(updateBtn, addBtn, search)
        .withFlexGrow(1d, search)
        .withWidthFull();

    VerticalLayoutEx vl = new VerticalLayoutEx(hl, grid)
        .withHeightFull()
        .withWidth(740, Unit.PIXELS)
        .withSelfFlexGrow(.9d);

    HorizontalLayoutEx hl2 = new HorizontalLayoutEx(sidebar, vl)
        .withSizeUndefined()
        .withFlexGrow(0, sidebar, vl)
        .withSelfFlexGrow(0);

    add(hl2);
  }

  private TextFieldEx createSearch(MangaGrid grid) {
    GridListDataView<MangaEntity> dataView = grid.getListDataView();

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

  private ButtonEx createUpdateButton(MangaGrid grid) {
    UI ui = UI.getCurrent();
    return new ButtonEx("Update")
        .withClickListener(e -> grid.getDataProvider()
            .getItems()
            .forEach(entity -> new Thread(() -> rsCrawler.fetchLatestChapterNumber(entity)
                .addCallback(
                    result -> {
                      entity.setLatestChNum(result);
                      ui.access(() -> grid.getDataProvider().refreshItem(entity));
                    },
                    err -> ui.access(() -> Notification.show("Failed to parse latest chapter for " + entity.getName()))
                )
            ).start()));
  }

  private ButtonEx createAddButton(MangaGrid grid) {
    return new ButtonEx("Add")
        .withClickListener(e -> new AddMangaDialog(grid).open());
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    VaadinSession.getCurrent().setAttribute(MangaEntity.class, null);
  }
}