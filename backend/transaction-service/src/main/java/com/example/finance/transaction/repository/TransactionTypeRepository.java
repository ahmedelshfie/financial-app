package com.example.finance.transaction.repository;

import com.example.finance.transaction.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
    Optional<TransactionType> findByCode(String code);
}