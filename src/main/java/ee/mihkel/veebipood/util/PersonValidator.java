package ee.mihkel.veebipood.util;

import org.springframework.stereotype.Component;

@Component
public class PersonValidator {

    public boolean validateEmail(String email){
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public void validatePassword(String password){
        if (password == null) {
            throw new RuntimeException("Password is required");
        }
        if (password.length() < 6) {
            throw new RuntimeException("Password is too short");
        }
    }
}
