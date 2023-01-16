package com.dinosaur.foodbowl.domain.user;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_LOGIN_ID_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_NICKNAME_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_PASSWORD_LENGTH;
import static java.io.File.separator;

import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailUtil;
import com.dinosaur.foodbowl.global.util.thumbnail.file.ThumbnailFileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

@Component
public class UserTestHelper {

  @Autowired
  UserRepository userRepository;

  @Autowired
  ThumbnailRepository thumbnailRepository;

  public User generateUser() {
    return generateUser(generateThumbnail());
  }

  public Thumbnail generateThumbnail() {
    final ThumbnailUtil thumbnailUtil = new ThumbnailFileUtil(thumbnailRepository);
    return thumbnailUtil.saveIfExist(getThumbnailFile()).orElseThrow();
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

  private static MockMultipartFile getThumbnailFile() {
    try {
      return new MockMultipartFile("thumbnail",
          "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void deleteAllThumbnails() {
    try {
      FileUtils.cleanDirectory(new File(getTodayThumbnailFilesPath()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getTodayThumbnailFilesPath() {
    return new ClassPathResource("static").getPath() + separator +
        "thumbnail" + separator +
        LocalDate.now();
  }
}
