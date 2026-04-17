export interface AccountResponse {
  id: number;
  accountNumber: string;
  customerId: number;
  accountTypeCode: string;
  currencyCode: string;
  currentBalance: number;
  availableBalance: number;
  status: string;
}
