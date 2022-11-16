package com.mangareader.backend.data;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mangareader.backend.data.entity.MangaEntity;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Log4j2
@NoArgsConstructor
@SuppressWarnings("java:S1075") // CSV_FILE_PATH as customizable parameter
public class MangaDataProvider {

  private static final String CSV_FILE_PATH = Paths.get("src/main/resources/static/data.csv").toAbsolutePath().toString();
  private static final List<MangaEntity> mangaEntities = readMangaEntitiesFromCSV();

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
      log.error(e);
    }
    return Collections.emptyList();
  }

  public static void onExit() {
    writeToCsv();
  }

  private static void writeToCsv() {
    if (CollectionUtils.isEmpty(mangaEntities)) {
      return;
    }

    File csvOutputFile = new File(CSV_FILE_PATH);
    CsvMapper csvMapper = new CsvMapper();

    CsvSchema csvSchema = csvMapper
        .typedSchemaFor(MangaEntity.class)
        .withHeader()
        .withColumnSeparator(',')
        .withComments();

    ObjectWriter writer = csvMapper.writerFor(MangaEntity.class).with(csvSchema);
    try {
      writer.writeValues(csvOutputFile).writeAll(mangaEntities);
    } catch (IOException e) {
      log.error(e);
    }
  }

  public List<MangaEntity> getMangaEntities() {
    return mangaEntities;
  }
}