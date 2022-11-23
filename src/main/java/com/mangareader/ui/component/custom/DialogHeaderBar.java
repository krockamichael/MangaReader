package com.mangareader.ui.component.custom;

import com.mangareader.ui.component.extension.HorizontalLayoutEx;
import com.vaadin.flow.component.Component;

class DialogHeaderBar extends HorizontalLayoutEx {

  DialogHeaderBar(boolean spacing, Component... components) {
    super(components);
    setSpacing(spacing);
    setClassName("dialog-header");
  }
}
