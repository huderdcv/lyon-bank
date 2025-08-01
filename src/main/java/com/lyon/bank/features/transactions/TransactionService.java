package com.lyon.bank.features.transactions;

import com.lyon.bank.features.accounts.AccountEntity;
import com.lyon.bank.features.accounts.AccountRepository;
import com.lyon.bank.features.security.UserRepository;
import com.lyon.bank.features.transactions.dtos.TransferRequest;
import com.lyon.bank.shared.enums.TransactionStatus;
import com.lyon.bank.shared.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final UserRepository userRepository;

  @Transactional
  public String transfer(TransferRequest request) {

    // 1. Identify the User (Security Check)
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

    // 2. Fetch SOURCE account with LOCK (Wait here if someone else is using it)
    AccountEntity source = accountRepository.findByAccountNumberWithLock(request.sourceAccountNumber())
      .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

    // 3. Verify Ownership (IDOR Protection)
    if (!source.getUser().getUsername().equals(username)) {
      throw new SecurityException("You do not own the source account");
    }

    // 4. Fetch target account with LOCK (Prevent lost updates on receiver)
    // TODO: sort these by ID to prevent deadlocks
    AccountEntity target = accountRepository.findByAccountNumberWithLock(request.targetAccountNumber())
      .orElseThrow(() -> new IllegalArgumentException("Target account not found"));

    // 5. Business logic: Check balance
    if (source.getBalance().compareTo(request.amount()) < 0) {
      throw new IllegalArgumentException("Insufficient funds");
    }

    // 6. Execute transfer (in memory)
    source.setBalance(source.getBalance().subtract(request.amount()));
    target.setBalance(target.getBalance().add(request.amount()));

    // 7. Save changes (JPA will update DB at end of method)
    accountRepository.save(source);
    accountRepository.save(target);

    // 8. Create Transaction Audit Log
    TransactionEntity transaction = TransactionEntity.builder()
      .sourceAccount(source)
      .targetAccount(target)
      .amount(request.amount())
      .type(TransactionType.THIRD_PARTY_TRANSFER)
      .status(TransactionStatus.COMPLETED)
      .referenceCode(java.util.UUID.randomUUID().toString())
      .createdAt(LocalDateTime.now())
      .build();

    transactionRepository.save(transaction);

    return "Transfer successful! Ref: " + transaction.getReferenceCode();
  }
}