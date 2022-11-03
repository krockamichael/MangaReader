package com.mangareader;

import com.mangareader.data.MangaDataProvider;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.vaadin.artur.helpers.LaunchUtil;

import javax.annotation.PreDestroy;

@Push
@BodySize
@SpringBootApplication
@Theme(variant = Lumo.DARK)
@Viewport("minimum-scale=1, user-scalable=yes, viewport-fit=cover")
public class MangareaderApplication extends SpringBootServletInitializer implements AppShellConfigurator {

  public static void main(String[] args) {
    LaunchUtil.launchBrowserInDevelopmentMode(SpringApplication.run(MangareaderApplication.class, args));
  }

  @PreDestroy
  public void onExit() {
    MangaDataProvider.onExit();
  }
}
