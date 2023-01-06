package com.dinosaur.foodbowl.global.util.thumbnail;

import static com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailConstants.ROOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ThumbnailFileUtilTest {

  @Autowired
  private ThumbnailFileUtil thumbnailFileUtil;

  @Nested
  class deleteTest {

    @Test
    void should_deleteSuccessfully_when_existFile() throws IOException {
      MockMultipartFile validMultipartFile = new MockMultipartFile("image",
          "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_210x210.png"));
      Thumbnail savedThumbnail = thumbnailFileUtil.save(validMultipartFile);

      assertThat(Files.exists(Path.of(ROOT_PATH + savedThumbnail.getPath()))).isTrue();

      thumbnailFileUtil.delete(savedThumbnail);

      assertThat(Files.exists(Path.of(ROOT_PATH + savedThumbnail.getPath()))).isFalse();
    }
  }

  @Nested
  class SaveTest {

    private final MockMultipartFile validMultipartFile = new MockMultipartFile("image",
        "testImage_210x210.png", "image/png",
        new FileInputStream("src/test/resources/images/testImage_210x210.png"));

    SaveTest() throws IOException {
    }

    @Test
    void should_saveSuccessfully_when_validMultipartFile() {
      Thumbnail result = thumbnailFileUtil.save(validMultipartFile);
      assertThat(result).isNotNull();
      deleteTestFile(result);
    }

    private void deleteTestFile(Thumbnail thumbnail) {
      File thumbnailFile = new File(ROOT_PATH + thumbnail.getPath());
      assertThat(thumbnailFile).exists();
      assertThat(thumbnailFile.delete()).isTrue();
    }
  }
}