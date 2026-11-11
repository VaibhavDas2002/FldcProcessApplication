package com.nic.fldc.common.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret:defaultSecretKeyForDevelopmentMustBe32BytesOrLonger!}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            keyBytes = padded;
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public UserPrincipal getUserPrincipal(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        Long userId = parseLong(claims.get("userId"));
        if (userId == null) {
            userId = parseLong(claims.get("user_id"));
        }
        if (userId == null) {
            userId = parseLong(username);
        }

        Integer districtCode = parseInteger(claims.get("districtCode"));
        if (districtCode == null) districtCode = parseInteger(claims.get("lgd_district_code"));
        if (districtCode == null) districtCode = parseInteger(claims.get("district_id"));

        Integer blockCode = parseInteger(claims.get("blockCode"));
        if (blockCode == null) blockCode = parseInteger(claims.get("lgd_block_mc_code"));

        Integer gpCode = parseInteger(claims.get("gpCode"));
        if (gpCode == null) gpCode = parseInteger(claims.get("lgd_gp_ward_code"));

        Long localBodyCode = parseLong(claims.get("localBodyCode"));
        if (localBodyCode == null) localBodyCode = parseLong(claims.get("created_by_local_body_code"));

        String office = claims.get("office", String.class);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Object rolesObj = claims.get("roles");
        if (rolesObj == null) {
            rolesObj = claims.get("role");
        }

        if (rolesObj instanceof Collection<?> col) {
            for (Object r : col) {
                if (r != null) {
                    authorities.add(toAuthority(r.toString()));
                }
            }
        } else if (rolesObj instanceof String str) {
            for (String r : str.split(",")) {
                if (!r.isBlank()) {
                    authorities.add(toAuthority(r.trim()));
                }
            }
        }

        return UserPrincipal.builder()
                .userId(userId)
                .username(username)
                .authorities(authorities)
                .districtCode(districtCode)
                .blockCode(blockCode)
                .gpCode(gpCode)
                .localBodyCode(localBodyCode)
                .office(office)
                .build();
    }

    private SimpleGrantedAuthority toAuthority(String role) {
        String clean = role.trim().toUpperCase();
        if (!clean.startsWith("ROLE_")) {
            clean = "ROLE_" + clean;
        }
        return new SimpleGrantedAuthority(clean);
    }

    private Long parseLong(Object obj) {
        if (obj instanceof Number n) return n.longValue();
        if (obj != null) {
            try {
                return Long.parseLong(obj.toString().trim());
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private Integer parseInteger(Object obj) {
        if (obj instanceof Number n) return n.intValue();
        if (obj != null) {
            try {
                return Integer.parseInt(obj.toString().trim());
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    /**
     * Helper method to generate tokens for tests and mock clients.
     */
    public String generateTestToken(Long userId, String username, List<String> roles, Integer districtCode, Long localBodyCode) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("districtCode", districtCode)
                .claim("localBodyCode", localBodyCode)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }
}

