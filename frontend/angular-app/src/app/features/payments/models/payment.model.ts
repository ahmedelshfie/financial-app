export interface Beneficiary {
  id: number;
  customerId: number;
  name: string;
  accountNumber: string;
  bankCode: string;
}

export interface PaymentRequest {
  sourceAccountId: number;
  beneficiaryId: number;
  amount: number;
}

export interface PaymentApiResponse {
  id: number;
  paymentReference: string;
  sourceAccountId: number;
  beneficiaryId: number;
  amount: number;
  currencyCode: string;
  status: string;
  createdAt?: string;
}

export interface PaymentRecord {
  id: number;
  reference: string;
  sourceAccountId: number;
  beneficiaryId: number;
  beneficiaryName: string;
  amount: number;
  currencyCode: string;
  status: string;
  createdAt: string | null;
}
