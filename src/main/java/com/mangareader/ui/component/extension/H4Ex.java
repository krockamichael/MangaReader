package com.mangareader.ui.component.extension;

import com.vaadin.flow.component.html.H4;

public class H4Ex extends H4 {

  public H4Ex(String text) {
    super(text);
  }

  public H4Ex withStyle(String styleName, String styleValue) {
    getStyle().set(styleName, styleValue);
    return this;
  }
}
