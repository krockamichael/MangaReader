package com.mangareader.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class VerticalLayoutEx extends VerticalLayout {

  public VerticalLayoutEx(Component... components) {
    super(components);
  }

  public VerticalLayoutEx withPadding(boolean padding) {
    setPadding(padding);
    return this;
  }

  public VerticalLayoutEx withSpacing(boolean spacing) {
    setSpacing(spacing);
    return this;
  }

  public VerticalLayoutEx withAlignItems(FlexComponent.Alignment alignment) {
    setAlignItems(alignment);
    return this;
  }

  public VerticalLayoutEx withStyle(String styleName, String styleValue) {
    getStyle().set(styleName, styleValue);
    return this;
  }
}
