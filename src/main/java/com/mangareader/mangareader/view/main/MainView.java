package com.mangareader.mangareader.view.main;

import com.mangareader.mangareader.crawler.ReaperScansCrawler;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.HashSet;
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
        Set<Image> imageComponents = createImageComponents();
        imageComponents.forEach(this::add);
    }

    private Set<Image> createImageComponents() {
        ReaperScansCrawler rsCrawler = new ReaperScansCrawler();
        Set<Image> images = new HashSet<>();

        String link = "https://media.reaperscans.com/file/4SRBHm/comics/312fc4a4-ce24-46e3-b4e6-38ee08d20c40/chapters/877e549d-381c-461f-9b3f-c3e977abc114/000.jpg";
        Image img = new Image(link, "");
        images.add(img);

        return images;
    }
}
