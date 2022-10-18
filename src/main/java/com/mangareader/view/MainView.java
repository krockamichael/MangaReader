package com.mangareader.view;

import com.mangareader.crawler.ReaperScansCrawler;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PageTitle("Main")
@Route(value = "")
public class MainView extends VerticalLayout {

    public MainView() {
        setupContent();
    }

    private void setupContent() {
        Button homeButton = createHomeButton();
        Button bookmarksButton = createBookmarksButton();
        add(new HorizontalLayout(homeButton, bookmarksButton));

        setupImageContent();

        setMargin(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private Button createHomeButton() {
        return new Button("Home");
    }

    private Button createBookmarksButton() {
        return new Button("Bookmarks");
    }

    private void setupImageContent() {
        List<Image> imageComponents = createImageComponents();
        imageComponents.forEach(this::add);
    }

    private List<Image> createImageComponents() {
        ReaperScansCrawler rsCrawler = new ReaperScansCrawler();

        return rsCrawler.parseMangas()
            .stream()
            .map(url -> new Image(url, ""))
            .toList();
    }
}
