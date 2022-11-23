package com.mangareader.backend.repository;

import com.mangareader.backend.entity.Manga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MangaRepository extends JpaRepository<Manga, Long> {

  @Query("SELECT m FROM Manga m " +
      "WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
  List<Manga> search(@Param("searchTerm") String searchTerm);

}
