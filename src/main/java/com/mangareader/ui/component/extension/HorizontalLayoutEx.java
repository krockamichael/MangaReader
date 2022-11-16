package com.mangareader.ui.component.extension;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class HorizontalLayoutEx extends HorizontalLayout {

  public HorizontalLayoutEx(Component... components) {
    super(components);
  }

  public HorizontalLayoutEx withAlignSelf(FlexComponent.Alignment alignment, HasElement... elementContainers) {
    setAlignSelf(alignment, elementContainers);
    return this;
  }

  public HorizontalLayoutEx withHeight(String height) {
    setHeight(height);
    return this;
  }

  public HorizontalLayoutEx withHeightFull() {
    setHeightFull();
    return this;
  }

  public HorizontalLayoutEx withWidth(float width, Unit unit) {
    setWidth(width, unit);
    return this;
  }

  public HorizontalLayoutEx withWidthFull() {
    setWidthFull();
    return this;
  }

  public HorizontalLayoutEx withSizeFull() {
    setSizeFull();
    return this;
  }

  public HorizontalLayoutEx withSizeUndefined() {
    setSizeUndefined();
    return this;
  }

  public HorizontalLayoutEx withSpacing(boolean spacing) {
    setSpacing(spacing);
    return this;
  }

  public HorizontalLayoutEx withFlexGrow(double flexGrow, HasElement... elementContainer) {
    setFlexGrow(flexGrow, elementContainer);
    return this;
  }

  public HorizontalLayoutEx withSelfFlexGrow(double flexGrow) {
    setFlexGrow(flexGrow, this);
    return this;
  }
}
