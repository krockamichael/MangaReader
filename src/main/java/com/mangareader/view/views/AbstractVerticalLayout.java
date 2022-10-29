package com.mangareader.view.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

abstract class AbstractVerticalLayout extends VerticalLayout {


  AbstractVerticalLayout() {
    setHeightFull();
    setAlignItems(Alignment.CENTER);
    setMargin(false);
    setPadding(false);
    setSpacing(false);
  }

  String getImageName(String url) {
    String[] split = url.split("\\\\");
    return split[split.length - 1];
  }
}
