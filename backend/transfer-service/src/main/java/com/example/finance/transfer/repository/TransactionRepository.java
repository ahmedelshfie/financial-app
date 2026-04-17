package com.example.finance.transfer.repository;

import com.example.finance.transfer.entity.Transaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  List<Transaction> findAllByOrderByCreatedAtDesc();

  List<Transaction> findBySourceAccountIdOrDestinationAccountId(
      Long sourceAccountId, Long destinationAccountId);

  Optional<Transaction> findByTransactionReference(String transactionReference);
}
