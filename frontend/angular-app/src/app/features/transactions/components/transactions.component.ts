import { CommonModule, DatePipe } from '@angular/common';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { TransactionsService } from '../services/transactions.service';
import { TransactionItem } from '../models/transaction.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner.component';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, LoadingSpinnerComponent],
  template: `
    <section class="page-header">
      <h2>Transactions</h2>
      <p class="muted">Search and inspect transaction history across channels.</p>
    </section>

    <article class="panel transaction-filters">
      <div>
        <label for="tx-filter">Filter transactions</label>
        <input id="tx-filter" type="text" [(ngModel)]="searchTerm" placeholder="Search reference, source, destination" />
      </div>

      <div>
        <label for="tx-status">Status</label>
        <select id="tx-status" [(ngModel)]="statusFilter">
          <option value="ALL">All</option>
          <option value="COMPLETED">Completed</option>
          <option value="PENDING">Pending</option>
          <option value="FAILED">Failed</option>
        </select>
      </div>
    </article>

    <app-loading-spinner *ngIf="isLoading()" label="Loading transactions..."></app-loading-spinner>
    <article class="panel" *ngIf="errorMessage()"><p class="error">{{ errorMessage() }}</p></article>

    <article class="panel table-panel" *ngIf="!isLoading()">
      <table>
        <thead>
          <tr><th>Reference</th><th>From</th><th>To</th><th>Amount</th><th>Status</th><th>Time</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let tx of filteredTransactions()">
            <td>{{ tx.reference }}</td>
            <td>{{ tx.fromAccountId }}</td>
            <td>{{ tx.toAccountId }}</td>
            <td>{{ tx.amount | currency: tx.currency }}</td>
            <td><span class="chip" [class.success]="tx.status === 'COMPLETED'">{{ tx.status }}</span></td>
            <td>{{ tx.timestamp | date: 'short' }}</td>
          </tr>
          <tr *ngIf="!filteredTransactions().length">
            <td colspan="6" class="muted">No transactions match your current filters.</td>
          </tr>
        </tbody>
      </table>
    </article>
  `
})
export class TransactionsComponent {
  private readonly transactionsService = inject(TransactionsService);
  private readonly destroyRef = inject(DestroyRef);

  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly transactions = signal<TransactionItem[]>([]);

  searchTerm = '';
  statusFilter: 'ALL' | TransactionItem['status'] = 'ALL';

  readonly filteredTransactions = computed(() => {
    const query = this.searchTerm.toLowerCase().trim();

    return this.transactions().filter((tx) => {
      const matchesSearch =
        !query || [tx.reference, String(tx.fromAccountId), String(tx.toAccountId), tx.status].some((field) => field.toLowerCase().includes(query));
      const matchesStatus = this.statusFilter === 'ALL' || tx.status === this.statusFilter;
      return matchesSearch && matchesStatus;
    });
  });

  constructor() {
    this.loadTransactions();
  }

  private loadTransactions(): void {
    this.isLoading.set(true);

    this.transactionsService
      .listTransactions()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        next: (transactions) => this.transactions.set(transactions),
        error: () => this.errorMessage.set('Unable to load transactions.')
      });
  }
}
