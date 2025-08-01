package com.lyon.bank.features.accounts;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

  Optional<AccountEntity> findByAccountNumber(String accountNumber);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM AccountEntity a WHERE a.accountNumber = :accountNumber")
  Optional<AccountEntity> findByAccountNumberWithLock(@Param("accountNumber") String accountNumber);

  boolean existsByAccountNumber(String accountNumber);

  List<AccountEntity> findByUserId(Long id);
}