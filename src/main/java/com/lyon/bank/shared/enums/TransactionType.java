package com.lyon.bank.shared.enums;

public enum TransactionType {
  SELF_TRANSFER,       // Between own accounts
  THIRD_PARTY_TRANSFER,// To another user
  TAX_ITF              // Financial Transaction Tax
}