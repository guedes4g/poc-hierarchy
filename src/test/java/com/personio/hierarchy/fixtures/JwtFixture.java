package com.personio.hierarchy.fixtures;

import org.springframework.security.oauth2.jwt.Jwt;

public class JwtFixture {

  public static Jwt jwtFixture() {
    return Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("sub", "user")
        .claim("user_name", "I508889")
        .claim("scope", "openid lpx-editor-auth!t1807.USER")
        .build();
  }
}
