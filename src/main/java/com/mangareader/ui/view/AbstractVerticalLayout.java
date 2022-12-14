package com.mangareader.ui.view;

import com.mangareader.backend.event.ComponentCloseEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Abstract Vertical Layout component that defines the basic behaviour for view pages.
 */
public abstract class AbstractVerticalLayout extends VerticalLayout {

  AbstractVerticalLayout() {
    setHeightFull();
    setAlignItems(Alignment.CENTER);
    setMargin(false);
    setPadding(false);
    setSpacing(false);
  }

  void addComponentCloseListener(ComponentEventListener<ComponentCloseEvent> listener) {
    addListener(ComponentCloseEvent.class, listener);
  }
}
