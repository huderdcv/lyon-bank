package com.lyon.bank.features.accounts;

import com.lyon.bank.features.accounts.dtos.AccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  public AccountDTO createAccount(){
    return accountService.createAccount();
  }

  @GetMapping
  public List<AccountDTO> getMyAccounts(){
    return accountService.getMyAccounts();
  }
}
