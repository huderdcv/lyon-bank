package com.lyon.bank.features.security.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Set;

public record LoginResponse(
  @JsonProperty("access_token")
  String accessToken,

  @JsonProperty("token_type")
  String tokenType,

  @JsonProperty("expire_in")
  Long expireIn,

  @JsonProperty("user_summary")
  UserSummary userSummary
) {
}
