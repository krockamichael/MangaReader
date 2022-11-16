package com.mangareader.ui.component;

import com.mangareader.backend.data.DtoEntityMapper;
import com.mangareader.backend.data.dto.SearchResultDto;
import com.mangareader.backend.data.entity.MangaEntity;
import com.mangareader.backend.data.entity.ScansEnum;
import com.mangareader.backend.service.crawler.ReaperScansCrawler;
import com.mangareader.ui.component.extension.*;
import com.mangareader.ui.component.grid.MangaGrid;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import org.springframework.data.domain.PageRequest;

import java.util.stream.Stream;

import static com.mangareader.backend.data.Constants.*;

public class AddMangaDialog extends Dialog {

  private final transient MangaGrid grid;
  private final transient ReaperScansCrawler rsCrawler = new ReaperScansCrawler();
  private transient MangaEntity entity;

  public AddMangaDialog(MangaGrid grid) {
    this.grid = grid;
    setDraggable(true);
    setPosition();
    setupContent();
  }

  public void setPosition() {
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "position", "fixed");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "left", "92%");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "top", "9%");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "transform", "translate(-90%, -9%)");
  }

  private void setupContent() {
    setHeaderTitle("Add new manga");
    add(createDialogLayout());
    getFooter().add(createAddButton(), new Button("Cancel", e -> close()));
  }

  private VerticalLayout createDialogLayout() {
    ComboBoxEx<SearchResultDto> searchCombo = new ComboBoxEx<SearchResultDto>("Name")
        .withAllowCustomValue(true)
        .withAutofocus(true)
        .withItems(this::getQuerySearchResult)
        .withRenderer(createRenderer())
        .withItemLabelGenerator(SearchResultDto::name)
        .withValueChangeListener(e -> entity = DtoEntityMapper.INSTANCE.dtoToEntity(e.getValue()));

    ComboBoxEx<ScansEnum> scansCombo = new ComboBoxEx<ScansEnum>("Scans")
        .withItems(ScansEnum.values())
        .withItemLabelGenerator(ScansEnum::getName);

    Binder<MangaEntity> binder = new Binder<>();
    binder.forField(scansCombo)
        .withConverter(new ScansEnum.NameScansEnumConverter())
        .bind(MangaEntity::getScansName, MangaEntity::setScansName);
    binder.forField(scansCombo)
        .withConverter(new ScansEnum.UrlScansEnumConverter())
        .bind(MangaEntity::getScansUrlName, MangaEntity::setScansUrlName);
    binder.setBean(entity);

    return new VerticalLayoutEx(searchCombo, scansCombo)
        .withPadding(false)
        .withSpacing(false)
        .withAlignItems(FlexComponent.Alignment.STRETCH)
        .withStyle("width", "22rem")
        .withStyle("max-width", "100%");
  }

  private Stream<SearchResultDto> getQuerySearchResult(Query<SearchResultDto, String> query) {
    String value = query.getFilter().orElse(null);
    if (value == null || value.equals("")) {
      query.getPage();
      query.getPageSize();
      return Stream.empty();
    }

    return rsCrawler.getMangaUrl(
            value,
            PageRequest.of(query.getPage(), query.getPageSize()))
        .stream();
  }

  private Renderer<SearchResultDto> createRenderer() {
    return new ComponentRenderer<>(item -> {
      // TODO FUTURE create css classes: image.setClassName("");
      ImageEx image = new ImageEx(item.icon(), "")
          .withHeightAndWidth(SEARCH_IMG_HEIGHT, SEARCH_IMG_WIDTH)
          .withStyle(MARGIN_TOP, "-5px")
          .withStyle(MARGIN_BOTTOM, "-5px")
          .withStyle(MARGIN_LEFT, "-30px");

      H4Ex nameLabel = new H4Ex(item.name())
          .withStyle(MARGIN, ZERO)
          .withStyle(PADDING, ZERO);

      LabelEx chapterLabel = new LabelEx(item.latestChNum() + " Chapters")
          .withStyle(MARGIN, ZERO)
          .withStyle(PADDING, ZERO);

      VerticalLayoutEx vl = new VerticalLayoutEx(nameLabel, chapterLabel)
          .withAlignItems(FlexComponent.Alignment.CENTER)
          .withStyle(MARGIN_TOP, "-10px");

      return new HorizontalLayoutEx(image, vl)
          .withAlignSelf(FlexComponent.Alignment.CENTER, vl)
          .withHeight(SEARCH_HL_HEIGHT)
          .withSpacing(false);
    });
  }

  private Button createAddButton() {
    return new ButtonEx("Add")
        .withClickListener(this::closeDialog)
        .withThemeVariant(ButtonVariant.LUMO_PRIMARY);
  }

  private void closeDialog(ClickEvent<Button> event) {
    grid.addItem(entity);
    grid.sort(grid.getSortOrder());
    close();
  }
}
