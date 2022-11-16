package com.mangareader.backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import java.util.Objects;

/**
 * Manga Entity containing basic information.
 */
@Getter
@Setter
@Entity
@ToString
@JsonPropertyOrder({"name", "urlName", "scansName", "scansUrlName", "latestChNum", "currentChNum", "iconPath"})
public class Manga extends AbstractEntity {
  private String name;
  private String urlName;
  private String scansName;
  private String scansUrlName;
  private Integer latestChNum;
  private Integer currentChNum = 1;
  private String iconPath;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Manga manga = (Manga) o;
    return getId() != null && Objects.equals(getId(), manga.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
