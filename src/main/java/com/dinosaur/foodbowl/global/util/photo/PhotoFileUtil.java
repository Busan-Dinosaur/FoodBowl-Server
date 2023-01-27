package com.dinosaur.foodbowl.global.util.photo;

import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailType;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PhotoFileUtil extends PhotoUtil {

  @Override
  public List<Photo> save(List<MultipartFile> files) {
    return Collections.emptyList();
  }
}
