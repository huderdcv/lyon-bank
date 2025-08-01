package com.lyon.bank.security.config;

import com.lyon.bank.features.security.RoleEntity;
import com.lyon.bank.features.security.RoleRepository;
import com.lyon.bank.shared.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final RoleRepository roleRepository;

  @Override
  public void run(String... args) {
    Arrays.stream(RoleEnum.values()).forEach(roleName -> {
      if (roleRepository.findByName(roleName).isEmpty()) {
        roleRepository.save(RoleEntity.builder().name(roleName).build());
        System.out.println("Role initialized: " + roleName);
      }
    });
  }
}