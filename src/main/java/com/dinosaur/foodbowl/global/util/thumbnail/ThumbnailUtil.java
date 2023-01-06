package com.dinosaur.foodbowl.global.util.thumbnail;

import static com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailType.DEFAULT;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.global.util.thumbnail.exception.ThumbnailException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public abstract class ThumbnailUtil {

  /**
   * 타입은 기본적으로 {@link  ThumbnailType#DEFAULT} 가 들어갑니다. 그 외에는 {@code @see}를 참고해주세요.
   *
   * @see ThumbnailFileUtil#save(MultipartFile, ThumbnailType)
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
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (IOException e) {
      String message = "썸네일을 저장하는 도중 오류가 발생하였습니다. 파일명: " + multipartFile.getOriginalFilename();
      log.warn(message, e);
      throw new ThumbnailException(message, e);
    }
  }

  protected abstract Thumbnail trySave(MultipartFile file, ThumbnailType type) throws IOException;
}
