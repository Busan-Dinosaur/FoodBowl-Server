package com.dinosaur.foodbowl.global.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.domain.user.entity.Role.RoleType;
import com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@ContextConfiguration
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtTokenProviderTest {

    private static final String TEST_SECRET_KEY = "2B4B6250655368566D597133743677397A244326452948404D635166546A576E";
    private static final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

    @BeforeAll
    static void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", TEST_SECRET_KEY);
    }

    @Test
    void 엑세스_토큰을_생성한다() {
        long userPk = 1;
        RoleType[] roleType = new RoleType[]{RoleType.ROLE_회원, RoleType.ROLE_관리자};

        String accessToken = jwtTokenProvider.createAccessToken(userPk, roleType);
        Claims claims = Jwts.parser().setSigningKey(TEST_SECRET_KEY)
                .parseClaimsJws(accessToken)
                .getBody();

        long resultUserPK = Long.parseLong(claims.getSubject());
        String roles = claims.get("roles").toString();
        assertThat(resultUserPK).isEqualTo(userPk);
        assertThat(roles).isEqualTo("ROLE_회원,ROLE_관리자");
    }
}

