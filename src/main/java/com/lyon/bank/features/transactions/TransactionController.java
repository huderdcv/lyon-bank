package com.lyon.bank.features.transactions;

import com.lyon.bank.features.transactions.dtos.TransferRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/transfer")
  public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequest request) {
    String result = transactionService.transfer(request);
    return ResponseEntity.ok(result);
  }
}