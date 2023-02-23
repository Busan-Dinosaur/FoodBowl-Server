package com.dinosaur.foodbowl.domain.photo.application;

import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

public abstract class PhotoService {

  public abstract Photo save(MultipartFile photoFile, Post post);

  public abstract List<Photo> saveAll(List<MultipartFile> photoFiles, Post post);

  public abstract void delete(@NonNull Photo photo);

}
