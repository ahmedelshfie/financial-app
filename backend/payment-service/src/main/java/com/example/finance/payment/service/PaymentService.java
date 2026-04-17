package com.example.finance.payment.service;

import com.example.finance.payment.dto.*;
import java.util.List;

public interface PaymentService {
  BeneficiaryResponse addBeneficiary(BeneficiaryRequest request);

  List<BeneficiaryResponse> listBeneficiaries(Long customerId);

  PaymentResponse initiate(PaymentRequest request, String token);

  PaymentResponse confirm(String paymentReference, PaymentStatusUpdateRequest request);

  List<PaymentResponse> history(Long accountId);
}
