package com.mangareader;

import com.mangareader.data.MangaDataProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.vaadin.artur.helpers.LaunchUtil;

import javax.annotation.PreDestroy;

@EnableAsync
@SpringBootApplication
public class MangareaderApplication {

  public static void main(String[] args) {
    LaunchUtil.launchBrowserInDevelopmentMode(SpringApplication.run(MangareaderApplication.class, args));
  }

  @PreDestroy
  public void onExit() {
    MangaDataProvider.onExit();
  }
}
