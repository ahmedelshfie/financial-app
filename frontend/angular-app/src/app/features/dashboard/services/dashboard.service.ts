import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { ApiUrlService } from '../../../core/services/api-url.service';
import { DashboardApiResponse, DashboardData } from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiUrlService: ApiUrlService
  ) {}

  getDashboardData(customerId: number): Observable<DashboardData> {
    return this.http
      .get<DashboardApiResponse>(this.apiUrlService.endpoint('dashboard', `customer/${customerId}`))
      .pipe(map((response) => this.mapResponse(response)));
  }

  private mapResponse(response: DashboardApiResponse): DashboardData {
    return {
      metrics: [
        { label: 'Total Balance', value: `${response.summaryCards.totalBalance}`, trend: `${response.summaryCards.activeAccounts}/${response.summaryCards.totalAccounts} active accounts` },
        { label: 'Available Balance', value: `${response.summaryCards.availableBalance}`, trend: `${response.customerTotals.allCustomers} customers` },
        { label: 'Transactions', value: `${response.quickMetrics.totalTransactions}`, trend: `${response.quickMetrics.totalTransfers} transfers` },
        { label: 'Payments', value: `${response.quickMetrics.totalPayments}`, trend: `${response.quickMetrics.failedPayments} failed` }
      ],
      activities: [...response.recentTransactions, ...response.recentTransfers, ...response.recentPayments].slice(0, 8).map((item) => ({
        title: item.reference,
        subtitle: `${item.amount} ${item.currencyCode} • ${item.description}`,
        status: item.status
      })),
      alerts: response.alerts.map((alert) => ({
        message: alert.message,
        severity: alert.severity === 'HIGH' ? 'critical' : alert.severity === 'MEDIUM' ? 'warning' : 'info'
      })),
      shortcuts: [
        { label: 'New Transfer', route: '/transfers', description: 'Initiate an internal or external transfer.' },
        { label: 'Run Reports', route: '/reports', description: 'Generate operational and financial reports.' },
        { label: 'Manage Payments', route: '/payments', description: 'Track scheduled and ad-hoc payouts.' }
      ]
    };
  }
}
