package com.lyon.bank.features.security.dtos;

public record RegisterRequest(
  String username,
  String password,
  String email
) {
}
