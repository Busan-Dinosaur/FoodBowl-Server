package com.dinosaur.foodbowl.domain.category;

import com.dinosaur.foodbowl.domain.category.dao.CategoryRepository;
import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.domain.category.entity.Category.CategoryType;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryTestHelper {

  private static final Random random = new Random();

  @Autowired
  private CategoryRepository categoryRepository;

  public Category generateRandomCategory() {
    int choiceIndex = random.nextInt(CategoryType.values().length);
    long choiceCategoryId = CategoryType.values()[choiceIndex].getId();
    return categoryRepository.findById(choiceCategoryId).orElseThrow();
  }
}
