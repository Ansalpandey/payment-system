package com.squareupdigital.paymentsystem.validators;

import com.squareupdigital.paymentsystem.model.User;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class AuthValidator {
  public void validateUser(User user) {

    if (user.getEmail().isBlank() || user.getPassword().isBlank()) {
      throw new IllegalArgumentException("Password and email cannot be empty");
    }

    if (user.getEmail().length() < 4 || user.getEmail().length() > 256) {
      throw new IllegalArgumentException("Email must be between 4 and 256 characters");
    }

    if (user.getPassword().length() < 6 || user.getPassword().length() > 20) {
      throw new IllegalArgumentException("Password must be between 6 and 20 characters");
    }

    if (!Pattern.matches(
        "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*$",
        user.getPassword())) {
      throw new IllegalArgumentException(
          "Password must contain at least one capital letter, one number, and one special character.");
    }
  }
}
