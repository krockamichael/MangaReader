package com.mangareader.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@SuppressWarnings("java:S110")
public class TextFieldEx extends TextField {

  public TextFieldEx(String label) {
    setLabel(label);
  }

  public TextFieldEx withPlaceholder(String placeholder) {
    setPlaceholder(placeholder);
    return this;
  }

  public TextFieldEx withWidth(String width) {
    setWidth(width);
    return this;
  }

  public TextFieldEx withStyle(String styleName, String styleValue) {
    getStyle().set(styleName, styleValue);
    return this;
  }

  public TextFieldEx withPrefixComponent(Component component) {
    setPrefixComponent(component);
    return this;
  }

  public TextFieldEx withValueChangeMode(ValueChangeMode valueChangeMode) {
    setValueChangeMode(valueChangeMode);
    return this;
  }

  public TextFieldEx withValueChangeListener(ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
    addValueChangeListener(listener);
    return this;
  }

  public TextFieldEx withAutofocus(boolean autofocus) {
    setAutofocus(autofocus);
    return this;
  }
}
