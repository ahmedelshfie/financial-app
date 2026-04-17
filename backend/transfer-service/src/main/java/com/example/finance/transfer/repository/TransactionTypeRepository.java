package com.example.finance.transfer.repository;

import com.example.finance.transfer.entity.TransactionType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
  Optional<TransactionType> findByCode(String code);
}
