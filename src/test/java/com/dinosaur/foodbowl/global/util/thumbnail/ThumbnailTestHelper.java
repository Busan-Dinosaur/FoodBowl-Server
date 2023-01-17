package com.dinosaur.foodbowl.global.util.thumbnail;

import static java.io.File.separator;

import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.global.util.thumbnail.file.ThumbnailFileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
