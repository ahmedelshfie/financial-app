import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { ApiUrlService } from '../../../core/services/api-url.service';
import { Customer, CustomerApiResponse, CustomerSummary } from '../models/customer.model';

@Injectable({ providedIn: 'root' })
export class CustomersService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiUrlService: ApiUrlService
  ) {}

  getCustomers(): Observable<Customer[]> {
    return this.http.get<CustomerApiResponse[]>(this.apiUrlService.endpoint('customers')).pipe(
      map((customers) =>
        customers.map((customer) => ({
          id: customer.id,
          customerCode: customer.customerCode,
          fullName: `${customer.firstName} ${customer.lastName}`.trim(),
          email: customer.email,
          status: customer.status,
          kycStatus: customer.kycStatus,
          joinedOn: customer.createdAt
        }))
      )
    );
  }

  getSummary(customers: Customer[]): CustomerSummary {
    return {
      totalCustomers: customers.length,
      pendingKyc: customers.filter((customer) => customer.kycStatus === 'PENDING').length,
      verifiedCustomers: customers.filter((customer) => customer.kycStatus === 'VERIFIED').length
    };
  }
}
