package com.lyon.bank.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
  Long expiration,
  String issuer
) {
}
