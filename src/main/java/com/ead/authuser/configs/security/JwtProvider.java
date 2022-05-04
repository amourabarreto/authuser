package com.ead.authuser.configs.security;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Log4j2
@Component
public class JwtProvider {

    @Value("${ead.auth.jwtSecret}")
    private String jwtSecret;

    @Value("${ead.auth.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwt(Authentication authentication){
        UserDetailsImpl userPrincial = (UserDetailsImpl) authentication.getPrincipal();
        final String roles = userPrincial.getAuthorities().stream().map(
                role -> {return role.getAuthority();}
        ).collect(Collectors.joining(","));
        return Jwts.builder()
                .setSubject(userPrincial.getUserId().toString())
                .claim("roles",roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date( (new Date()).getTime() + jwtExpirationMs ))
                .signWith(SignatureAlgorithm.HS512,jwtSecret)
                .compact();
    }

    public String getSubjectJwt(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwt(String token){
       try {
           Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
           return true;
       }catch(SignatureException e){
           log.error("Invalide JWT Signature: {}",e.getMessage());
       } catch (MalformedJwtException e){
           log.error("Invalide JWT Token: {}",e.getMessage());
       }catch(ExpiredJwtException e){
           log.error("JWT Token Expired: {}",e.getMessage());
       } catch (UnsupportedJwtException e){
           log.error("JWT Unsupported: {}",e.getMessage());
       } catch(IllegalArgumentException e){
           log.error("JWT claims string is empity: {}",e.getMessage());
       }
     return false;
    }
}
