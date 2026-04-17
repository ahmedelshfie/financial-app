export interface DashboardApiResponse {
  summaryCards: {
    totalBalance: number;
    availableBalance: number;
    totalAccounts: number;
    activeAccounts: number;
  };
  customerTotals: {
    allCustomers: number;
    activeCustomers: number;
    verifiedCustomers: number;
  };
  quickMetrics: {
    totalTransactions: number;
    totalTransfers: number;
    totalPayments: number;
    failedPayments: number;
    pendingTransfers: number;
  };
  recentTransactions: DashboardActivityApi[];
  recentTransfers: DashboardActivityApi[];
  recentPayments: DashboardActivityApi[];
  alerts: DashboardAlertApi[];
}

interface DashboardActivityApi {
  reference: string;
  status: string;
  description: string;
  amount: number;
  currencyCode: string;
}

interface DashboardAlertApi {
  severity: string;
  message: string;
}

export interface DashboardMetric {
  label: string;
  value: string;
  trend: string;
}

export interface DashboardActivity {
  title: string;
  subtitle: string;
  status: string;
}

export interface DashboardAlert {
  severity: 'info' | 'warning' | 'critical';
  message: string;
}

export interface DashboardShortcut {
  label: string;
  route: string;
  description: string;
}

export interface DashboardData {
  metrics: DashboardMetric[];
  activities: DashboardActivity[];
  alerts: DashboardAlert[];
  shortcuts: DashboardShortcut[];
}
