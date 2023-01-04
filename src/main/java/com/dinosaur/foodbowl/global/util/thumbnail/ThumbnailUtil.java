package com.dinosaur.foodbowl.global.util.thumbnail;

import static com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailType.DEFAULT;

import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThumbnailUtil {

  private final ThumbnailRepository thumbnailRepository;

  public Optional<Thumbnail> save(MultipartFile multipartFile) {
    return this.save(multipartFile, DEFAULT);
  }

  public Optional<Thumbnail> save(MultipartFile multipartFile, ThumbnailType type) {
    try {
      return trySave(multipartFile, type);
    } catch (IllegalArgumentException | IOException e) {
      log.warn("썸네일을 저장하는 도중 오류가 발생하였습니다. 파일명: {}", multipartFile.getOriginalFilename(), e);
      return Optional.empty();
    }
  }

  private Optional<Thumbnail> trySave(MultipartFile file, ThumbnailType type) throws IOException {
    ThumbnailInfoDto thumbnail = ThumbnailInfoDto.from(file);
    tryResizingAndSave(thumbnail, type);
    return Optional.of(saveThumbnailEntity(thumbnail.getFullPath()));
  }

  private static void tryResizingAndSave(ThumbnailInfoDto thumbnailInfoDto, ThumbnailType type)
      throws IOException {
    BufferedImage bufferedImage = ImageIO.read(thumbnailInfoDto.getOriginalInputStream());
    BufferedImage resizingBufferedImage = type.resizing(bufferedImage);
    File thumbnail = new File(thumbnailInfoDto.getFullPath());
    ImageIO.write(resizingBufferedImage, "jpeg", thumbnail);
  }

  private Thumbnail saveThumbnailEntity(String thumbnailFullPath) {
    return thumbnailRepository.save(Thumbnail.builder()
        .path(thumbnailFullPath)
        .build());
  }
}
