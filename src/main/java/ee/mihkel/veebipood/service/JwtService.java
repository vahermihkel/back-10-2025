package ee.mihkel.veebipood.service;

import ee.mihkel.veebipood.entity.Person;
import ee.mihkel.veebipood.model.AuthToken;
import ee.mihkel.veebipood.model.TokenData;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {
    Key superSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode("IjjI9AE8fuQeY39XrBdt8XWzqzMldV9E918nc8SwbwQ"));

    public AuthToken generateToken(Person person, boolean isUpdate){

        String accessToken = createToken(person, "access", TimeUnit.MINUTES.toMillis(2));


        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(accessToken);

        if (!isUpdate) {
            String refreshToken = createToken(person, "refresh", TimeUnit.HOURS.toMillis(0) + TimeUnit.MINUTES.toMillis(30));
            authToken.setRefreshToken(refreshToken);
        }


        authToken.setExpiration(TimeUnit.MINUTES.toMillis(15)); // frontendile saatmiseks expiration, et ei teeks üleliigseid päringuid
        return authToken;
    }

    private String createToken(Person person, String tokenType, long expirationTime) {
        Date expiration = new Date(System.currentTimeMillis() + expirationTime);

        return Jwts
                .builder()
                .signWith(superSecretKey)
                .setId(person.getId().toString())
                .setIssuer(tokenType)
                .setSubject(person.getEmail())
                .setAudience(person.getRole().toString())
                .setExpiration(expiration) // päriselt tokenis ---> automaatika, kui on parse-mine ja on aegunud, siis viskab errori
                .compact();
    }

    public TokenData parseToken(String token){
        // TODO: kui refreshtoken saab aegumise exceptioni, siis anname uue refreshtokeni

        Claims claims = null;
        try {
            claims = Jwts
                    .parserBuilder()
                    .setSigningKey(superSecretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("TOKEN_EXPIRED");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        TokenData tokenData = new TokenData();
        tokenData.setId(Long.parseLong(claims.getId()));
        tokenData.setEmail(claims.getSubject());
        tokenData.setRole(claims.getAudience());
        return tokenData;
    }
}
