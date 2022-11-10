package com.mangareader.view.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Abstract Vertical Layout component that defines the basic behaviour for view pages.
 */
public abstract class AbstractVerticalLayout extends VerticalLayout {

  protected AbstractVerticalLayout() {
    setHeightFull();
    setAlignItems(Alignment.CENTER);
    setMargin(false);
    setPadding(false);
    setSpacing(false);
  }
}
