package com.dinosaur.foodbowl.global.util.thumbnail.file;

import static com.dinosaur.foodbowl.global.util.thumbnail.file.ThumbnailFileConstants.ROOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DisplayName;
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

      thumbnailFileUtil.deleteFileAndEntity(savedThumbnail);

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

    @Test
    void should_resizingWell_when_validMultipartFile() throws IOException {
      Thumbnail savedThumbnailEntity = thumbnailFileUtil.save(validMultipartFile);
      File result = new File(ROOT_PATH + savedThumbnailEntity.getPath());

      assertThat(result).exists();
      BufferedImage image = ImageIO.read(result);
      assertThat(image).isNotNull();
      assertThat(image.getHeight()).isEqualTo(200);
      assertThat(image.getWidth()).isEqualTo(200);

      deleteTestFile(savedThumbnailEntity);
    }

    @Test
    @DisplayName("썸네일 저장 시 파라미터가 null이면 NullPointerException을 발생시킨다.")
    void should_throwNullPointerException_when_parameterIsNull() {
      assertThatThrownBy(() -> thumbnailFileUtil.save(null))
          .isInstanceOf(NullPointerException.class);
      assertThatThrownBy(() -> thumbnailFileUtil.save(null, null))
          .isInstanceOf(NullPointerException.class);
      assertThatThrownBy(() -> thumbnailFileUtil.saveIfExist(validMultipartFile, null))
          .isInstanceOf(NullPointerException.class);
      assertThatThrownBy(() -> thumbnailFileUtil.deleteFileAndEntity(null))
          .isInstanceOf(NullPointerException.class);
    }
  }
}