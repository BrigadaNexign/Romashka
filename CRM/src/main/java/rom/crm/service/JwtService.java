package rom.crm.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rom.crm.entity.User;

import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

/**
 * Сервис для работы с JWT токенами.
 * Обеспечивает генерацию, валидацию и парсинг токенов.
 */
@Service
public class JwtService {

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractIssuer(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    public String extractScope(String token) {
        return extractClaim(token, claims -> claims.get("scope", String.class));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("email", customUserDetails.getEmail());
            claims.put("msisdn", customUserDetails.getMsisdn());
            claims.put("role", customUserDetails.getRole().name());
        }
        return generateToken(claims, userDetails, Duration.ofHours(24));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }


    public boolean isTokenValid(String token, String expectedIssuer, String expectedAudience) {
        try {
            final Claims claims = extractAllClaims(token);
            final Date expiration = claims.getExpiration();

            boolean isNotExpired = expiration == null || expiration.after(new Date());

            String issuer = claims.getIssuer();
            boolean isIssuerValid = issuer != null && issuer.equals(expectedIssuer);

            Set<String> audiences = claims.getAudience();
            boolean isAudienceValid = audiences != null && audiences.contains(expectedAudience);

            return isNotExpired && isIssuerValid && isAudienceValid;
        } catch (Exception e) {
            return false;
        }
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Duration ttl) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}