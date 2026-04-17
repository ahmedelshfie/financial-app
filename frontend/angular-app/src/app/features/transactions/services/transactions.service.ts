import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, forkJoin, map, Observable, of, switchMap, throwError } from 'rxjs';
import { ApiUrlService } from '../../../core/services/api-url.service';
import { TransactionApiResponse, TransactionItem } from '../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class TransactionsService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiUrlService: ApiUrlService
  ) {}

  listTransactions(): Observable<TransactionItem[]> {
    const transactionsRequest$ = this.fetchHistoryFrom('transactions');
    const transfersRequest$ = this.fetchHistoryFrom('transfers');

    return forkJoin({ transactions: transactionsRequest$, transfers: transfersRequest$ }).pipe(
      switchMap(({ transactions, transfers }) => {
        if (!transactions.ok && !transfers.ok) {
          return throwError(() => new Error('Unable to load transaction history from available sources.'));
        }

        const byReference = new Map<string, TransactionApiResponse>();
        [...transactions.data, ...transfers.data].forEach((item) => {
          byReference.set(item.transactionReference, item);
        });

        return of(
          Array.from(byReference.values())
            .map((tx) => this.toTransactionItem(tx))
            .sort((a, b) => (b.timestamp ?? '').localeCompare(a.timestamp ?? ''))
        );
      })
    );
  }

  private fetchHistoryFrom(endpoint: 'transactions' | 'transfers'): Observable<{ data: TransactionApiResponse[]; ok: boolean }> {
    return this.http.get<TransactionApiResponse[]>(this.apiUrlService.endpoint(endpoint)).pipe(
      map((data) => ({ data, ok: true })),
      catchError(() => of({ data: [], ok: false }))
    );
  }

  private toTransactionItem(tx: TransactionApiResponse): TransactionItem {
    return {
      id: tx.id,
      reference: tx.transactionReference,
      fromAccountId: tx.sourceAccountId,
      toAccountId: tx.destinationAccountId,
      amount: tx.amount,
      currency: tx.currencyCode,
      status: tx.status,
      timestamp: tx.createdAt ?? null
    };
  }
}
