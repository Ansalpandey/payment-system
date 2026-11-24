package com.squareupdigital.paymentsystem.utils;

import com.squareupdigital.paymentsystem.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final SecretKey secretKey;
  private final JwtParser jwtParser;
  private final long jwtExpirationMs;
  private final String issuer;

  public JwtUtil(
      @Value("${security.jwt.secret}") String base64Secret,
      @Value("${security.jwt.expiration-ms:900000}") long jwtExpirationMs,
      @Value("${security.jwt.issuer:payment-system}") String issuer) {
    byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
    if (keyBytes.length < 32) {
      throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 bytes)");
    }
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    this.jwtExpirationMs = jwtExpirationMs;
    this.issuer = issuer;
    this.jwtParser =
        Jwts.parser()
            .verifyWith(this.secretKey)
            .requireIssuer(this.issuer)
            .clockSkewSeconds(30)
            .build();
  }

  public String generateToken(User user) {
    Instant now = Instant.now();
    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .issuer(issuer)
        .subject(String.valueOf(user.getId()))
        .claim("email", user.getEmail())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusMillis(jwtExpirationMs)))
        .signWith(secretKey, Jwts.SIG.HS256)
        .compact();
  }

  public Claims parseAndValidateClaims(String token) throws JwtException {
    return jwtParser.parseSignedClaims(token).getPayload();
  }

  public String extractUserId(String token) {
    return parseAndValidateClaims(token).getSubject();
  }
}
