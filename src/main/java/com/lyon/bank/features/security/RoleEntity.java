package com.lyon.bank.features.security;

import com.lyon.bank.shared.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true)
  private RoleEnum name;
}
