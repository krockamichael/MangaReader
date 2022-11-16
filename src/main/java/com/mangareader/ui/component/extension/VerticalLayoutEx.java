package com.mangareader.ui.component.extension;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Unit;
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

  public VerticalLayoutEx withSizeFull() {
    setSizeFull();
    return this;
  }

  public VerticalLayoutEx withHeightFull() {
    setHeightFull();
    return this;
  }

  public VerticalLayoutEx withWidth(float width, Unit unit) {
    setWidth(width, unit);
    return this;
  }

  public VerticalLayoutEx withFlexGrow(double flexGrow, HasElement... elementContainer) {
    setFlexGrow(flexGrow, elementContainer);
    return this;
  }

  public VerticalLayoutEx withSelfFlexGrow(double flexGrow) {
    setFlexGrow(flexGrow, this);
    return this;
  }
}
