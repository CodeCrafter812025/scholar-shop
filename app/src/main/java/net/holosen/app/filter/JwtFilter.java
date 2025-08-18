package net.holosen.app.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import net.holosen.service.user.UserService;
import net.holosen.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtFilter implements Filter {

    public static final String CURRENT_USER = "CURRENT_USER";
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtFilter(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @SneakyThrows
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String token = extractTokenFromHeader(httpRequest);
        if (!token.isEmpty() && jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromJWT(token);
            httpRequest.setAttribute(CURRENT_USER, userService.readUserByUsername(username));
            chain.doFilter(request, response);
        } else {
            httpResponse.getWriter().write("Access Denied!");
        }
    }

    private static String extractTokenFromHeader(HttpServletRequest httpRequest) {
        String bearerToken = httpRequest.getHeader("Authorization");
        String prefix = "Bearer ";
        String token = "";
        if (bearerToken != null && bearerToken.startsWith(prefix)) {
            token = bearerToken.substring(prefix.length());
        }
        return token;
    }
}
