package com.dinosaur.foodbowl.global.api;

import static java.io.File.separator;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.dinosaur.foodbowl.global.config.security.JwtTokenProvider;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * 상속받은 Controller에
 * {@link org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest @WebMvcTest} 어노테이션을
 * 붙여주어야 합니다.
 * <p>
 * Ex. {@code @WebMvcTest(UserController.class)}
 */
@ExtendWith({RestDocumentationExtension.class})
@MockBean(JpaMetamodelMappingContext.class)
@ContextConfiguration
public class ControllerTest {

  @TestConfiguration
  @ComponentScan(basePackageClasses = JwtTokenProvider.class)
  static class ContextConfiguration {

  }

  @Autowired
  protected JwtTokenProvider jwtTokenProvider;

  @Autowired
  protected WebApplicationContext webApplicationContext;

  protected MockMvc mockMvc;

  @BeforeEach
  protected void setUpAll(RestDocumentationContextProvider restDocumentationContextProvider) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilter(new CharacterEncodingFilter("UTF-8", true))
        .apply(springSecurity())
        .apply(documentationConfiguration(restDocumentationContextProvider)
            .operationPreprocessors()
            .withRequestDefaults(
                modifyUris().scheme("https").host("docs.api.com").removePort(), prettyPrint())
            .withResponseDefaults(prettyPrint())
        )
        .build();
  }

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