package com.lyon.bank.features.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
  // USERNAME
  @NotBlank(message = "Username is required")
  @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
  @Pattern(
    regexp = "^[a-zA-Z0-9._-]+$",
    message = "Username can only contain letters, numbers, dots, underscores and hyphens"
  )
  String username,

  // PASSWORD
  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 64, message = "The password must be between 8 and 64 characters")
  @Pattern(
    regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._*-])(?=\\S+$).{8,}$",
    message = "Password must contain at least one uppercase, one lowercase, one number and one special character (@, #, $, %, ^, &, +, =, !, -, _)"
  )
  String password,

  // EMAIL
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  String email
) {
}
