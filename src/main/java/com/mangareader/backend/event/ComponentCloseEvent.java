package com.mangareader.backend.event;

import com.mangareader.ui.component.extension.DialogEx;
import com.vaadin.flow.component.ComponentEvent;

public class ComponentCloseEvent extends ComponentEvent<DialogEx> {

  /**
   * Creates a new event using the given source and indicator whether the
   * event originated from the client side or the server side.
   *
   * @param source     the source component
   * @param fromClient <code>true</code> if the event originated from the client
   *                   side, <code>false</code> otherwise
   */
  public <T extends DialogEx> ComponentCloseEvent(T source, boolean fromClient) {
    super(source, fromClient);
  }
}
