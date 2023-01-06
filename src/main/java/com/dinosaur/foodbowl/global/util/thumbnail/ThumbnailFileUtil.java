package com.dinosaur.foodbowl.global.util.thumbnail;

import static com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailConstants.ROOT_PATH;

import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThumbnailFileUtil extends ThumbnailUtil {

  private final ThumbnailRepository thumbnailRepository;

  @Override
  protected Thumbnail trySave(MultipartFile file, ThumbnailType type) throws IOException {
    ThumbnailInfoDto thumbnail = ThumbnailInfoDto.from(file);
    tryResizingAndSave(thumbnail, type);
    return saveThumbnailEntity(thumbnail.getFullPath());
  }

  private static void tryResizingAndSave(ThumbnailInfoDto thumbnailInfoDto, ThumbnailType type)
      throws IOException {
    BufferedImage bufferedImage = ImageIO.read(thumbnailInfoDto.getOriginalInputStream());
    BufferedImage resizingBufferedImage = type.resizing(bufferedImage);
    File thumbnail = new File(thumbnailInfoDto.getFullPath());
    ImageIO.write(resizingBufferedImage, "jpeg", thumbnail);
  }

  private Thumbnail saveThumbnailEntity(String thumbnailFullPath) throws IOException {
    try {
      return trySaveThumbnailEntity(thumbnailFullPath);
    } catch (RuntimeException e) {
      Files.deleteIfExists(Path.of(thumbnailFullPath));
      throw e;
    }
  }

  private Thumbnail trySaveThumbnailEntity(String thumbnailFullPath) {
    String thumbnailURI = getThumbnailURI(thumbnailFullPath);
    return thumbnailRepository.save(Thumbnail.builder()
        .path(thumbnailURI)
        .build());
  }

  private String getThumbnailURI(String thumbnailFullPath) {
    String thumbnailURI = thumbnailFullPath;
    if (thumbnailFullPath.startsWith(ROOT_PATH)) {
      thumbnailURI = thumbnailFullPath.substring(ROOT_PATH.length());
    }
    return thumbnailURI;
  }
}
