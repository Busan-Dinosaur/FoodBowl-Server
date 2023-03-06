package com.dinosaur.foodbowl.domain.post;

import com.dinosaur.foodbowl.domain.category.CategoryTestHelper;
import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.domain.post.dao.PostCategoryRepository;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.post.entity.PostCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostCategoryTestHelper {

    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Autowired
    private PostTestHelper postTestHelper;

    @Autowired
    private CategoryTestHelper categoryTestHelper;

    public PostCategoryBuilder builder() {
        return new PostCategoryBuilder();
    }

    public final class PostCategoryBuilder {

        private Post post;
        private Category category;

        private PostCategoryBuilder() {
        }

        public PostCategoryBuilder post(Post post) {
            this.post = post;
            return this;
        }

        public PostCategoryBuilder category(Category category) {
            this.category = category;
            return this;
        }

        public PostCategory build() {
            return postCategoryRepository.save(PostCategory.builder()
                    .post(post != null ? post : postTestHelper.builder().build())
                    .category(category != null ?
                            category : categoryTestHelper.generateRandomCategory()
                    )
                    .build());
        }
    }
}
