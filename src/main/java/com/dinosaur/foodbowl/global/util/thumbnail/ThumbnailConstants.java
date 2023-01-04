package com.dinosaur.foodbowl.global.util.thumbnail;

import static java.io.File.separator;

import org.springframework.core.io.ClassPathResource;

class ThumbnailConstants {

  public static final String RESOURCE_PATH = new ClassPathResource("static").getPath() + separator;
  public static final String DEFAULT_THUMBNAIL_PATH = RESOURCE_PATH + "thumbnail" + separator;
}
