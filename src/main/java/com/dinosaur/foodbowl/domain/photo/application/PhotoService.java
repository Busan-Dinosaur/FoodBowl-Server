package com.dinosaur.foodbowl.domain.photo.application;

import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import java.io.IOException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public abstract class PhotoService {

  public abstract Photo save(MultipartFile photoFile, Post post) throws IOException;

  public abstract void delete(@NonNull Photo photo);

}
