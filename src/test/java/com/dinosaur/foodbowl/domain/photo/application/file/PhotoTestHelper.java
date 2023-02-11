package com.dinosaur.foodbowl.domain.photo.application.file;

import com.dinosaur.foodbowl.domain.photo.dao.PhotoRepository;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

@Component
public class PhotoTestHelper {

  @Autowired
  PhotoRepository photoRepository;

  public MockMultipartFile getImageFile() {
    try {
      return new MockMultipartFile("photo", "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public MockMultipartFile getFakeImageFile() {
    try {
      return new MockMultipartFile("photo", "fakeImage.png", "image/png",
          new FileInputStream("src/test/resources/images/fakeImage.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public MockMultipartFile getTooLongNameImageFile(int length) {
    try {
      return new MockMultipartFile("photo", "a".repeat(length) + ".png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
