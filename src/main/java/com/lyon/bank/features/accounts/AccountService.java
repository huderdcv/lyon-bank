package com.lyon.bank.features.accounts;

import com.lyon.bank.features.accounts.dtos.AccountDTO;
import com.lyon.bank.features.security.UserEntity;
import com.lyon.bank.features.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  @Transactional
  public AccountDTO createAccount() {
    // 1. Get Logged-in User from JWT
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity user = userRepository.findByUsername(username)
      .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

    // 2. Generate Unique Numbers
    String accNumber = generateRandomString(14);
    String cci = "002" + generateRandomString(17); // 002 is bank code for this project

    // 3. Create Account
    AccountEntity account = AccountEntity.builder()
      .accountNumber(accNumber)
      .cci(cci)
      .balance(BigDecimal.ZERO) // Start with 0.00
      .user(user)
      .build();

    AccountEntity saved = accountRepository.save(account);

    return mapToDTO(saved);
  }

  @Transactional(readOnly = true)
  public List<AccountDTO> getMyAccounts() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    // We find the user first to ensure security, or query accounts by username if joined
    UserEntity user = userRepository.findByUsername(username)
      .orElseThrow(() -> new RuntimeException("User not found"));

    return accountRepository.findByUserId(user.getId()).stream()
      .map(this::mapToDTO)
      .collect(Collectors.toList());
  }

  // --- Helpers ---

  private AccountDTO mapToDTO(AccountEntity entity) {
    return new AccountDTO(
      entity.getId(),
      entity.getAccountNumber(),
      entity.getCci(),
      entity.getBalance()
    );
  }

  //TODO: I can improve this
  private String generateRandomString(int length) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(ThreadLocalRandom.current().nextInt(0, 10));
    }
    return sb.toString();
  }
}