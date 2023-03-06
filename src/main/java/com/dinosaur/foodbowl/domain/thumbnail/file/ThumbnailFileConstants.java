package com.dinosaur.foodbowl.domain.thumbnail.file;

import static java.io.File.separator;

import org.springframework.core.io.ClassPathResource;

class ThumbnailFileConstants {

    public static final String ROOT_PATH = "static";
    public static final String RESOURCE_PATH =
            new ClassPathResource(ROOT_PATH).getPath() + separator;
    public static final String DEFAULT_THUMBNAIL_PATH = RESOURCE_PATH + "thumbnail" + separator;
}
