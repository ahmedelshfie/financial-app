import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { ApiUrlService } from '../../../core/services/api-url.service';
import { Beneficiary, PaymentApiResponse, PaymentRecord, PaymentRequest } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentsService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiUrlService: ApiUrlService
  ) {}

  getBeneficiaries(customerId: number): Observable<Beneficiary[]> {
    return this.http.get<Beneficiary[]>(this.apiUrlService.endpoint('payments', `beneficiaries/customer/${customerId}`));
  }

  getPaymentHistory(accountId: number): Observable<PaymentRecord[]> {
    return this.http.get<PaymentApiResponse[]>(this.apiUrlService.endpoint('payments', `account/${accountId}`)).pipe(
      map((records) =>
        records.map((record) => ({
          id: record.id,
          reference: record.paymentReference,
          sourceAccountId: record.sourceAccountId,
          beneficiaryId: record.beneficiaryId,
          beneficiaryName: `Beneficiary #${record.beneficiaryId}`,
          amount: record.amount,
          currencyCode: record.currencyCode,
          status: record.status,
          createdAt: record.createdAt ?? null
        }))
      )
    );
  }

  initiatePayment(payload: PaymentRequest): Observable<PaymentApiResponse> {
    return this.http.post<PaymentApiResponse>(this.apiUrlService.endpoint('payments'), payload);
  }
}
