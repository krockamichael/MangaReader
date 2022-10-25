package com.mangareader.data;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mangareader.entity.MangaEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MangaDataProvider {

  private static final Logger LOGGER = LogManager.getLogger(MangaDataProvider.class.getName());
  private final String csvFilePath;
  private List<MangaEntity> mangaEntities = new ArrayList<>();

  public MangaDataProvider(String csvFilePath) {
    this.csvFilePath = csvFilePath;
  }

  public List<MangaEntity> getMangaEntities() {
    if (CollectionUtils.isEmpty(mangaEntities)) {
      mangaEntities = readMangaEntitiesFromCSV();
    }
    return mangaEntities;
  }

  private List<MangaEntity> readMangaEntitiesFromCSV() {
    // TODO: use builder and dto?
    File csvFile = new File(csvFilePath);
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
      LOGGER.error(e);
    }
    return Collections.emptyList();
  }
}
