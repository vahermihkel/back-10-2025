package ee.mihkel.veebipood.model;

import lombok.Data;

@Data
public class AuthToken {
    private String token;
    private long expiration;
}
