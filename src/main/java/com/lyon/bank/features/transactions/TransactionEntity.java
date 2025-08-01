package com.lyon.bank.features.transactions;

import com.lyon.bank.features.accounts.AccountEntity;
import com.lyon.bank.shared.enums.TransactionStatus;
import com.lyon.bank.shared.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransactionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "source_account_id", nullable = false)
  private AccountEntity sourceAccount;

  @ManyToOne
  @JoinColumn(name = "target_account_id", nullable = false)
  private AccountEntity targetAccount;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionStatus status;

  private String referenceCode; // Idempotency key

  @CreationTimestamp
  private LocalDateTime createdAt;
}
