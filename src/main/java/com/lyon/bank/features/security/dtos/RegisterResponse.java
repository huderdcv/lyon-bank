package com.lyon.bank.features.security.dtos;

import com.lyon.bank.shared.enums.RoleEnum;

import java.time.LocalDateTime;
import java.util.Set;

public record RegisterResponse(
  Long id,
  String username,
  String email,
  Set<String> roles,
  LocalDateTime createdAt
) {
}
