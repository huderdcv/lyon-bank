package com.lyon.bank.features.security;

import com.lyon.bank.features.security.dtos.RegisterRequest;
import com.lyon.bank.features.security.dtos.RegisterResponse;
import com.lyon.bank.shared.enums.RoleEnum;
import com.lyon.bank.shared.exceptions.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public RegisterResponse register(RegisterRequest request) {
    // 1. Validate duplicates
    if (userRepository.existsByUsername(request.username())) {
      throw new DuplicateResourceException("Username already exists");
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

    UserEntity userDb = userRepository.save(user);
    return mapUserEntityToRegisterResponse(userDb);
  }

  // -- HELPERS
  private RegisterResponse mapUserEntityToRegisterResponse(UserEntity user) {
    Set<String> roleNames = user.getRoles().stream()
      .map(r -> r.getName() != null ? r.getName().name() : "UNKNOW")
      .collect(Collectors.toSet());
    return new RegisterResponse(
      user.getId(),
      user.getUsername(),
      user.getEmail(),
      roleNames,
      user.getCreatedAt()
    );
  }
}