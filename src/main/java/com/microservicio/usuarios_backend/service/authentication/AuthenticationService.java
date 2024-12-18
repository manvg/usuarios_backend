package com.microservicio.usuarios_backend.service.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import com.microservicio.usuarios_backend.config.Constants;
import com.microservicio.usuarios_backend.model.entities.Usuario;

import java.security.Key;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class AuthenticationService {

    private final Key key;

    public AuthenticationService() {
        this.key = Constants.getSigningKey();
    }

    public String authenticate(Usuario usuario) {
        return generateToken(usuario);
    }

    private String generateToken(Usuario usuario) {
        return Jwts.builder()
            .setIssuer(Constants.ISSUER_INFO)
            .setSubject(usuario.getEmail())
            .claim("id", usuario.getIdUsuario())
            .claim("email", usuario.getEmail())
            .claim("perfil", usuario.getPerfil())
            .claim("nombre", usuario.getNombre())
            .claim("apellidoPaterno", usuario.getApellidoPaterno())
            .claim("apellidoMaterno", usuario.getApellidoMaterno())
            .claim("fechaNacimiento", usuario.getFechaNacimiento().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .claim("direccion", usuario.getDireccion())
            .claim("telefono", usuario.getTelefono())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + Constants.TOKEN_EXPIRATION_TIME)) 
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Integer getIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

        return claims.get("id", Integer.class);
    }
}