package com.mangareader.ui.component.extension;

import com.mangareader.backend.event.ComponentCloseEvent;
import com.mangareader.ui.component.custom.HasWidgetCaption;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;

import static com.mangareader.backend.data.Constants.POSITION;
import static com.mangareader.backend.data.Constants.SET_PROPERTY_IN_OVERLAY_JS;

public class DialogEx extends Dialog implements HasWidgetCaption {

  protected void setupContent() {
    addThemeVariants(DialogVariant.LUMO_NO_PADDING);
    setModal(false);
    setOpened(true);
    setDraggable(true);
    setCloseOnEsc(false);

    setOverlayProperties();
    setupHeader();
  }

  protected void setupHeader() {
    // setup in children
  }

  public ButtonEx onCloseCreatePlusButton() {
    return new ButtonEx("+ " + getWidgetCaption())
        .withClickListener(e -> getComponent().setOpened(true));
  }

  protected IconEx getCloseIcon() {
    return new IconEx("lumo", "cross")
        .withClassName("close-icon")
        .withClickListener(e -> {
          close();
          ComponentUtil.fireEvent(UI.getCurrent(), new ComponentCloseEvent(this, true));
        });
  }

  protected void setOverlayProperties() {
    getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, POSITION, "fixed");
  }

  @Override
  public String getWidgetCaption() {
    return null;
  }

  protected DialogEx getComponent() {
    return this;
  }
}
