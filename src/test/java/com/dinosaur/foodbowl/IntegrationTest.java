package com.dinosaur.foodbowl;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.VALID;
import static java.io.File.separator;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.dinosaur.foodbowl.domain.auth.application.AuthService;
import com.dinosaur.foodbowl.domain.auth.application.TokenService;
import com.dinosaur.foodbowl.domain.blame.BlameTestHelper;
import com.dinosaur.foodbowl.domain.blame.dao.BlameRepository;
import com.dinosaur.foodbowl.domain.category.dao.CategoryRepository;
import com.dinosaur.foodbowl.domain.comment.CommentTestHelper;
import com.dinosaur.foodbowl.domain.comment.application.CommentFindService;
import com.dinosaur.foodbowl.domain.comment.application.CommentService;
import com.dinosaur.foodbowl.domain.comment.dao.CommentRepository;
import com.dinosaur.foodbowl.domain.follow.application.FollowService;
import com.dinosaur.foodbowl.domain.follow.dao.FollowRepository;
import com.dinosaur.foodbowl.domain.post.PostTestHelper;
import com.dinosaur.foodbowl.domain.post.application.PostFindService;
import com.dinosaur.foodbowl.domain.thumbnail.ThumbnailTestHelper;
import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.thumbnail.file.ThumbnailFileUtil;
import com.dinosaur.foodbowl.domain.user.UserTestHelper;
import com.dinosaur.foodbowl.domain.user.application.DeleteAccountService;
import com.dinosaur.foodbowl.domain.user.application.GetProfileService;
import com.dinosaur.foodbowl.domain.user.application.UpdateProfileService;
import com.dinosaur.foodbowl.domain.user.application.UserFindService;
import com.dinosaur.foodbowl.domain.user.dao.RoleRepository;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.dao.UserRoleRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.config.security.dto.TokenValidationDto;
import com.dinosaur.foodbowl.global.config.security.jwt.JwtToken;
import com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import com.dinosaur.foodbowl.global.config.security.jwt.JwtUserEntity;
import com.dinosaur.foodbowl.global.util.CookieUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
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
  protected CommentRepository commentRepository;

  @SpyBean
  protected BlameRepository blameRepository;

  /******* Service *******/
  @SpyBean
  protected GetProfileService getProfileService;

  @SpyBean
  protected AuthService authService;

  @SpyBean
  protected TokenService tokenService;

  @SpyBean
  protected UserFindService userFindService;

  @SpyBean
  protected DeleteAccountService deleteAccountService;

  @SpyBean
  protected UpdateProfileService updateProfileService;

  @SpyBean
  protected FollowService followService;

  @SpyBean
  protected PostFindService postFindService;

  @SpyBean
  protected CommentService commentService;

  @SpyBean
  protected CommentFindService commentFindService;

  /******* TestHelper *******/
  @Autowired
  protected UserTestHelper userTestHelper;

  @Autowired
  protected ThumbnailTestHelper thumbnailTestHelper;

  @Autowired
  protected PostTestHelper postTestHelper;

  @Autowired
  protected CommentTestHelper commentTestHelper;

  @Autowired
  protected BlameTestHelper blameTestHelper;

  /******* Util *******/
  @SpyBean
  protected ThumbnailFileUtil thumbnailFileUtil;

  @SpyBean
  protected CookieUtils cookieUtils;

  /******* Spring Bean *******/
  @Autowired
  protected WebApplicationContext webApplicationContext;

  @SpyBean
  protected JwtTokenProvider jwtTokenProvider;

  @Autowired
  protected PasswordEncoder passwordEncoder;

  @Autowired
  protected RedisTemplate redisTemplate;

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

  protected String asJsonString(final Object obj) {
    try {
      final ObjectMapper objectMapper = new ObjectMapper();
      final String content = objectMapper.writeValueAsString(obj);
      return content;
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

  protected void mockingAuth() {
    User user = userTestHelper.builder().build();
    ReflectionTestUtils.setField(user, "id", 1L);
    UserDetails userDetails = new JwtUserEntity(String.valueOf(user.getId()), List.of("ROLE_회원"));
    TokenValidationDto tokenDto = TokenValidationDto.of(true, VALID, "token");
    Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "",
        userDetails.getAuthorities());

    doReturn(tokenDto).when(jwtTokenProvider)
        .tryCheckTokenValid(any(HttpServletRequest.class), any(JwtToken.class));
    doReturn(auth).when(jwtTokenProvider).getAuthentication(anyString());
    doReturn(user).when(userFindService).findById(anyLong());
  }
}
