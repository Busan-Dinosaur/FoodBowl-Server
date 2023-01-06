package com.dinosaur.foodbowl.domain.thumbnail.entity;

import static javax.persistence.GenerationType.IDENTITY;

import com.dinosaur.foodbowl.global.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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
@ToString(of = {"id", "path"})
public class Thumbnail extends BaseEntity {

  public static final int MAX_PATH_LENGTH = 512;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Getter
  @Column(name = "path", nullable = false, length = MAX_PATH_LENGTH)
  private String path;

  @Builder
  private Thumbnail(String path) {
    this.path = path;
  }
}
