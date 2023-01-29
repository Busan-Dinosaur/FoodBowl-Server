package com.dinosaur.foodbowl.domain.photo;

import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

@Component
public class PhotoTestHelper {

  public MockMultipartFile getPhotoFile() {
    try {
      return new MockMultipartFile("images",
          "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
