package com.mangareader.ui.view;

import com.mangareader.backend.entity.Manga;
import com.mangareader.backend.event.ComponentCloseEvent;
import com.mangareader.backend.service.MangaService;
import com.mangareader.ui.MyAppLayout;
import com.mangareader.ui.component.custom.MainGridComponent;
import com.mangareader.ui.component.custom.NewMangaSidebarComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

@Route(value = "", layout = MyAppLayout.class)
public class MainView extends AbstractVerticalLayout implements BeforeEnterObserver {

  private final MainGridComponent mainGrid;
  private Registration registration;

  public MainView(MangaService mangaService) {
    super();
    mainGrid = new MainGridComponent(mangaService);
    add(new NewMangaSidebarComponent(mangaService), mainGrid);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    registration = ComponentUtil.addListener(
        attachEvent.getUI(),
        ComponentCloseEvent.class,
        e -> add(e.getSource().onCloseCreatePlusButton()
            .withClickListener(click -> remove(click.getSource())))
    );
    mainGrid.updateLatestChapters();
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    registration.remove();
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    VaadinSession.getCurrent().setAttribute(Manga.class, null);
  }
}