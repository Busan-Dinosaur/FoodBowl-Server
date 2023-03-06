package com.dinosaur.foodbowl.domain.photo.application.file;

import static java.io.File.separator;

import org.springframework.core.io.ClassPathResource;

public class PhotoFileConstants {

    static final String ROOT_PATH = "static";
    static final String RESOURCE_PATH = new ClassPathResource(ROOT_PATH).getPath() + separator;
    static final String DEFAULT_PHOTO_PATH = RESOURCE_PATH + "photo" + separator;
}
