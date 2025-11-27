package ee.mihkel.veebipood.service;

import ee.mihkel.veebipood.entity.Person;
import ee.mihkel.veebipood.model.AuthToken;
import ee.mihkel.veebipood.model.TokenData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {
    Key superSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode("IjjI9AE8fuQeY39XrBdt8XWzqzMldV9E918nc8SwbwQ"));

    public AuthToken generateToken(Person person){
                                                        //      h   m     s   ms
        Date expiration = new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000);

        String token = Jwts
                .builder()
                .signWith(superSecretKey)
                .setId(person.getId().toString())
                .setSubject(person.getEmail())
                .setAudience(person.getRole().toString())
                .setExpiration(expiration) // päriselt tokenis ---> automaatika, kui on parse-mine ja on aegunud, siis viskab errori
                .compact();

        AuthToken authToken = new AuthToken();
        authToken.setToken(token);
        authToken.setExpiration(expiration.getTime()); // frontendile saatmiseks expiration, et ei teeks üleliigseid päringuid
        return authToken;
    }

    public TokenData parseToken(String token){

        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(superSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        TokenData tokenData = new TokenData();
        tokenData.setId(Long.parseLong(claims.getId()));
        tokenData.setEmail(claims.getSubject());
        tokenData.setRole(claims.getAudience());
        return tokenData;
    }
}
