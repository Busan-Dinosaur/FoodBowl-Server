package com.dinosaur.foodbowl.domain.thumbnail.file;

import static com.dinosaur.foodbowl.domain.thumbnail.file.ThumbnailFileConstants.ROOT_PATH;

import com.dinosaur.foodbowl.domain.thumbnail.ThumbnailType;
import com.dinosaur.foodbowl.domain.thumbnail.ThumbnailUtil;
import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.thumbnail.exception.ThumbnailException;
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
  public Thumbnail save(MultipartFile multipartFile, ThumbnailType type) {
    try {
      return trySave(multipartFile, type);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (RuntimeException | IOException e) {
      String message = "썸네일을 저장하는 도중 오류가 발생하였습니다. 파일명: " + multipartFile.getOriginalFilename();
      log.warn(message, e);
      throw new ThumbnailException(message, e);
    }
  }

  private Thumbnail trySave(MultipartFile file, ThumbnailType type) throws IOException {
    ThumbnailFileDto thumbnail = ThumbnailFileDto.from(file);
    tryResizingAndSave(thumbnail, type);
    return saveThumbnailEntity(thumbnail.getFullPath(), type);
  }

  private static void tryResizingAndSave(ThumbnailFileDto thumbnailFileDto, ThumbnailType type)
      throws IOException {
    BufferedImage bufferedImage = ImageIO.read(thumbnailFileDto.getOriginalInputStream());
    BufferedImage resizingBufferedImage = type.resizing(bufferedImage);
    File thumbnail = new File(thumbnailFileDto.getFullPath());
    ImageIO.write(resizingBufferedImage, "jpeg", thumbnail);
  }

  private Thumbnail saveThumbnailEntity(String thumbnailFullPath, ThumbnailType type)
      throws IOException {
    try {
      return trySaveThumbnailEntity(thumbnailFullPath, type);
    } catch (RuntimeException e) {
      Files.deleteIfExists(Path.of(thumbnailFullPath));
      throw e;
    }
  }

  private Thumbnail trySaveThumbnailEntity(String thumbnailFullPath, ThumbnailType type) {
    String thumbnailURI = getThumbnailURI(thumbnailFullPath);
    return thumbnailRepository.save(Thumbnail.builder()
        .path(thumbnailURI)
        .width(type.getWidthPixel())
        .height(type.getHeightPixel())
        .build());
  }

  private String getThumbnailURI(String thumbnailFullPath) {
    String thumbnailURI = thumbnailFullPath;
    if (thumbnailFullPath.startsWith(ROOT_PATH)) {
      thumbnailURI = thumbnailFullPath.substring(ROOT_PATH.length());
    }
    return thumbnailURI;
  }

  @Override
  protected void deleteEntity(Thumbnail thumbnail) {
    thumbnailRepository.delete(thumbnail);
  }

  @Override
  protected void deleteFile(Thumbnail thumbnail) {
    String thumbnailFilePath = ROOT_PATH + thumbnail.getPath();
    try {
      Files.deleteIfExists(Path.of(thumbnailFilePath));
    } catch (IOException e) {
      throw new RuntimeException("파일 삭제 도중 문제가 발생했습니다. 파일 삭제 Entity: " + thumbnail);
    }
  }
}
