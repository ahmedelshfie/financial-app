export interface TransactionApiResponse {
  id: number;
  transactionReference: string;
  sourceAccountId: number;
  destinationAccountId: number;
  amount: number;
  currencyCode: string;
  status: string;
  createdAt?: string;
}

export interface TransactionItem {
  id: number;
  reference: string;
  fromAccountId: number;
  toAccountId: number;
  amount: number;
  currency: string;
  status: string;
  timestamp: string | null;
}
