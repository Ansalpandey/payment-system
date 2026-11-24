package com.squareupdigital.paymentsystem.config;

import com.squareupdigital.paymentsystem.filter.JwtFilter;
import com.squareupdigital.paymentsystem.model.User;
import com.squareupdigital.paymentsystem.repository.AuthRepository;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtFilter jwtFilter;
  private final AuthRepository authRepository;

  public SecurityConfig(JwtFilter jwtFilter, AuthRepository authRepository) {
    this.jwtFilter = jwtFilter;
    this.authRepository = authRepository;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(13);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> {})
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth ->
            auth.requestMatchers("/api/auth/register", "/api/auth/login", "/actuator/**", "/error")
                .permitAll()
                .anyRequest()
                .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    return authentication -> {
      String email = authentication.getName();

      User user =
          authRepository
              .findByEmail(email)
              .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

      if (!passwordEncoder()
          .matches(authentication.getCredentials().toString(), user.getPassword())) {
        throw new BadCredentialsException("Invalid credentials");
      }

      return new UsernamePasswordAuthenticationToken(
          user.getId().toString(), null, Collections.emptyList());
    };
  }
}
