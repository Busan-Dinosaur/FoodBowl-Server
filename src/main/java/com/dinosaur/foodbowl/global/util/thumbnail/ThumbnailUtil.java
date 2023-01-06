package com.dinosaur.foodbowl.global.util.thumbnail;

import static com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailConstants.ROOT_PATH;
import static com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailType.DEFAULT;

import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.global.util.thumbnail.exception.ThumbnailException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

  /**
   * 타입은 기본적으로 {@link  ThumbnailType#DEFAULT} 가 들어갑니다. 그 외에는 {@code @see}를 참고해주세요.
   *
   * @see ThumbnailUtil#save(MultipartFile, ThumbnailType)
   */
  public Thumbnail save(MultipartFile multipartFile) {
    return this.save(multipartFile, DEFAULT);
  }

  /**
   * @param multipartFile 이미지 파일이어야 합니다.
   * @return 썸네일 저장에 성공할 경우 {@link Thumbnail} 엔티티를 반환합니다.
   * @throws IllegalArgumentException 이미지 파일이 아니거나 파일 이름의 길이가 너무 길 경우 발생합니다.
   * @throws IOException              `ThumbnailUtil` 자체에 문제가 있을 경우 발생합니다.
   */
  public Thumbnail save(MultipartFile multipartFile, ThumbnailType type) {
    try {
      return trySave(multipartFile, type);
    } catch (IOException e) {
      String exceptionMessage = "썸네일을 저장하는 도중 오류가 발생하였습니다. 파일명: "
          + multipartFile.getOriginalFilename();
      log.warn(exceptionMessage, e);
      throw new ThumbnailException(exceptionMessage, e);
    }
  }

  private Thumbnail trySave(MultipartFile file, ThumbnailType type) throws IOException {
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

  private Thumbnail saveThumbnailEntity(String thumbnailFullPath) {
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
