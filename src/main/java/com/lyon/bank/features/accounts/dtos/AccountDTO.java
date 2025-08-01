package com.lyon.bank.features.accounts.dtos;

import java.math.BigDecimal;

public record AccountDTO(
  Long id,
  String accountNumber,
  String cci,
  BigDecimal balance
) {
}
