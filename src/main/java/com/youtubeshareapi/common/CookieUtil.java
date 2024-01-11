package com.youtubeshareapi.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class CookieUtil {
  public static String resolveToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    String token = "";
    if (cookies != null) {
      token = Arrays.stream(cookies).filter(c -> "jwt".equals(c.getName()))
          .findFirst()
          .map(Cookie::getValue)
          .orElse("");
    }
    return token;
  }
}
