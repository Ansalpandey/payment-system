package com.squareupdigital.paymentsystem.repository;

import com.squareupdigital.paymentsystem.model.User;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, UUID> {
  public Optional<User> findByEmail(String email);

    boolean existsUserByEmail(@NotBlank @Email String email);
}
