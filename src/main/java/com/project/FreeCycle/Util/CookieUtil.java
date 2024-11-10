package com.project.FreeCycle.Util;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60); // 쿠키가 살아있을 시간
        //cookie.setSecure(true);  //https 일 경우 주석 삭제
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
