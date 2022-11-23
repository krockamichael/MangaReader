package com.mangareader.ui.component.extension;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.icon.Icon;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IconEx extends Icon {

  IconEx(String collection, String iconName) {
    super(collection, iconName);
  }

  IconEx withClickListener(ComponentEventListener<ClickEvent<Icon>> listener) {
    addClickListener(listener);
    return this;
  }

  public IconEx withStyle(String styleName, String styleValue) {
    getStyle().set(styleName, styleValue);
    return this;
  }

  IconEx withClassName(String className) {
    setClassName(className);
    return this;
  }
}
