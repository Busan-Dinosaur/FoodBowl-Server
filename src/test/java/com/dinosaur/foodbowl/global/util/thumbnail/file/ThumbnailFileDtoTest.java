package com.dinosaur.foodbowl.global.util.thumbnail.file;

import static com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail.MAX_PATH_LENGTH;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ThumbnailFileDtoTest {

  @Nested
  class from {

    @Test
    void should_throwException_when_tooLongFileNameLength() throws IOException {
      final MockMultipartFile tooLongFileNameMultipartFile = new MockMultipartFile("image",
          "a".repeat(MAX_PATH_LENGTH) + ".png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_210x210.png"));
      assertThatThrownBy(() -> ThumbnailFileDto.from(tooLongFileNameMultipartFile))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_notImageMultipartFile() throws IOException {
      final MockMultipartFile fakeImageFile = new MockMultipartFile("image",
          "fakeImage.png", "image/png",
          new FileInputStream("src/test/resources/images/fakeImage.png"));
      assertThatThrownBy(() -> ThumbnailFileDto.from(fakeImageFile))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }
}