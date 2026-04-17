package com.example.finance.dashboard.service;

import com.example.finance.dashboard.dto.DashboardResponse;

public interface DashboardService {
  DashboardResponse customerDashboard(Long customerId, String token, int limit, int trendDays);
}
