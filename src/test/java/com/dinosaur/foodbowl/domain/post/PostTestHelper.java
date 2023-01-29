package com.dinosaur.foodbowl.domain.post;

import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.StoreTestHelper;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import com.dinosaur.foodbowl.domain.thumbnail.ThumbnailTestHelper;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.UserTestHelper;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostTestHelper {

  @Autowired
  UserTestHelper userTestHelper;

  @Autowired
  ThumbnailTestHelper thumbnailTestHelper;

  @Autowired
  StoreTestHelper storeTestHelper;

  @Autowired
  PostRepository postRepository;

  private String getRandomUUIDLengthWith(int length) {
    String randomString = UUID.randomUUID()
        .toString();
    length = Math.min(length, randomString.length());
    return randomString.substring(0, length);
  }

  public PostBuilder builder() {
    return new PostBuilder();
  }

  public final class PostBuilder {

    private User user;
    private Thumbnail thumbnail;
    private Store store;
    private String content;

    private PostBuilder() {
    }

    public PostBuilder user(User user) {
      this.user = user;
      return this;
    }

    public PostBuilder thumbnail(Thumbnail thumbnail) {
      this.thumbnail = thumbnail;
      return this;
    }

    public PostBuilder store(Store store) {
      this.store = store;
      return this;
    }

    public PostBuilder content(String content) {
      this.content = content;
      return this;
    }

    public Post build() {
      return postRepository.save(Post.builder()
          .user(user != null ? user : userTestHelper.builder().build())
          .thumbnail(thumbnail != null ? thumbnail : thumbnailTestHelper.generateThumbnail())
          .store(store != null ? store : storeTestHelper.builder().build())
          .content(content != null ? content : getRandomUUIDLengthWith(100))
          .build());
    }
  }
}
