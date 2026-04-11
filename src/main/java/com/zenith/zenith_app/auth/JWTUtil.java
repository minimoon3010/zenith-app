package com.zenith.zenith_app.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration}")
  private long expiration;

  public String generateToken(Long id) {
    return Jwts.builder()
        .subject(String.valueOf(id))
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
  }

  public boolean validateToken(String token) {
    return extractExpiration(token).after(new Date());
  }

  public String extractSubject(String token) {
    return Jwts.parser()
        .verifyWith((SecretKey) getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  private Date extractExpiration(String token) {
    return Jwts.parser()
        .verifyWith((SecretKey) getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration();
  }

  private Key getSigningKey() {
    byte[] keyByte = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyByte);
  }
}
