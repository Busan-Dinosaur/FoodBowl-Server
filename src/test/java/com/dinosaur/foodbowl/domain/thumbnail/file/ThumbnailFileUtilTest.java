package com.dinosaur.foodbowl.domain.thumbnail.file;

import static com.dinosaur.foodbowl.domain.thumbnail.file.ThumbnailFileConstants.ROOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
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
import org.springframework.mock.web.MockMultipartFile;

class ThumbnailFileUtilTest extends IntegrationTest {

  @Nested
  class 썸네일_싹제 {

    @Test
    void 썸네일이_존재하면_삭제에_성공한다() throws IOException {
      MockMultipartFile validMultipartFile = new MockMultipartFile("image",
          "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_210x210.png"));
      Thumbnail savedThumbnail = thumbnailFileUtil.saveIfExist(validMultipartFile).orElseThrow();

      assertThat(Files.exists(Path.of(ROOT_PATH + savedThumbnail.getPath()))).isTrue();

      thumbnailFileUtil.deleteFileAndEntity(savedThumbnail);

      assertThat(Files.exists(Path.of(ROOT_PATH + savedThumbnail.getPath()))).isFalse();
    }
  }

  @Nested
  class 썸네일_저장 {

    private final MockMultipartFile validMultipartFile = new MockMultipartFile("image",
        "testImage_210x210.png", "image/png",
        new FileInputStream("src/test/resources/images/testImage_210x210.png"));

    썸네일_저장() throws IOException {
    }

    @Test
    void 썸네일_파일이_유효하면_저장에_성공한다() {
      Thumbnail result = thumbnailFileUtil.saveIfExist(validMultipartFile).orElseThrow();
      assertThat(result).isNotNull();
      deleteTestFile(result);
    }

    @Test
    void 썸네일_파일이_유효하면_사이즈_변환에_성공한다() throws IOException {
      Thumbnail savedThumbnailEntity = thumbnailFileUtil.saveIfExist(validMultipartFile)
          .orElseThrow();
      File result = new File(ROOT_PATH + savedThumbnailEntity.getPath());

      assertThat(result).exists();
      BufferedImage image = ImageIO.read(result);
      assertThat(image).isNotNull();
      assertThat(image.getHeight()).isEqualTo(200);
      assertThat(image.getWidth()).isEqualTo(200);

      deleteTestFile(savedThumbnailEntity);
    }

    @Test
    void 썸네일_저장_시_타입이_null_이면_예외가_발생한다() {
      assertThatThrownBy(() -> thumbnailFileUtil.saveIfExist(null, null))
          .isInstanceOf(NullPointerException.class);
      assertThatThrownBy(() -> thumbnailFileUtil.saveIfExist(validMultipartFile, null))
          .isInstanceOf(NullPointerException.class);
      assertThatThrownBy(() -> thumbnailFileUtil.deleteFileAndEntity(null))
          .isInstanceOf(NullPointerException.class);
    }

    private void deleteTestFile(Thumbnail thumbnail) {
      File thumbnailFile = new File(ROOT_PATH + thumbnail.getPath());
      assertThat(thumbnailFile).exists();
      assertThat(thumbnailFile.delete()).isTrue();
    }
  }
}
