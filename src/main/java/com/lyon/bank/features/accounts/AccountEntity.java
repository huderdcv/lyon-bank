package com.lyon.bank.features.accounts;

import com.lyon.bank.features.security.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 14)
  private String accountNumber;

  @Column(nullable = false, unique = true, length = 20)
  private String cci; // account Code

  // 19 digits total, 4 decimals for good precision
  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal balance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @CreationTimestamp
  private LocalDateTime createdAt;
}