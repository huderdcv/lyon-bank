package com.lyon.bank.security.auth;


import com.lyon.bank.features.security.UserEntity;
import com.lyon.bank.features.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    return new User(
      user.getUsername(),
      user.getPassword(),
      user.isEnabled(),
      true, true, true,
      user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
        .collect(Collectors.toSet())
    );
  }
}