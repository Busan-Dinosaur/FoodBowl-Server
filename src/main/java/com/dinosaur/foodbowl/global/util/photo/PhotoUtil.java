package com.dinosaur.foodbowl.global.util.photo;

import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public abstract class PhotoUtil {

  public abstract List<Photo> save(List<MultipartFile> files);
}
