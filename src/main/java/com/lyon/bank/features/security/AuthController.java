package com.lyon.bank.features.security;

import com.lyon.bank.features.security.dtos.*;

import com.lyon.bank.shared.dtos.RestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<RestResponse<UserSummary>> register(@Valid @RequestBody RegisterRequest request){
    return ResponseEntity.status(HttpStatus.CREATED).body(
      RestResponse.success("User registered successfully", authService.register(request))
    );
  }

  @PostMapping("/login")
  public ResponseEntity<RestResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(
      RestResponse.success("Login successful", authService.login(request))
    );
  }
}