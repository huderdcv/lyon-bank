package com.lyon.bank.features.security;

import com.lyon.bank.shared.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
  Optional<RoleEntity> findByName(RoleEnum name);
}