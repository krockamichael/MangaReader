package com.mangareader.components;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.renderer.Renderer;

@SuppressWarnings("java:S110")
public class ComboBoxEx<T> extends ComboBox<T> {

  public ComboBoxEx(String label) {
    super(label);
  }

  public ComboBoxEx<T> withAllowCustomValue(boolean allowCustomValue) {
    setAllowCustomValue(allowCustomValue);
    return this;
  }

  public ComboBoxEx<T> withAutofocus(boolean autofocus) {
    setAutofocus(autofocus);
    return this;
  }

  public ComboBoxEx<T> withItems(CallbackDataProvider.FetchCallback<T, String> fetchCallback) {
    setItems(fetchCallback);
    return this;
  }

  public ComboBoxEx<T> withItems(T[] items) {
    setItems(items);
    return this;
  }

  public ComboBoxEx<T> withRenderer(Renderer<T> renderer) {
    setRenderer(renderer);
    return this;
  }

  public ComboBoxEx<T> withItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    setItemLabelGenerator(itemLabelGenerator);
    return this;
  }

  public ComboBoxEx<T> withValueChangeListener(ValueChangeListener<ComponentValueChangeEvent<ComboBox<T>, T>> listener) {
    addValueChangeListener(listener);
    return this;
  }
}
