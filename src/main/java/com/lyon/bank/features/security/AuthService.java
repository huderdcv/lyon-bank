package com.lyon.bank.features.security;

import com.lyon.bank.features.security.dtos.*;
import com.lyon.bank.security.config.JwtProperties;
import com.lyon.bank.security.jwt.TokenService;
import com.lyon.bank.shared.enums.RoleEnum;
import com.lyon.bank.shared.exceptions.DuplicateResourceException;
import com.lyon.bank.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
  private static final String TOKEN_TYPE = "Bearer";

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;
  private final JwtProperties jwtProperties;

  @Transactional(rollbackFor = Exception.class)
  public UserSummary register(RegisterRequest request) {
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
    return mapUserEntityToUserSummary(userDb);
  }

  public LoginResponse login(LoginRequest request){
    // authenticate
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );

    // generate token
    String token = tokenService.generateToken(authentication);

    // find user to return some values
    UserEntity user = userRepository.findByUsername(request.username())
      .orElseThrow(() -> new ResourceNotFoundException("Username not found"));

    return new LoginResponse(
      token,
      TOKEN_TYPE,
      jwtProperties.expiration(),
      mapUserEntityToUserSummary(user)
    );
  }

  // -- HELPERS
  private UserSummary mapUserEntityToUserSummary(UserEntity user) {
    Set<String> roleNames = user.getRoles().stream()
      .map(r -> r.getName() != null ? r.getName().name() : "UNKNOWN")
      .collect(Collectors.toSet());
    return new UserSummary(
      user.getId(),
      user.getUsername(),
      user.getEmail(),
      roleNames,
      user.getCreatedAt()
    );
  }
}