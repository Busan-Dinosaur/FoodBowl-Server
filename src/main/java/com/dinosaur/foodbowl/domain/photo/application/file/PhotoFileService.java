package com.dinosaur.foodbowl.domain.photo.application.file;

import static com.dinosaur.foodbowl.domain.photo.application.file.PhotoFileConstants.DEFAULT_PHOTO_PATH;
import static com.dinosaur.foodbowl.domain.photo.application.file.PhotoFileConstants.ROOT_PATH;
import static com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail.MAX_PATH_LENGTH;
import static com.dinosaur.foodbowl.global.error.ErrorCode.PHOTO_FILE_READ_FAIL;
import static com.dinosaur.foodbowl.global.error.ErrorCode.PHOTO_NOT_EXISTS;
import static com.dinosaur.foodbowl.global.error.ErrorCode.PHOTO_NOT_IMAGE_FILE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.PHOTO_NULL_IMAGE_FILE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.PHOTO_POST_NOT_FOUND;
import static java.io.File.separator;

import com.dinosaur.foodbowl.domain.photo.application.PhotoService;
import com.dinosaur.foodbowl.domain.photo.dao.PhotoRepository;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.global.error.BusinessException;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PhotoFileService extends PhotoService {

  private final PhotoRepository photoRepository;

  private final PostRepository postRepository;

  static {
    createDirectoryWhenIsNotExist(DEFAULT_PHOTO_PATH);
  }

  @Override
  public Photo save(MultipartFile file, Post post) {
    checkImageFile(file);
    checkPost(post);
    String fileFullPath = generateFileFullPath(file);
    checkPhotoFullPathLength(fileFullPath, file.getOriginalFilename());

    try {
      file.transferTo(new File(fileFullPath));
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("이미지 저장에 실패하였습니다. 파일명: " + file.getOriginalFilename());
    }

    return photoRepository.save(Photo.builder()
        .post(post)
        .path(getPhotoURI(fileFullPath))
        .build());
  }

  private void checkPost(Post post) {
    if (!postRepository.existsById(post.getId())) {
      throw new BusinessException(post, "post", PHOTO_POST_NOT_FOUND);
    }
  }

  private static void checkImageFile(MultipartFile file) {
    if (isNotImageFile(file)) {
      throw new BusinessException(file, "photo", PHOTO_NOT_IMAGE_FILE);
    }
  }

  private static boolean isNotImageFile(MultipartFile file) {
    try (InputStream originalInputStream = new BufferedInputStream(file.getInputStream())) {
      return ImageIO.read(originalInputStream) == null;
    } catch (IOException e) {
      throw new BusinessException(file, "photo", PHOTO_FILE_READ_FAIL);
    } catch (NullPointerException e) {
      throw new BusinessException(file, "photo", PHOTO_NULL_IMAGE_FILE);
    }
  }

  private static void checkPhotoFullPathLength(String photoFullPath, String fileName) {
    if (photoFullPath.length() > MAX_PATH_LENGTH) {
      throw new BusinessException(fileName, "fileName", HttpStatus.BAD_REQUEST,
          "파일 이름 길이가 너무 깁니다. 가능한 파일 이름 길이: " + (MAX_PATH_LENGTH - photoFullPath.length()
              + fileName.length()));
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

  private String getPhotoURI(String photoFullPath) {
    String photoURI = photoFullPath;
    if (photoFullPath.startsWith(ROOT_PATH)) {
      photoURI = photoFullPath.substring(ROOT_PATH.length());
    }
    return photoURI;
  }

  @Override
  public void delete(@NonNull Photo photo) {
    deletePhotoIfExists(photo);
    deleteFileIfExists(photo);
  }

  public void deletePhotoIfExists(Photo photo) {
    if (!photoRepository.existsById(photo.getId())) {
      throw new BusinessException(photo.getPath(), "photo path", PHOTO_NOT_EXISTS);
    }
    photoRepository.delete(photo);
  }

  private void deleteFileIfExists(Photo photo) {
    try {
      Files.deleteIfExists(Path.of(ROOT_PATH + photo.getPath()));
    } catch (IOException e) {
      throw new RuntimeException("파일 삭제 도중 문제가 발생했습니다. Photo path: " + photo.getPath());
    }
  }

}
