package com.dinosaur.foodbowl.global.util.thumbnail;

import static com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailConstants.DEFAULT_THUMBNAIL_PATH;
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
class ThumbnailInfoDto {

  static {
    createDirectoryWhenIsNotExist(DEFAULT_THUMBNAIL_PATH);
  }

  private final String fullPath;
  private final InputStream originalInputStream;

  static ThumbnailInfoDto from(MultipartFile file) throws IOException {
    String thumbnailFullPath = generateThumbnailFullPath(file);
    checkIsInvalidImageFile(file);
    InputStream inputStream = new BufferedInputStream(file.getInputStream());
    return new ThumbnailInfoDto(thumbnailFullPath, inputStream);
  }

  private static void checkIsInvalidImageFile(MultipartFile file) throws IOException {
    if (isNotImageFile(file)) {
      throw new IllegalArgumentException();
    }
  }

  private static boolean isNotImageFile(MultipartFile file) throws IOException {
    InputStream originalInputStream = new BufferedInputStream(file.getInputStream());
    return ImageIO.read(originalInputStream) == null;
  }

  private static String generateThumbnailFullPath(MultipartFile multipartFile) {
    LocalDate fileUploadDate = LocalDate.now();
    String thumbnailUploadPath = getThumbnailUploadPath(fileUploadDate);
    return generateFullPath(thumbnailUploadPath, multipartFile.getOriginalFilename());
  }

  private static String getThumbnailUploadPath(LocalDate fileUploadDate) {
    String thumbnailUploadPath = DEFAULT_THUMBNAIL_PATH + separator + fileUploadDate + separator;
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
    return thumbnailUploadPath + separator + getRandomThumbnailName(fileName);
  }

  private static String getRandomThumbnailName(String fileName) {
    return fileName + "_" + UUID.randomUUID();
  }
}
