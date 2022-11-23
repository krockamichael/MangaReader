package com.mangareader.ui.component.custom;

import com.mangareader.backend.data.DtoEntityMapper;
import com.mangareader.backend.dto.SearchResultDto;
import com.mangareader.backend.entity.Manga;
import com.mangareader.backend.entity.ScansEnum;
import com.mangareader.backend.service.MangaService;
import com.mangareader.backend.service.crawler.ReaperScansCrawler;
import com.mangareader.ui.component.extension.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.mangareader.backend.data.Constants.*;

public class AddMangaDialog extends Dialog {

  private final transient ReaperScansCrawler rsCrawler = new ReaperScansCrawler();
  private final transient MangaService mangaService;
  private final List<Binder<Manga>> binders = new ArrayList<>();
  private transient Manga entity;

  public AddMangaDialog(MangaService mangaService) {
    this.mangaService = mangaService;
    setDraggable(true);
    setPosition();
    setupContent();
  }

  public void setPosition() {
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, POSITION, "fixed");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, LEFT, "75%");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, TOP, "6.5%");
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

    Binder<Manga> nameBinder = new Binder<>();
    nameBinder.forField(scansCombo)
        .withConverter(new ScansEnum.UrlScansEnumConverter())
        .bind(Manga::getScansUrlName, Manga::setScansUrlName);
    Binder<Manga> urlNameBinder = new Binder<>();
    urlNameBinder.forField(scansCombo)
        .withConverter(new ScansEnum.NameScansEnumConverter())
        .bind(Manga::getScansName, Manga::setScansName);
    binders.addAll(List.of(nameBinder, urlNameBinder));

    return new VerticalLayoutEx(searchCombo, scansCombo)
        .withPadding(false)
        .withSpacing(false)
        .withAlignItems(FlexComponent.Alignment.STRETCH)
        .withStyle(WIDTH, "22rem")
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
    binders.forEach(b -> b.setBean(entity));
    mangaService.save(entity);
    close();
  }
}
