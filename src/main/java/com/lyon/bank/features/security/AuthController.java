package com.lyon.bank.features.security;

import com.lyon.bank.features.security.dtos.LoginRequest;
import com.lyon.bank.features.security.dtos.RegisterRequest;
import com.lyon.bank.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
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
  public String register(@RequestBody RegisterRequest request){
    authService.register(request);
    return "user registered successfully";
  }

  @PostMapping("/login")
  public String login(@RequestBody LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );
    return tokenService.generateToken(authentication);
  }
}