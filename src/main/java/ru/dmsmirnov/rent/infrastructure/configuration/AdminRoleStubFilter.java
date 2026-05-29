package ru.dmsmirnov.rent.infrastructure.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Временная заглушка до подключения JWT (шаг 12).
 * Передавайте заголовок {@code X-Admin-Role: ADMIN} для доступа к admin-эндпоинтам.
 */
@Component
@ConditionalOnProperty(name = "rent.admin.stub-enabled", havingValue = "true", matchIfMissing = true)
public class AdminRoleStubFilter extends OncePerRequestFilter {

    public static final String ADMIN_ROLE_HEADER = "X-Admin-Role";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String adminRole = request.getHeader(ADMIN_ROLE_HEADER);
        if (adminRole != null && "ADMIN".equalsIgnoreCase(adminRole.trim())) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    "admin-stub",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

}
