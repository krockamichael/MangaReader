package com.mangareader.view.views;

import com.mangareader.crawler.ReaperScansCrawler;
import com.mangareader.data.MangaDataProvider;
import com.mangareader.entity.MangaEntity;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

@PageTitle("Home")
@Route(value = "", layout = MyAppLayout.class)
public class MainView extends AbstractVerticalLayout {

  private static final MangaDataProvider dataProvider = new MangaDataProvider("C:\\Users\\krock\\Desktop\\mangareader\\src\\main\\resources\\static\\data.csv");

  public MainView() {
    super();
    setupGrid();
    setMargin(true);
  }

  private void setupGrid() {
    Grid<MangaEntity> grid = new Grid<>();
    grid.addComponentColumn(this::getIconColumn).setHeader("Icon").setWidth("100px");
    grid.addComponentColumn(this::getMainColumn).setHeader("Name").setAutoWidth(true);

    grid.setItems(dataProvider.getMangaEntities());

    grid.addItemClickListener(event -> {
      UI ui = UI.getCurrent();
      ComponentUtil.setData(ui, MangaEntity.class, event.getItem());
      ui.navigate(ChapterView.class);
    });

    VerticalLayout vl = new VerticalLayout(grid);
    vl.setWidth(40f, Unit.PERCENTAGE);
    setFlexGrow(0.9d, vl);

    add(vl);
  }

  private Image getIconColumn(MangaEntity entity) {
    if (entity.getIconPath() == null || entity.getIconPath().equals("")) {
      ReaperScansCrawler rsCrawler = new ReaperScansCrawler();
      rsCrawler.parseIcon(entity);
    }
    return new Image(new StreamResource(getImageName(entity.getIconPath()),
        () -> getClass().getResourceAsStream("/images/" + entity.getIconPath())), "");
  }

  private Component getMainColumn(MangaEntity entity) {
    H3 name = new H3(entity.getName());
    name.getStyle().set(MARGIN_TOP, "5px").set(MARGIN_BOTTOM, ZERO);

    Component viewedChapterLink = createChapterLink(entity, entity.getLastReadChapter(), "Viewed:");
    Component currentChapterLink = createChapterLink(entity, entity.getNumOfChapters(), "Current:");

    VerticalLayout vl = new VerticalLayout(name, viewedChapterLink, currentChapterLink);
    vl.setPadding(false);

    return vl;
  }

  private Component createChapterLink(MangaEntity entity, Integer chapterNumber, String caption) {
    H4 currentChapter = new H4(caption);
    currentChapter.getStyle().set(MARGIN_TOP, ZERO);

    Button chapterLink = new Button("Chapter " + chapterNumber);
    chapterLink.addClickListener(event -> {
      UI ui = UI.getCurrent();
      ComponentUtil.setData(ui, MangaEntity.class, entity);
      ui.navigate(ChapterView.class);
    });
    chapterLink.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    chapterLink.getStyle().set(MARGIN_TOP, "-7px").set(MARGIN_BOTTOM, ZERO);

    return new HorizontalLayout(currentChapter, chapterLink);
  }
}