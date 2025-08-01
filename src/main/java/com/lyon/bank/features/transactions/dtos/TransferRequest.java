package com.lyon.bank.features.transactions.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferRequest(
  @NotBlank
  String sourceAccountNumber,

  @NotBlank
  String targetAccountNumber,

  @NotNull
  @DecimalMin(value = "0.01", message = "Minimum transfer is 0.01")
  BigDecimal amount
) {}