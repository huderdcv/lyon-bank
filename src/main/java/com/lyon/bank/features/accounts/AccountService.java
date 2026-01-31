package com.lyon.bank.features.accounts;

import com.lyon.bank.features.accounts.dtos.AccountDTO;
import com.lyon.bank.features.security.UserEntity;
import com.lyon.bank.features.security.UserRepository;
import com.lyon.bank.shared.exceptions.ResourceNotFoundException;
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

  private static final String CCI_NUMBER = "002"; // 002 is bank code for this project

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  // -- CREATE ACCOUNT
  @Transactional
  public AccountDTO createAccount() {
    // verify authentication and find user
    String username = getUsername();
    UserEntity user = userRepository.findByUsername(username)
      .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

    // generate unique numbers
    String accNumber = generateRandomString(14);
    String cci = CCI_NUMBER + generateRandomString(17);

    // create the account
    AccountEntity account = AccountEntity.builder()
      .accountNumber(accNumber)
      .cci(cci)
      .balance(BigDecimal.ZERO) // Start with 0.00
      .user(user)
      .build();

    // save in db and answer in dto
    AccountEntity accountInDB = accountRepository.save(account);
    return mapToDTO(accountInDB);
  }

  // -- FIND ACCOUNTS
  @Transactional(readOnly = true)
  public List<AccountDTO> getMyAccounts() {
    // get the username of the authentication
    String username = getUsername();

    // find in db and answer in dto
    return accountRepository.findByUser_Username(username).stream()
      .map(this::mapToDTO)
      .collect(Collectors.toList());
  }

  // --- HELPERS
  private String getUsername(){
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  private AccountDTO mapToDTO(AccountEntity entity) {
    return new AccountDTO(
      entity.getId(),
      entity.getAccountNumber(),
      entity.getCci(),
      entity.getBalance()
    );
  }

  //TODO: Resolve collision risk
  private String generateRandomString(int length) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(ThreadLocalRandom.current().nextInt(0, 10));
    }
    return sb.toString();
  }
}