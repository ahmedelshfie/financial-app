package com.example.finance.payment.repository;

import com.example.finance.payment.entity.TransactionType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
  Optional<TransactionType> findByCode(String code);
}
