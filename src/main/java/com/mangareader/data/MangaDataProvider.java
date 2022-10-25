package com.mangareader.data;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mangareader.entity.MangaEntity;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MangaDataProvider {

  private static final Logger LOGGER = LogManager.getLogger(MangaDataProvider.class.getName());
  private List<MangaEntity> mangaEntities = new ArrayList<>();

  public MangaDataProvider(String path) {
    readMangaEntitiesFromCSV(path);
  }

  private void readMangaEntitiesFromCSV(String path) {
    // TODO: use builder and dto?
    File csvFile = new File(path);
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
      mangaEntities = mangasIter.readAll();
    } catch (IOException e) {
      LOGGER.error(e);
    }
  }
}
