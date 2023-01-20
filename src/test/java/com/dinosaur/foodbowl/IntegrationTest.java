package com.dinosaur.foodbowl;

import static java.io.File.separator;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.dinosaur.foodbowl.domain.category.dao.CategoryRepository;
import com.dinosaur.foodbowl.domain.follow.application.FollowService;
import com.dinosaur.foodbowl.domain.follow.dao.FollowRepository;
import com.dinosaur.foodbowl.domain.post.PostTestHelper;
import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.user.UserTestHelper;
import com.dinosaur.foodbowl.domain.user.application.DeleteAccountService;
import com.dinosaur.foodbowl.domain.user.application.GetProfileService;
import com.dinosaur.foodbowl.domain.user.application.UpdateProfileService;
import com.dinosaur.foodbowl.domain.user.application.signup.SignUpService;
import com.dinosaur.foodbowl.domain.user.dao.RoleRepository;
import com.dinosaur.foodbowl.domain.user.dao.UserFindDao;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.dao.UserRoleRepository;
import com.dinosaur.foodbowl.global.config.security.JwtTokenProvider;
import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailTestHelper;
import com.dinosaur.foodbowl.global.util.thumbnail.file.ThumbnailFileUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith({RestDocumentationExtension.class})
@Transactional
@SpringBootTest
public class IntegrationTest {

  /******* Repository *******/
  @SpyBean
  protected RoleRepository roleRepository;

  @SpyBean
  protected UserRepository userRepository;

  @SpyBean
  protected ThumbnailRepository thumbnailRepository;

  @SpyBean
  protected UserRoleRepository userRoleRepository;

  @SpyBean
  protected FollowRepository followRepository;

  @SpyBean
  protected CategoryRepository categoryRepository;

  @SpyBean
  protected UserFindDao userFindDao;

  /******* Service *******/
  @SpyBean
  protected GetProfileService getProfileService;

  @SpyBean
  protected SignUpService signUpService;

  @SpyBean
  protected DeleteAccountService deleteAccountService;

  @SpyBean
  protected UpdateProfileService updateProfileService;

  @SpyBean
  protected FollowService followService;

  /******* Helper *******/
  @SpyBean
  protected UserTestHelper userTestHelper;

  @SpyBean
  protected ThumbnailTestHelper thumbnailTestHelper;

  @SpyBean
  protected PostTestHelper postTestHelper;

  /******* Util *******/
  @SpyBean
  protected ThumbnailFileUtil thumbnailFileUtil;

  @SpyBean
  protected AuthUtil authUtil;

  /******* Spring Bean *******/
  @Autowired
  protected WebApplicationContext webApplicationContext;

  @Autowired
  protected JwtTokenProvider jwtTokenProvider;

  @PersistenceContext
  protected EntityManager em;

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
    File todayThumbnailDirectory = new File(getTodayThumbnailFilesPath());
    if (todayThumbnailDirectory.exists()) {
      FileUtils.cleanDirectory(todayThumbnailDirectory);
    }
  }

  private static String getTodayThumbnailFilesPath() {
    return new ClassPathResource("static").getPath() + separator +
        "thumbnail" + separator +
        LocalDate.now();
  }
}