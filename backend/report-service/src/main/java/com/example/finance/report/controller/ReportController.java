package com.example.finance.report.controller;

import com.example.finance.report.dto.ReportSummaryResponse;
import com.example.finance.report.dto.ReportListItemResponse;
import com.example.finance.report.service.ReportService;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
  private final ReportService service;

  public ReportController(ReportService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<List<ReportListItemResponse>> listReports() {
    return ResponseEntity.ok(service.listReports());
  }

  @GetMapping("/customer/{customerId}/summary")
  public ResponseEntity<ReportSummaryResponse> customerSummary(
      @PathVariable Long customerId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    return ResponseEntity.ok(
        service.customerSummary(customerId, authorization.replace("Bearer ", "").trim()));
  }
}
