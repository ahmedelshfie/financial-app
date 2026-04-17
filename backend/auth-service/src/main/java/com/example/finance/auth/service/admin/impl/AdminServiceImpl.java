package com.example.finance.auth.service.admin.impl;

import com.example.finance.auth.dto.admin.AdminConfigResponse;
import com.example.finance.auth.dto.admin.AdminUserResponse;
import com.example.finance.auth.entity.User;
import com.example.finance.auth.repository.UserRepository;
import com.example.finance.auth.service.admin.AdminService;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

  private final UserRepository userRepository;

  @Value("${security.mfa.enabled:true}")
  private boolean mfaEnabled;

  @Value("${app.transfer.max-limit:500000}")
  private String maxTransferLimit;

  @Value("${app.reconciliation.auto-time:02:00 UTC}")
  private String autoReconciliationTime;

  public AdminServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<AdminUserResponse> listUsers() {
    return userRepository.findAll().stream()
        .sorted(Comparator.comparing(User::getId))
        .map(
            user ->
                AdminUserResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .role(
                        user.getRoles().stream()
                            .map(role -> role.getName().replace("ROLE_", ""))
                            .sorted()
                            .findFirst()
                            .orElse("USER"))
                    .status(user.getStatus())
                    .build())
        .toList();
  }

  @Override
  public List<AdminConfigResponse> listConfigs() {
    return List.of(
        AdminConfigResponse.builder().key("MFA_REQUIRED").value(Boolean.toString(mfaEnabled)).build(),
        AdminConfigResponse.builder().key("MAX_TRANSFER_LIMIT").value(maxTransferLimit).build(),
        AdminConfigResponse.builder().key("AUTO_RECONCILIATION_TIME").value(autoReconciliationTime).build());
  }
}
