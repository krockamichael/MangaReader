package com.mangareader.backend.repository;

import com.mangareader.backend.entity.Manga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MangaRepository extends JpaRepository<Manga, Long> {

//  @Query("SELECT m FROM manga m " +
//      "WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
//  List<Manga> search(@Param("searchTerm") String searchTerm);

}
