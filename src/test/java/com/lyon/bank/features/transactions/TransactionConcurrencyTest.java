package com.lyon.bank.features.transactions;

import com.lyon.bank.features.accounts.AccountEntity;
import com.lyon.bank.features.accounts.AccountRepository;
import com.lyon.bank.features.security.RoleEntity;
import com.lyon.bank.features.security.RoleRepository;
import com.lyon.bank.features.security.UserEntity;
import com.lyon.bank.features.security.UserRepository;
import com.lyon.bank.features.transactions.dtos.TransferRequest;
import com.lyon.bank.shared.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TransactionConcurrencyTest {

  // 1. Spin up a REAL postgres database for this test
  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @Autowired private TransactionService transactionService;
  @Autowired private AccountRepository accountRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private RoleRepository roleRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private String sourceAccNum = "11111";
  private String targetAccNum = "22222";

  @BeforeEach
  void setup() {
    // Clean DB before each test
    accountRepository.deleteAll();
    userRepository.deleteAll();
    roleRepository.deleteAll();

    // Setup data
    RoleEntity clientRole = roleRepository.save(RoleEntity.builder().name(RoleEnum.CLIENT).build());

    UserEntity user = UserEntity.builder()
      .username("testuser")
      .password(passwordEncoder.encode("pass"))
      .email("test@mail.com")
      .roles(Set.of(clientRole))
      .enabled(true)
      .build();
    userRepository.save(user);

    // Account A (source) has 100.00
    accountRepository.save(AccountEntity.builder()
      .accountNumber(sourceAccNum).cci("002111").balance(new BigDecimal("100.00"))
      .user(user).build());

    // Account B (target) has 0.00
    accountRepository.save(AccountEntity.builder()
      .accountNumber(targetAccNum).cci("002222").balance(new BigDecimal("0.00"))
      .user(user).build());

    // Mock security context (Simulate logged in user)
    SecurityContextHolder.getContext().setAuthentication(
      new UsernamePasswordAuthenticationToken("testuser", "pass")
    );
  }

  @Test
  @DisplayName("Should process transfers sequentially thanks to Pessimistic Locking")
  void testConcurrency() throws InterruptedException, ExecutionException {
    // GIVEN
    BigDecimal transferAmount = new BigDecimal("50.00");
    TransferRequest request = new TransferRequest(sourceAccNum, targetAccNum, transferAmount);

    // CAPTURE THE CONTEXT (The "Passport")
    // We grab the security credentials from the Main Thread before splitting
    var context = SecurityContextHolder.getContext();

    // WHEN
    CompletableFuture<Void> thread1 = CompletableFuture.runAsync(() -> {
      // INJECT THE CONTEXT into Worker Thread 1
      SecurityContextHolder.setContext(context);
      try {
        transactionService.transfer(request);
      } finally {
        SecurityContextHolder.clearContext(); // Clean up after work
      }
    });

    CompletableFuture<Void> thread2 = CompletableFuture.runAsync(() -> {
      // INJECT THE CONTEXT into Worker Thread 2
      SecurityContextHolder.setContext(context);
      try {
        transactionService.transfer(request);
      } finally {
        SecurityContextHolder.clearContext();
      }
    });

    // Wait for both to finish
    CompletableFuture.allOf(thread1, thread2).get();

    // THEN
    AccountEntity source = accountRepository.findByAccountNumber(sourceAccNum).orElseThrow();
    AccountEntity target = accountRepository.findByAccountNumber(targetAccNum).orElseThrow();

    System.out.println("Final Source Balance: " + source.getBalance());
    System.out.println("Final Target Balance: " + target.getBalance());

    // Assertions
    assertEquals(0, new BigDecimal("0.00").compareTo(source.getBalance()), "Source should be 0.00");
    assertEquals(0, new BigDecimal("100.00").compareTo(target.getBalance()), "Target should be 100.00");
  }
}