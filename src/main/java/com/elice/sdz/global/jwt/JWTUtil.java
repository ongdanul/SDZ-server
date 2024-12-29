package com.elice.sdz.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T getClaim(String token, String claimName, Class<T> type) {
        return getClaims(token).get(claimName, type);
    }

    public String getEmail(String token) {
        return getClaim(token, "email", String.class);
    }

    public String getAuth(String token) {
        return getClaim(token, "auth", String.class);
    }

    public String getCategory(String token) {
        return getClaim(token, "category", String.class);
    }

    public String getLoginType(String token) { return getClaim(token, "loginType", String.class); }

    public boolean isValidCategory(String token, String tokenName) {
        String category = getCategory(token);
        return tokenName.equals(category);
    }

    public boolean isExpired(String token) {
        Date expirationDate = getClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    public String createJwt(String category, String email, String auth, String loginType, Long expiredMs) {

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .claim("category", category)
                .claim("email", email)
                .claim("auth", auth)
                .claim("loginType", loginType)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
