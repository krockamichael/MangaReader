package com.mangareader.components;

import com.vaadin.flow.component.html.Label;

public class LabelEx extends Label {

  public LabelEx(String text) {
    super(text);
  }

  public LabelEx withStyle(String styleName, String styleValue) {
    getStyle().set(styleName, styleValue);
    return this;
  }
}
