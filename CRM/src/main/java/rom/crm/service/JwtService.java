package rom.crm.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rom.crm.entity.ServiceName;
import rom.crm.entity.User;

import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @Value("${token.signing.service-ttl}")
    private Duration serviceTokenTtl;

    /**
     * Extract username from token
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract issuer from token
     */
    public String extractIssuer(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    /**
     * Extract scope from token
     */
    public String extractScope(String token) {
        return extractClaim(token, claims -> claims.get("scope", String.class));
    }

    /**
     * Generate token for user authentication
     */
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

    /**
     * Validate token for user authentication
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Validate service token with issuer and audience
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

            // Check audience
            Set<String> audiences = claims.getAudience();
            boolean isAudienceValid = audiences != null && audiences.contains(expectedAudience);

            return isNotExpired && isIssuerValid && isAudienceValid;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate service token
     */
    public String generateServiceToken(ServiceName issuer, ServiceName audience, String scope) {
        return Jwts.builder()
                .issuer(issuer.name())
                .audience()
                    .add(audience.getCode())
                    .and()
                .claim("scope", scope)
                .subject("service-communication")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract claim from token
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generate token with custom claims and expiration
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Duration ttl) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract expiration date from token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}