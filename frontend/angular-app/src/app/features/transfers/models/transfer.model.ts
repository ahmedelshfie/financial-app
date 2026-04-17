export interface TransferRequest {
  sourceAccountId: number;
  destinationAccountId: number;
  amount: number;
}

export interface TransferApiResponse {
  id: number;
  transactionReference: string;
  sourceAccountId: number;
  destinationAccountId: number;
  amount: number;
  currencyCode: string;
  status: string;
  createdAt?: string;
}

export interface TransferRecord {
  id: number;
  reference: string;
  fromAccountId: number;
  toAccountId: number;
  amount: number;
  currencyCode: string;
  status: string;
  createdAt: string | null;
}
