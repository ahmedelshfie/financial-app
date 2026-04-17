export const API_ENDPOINTS = {
  auth: 'auth',
  dashboard: 'dashboard',
  accounts: 'accounts',
  customers: 'customers',
  transactions: 'transactions',
  transfers: 'transfers',
  payments: 'payments',
  reports: 'reports',
  admin: 'admin'
} as const;

export type ApiEndpointKey = keyof typeof API_ENDPOINTS;
