package com.dinosaur.foodbowl.domain.photo.application.file;

import static com.dinosaur.foodbowl.domain.photo.application.file.PhotoFileConstants.ROOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.domain.photo.application.PhotoService;
import com.dinosaur.foodbowl.domain.photo.dao.PhotoRepository;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import java.io.File;
import com.dinosaur.foodbowl.domain.post.PostTestHelper;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

@Component
public class PhotoTestHelper {

  @Autowired
  PhotoRepository photoRepository;

  @Autowired
  PostRepository postRepository;

  @Autowired
  private PostTestHelper postTestHelper;

  public MockMultipartFile getImageFile() {
    try {
      return new MockMultipartFile("images", "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public MockMultipartFile getFakeImageFile() {
    try {
      return new MockMultipartFile("images", "fakeImage.png", "image/png",
          new FileInputStream("src/test/resources/images/fakeImage.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public MockMultipartFile getTooLongNameImageFile(int length) {
    try {
      return new MockMultipartFile("images", "a".repeat(length) + ".png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Photo generatePhoto(Post post) {
    final PhotoService photoService = new PhotoFileService(photoRepository, postRepository);
    return photoService.save(getImageFile(), post);
  }

  public void deleteTestFile(Photo photo) {
    File photoFile = new File(ROOT_PATH + photo.getPath());
    assertThat(photoFile).exists();
    assertThat(photoFile.delete()).isTrue();
  }

  public PhotoBuilder builder() {
    return new PhotoBuilder();
  }

  private String getRandomUUIDLengthWith(int length) {
    String randomString = UUID.randomUUID()
        .toString();
    length = Math.min(length, randomString.length());
    return randomString.substring(0, length);
  }

  public final class PhotoBuilder {

    private Post post;
    private String path;

    private PhotoBuilder() {
    }

    public PhotoBuilder post(Post post) {
      this.post = post;
      return this;
    }

    public PhotoBuilder path(String path) {
      this.path = path;
      return this;
    }

    public Photo build() {
      return photoRepository.save(Photo.builder()
          .post(post != null ? post : postTestHelper.builder().build())
          .path(path != null ? path : getRandomUUIDLengthWith(Photo.MAX_PATH_LENGTH))
          .build());
    }
  }
}
