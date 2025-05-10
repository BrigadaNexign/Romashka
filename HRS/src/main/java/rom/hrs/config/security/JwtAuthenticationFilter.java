package rom.hrs.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rom.hrs.service.JwtService;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HEADER_NAME);
        logger.debug("Received Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("Missing or invalid Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(BEARER_PREFIX.length());
        logger.debug("Extracted JWT: {}", jwt);

        try {
            // Извлекаем issuer (отправитель) из токена
            String issuer = jwtService.extractIssuer(jwt);
            // Извлекаем scope из токена
            String scope = jwtService.extractScope(jwt);
            logger.debug("Extracted issuer: {}, scope: {}", issuer, scope);

            if (scope != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Проверяем, что токен валиден для HRS сервиса
                // Используем разную логику проверки в зависимости от отправителя (BRT или CRM)
                boolean isValid = false;

                if ("BRT".equals(issuer)) {
                    isValid = jwtService.isTokenValid(jwt, "BRT", "HRS");
                    logger.debug("Validating token from BRT to HRS: {}", isValid);
                } else if ("CRM".equals(issuer)) {
                    isValid = jwtService.isTokenValid(jwt, "CRM", "HRS");
                    logger.debug("Validating token from CRM to HRS: {}", isValid);
                } else {
                    logger.warn("Unknown issuer: {}", issuer);
                }

                if (isValid) {
                    logger.debug("JWT is valid, setting authentication with scope: {}", scope);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            issuer, // Используем issuer как principal
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("SCOPE_" + scope))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    logger.error("Invalid or expired JWT from issuer {}: {}", issuer, jwt);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT");
                    return;
                }
            } else {
                logger.warn("Missing or invalid scope in JWT: {}", scope);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing or invalid scope in JWT");
                return;
            }
        } catch (Exception e) {
            logger.error("JWT validation failed: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT validation error: " + e.getMessage());
            return;
        }
    }
}