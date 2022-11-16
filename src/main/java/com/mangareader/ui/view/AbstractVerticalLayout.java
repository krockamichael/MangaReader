package com.mangareader.ui.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Abstract Vertical Layout component that defines the basic behaviour for view pages.
 */
abstract class AbstractVerticalLayout extends VerticalLayout {

  AbstractVerticalLayout() {
    setHeightFull();
    setAlignItems(Alignment.CENTER);
    setMargin(false);
    setPadding(false);
    setSpacing(false);
  }
}
