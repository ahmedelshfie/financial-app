package com.example.finance.payment.repository;

import com.example.finance.payment.entity.Beneficiary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
  List<Beneficiary> findByCustomerId(Long customerId);
}
