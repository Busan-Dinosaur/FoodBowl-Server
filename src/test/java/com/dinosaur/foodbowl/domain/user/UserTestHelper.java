package com.dinosaur.foodbowl.domain.user;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_LOGIN_ID_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_NICKNAME_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_PASSWORD_LENGTH;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailTestHelper;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTestHelper {

  @Autowired
  UserRepository userRepository;

  @Autowired
  ThumbnailTestHelper thumbnailTestHelper;

  public User generateUser() {
    return generateUser(thumbnailTestHelper.generateThumbnail());
  }

  public User generateUserWithoutThumbnail() {
    return generateUser(null);
  }

  private User generateUser(Thumbnail thumbnail) {
    User userWithThumbnail = User.builder()
        .loginId(getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH))
        .nickname(getRandomUUIDLengthWith(MAX_NICKNAME_LENGTH))
        .password(getRandomUUIDLengthWith(MAX_PASSWORD_LENGTH))
        .introduce(getRandomUUIDLengthWith(MAX_INTRODUCE_LENGTH))
        .thumbnail(thumbnail)
        .build();
    return userRepository.save(userWithThumbnail);
  }

  private static String getRandomUUIDLengthWith(int length) {
    String randomString = UUID.randomUUID()
        .toString();
    length = Math.min(length, randomString.length());
    return randomString.substring(0, length);
  }
}
