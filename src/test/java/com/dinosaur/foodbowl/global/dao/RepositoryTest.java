package com.dinosaur.foodbowl.global.dao;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoryTest {

  @AfterAll
  static void deleteAllThumbnails() throws IOException {
    FileUtils.cleanDirectory(new File(getTodayThumbnailFilesPath()));
  }

  private static String getTodayThumbnailFilesPath() {
    return new ClassPathResource("static").getPath() + separator +
        "thumbnail" + separator +
        LocalDate.now();
  }
}
