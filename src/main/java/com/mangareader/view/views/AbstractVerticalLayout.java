package com.mangareader.view.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

abstract class AbstractVerticalLayout extends VerticalLayout {

  static final String MARGIN_TOP = "margin-top";
  static final String MARGIN_BOTTOM = "margin-bottom";
  static final String ZERO = "0";

  AbstractVerticalLayout() {
    setHeightFull();
    setAlignItems(Alignment.CENTER);
    getStyle().set("margin", ZERO).set("padding", "0");
  }

  String getImageName(String url) {
    String[] split = url.split("\\\\");
    return split[split.length - 1];
  }
}
