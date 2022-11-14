package com.mangareader.entity;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DtoEntityMapper {

  DtoEntityMapper INSTANCE = Mappers.getMapper(DtoEntityMapper.class);

  SearchResultDto entityToDTO(MangaEntity mangaEntity);

  MangaEntity dtoToEntity(SearchResultDto searchResultDto);
}
