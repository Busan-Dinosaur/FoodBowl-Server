package com.dinosaur.foodbowl.domain.photo.application.file;

import static com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail.MAX_PATH_LENGTH;
import static java.io.File.separator;

import com.dinosaur.foodbowl.domain.photo.application.PhotoService;
import com.dinosaur.foodbowl.domain.photo.dao.PhotoRepository;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class PhotoFileService extends PhotoService {

  private static final String ROOT_PATH = "static";
  private static final String RESOURCE_PATH =
      new ClassPathResource(ROOT_PATH).getPath() + separator;
  private static final String DEFAULT_PHOTO_PATH = RESOURCE_PATH + "photo" + separator;
  private final PhotoRepository photoRepository;

  static {
    createDirectoryWhenIsNotExist(DEFAULT_PHOTO_PATH);
  }

  @Override
  public Photo save(MultipartFile file, Post post) throws IOException {
    checkImageFile(file);

    String fileFullPath = generateFileFullPath(file);
    checkPhotoFullPathLength(fileFullPath, file.getOriginalFilename());

    file.transferTo(new File(fileFullPath));

    return photoRepository.save(Photo.builder()
        .post(post)
        .path(fileFullPath)
        .build());
  }

  private static void checkImageFile(MultipartFile file) throws IOException {
    if (isNotImageFile(file)) {
      throw new IllegalArgumentException("파일이 이미지가 아닙니다. 파일 이름: " + file.getOriginalFilename());
    }
  }

  private static boolean isNotImageFile(MultipartFile file) throws IOException {
    InputStream originalInputStream = new BufferedInputStream(file.getInputStream());
    return ImageIO.read(originalInputStream) == null;
  }

  private static void checkPhotoFullPathLength(String photoFullPath, String fileName) {
    if (photoFullPath.length() > MAX_PATH_LENGTH) {
      throw new IllegalArgumentException("파일 이름 길이가 너무 깁니다. 가능한 파일 이름 길이: " +
          (MAX_PATH_LENGTH - photoFullPath.length() + fileName.length()));
    }
  }

  private static String generateFileFullPath(MultipartFile file) {
    String photoUploadPath = DEFAULT_PHOTO_PATH + LocalDate.now() + separator;
    createDirectoryWhenIsNotExist(photoUploadPath);

    return photoUploadPath + UUID.randomUUID() + "_" + file.getOriginalFilename();
  }

  private static void createDirectoryWhenIsNotExist(String path) {
    try {
      Files.createDirectories(Paths.get(path));
    } catch (IOException ignore) {
    }
  }

  @Override
  public void delete(Photo photo) {
    photoRepository.delete(photo);
    try {
      Files.deleteIfExists(Path.of(ROOT_PATH + photo.getPath()));
    } catch (IOException e) {
      throw new RuntimeException("파일 삭제 도중 문제가 발생했습니다. 파일 삭제 Entity: " + photo);
    }
  }
}
