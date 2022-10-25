package com.mangareader;

import com.mangareader.data.MangaDataProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class MangareaderApplication {

  public static void main(String[] args) {
    SpringApplication.run(MangareaderApplication.class, args);
  }

  @PreDestroy
  public void onExit() {
    MangaDataProvider.onExit();
  }
}
