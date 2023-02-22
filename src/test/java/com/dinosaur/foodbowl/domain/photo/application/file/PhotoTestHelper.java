package com.dinosaur.foodbowl.domain.photo.application.file;

import static com.dinosaur.foodbowl.domain.photo.application.file.PhotoFileConstants.ROOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.domain.photo.application.PhotoService;
import com.dinosaur.foodbowl.domain.photo.dao.PhotoRepository;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

@Component
public class PhotoTestHelper {

  @Autowired
  PhotoRepository photoRepository;
  @Autowired
  PostRepository postRepository;

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
}
