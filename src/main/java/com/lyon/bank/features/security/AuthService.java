package com.lyon.bank.features.security;

import com.lyon.bank.features.security.dtos.RegisterRequest;
import com.lyon.bank.shared.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserEntity register(RegisterRequest request) {
    // 1. Validate duplicates
    if (userRepository.existsByUsername(request.username())) {
      throw new IllegalArgumentException("Username already exists");
    }

    // 2. Fetch Default Role (CLIENT)
    RoleEntity clientRole = roleRepository.findByName(RoleEnum.CLIENT)
      .orElseThrow(() -> new IllegalStateException("ROLE_CLIENT was not initialized"));

    // 3. Create Entity
    UserEntity user = UserEntity.builder()
      .username(request.username())
      .password(passwordEncoder.encode(request.password())) // Hashing
      .email(request.email())
      .roles(Set.of(clientRole))
      .enabled(true)
      .build();

    return userRepository.save(user);
  }
}