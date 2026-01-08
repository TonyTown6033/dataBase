package com.database.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {
    private final Algorithm alg;
    private final String issuer;
    private final long expireSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expire-seconds}") long expireSeconds
    ) {
        this.alg = Algorithm.HMAC256(secret);
        this.issuer = issuer;
        this.expireSeconds = expireSeconds;
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(now.plusSeconds(expireSeconds))
                .sign(alg);
    }

    public DecodedJWT verify(String token) {
        return JWT.require(alg)
                .withIssuer(issuer)
                .build()
                .verify(token);
    }
}
