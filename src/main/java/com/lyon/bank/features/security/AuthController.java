package com.lyon.bank.features.security;

import com.lyon.bank.features.security.dtos.LoginRequest;
import com.lyon.bank.features.security.dtos.RegisterRequest;
import com.lyon.bank.features.security.dtos.RegisterResponse;
import com.lyon.bank.security.jwt.TokenService;
import com.lyon.bank.shared.dtos.RestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final TokenService tokenService;
  private final AuthenticationManager authenticationManager;
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<RestResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request){
    return ResponseEntity.status(HttpStatus.CREATED).body(
      RestResponse.success("User registered successfully", authService.register(request))
    );
  }

  @PostMapping("/login")
  public String login(@RequestBody LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );
    return tokenService.generateToken(authentication);
  }
}