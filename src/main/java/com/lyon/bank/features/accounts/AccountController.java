package com.lyon.bank.features.accounts;

import com.lyon.bank.features.accounts.dtos.AccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AccountDTO createAccount(){
    return accountService.createAccount();
  }

  @GetMapping
  public List<AccountDTO> getMyAccounts(){
    return accountService.getMyAccounts();
  }
}
