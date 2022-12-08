package com.mangareader.ui.component.extension;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.jetbrains.annotations.NotNull;

/** Utility class extending Notification. */
public class NotificationEx extends Notification {

  /** Utility method for displaying success notification. */
  public static @NotNull Notification success(String text) {
    Notification notification = show(text);
    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    return notification;
  }

  /** Utility method for displaying error notification. */
  public static @NotNull Notification error(String text) {
    Notification notification = show(text);
    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    return notification;
  }
}
