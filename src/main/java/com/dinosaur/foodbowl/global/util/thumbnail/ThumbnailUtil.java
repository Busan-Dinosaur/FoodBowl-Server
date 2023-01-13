package com.dinosaur.foodbowl.global.util.thumbnail;

import static com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailType.DEFAULT;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public abstract class ThumbnailUtil {

  /**
   * @param thumbnail 이 {@code null}일 가능성이 있는 경우 이 메서드를 사용하시는 게 좋습니다.
   * @return {@link Thumbnail} if null, {@code Optional.empty()}
   */
  public Optional<Thumbnail> saveIfExist(MultipartFile thumbnail) {
    if (thumbnail == null) {
      return Optional.empty();
    }
    return Optional.of(save(thumbnail));
  }

  /**
   * 타입은 기본적으로 {@link  ThumbnailType#DEFAULT} 가 들어갑니다. 그 외에는 {@code @see}를 참고해주세요.
   *
   * @see ThumbnailFileUtil#save(MultipartFile, ThumbnailType)
   */
  public Thumbnail save(MultipartFile multipartFile) {
    return this.save(multipartFile, DEFAULT);
  }

  /**
   * @param multipartFile {@code @NotNull} 이미지 파일이어야 합니다.
   * @return 썸네일 저장에 성공할 경우 {@link Thumbnail} 엔티티를 반환합니다.
   * @throws IllegalArgumentException 이미지 파일이 아니거나 파일 이름의 길이가 너무 길 경우 발생합니다.
   * @throws IOException              `ThumbnailUtil` 자체에 문제가 있을 경우 발생합니다.
   */
  public abstract Thumbnail save(MultipartFile multipartFile, ThumbnailType type);

  /**
   * 저장된 썸네일 파일과 Entity 모두 삭제합니다.
   */
  public void deleteFileAndEntity(Thumbnail thumbnail) {
    deleteEntity(thumbnail);
    deleteFile(thumbnail);
  }

  protected abstract void deleteFile(Thumbnail thumbnail);

  protected abstract void deleteEntity(Thumbnail thumbnail);
}
