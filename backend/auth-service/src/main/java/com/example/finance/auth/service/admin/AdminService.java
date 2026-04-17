package com.example.finance.auth.service.admin;

import com.example.finance.auth.dto.admin.AdminConfigResponse;
import com.example.finance.auth.dto.admin.AdminUserResponse;
import java.util.List;

public interface AdminService {
  List<AdminUserResponse> listUsers();

  List<AdminConfigResponse> listConfigs();
}
