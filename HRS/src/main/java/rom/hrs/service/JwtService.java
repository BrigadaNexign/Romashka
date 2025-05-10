package rom.hrs.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${token.signing.key}")
    private String secretKey;

    public String extractScope(String token) {
        return extractClaim(token, claims -> claims.get("scope", String.class));
    }

    public String extractIssuer(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    public String extractAudience(String token) {
        return extractClaim(token, Claims::getAudience).toString();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Проверяет валидность токена для HRS сервиса (устаревший метод)
     * @deprecated Используйте {@link #isTokenValid(String, String, String)}
     */
    @Deprecated
    public boolean isTokenValidForHrs(String token, String issuer, String audience) {
        return isTokenValid(token, issuer, audience);
    }

    /**
     * Проверяет валидность токена
     * @param token JWT токен
     * @param expectedIssuer ожидаемый отправитель
     * @param expectedAudience ожидаемый получатель
     * @return true если токен валиден
     */
    public boolean isTokenValid(String token, String expectedIssuer, String expectedAudience) {
        try {
            final Claims claims = extractAllClaims(token);
            final Date expiration = claims.getExpiration();

            // Check expiration
            boolean isNotExpired = expiration == null || expiration.after(new Date());

            // Check issuer
            String issuer = claims.getIssuer();
            boolean isIssuerValid = issuer != null && issuer.equals(expectedIssuer);

            // PROPER AUDIENCE VALIDATION - FIXED
            Set<String> audiences = claims.getAudience();
            boolean isAudienceValid = audiences != null && audiences.contains(expectedAudience);

            logger.debug("Token validation - not expired: {}, issuer valid: {}, audience valid: {}",
                    isNotExpired, isIssuerValid, isAudienceValid);
            logger.debug("Actual audiences: {}, Expected: {}", audiences, expectedAudience);

            return isNotExpired && isIssuerValid && isAudienceValid;
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }
}