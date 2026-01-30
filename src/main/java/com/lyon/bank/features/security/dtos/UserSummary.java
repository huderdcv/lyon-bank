package com.lyon.bank.features.security.dtos;

import java.time.LocalDateTime;
import java.util.Set;

public record UserSummary(
  Long id,
  String username,
  String email,
  Set<String> roles,
  LocalDateTime createdAt
) {
}
