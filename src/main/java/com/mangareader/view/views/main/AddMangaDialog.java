package com.mangareader.view.views.main;

import com.mangareader.components.ButtonEx;
import com.mangareader.components.ComboBoxEx;
import com.mangareader.components.VerticalLayoutEx;
import com.mangareader.entity.MangaEntity;
import com.mangareader.entity.ScansEnum;
import com.mangareader.entity.SearchResultDto;
import com.mangareader.service.crawler.ReaperScansCrawler;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Stream;

import static com.mangareader.constants.StringConstants.*;

class AddMangaDialog extends Dialog {

  private final transient Grid<MangaEntity> grid;
  private final transient List<MangaEntity> entityList;
  private final transient ReaperScansCrawler rsCrawler = new ReaperScansCrawler();
  private transient MangaEntity entity;

  AddMangaDialog(Grid<MangaEntity> grid, List<MangaEntity> entityList) {
    this.grid = grid;
    this.entityList = entityList;
    setDraggable(true);
    setPosition();
    setupContent();
  }

  public void setPosition() {
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "align-self", "flex-start");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "position", "absolute");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "left", "1420px");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "top", "48px");
  }

  private void setupContent() {
    setHeaderTitle("Add new manga");
    add(createDialogLayout());
    getFooter().add(createSaveButton(), new Button("Cancel", e -> close()));
  }

  private VerticalLayout createDialogLayout() {
    Binder<MangaEntity> binder = new Binder<>();

    ComboBoxEx<SearchResultDto> comboBox = new ComboBoxEx<SearchResultDto>("Name")
        .withAllowCustomValue(true)
        .withAutofocus(true)
        .withItems(this::getQuerySearchResult)
        .withRenderer(createRenderer())
        .withItemLabelGenerator(SearchResultDto::name)
        .withValueChangeListener(e ->
            entity = MangaEntity.builder()
                .name(e.getValue().name())
                .urlName(e.getValue().urlName())
                .latestChNum(e.getValue().latestChNum())
                .iconPath(e.getValue().icon())
                .build());

    ComboBoxEx<ScansEnum> scansCombo = new ComboBoxEx<ScansEnum>("Scans")
        .withItems(ScansEnum.values())
        .withItemLabelGenerator(ScansEnum::getName);

    binder.forField(scansCombo)
        .withConverter(new ScansEnum.NameScansEnumConverter())
        .bind(MangaEntity::getScansName, MangaEntity::setScansName);
    binder.forField(scansCombo)
        .withConverter(new ScansEnum.UrlScansEnumConverter())
        .bind(MangaEntity::getScansUrlName, MangaEntity::setScansUrlName);

    binder.setBean(entity);

    return new VerticalLayoutEx(comboBox, scansCombo)
        .withPadding(false)
        .withSpacing(false)
        .withAlignItems(FlexComponent.Alignment.STRETCH)
        .withStyle("width", "22rem")
        .withStyle("max-width", "100%");
  }

  private Stream<SearchResultDto> getQuerySearchResult(Query<SearchResultDto, String> query) {
    return rsCrawler
        .getMangaUrl(
            query.getFilter().orElse(null),
            PageRequest.of(query.getPage(), query.getPageSize()))
        .stream();
  }

  private Renderer<SearchResultDto> createRenderer() {
    return new ComponentRenderer<>(item -> {
      Image image = new Image(item.icon(), "");
      image.setHeight(SEARCH_IMG_HEIGHT);
      image.setWidth(SEARCH_IMG_WIDTH);
//      image.setClassName(""); // TODO FUTURE create css classes
      image.getStyle().set(MARGIN_TOP, "-5px").set(MARGIN_BOTTOM, "-5px").set(MARGIN_LEFT, "-30px");

      H4 nameLabel = new H4(item.name());
      nameLabel.getStyle().set(MARGIN, ZERO).set(PADDING, ZERO);

      Label chapterLabel = new Label(item.latestChNum() + " Chapters");
      chapterLabel.getStyle().set(MARGIN, ZERO).set(PADDING, ZERO);

      VerticalLayout vl = new VerticalLayout(nameLabel, chapterLabel);
      vl.setAlignSelf(FlexComponent.Alignment.CENTER, nameLabel, chapterLabel);
      vl.getStyle().set(MARGIN_TOP, "-10px");

      HorizontalLayout hl = new HorizontalLayout(image, vl);
      hl.setAlignSelf(FlexComponent.Alignment.CENTER, vl);
      hl.setHeight(SEARCH_HL_HEIGHT);
      hl.setSpacing(false);

      return hl;
    });
  }

  private Button createSaveButton() {
    return new ButtonEx("Add")
        .withClickListener(this::closeDialog)
        .withThemeVariant(ButtonVariant.LUMO_PRIMARY);
  }

  private void closeDialog(ClickEvent<Button> event) {
    entityList.add(entity);
    grid.getDataProvider().refreshAll();
    grid.sort(grid.getSortOrder());
    close();
  }
}
