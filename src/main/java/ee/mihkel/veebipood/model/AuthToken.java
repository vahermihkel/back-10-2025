package ee.mihkel.veebipood.model;

import lombok.Data;

@Data
public class AuthToken {
    private String refreshToken;
    private String accessToken;
    private long expiration;
}
