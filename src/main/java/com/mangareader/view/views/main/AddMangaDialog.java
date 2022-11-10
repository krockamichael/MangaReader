package com.mangareader.view.views.main;

import com.mangareader.components.ButtonEx;
import com.mangareader.entity.MangaEntity;
import com.mangareader.entity.ScansEnum;
import com.mangareader.service.crawler.ReaperScansCrawler;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mangareader.constants.StringConstants.SET_PROPERTY_IN_OVERLAY_JS;

class AddMangaDialog extends Dialog {

  private final transient Grid<MangaEntity> grid;
  private final transient List<MangaEntity> entityList;
  private final transient MangaEntity entity = new MangaEntity();
  private Map<String, String> resultList = new HashMap<>();

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
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "left", "1440px");
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "top", "48px");
  }

  private void setupContent() {
    setHeaderTitle("Add new manga");
    add(createDialogLayout());
    getFooter().add(createSaveButton(), new Button("Cancel", e -> close()));
  }

  private VerticalLayout createDialogLayout() {
    Binder<MangaEntity> binder = new Binder<>();
    ReaperScansCrawler rsCrawler = new ReaperScansCrawler();

    ComboBox<String> comboBox = new ComboBox<>("Name");
    comboBox.setAllowCustomValue(true);
    comboBox.setAutofocus(true);
    comboBox.setItems(
        query -> {
          resultList = rsCrawler.getMangaUrl(
              query.getFilter().orElse(null),
              PageRequest.of(query.getPage(), query.getPageSize()));
          return resultList.values().stream();
        }
    );
    comboBox.addValueChangeListener(e -> {
      entity.setName(e.getValue());
      entity.setUrlName(resultList.get(e.getValue()));
    });

    ComboBox<ScansEnum> scansCombo = new ComboBox<>("Scans");
    scansCombo.setItems(ScansEnum.values());
    scansCombo.setItemLabelGenerator(ScansEnum::getName);
    binder.forField(scansCombo)
        .withConverter(new ScansEnum.NameScansEnumConverter())
        .bind(MangaEntity::getScansName, MangaEntity::setScansName);
    binder.forField(scansCombo)
        .withConverter(new ScansEnum.UrlScansEnumConverter())
        .bind(MangaEntity::getScansUrlName, MangaEntity::setScansUrlName);

    binder.setBean(entity);

    VerticalLayout vl = new VerticalLayout(comboBox, scansCombo);
    vl.setPadding(false);
    vl.setSpacing(false);
    vl.setAlignItems(FlexComponent.Alignment.STRETCH);
    vl.getStyle().set("width", "18rem").set("max-width", "100%");

    return vl;
  }

  private Button createSaveButton() {
    return new ButtonEx("Add")
        .withClickListener(this::closeDialog)
        .withThemeVariant(ButtonVariant.LUMO_PRIMARY);
  }

  private void closeDialog(ClickEvent<Button> event) {
    entityList.add(entity);
    grid.getDataProvider().refreshAll();
    close();
  }
}
