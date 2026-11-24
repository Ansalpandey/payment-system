package com.squareupdigital.paymentsystem.dto;

import com.squareupdigital.paymentsystem.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {
  private String token;
  private User user;
}
