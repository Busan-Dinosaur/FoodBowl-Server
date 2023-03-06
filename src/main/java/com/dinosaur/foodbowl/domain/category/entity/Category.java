package com.dinosaur.foodbowl.domain.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
        전체(1L),
        카페(2L),
        한식(3L),
        양식(4L),
        일식(5L),
        중식(6L),
        치킨(7L),
        분식(8L),
        해산물(9L),
        샐러드(10L);

        private final long id;

        CategoryType(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }
    }
}
