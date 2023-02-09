package com.dinosaur.foodbowl.domain.clip;

import com.dinosaur.foodbowl.domain.clip.dao.ClipRepository;
import com.dinosaur.foodbowl.domain.clip.entity.Clip;
import com.dinosaur.foodbowl.domain.post.PostTestHelper;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.UserTestHelper;
import com.dinosaur.foodbowl.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClipTestHelper {

  @Autowired
  private ClipRepository clipRepository;

  @Autowired
  private UserTestHelper userTestHelper;

  @Autowired
  private PostTestHelper postTestHelper;

  public ClipBuilder builder() {
    return new ClipBuilder();
  }

  public final class ClipBuilder {

    private User user;
    private Post post;

    public ClipBuilder user(User user) {
      this.user = user;
      return this;
    }

    public ClipBuilder post(Post post) {
      this.post = post;
      return this;
    }

    public Clip build() {
      return clipRepository.save(Clip.builder()
          .user(user != null ? user : userTestHelper.builder().build())
          .post(post != null ? post : postTestHelper.builder().build())
          .build());
    }
  }
}
