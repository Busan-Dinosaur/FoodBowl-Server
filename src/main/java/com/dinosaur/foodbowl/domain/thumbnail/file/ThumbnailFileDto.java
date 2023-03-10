package com.dinosaur.foodbowl.domain.thumbnail.file;

import static com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail.MAX_PATH_LENGTH;
import static com.dinosaur.foodbowl.domain.thumbnail.file.ThumbnailFileConstants.DEFAULT_THUMBNAIL_PATH;
import static java.io.File.separator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class ThumbnailFileDto {

  static {
    createDirectoryWhenIsNotExist(DEFAULT_THUMBNAIL_PATH);
  }

  private final String fullPath;
  private final InputStream originalInputStream;

  static ThumbnailFileDto from(MultipartFile file) throws IOException {
    String thumbnailFullPath = generateThumbnailFullPath(file);
    checkThumbnailFullPathLength(thumbnailFullPath, file.getOriginalFilename());
    checkInvalidImageFile(file);
    InputStream inputStream = new BufferedInputStream(file.getInputStream());
    return new ThumbnailFileDto(thumbnailFullPath, inputStream);
  }

  private static void checkThumbnailFullPathLength(String thumbnailFullPath, String fileName) {
    if (thumbnailFullPath.length() > MAX_PATH_LENGTH) {
      throw new IllegalArgumentException("파일 이름 길이가 너무 깁니다. 가능한 파일 이름 길이: " +
          (MAX_PATH_LENGTH - thumbnailFullPath.length() + fileName.length()));
    }
  }

  private static void checkInvalidImageFile(MultipartFile file) throws IOException {
    if (isNotImageFile(file)) {
      throw new IllegalArgumentException("파일이 이미지가 아닙니다. 파일 이름: " + file.getOriginalFilename());
    }
  }

  private static boolean isNotImageFile(MultipartFile file) throws IOException {
    InputStream originalInputStream = new BufferedInputStream(file.getInputStream());
    return ImageIO.read(originalInputStream) == null;
  }

  private static String generateThumbnailFullPath(MultipartFile multipartFile) {
    LocalDate fileUploadDate = LocalDate.now();
    String thumbnailUploadPath = getThumbnailUploadPath(fileUploadDate);
    String fileExt = ".jpeg";
    return generateFullPath(thumbnailUploadPath, multipartFile.getOriginalFilename()) + fileExt;
  }

  private static String getThumbnailUploadPath(LocalDate fileUploadDate) {
    String thumbnailUploadPath = DEFAULT_THUMBNAIL_PATH + fileUploadDate + separator;
    createDirectoryWhenIsNotExist(thumbnailUploadPath);
    return thumbnailUploadPath;
  }

  private static void createDirectoryWhenIsNotExist(String path) {
    try {
      Files.createDirectories(Paths.get(path));
    } catch (IOException ignore) {
    }
  }

  private static String generateFullPath(String thumbnailUploadPath, String fileName) {
    return thumbnailUploadPath + getRandomThumbnailName(fileName);
  }

  private static String getRandomThumbnailName(String fileName) {
    return fileName + "_" + UUID.randomUUID();
  }
}
