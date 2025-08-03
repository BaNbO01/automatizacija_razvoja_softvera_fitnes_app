/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; 
import java.util.Date;
import java.util.stream.Collectors;


@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-milliseconds}")
    private long jwtExpirationDate; 

   
    public String generateToken(Authentication authentication) {
        String username = authentication.getName(); 
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

      
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles) 
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

   
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

  
    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

   
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Expired JWT token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Unsupported JWT token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}
