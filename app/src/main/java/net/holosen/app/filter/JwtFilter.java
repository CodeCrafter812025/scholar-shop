package net.holosen.app.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.holosen.dto.user.UserDto;
import net.holosen.service.user.UserService;
import net.holosen.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtFilter extends OncePerRequestFilter {

    public static final String CURRENT_USER = "CURRENT_USER";

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtFilter(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // مسیرهای آزاد «دقیق»
    private static final Set<String> OPEN_EXACT = Set.of(
            "/api/user/login",
            "/api/user/register",
            "/index.html",
            "/auth.html",
            "/admin.html",
            "/orders.html"
    );

    // مسیرهای آزاد بر اساس پیشوند (استاتیک‌ها)
    private static final Set<String> OPEN_PREFIXES = Set.of(
            "/css/", "/js/", "/images/", "/assets/"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (OPEN_EXACT.contains(path)) return true;
        for (String p : OPEN_PREFIXES) {
            if (path.startsWith(p)) return true;
        }
        return false;  // بقیهٔ /api/* باید فیلتر شوند
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");
        // 💡 لاگ موقت برای اطمینان از اجرای فیلتر — بعد از تست حذفش کن
        System.out.println("[JwtFilter] path=" + request.getRequestURI()
                + " hasAuth=" + (auth != null));

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromJWT(token);
                    UserDto user = userService.readUserByUsername(username);
                    if (user != null) {
                        request.setAttribute(CURRENT_USER, user);
                    }
                }
            } catch (Exception ignored) {
                // توکن نامعتبر/منقضی: کاری نکن؛ کنترلر پیام استاندارد می‌دهد
            }
        }

        chain.doFilter(request, response);
    }
}
