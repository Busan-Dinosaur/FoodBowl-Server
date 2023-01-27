package com.dinosaur.foodbowl.domain.thumbnail;

import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.thumbnail.file.ThumbnailFileUtil;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

@Component
public class ThumbnailTestHelper {

  @Autowired
  ThumbnailRepository thumbnailRepository;

  public Thumbnail generateThumbnail() {
    final ThumbnailUtil thumbnailUtil = new ThumbnailFileUtil(thumbnailRepository);
    return thumbnailUtil.saveIfExist(getThumbnailFile()).orElseThrow();
  }

  public MockMultipartFile getThumbnailFile() {
    try {
      return new MockMultipartFile("thumbnail",
          "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public MockMultipartFile getFakeImageFile() {
    try {
      return new MockMultipartFile("thumbnail",
          "fakeImage.png", "image/png",
          new FileInputStream("src/test/resources/images/fakeImage.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
