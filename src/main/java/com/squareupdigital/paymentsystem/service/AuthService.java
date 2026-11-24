package com.squareupdigital.paymentsystem.service;

import com.squareupdigital.paymentsystem.dto.LoginRequest;
import com.squareupdigital.paymentsystem.dto.LoginResponse;
import com.squareupdigital.paymentsystem.dto.VerifyRequest;
import com.squareupdigital.paymentsystem.enums.WalletStatus;
import com.squareupdigital.paymentsystem.model.User;
import com.squareupdigital.paymentsystem.model.Wallet;
import com.squareupdigital.paymentsystem.repository.AuthRepository;
import com.squareupdigital.paymentsystem.utils.JwtUtil;
import com.squareupdigital.paymentsystem.validators.AuthValidator;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final AuthRepository authRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthValidator authValidator;
  private final JwtUtil jwtUtil;

  public AuthService(
      AuthRepository authRepository,
      PasswordEncoder passwordEncoder,
      AuthValidator authValidator,
      JwtUtil jwtUtil) {
    this.authRepository = authRepository;
    this.passwordEncoder = passwordEncoder;
    this.authValidator = authValidator;
    this.jwtUtil = jwtUtil;
  }

  @Transactional
  public String registerUser(User user) {
    String normalizedEmail = user.getEmail().trim().toLowerCase(Locale.ROOT);
    user.setEmail(normalizedEmail);

    if (authRepository.existsUserByEmail(normalizedEmail)) {
      throw new IllegalArgumentException("User already exists");
    }

    authValidator.validateUser(user);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setCreatedAt(Instant.now());
    user.setUpdatedAt(Instant.now());

    Wallet wallet = new Wallet();
    wallet.setUser(user);
    wallet.setBalance(new BigDecimal(0));
    wallet.setCurrency("INR");
    wallet.setStatus(WalletStatus.ACTIVE);

    user.assignWallet(wallet);

    authRepository.save(user);

    return "User registered successfully";
  }

  @Transactional(readOnly = true)
  public LoginResponse loginUser(LoginRequest loginRequest) {
    String normalizedEmail = loginRequest.getEmail().trim().toLowerCase(Locale.ROOT);

    User user =
        authRepository
            .findByEmail(normalizedEmail)
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("Invalid email or password");
    }

    String token = jwtUtil.generateToken(user);

    return new LoginResponse(token, user);
  }

  @Transactional
  public String verifyUser(VerifyRequest request) {
    String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

    User user =
        authRepository
            .findByEmail(normalizedEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (user.isVerified()) {
      return "User is already verified";
    }

    user.setVerified(true);
    user.setUpdatedAt(Instant.now());
    authRepository.save(user);

    return "User verified successfully";
  }
}
