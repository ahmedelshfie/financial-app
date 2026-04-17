package com.example.finance.report.service;

import com.example.finance.report.dto.ReportSummaryResponse;
import com.example.finance.report.dto.ReportListItemResponse;
import java.util.List;

public interface ReportService {
  List<ReportListItemResponse> listReports();

  ReportSummaryResponse customerSummary(Long customerId, String token);
}
