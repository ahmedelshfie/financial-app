export interface CustomerApiResponse {
  id: number;
  customerCode: string;
  firstName: string;
  lastName: string;
  email: string;
  nationalId: string;
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  kycStatus: 'PENDING' | 'VERIFIED' | 'REJECTED';
  createdAt: string;
}

export interface Customer {
  id: number;
  customerCode: string;
  fullName: string;
  email: string;
  status: CustomerApiResponse['status'];
  kycStatus: CustomerApiResponse['kycStatus'];
  joinedOn: string;
}

export interface CustomerSummary {
  totalCustomers: number;
  pendingKyc: number;
  verifiedCustomers: number;
}
