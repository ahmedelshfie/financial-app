import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AccountResponse } from '../models/account.model';
import { ApiUrlService } from '../../../core/services/api-url.service';

@Injectable({ providedIn: 'root' })
export class AccountsService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiUrlService: ApiUrlService
  ) {}

  getAccountsByCustomer(customerId: number): Observable<AccountResponse[]> {
    return this.http.get<AccountResponse[]>(this.apiUrlService.endpoint('accounts', `customer/${customerId}`));
  }

  getAccountById(accountId: number): Observable<AccountResponse> {
    return this.http.get<AccountResponse>(this.apiUrlService.endpoint('accounts', String(accountId)));
  }
}
