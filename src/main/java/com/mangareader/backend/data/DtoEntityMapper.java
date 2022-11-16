package com.mangareader.backend.data;

import com.mangareader.backend.data.dto.SearchResultDto;
import com.mangareader.backend.data.entity.MangaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DtoEntityMapper {

  DtoEntityMapper INSTANCE = Mappers.getMapper(DtoEntityMapper.class);

  SearchResultDto entityToDTO(MangaEntity mangaEntity);

  MangaEntity dtoToEntity(SearchResultDto searchResultDto);
}
