package com.dinosaur.foodbowl.domain.category.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Category {

  private static final int MAX_NAME_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "name", nullable = false, updatable = false, unique = true, length = MAX_NAME_LENGTH)
  private CategoryType categoryType;

  public enum CategoryType {
    족발_보쌈(1, "족발∙보쌈"),
    찜_탕_찌개(2, "찜∙탕∙찌개"),
    돈까스_회_일식(3, "돈까스∙회∙일식"),
    피자(4, "피자"),
    고기_구이(5, "고기∙구이"),
    야식(6, "야식"),
    양식(7, "양식"),
    치킨(8, "치킨"),
    중식(9, "중식"),
    아시안(10, "아시안"),
    백반_죽_국수(11, "백반∙죽∙국수"),
    도시락(12, "도시락"),
    분식(13, "분식"),
    카페_디저트(14, "카페∙디저트"),
    패스트푸드(15, "패스트푸드");

    private final long id;
    private final String name;

    CategoryType(long id, String name) {
      this.id = id;
      this.name = name;
    }

    public long getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }
}
