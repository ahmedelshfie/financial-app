package com.example.finance.auth.controller.admin;

import com.example.finance.auth.dto.admin.AdminConfigResponse;
import com.example.finance.auth.dto.admin.AdminUserResponse;
import com.example.finance.auth.service.admin.AdminService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @GetMapping("/users")
  public ResponseEntity<List<AdminUserResponse>> listUsers() {
    return ResponseEntity.ok(adminService.listUsers());
  }

  @GetMapping("/configs")
  public ResponseEntity<List<AdminConfigResponse>> listConfigs() {
    return ResponseEntity.ok(adminService.listConfigs());
  }
}
