package ee.mihkel.veebipood.model;

import lombok.Data;

@Data
public class UpdatePassword {
    private Long personId;
    private String oldPassword;
    private String newPassword;
}
