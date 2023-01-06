package com.dinosaur.foodbowl.global.util.thumbnail;

import static com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailConstants.ROOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ThumbnailUtilTest {

  @Autowired
  private ThumbnailUtil thumbnailUtil;

  @Nested
  class SaveTest {

    private final MockMultipartFile validMultipartFile = new MockMultipartFile("image",
        "testImage_210x210.png", "image/png",
        new FileInputStream("src/test/resources/images/testImage_210x210.png"));

    SaveTest() throws IOException {
    }

    @Test
    void should_saveSuccessfully_when_validMultipartFile() {
      Thumbnail result = thumbnailUtil.save(validMultipartFile);
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