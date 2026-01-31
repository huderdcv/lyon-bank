package com.lyon.bank.features.accounts;

import com.lyon.bank.features.accounts.dtos.AccountDTO;
import com.lyon.bank.shared.dtos.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  public ResponseEntity<RestResponse<AccountDTO>> createAccount(){
    return ResponseEntity.status(HttpStatus.CREATED).body(
      RestResponse.success("Account created successfully", accountService.createAccount())
    );
  }

  @GetMapping
  public ResponseEntity<RestResponse<List<AccountDTO>>> getMyAccounts(){
    return ResponseEntity.ok(
      RestResponse.success("User accounts fetched successfully", accountService.getMyAccounts())
    );
  }
}
