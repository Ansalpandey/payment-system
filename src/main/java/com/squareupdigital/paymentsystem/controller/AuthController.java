package com.squareupdigital.paymentsystem.controller;

import com.squareupdigital.paymentsystem.dto.LoginRequest;
import com.squareupdigital.paymentsystem.dto.LoginResponse;
import com.squareupdigital.paymentsystem.dto.VerifyRequest;
import com.squareupdigital.paymentsystem.model.User;
import com.squareupdigital.paymentsystem.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @GetMapping
  public String index() {
    return "Welcome to the Auth API!";
  }

  @PostMapping("/register")
  public String register(@Valid @RequestBody User request) {
    return authService.registerUser(request);
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.loginUser(request);
  }

  @PatchMapping("/verify")
  public String verifyUser(@RequestBody VerifyRequest request) {
    return authService.verifyUser(request);
  }
}
