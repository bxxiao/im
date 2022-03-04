package com.bx.im.config.interceptor;

import com.bx.im.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    private static final String AUTHORIZATION_HEADER =  "Authorization";
    private static final String BEARER_PRE =  "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PRE)) {
            String token = authHeader.substring(BEARER_PRE.length());
            boolean verifyRet = JwtUtil.verifyJWT(token);
            if (!verifyRet)
                response.setStatus(403);
            return verifyRet;
        }

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }
}
