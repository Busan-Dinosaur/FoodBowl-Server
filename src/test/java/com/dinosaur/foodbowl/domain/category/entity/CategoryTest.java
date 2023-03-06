package com.dinosaur.foodbowl.domain.category.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.category.entity.Category.CategoryType;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

class CategoryTest extends IntegrationTest {

    @Nested
    class 디비_동일성 {

        @Test
        void 카테고리가_DB와_동일한_값을_가지는지_확인한다() {
            CategoryType[] categoryTypes = CategoryType.values();

            List<Category> categories = categoryRepository.findAll(Sort.by(Direction.ASC, "id"));

            for (int i = 0; i < categories.size(); i++) {
                assertThat(categories.get(i).getCategoryType()).isEqualTo(categoryTypes[i]);
                assertThat(categories.get(i).getId()).isEqualTo(categoryTypes[i].getId());
            }
        }
    }
}
