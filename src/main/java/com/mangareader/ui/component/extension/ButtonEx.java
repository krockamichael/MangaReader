package com.mangareader.ui.component.extension;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

public class ButtonEx extends Button {

  public ButtonEx(String label) {
    super(label);
  }

  public ButtonEx withClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
    addClickListener(listener);
    return this;
  }

  public ButtonEx withThemeVariant(ButtonVariant variant) {
    addThemeVariants(variant);
    return this;
  }

  public ButtonEx withVisibility(boolean visible) {
    setVisible(visible);
    return this;
  }

}
