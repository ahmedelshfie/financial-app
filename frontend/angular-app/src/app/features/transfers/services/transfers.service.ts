import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { ApiUrlService } from '../../../core/services/api-url.service';
import { TransferApiResponse, TransferRecord, TransferRequest } from '../models/transfer.model';

@Injectable({ providedIn: 'root' })
export class TransfersService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiUrlService: ApiUrlService
  ) {}

  listTransfers(): Observable<TransferRecord[]> {
    return this.http.get<TransferApiResponse[]>(this.apiUrlService.endpoint('transfers')).pipe(
      map((records) =>
        records.map((record) => ({
          id: record.id,
          reference: record.transactionReference,
          fromAccountId: record.sourceAccountId,
          toAccountId: record.destinationAccountId,
          amount: record.amount,
          currencyCode: record.currencyCode,
          status: record.status,
          createdAt: record.createdAt ?? null
        }))
      )
    );
  }

  createTransfer(payload: TransferRequest): Observable<TransferApiResponse> {
    return this.http.post<TransferApiResponse>(this.apiUrlService.endpoint('transfers'), payload);
  }
}
