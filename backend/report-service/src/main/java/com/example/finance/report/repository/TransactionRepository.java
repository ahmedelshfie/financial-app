package com.example.finance.report.repository;

import com.example.finance.report.entity.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  List<Transaction> findBySourceAccountIdOrDestinationAccountId(
      Long sourceAccountId, Long destinationAccountId);
}
