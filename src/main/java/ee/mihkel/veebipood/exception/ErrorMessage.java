package ee.mihkel.veebipood.exception;

import lombok.Data;

import java.util.Date;

@Data // Getter + Setter + NoArgsConstructor
public class ErrorMessage {
    private String message;
    private int status;
    private Date timestamp;
}
