package com.mangareader.components;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.AbstractStreamResource;

public class ImageEx extends Image {

  public ImageEx() {
  }

  public ImageEx(String src, String alt) {
    super(src, alt);
  }

  public ImageEx(AbstractStreamResource src, String alt) {
    super(src, alt);
  }

  public ImageEx withHeightAndWidth(String height, String width) {
    setHeight(height);
    setWidth(width);
    return this;
  }

  public ImageEx withStyle(String styleName, String styleValue) {
    getStyle().set(styleName, styleValue);
    return this;
  }
}
