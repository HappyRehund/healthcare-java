package com.rehund.healthcare.service.auth;

import com.rehund.healthcare.common.util.DateUtil;
import com.rehund.healthcare.config.JwtSecretConfig;
import com.rehund.healthcare.model.user.UserInfo;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final JwtSecretConfig jwtSecretConfig;
    private final SecretKey signInKey;

    @Override
    public String generateToken(UserInfo userInfo) {
        LocalDateTime expirationLocalDateTime = LocalDateTime.now().plus(jwtSecretConfig.getJwtExpirationTime());

        Date expiryTime = DateUtil.convertLocalDateTimeToDate(expirationLocalDateTime);

        return Jwts.builder()
                .subject(userInfo.getUsername())
                .issuedAt(new Date())
                .expiration(expiryTime)
                .signWith(signInKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {

        // penjelasan dikit
        // JWT itu String (token), dimana terdiri dari 3 bagian
        // Header, Payload (Claims), dan Signature

        try {
            // Ini tuh proses baca token (parser)
            // Jws = Signed JWT
            Jws<Claims> parsed = Jwts.parser()
                    .verifyWith(signInKey)
                    .build()
                    .parseSignedClaims(token);

              //Ini buat dapetin header sama payload
              //Header header = parsed.getHeader();
              //Claims claims = parsed.getPayload();

            return true;
        } catch (JwtException | IllegalArgumentException e){
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        Jws<Claims> parsed = Jwts.parser()
                .verifyWith(signInKey)
                .build()
                .parseSignedClaims(token);

        return parsed.getPayload().getSubject();
    }
}
