package com.mangareader.data;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mangareader.entity.MangaEntity;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
@SuppressWarnings("java:S1075")
public class MangaDataProvider {

  private static final String CSV_FILE_PATH = "C:\\Users\\krock\\Desktop\\mangareader\\src\\main\\resources\\static\\data.csv";
  private static List<MangaEntity> mangaEntities = readMangaEntitiesFromCSV();

  private static List<MangaEntity> readMangaEntitiesFromCSV() {
    // TODO: use builder and dto?
    File csvFile = new File(CSV_FILE_PATH);
    CsvMapper csvMapper = new CsvMapper();
    CsvSchema csvSchema = csvMapper
        .typedSchemaFor(MangaEntity.class)
        .withHeader()
        .withColumnSeparator(',')
        .withComments();

    try (MappingIterator<MangaEntity> mangasIter = csvMapper
        .readerWithTypedSchemaFor(MangaEntity.class)
        .with(csvSchema)
        .readValues(csvFile)) {
      return mangasIter.readAll();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return Collections.emptyList();
  }

  public static void onExit() {
    // TODO: on shutdown write items to csv
    log.info("\n".concat(mangaEntities.stream().map(MangaEntity::getName).collect(Collectors.joining("\n"))));
  }

  public List<MangaEntity> getMangaEntities() {
    return mangaEntities;
  }
}
