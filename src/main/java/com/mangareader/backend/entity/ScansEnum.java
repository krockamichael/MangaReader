package com.mangareader.backend.entity;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScansEnum {

  REAPER_SCANS("Reaper Scans", "https://reaperscans.com/comics/");

  private final String name;
  private final String url;

  public static ScansEnum fromValue(String value) {
    for (ScansEnum scans : values())
      if (scans.getName().equals(value))
        return scans;
    return null;
  }

  public static class UrlScansEnumConverter implements Converter<ScansEnum, String> {
    @Override
    public Result<String> convertToModel(ScansEnum enumValue, ValueContext context) {
      if (enumValue != null)
        return Result.ok(enumValue.getUrl());
      return Result.error("Failed to get URL of scans.");
    }

    @Override
    public ScansEnum convertToPresentation(String value, ValueContext context) {
      if (value != null)
        return ScansEnum.valueOf(value);
      return ScansEnum.REAPER_SCANS;
    }
  }

  public static class NameScansEnumConverter implements Converter<ScansEnum, String> {
    @Override
    public Result<String> convertToModel(ScansEnum enumValue, ValueContext context) {
      if (enumValue != null)
        return Result.ok(enumValue.getName());
      return Result.error("Failed to get Name of scans.");
    }

    @Override
    public ScansEnum convertToPresentation(String value, ValueContext context) {
      if (value != null)
        return ScansEnum.fromValue(value);
      return ScansEnum.REAPER_SCANS;
    }
  }

}
