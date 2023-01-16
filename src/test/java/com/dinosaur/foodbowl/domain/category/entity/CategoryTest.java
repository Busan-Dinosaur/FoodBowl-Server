package com.dinosaur.foodbowl.domain.category.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.domain.category.dao.CategoryRepository;
import com.dinosaur.foodbowl.domain.category.entity.Category.CategoryType;
import com.dinosaur.foodbowl.global.dao.RepositoryTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

class CategoryTest extends RepositoryTest {

  @Autowired
  CategoryRepository categoryRepository;

  @Nested
  @DisplayName("DB 동일성 테스트")
  class DBConsistencyTest {

    @DisplayName("카테고리 Enum이 DB와 동일한 값을 가지는지 확인한다.")
    @Test
    void hasSameValueAsDB() {
      CategoryType[] categoryTypes = CategoryType.values();

      List<Category> categories = categoryRepository.findAll(Sort.by(Direction.ASC, "id"));

      for (int i = 0; i < categories.size(); i++) {
        assertThat(categories.get(i).getCategoryType()).isEqualTo(categoryTypes[i]);
        assertThat(categories.get(i).getId()).isEqualTo(categoryTypes[i].getId());
      }
    }
  }

}