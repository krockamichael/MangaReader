package com.mangareader.backend.data;

import com.mangareader.backend.dto.SearchResultDto;
import com.mangareader.backend.entity.Manga;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DtoEntityMapper {

  DtoEntityMapper INSTANCE = Mappers.getMapper(DtoEntityMapper.class);

  SearchResultDto entityToDTO(Manga manga);

  Manga dtoToEntity(SearchResultDto searchResultDto);
}
