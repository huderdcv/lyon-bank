package com.lyon.bank.features.transactions;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}
