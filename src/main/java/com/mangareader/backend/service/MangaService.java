package com.mangareader.backend.service;

import com.mangareader.backend.entity.Manga;
import com.mangareader.backend.repository.MangaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class MangaService {

  @Autowired
  private final MangaRepository mangaRepository;
  private final Logger logger = Logger.getLogger(MangaService.class.getName());

  public MangaService(MangaRepository mangaRepository) {
    this.mangaRepository = mangaRepository;
  }

  public List<Manga> findAll() {
    return mangaRepository.findAll();
  }

  public List<Manga> findAll(String filter) {
    if (filter == null || filter.isEmpty()) {
      return mangaRepository.findAll();
    } else {
      return Collections.emptyList();// mangaRepository.search(filter);
    }
  }

  public long count() {
    return mangaRepository.count();
  }

  public void delete(Manga manga) {
    mangaRepository.delete(manga);
  }

  public void save(Manga manga) {
    if (manga == null) {
      logger.log(Level.SEVERE, "Manga is null cannot save.");
      return;
    }
    Manga result = mangaRepository.save(manga);
    logger.log(Level.INFO, "Manga \"{0}\" saved successfully.", result);
  }
}
