package com.dinosaur.foodbowl.domain.thumbnail.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.dinosaur.foodbowl.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "thumbnail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Thumbnail extends BaseEntity {

  public static final int MAX_PATH_LENGTH = 512;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Getter
  @Column(name = "path", nullable = false, length = MAX_PATH_LENGTH)
  private String path;

  @Column(name = "width", nullable = false)
  private Integer width;

  @Column(name = "height", nullable = false)
  private Integer height;

  @Builder
  private Thumbnail(String path, Integer width, Integer height) {
    this.path = path;
    this.width = width;
    this.height = height;
  }
}
