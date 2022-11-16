package com.mangareader;

import com.mangareader.backend.data.MangaDataProvider;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.vaadin.artur.helpers.LaunchUtil;

import javax.annotation.PreDestroy;

@Push
@BodySize
@Theme("mangaTheme")
@Viewport("minimum-scale=1, user-scalable=yes, viewport-fit=cover")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

  public static void main(String[] args) {
    LaunchUtil.launchBrowserInDevelopmentMode(SpringApplication.run(Application.class, args));
  }

  @PreDestroy
  public void onExit() {
    MangaDataProvider.onExit();
  }
}
