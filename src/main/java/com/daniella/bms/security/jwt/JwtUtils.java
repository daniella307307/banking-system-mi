package com.daniella.bms.security.jwt;

import com.daniella.bms.security.user.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecretKey;
    @Value("${jwt.expiration}")
    private long jwtAccessTokenExpiration;
    @Value("${jwt.refresh-token.expiration}")
    private long jwtRefreshTokenExpirations;
    private static final String CLAIM_KEY_USER_ID="userId";
    private static final String CLAIM_KEY_EMAIL="email";
    private static final String CLAIM_KEY_ROLE="role";
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateAccessToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtAccessTokenExpiration);

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setId(userPrincipal.getId().toString())
                .setSubject(userPrincipal.getUsername())
                .claim(CLAIM_KEY_USER_ID, userPrincipal.getId().toString())
                .claim(CLAIM_KEY_EMAIL, userPrincipal.getUsername())
                .claim(CLAIM_KEY_ROLE, roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Corrected generateRefreshToken method
    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshTokenExpirations);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim(CLAIM_KEY_USER_ID, userPrincipal.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Match access token method
                .compact();
    }


    public JwtUserInfo decodeToken(String token) throws RuntimeException{
        Claims claims = extractAllClaims(token);
        UUID userId = UUID.fromString((String) claims.get(CLAIM_KEY_USER_ID));
        String email = (String) claims.get(CLAIM_KEY_EMAIL);
        List<String> role = (List<String>) claims.get(CLAIM_KEY_ROLE);

        return new JwtUserInfo()
                .setUserId(userId)
                .setEmail(email)
                .setRole(role);
    }

    public boolean isTokenValid(String token, UserPrincipal userPrincipal) {
        String email = (String) extractAllClaims(token).get(CLAIM_KEY_EMAIL);
        return email.equals(userPrincipal.getUsername()) && !isTokenExpired(token);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
