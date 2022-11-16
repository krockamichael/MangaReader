package com.mangareader;

import com.mangareader.backend.data.MangaDataProvider;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.vaadin.artur.helpers.LaunchUtil;

import javax.annotation.PreDestroy;

@Push
@Theme("mangaTheme")
@SpringBootApplication
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

  public static void main(String[] args) {
    LaunchUtil.launchBrowserInDevelopmentMode(SpringApplication.run(Application.class, args));
  }

  @PreDestroy
  public void onExit() {
    MangaDataProvider.onExit();
  }
}
