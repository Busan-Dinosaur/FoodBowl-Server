package com.dinosaur.foodbowl.domain.photo.application;

import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import java.io.IOException;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

public abstract class PhotoService {

  public abstract Photo save(MultipartFile photoFile, Post post) throws IOException;

  public abstract void delete(@NonNull Photo photo);

}
