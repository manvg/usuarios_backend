package com.microservicio.usuarios_backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;

public class Constants {

    public static final String LOGIN_URL = "/authentication/login";
    public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    public static final String TOKEN_BEARER_PREFIX = "Bearer ";
    public static final String ISSUER_INFO = "BACKEND";
    public static final String SUPER_SECRET_KEY = "#E@M%R!&EGD%srHG823sAK3&gBi&U4Q7";//Clave secreta para firmar el token
    public static final long TOKEN_EXPIRATION_TIME = 86_400_000L; // 1 día

    //Generar la clave de firma usando la clave secreta
    public static Key getSigningKey() {
        byte[] keyBytes = Constants.SUPER_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Validar el token
    public static boolean validateToken(String token) {
        try {
            if (token.startsWith(TOKEN_BEARER_PREFIX)) {
                token = token.substring(TOKEN_BEARER_PREFIX.length());
            }

            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            String issuer = claimsJws.getBody().getIssuer();
            if (!ISSUER_INFO.equals(issuer)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}