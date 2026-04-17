import { CommonModule } from '@angular/common';
import { Component, OnInit, DestroyRef, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { AccountsService } from '../services/accounts.service';
import { ApplicationError } from '../../../core/error-handling/global-error-handler.service';
import { AccountResponse } from '../models/account.model';
import { FRONTEND_ERROR_MESSAGES } from '../../../core/error-handling/error-messages';
import { MaskSensitivePipe } from '../../../shared/pipes/mask-sensitive.pipe';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner.component';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, FormsModule, MaskSensitivePipe, LoadingSpinnerComponent],
  template: `
    <section class="page-header">
      <h2>Accounts</h2>
      <p class="muted">Account inventory and balance posture from Account Service.</p>
    </section>

    <article class="panel customer-filters">
      <div>
        <label for="account-customer-id">Customer ID</label>
        <input id="account-customer-id" type="number" min="1" [(ngModel)]="selectedCustomerId" />
      </div>
      <div>
        <label for="account-search">Search account</label>
        <input id="account-search" type="text" [(ngModel)]="searchTerm" placeholder="Filter by account number or status" />
      </div>
      <button class="btn ghost" type="button" (click)="loadAccounts()" [disabled]="!selectedCustomerId">Load Accounts</button>
    </article>

    <article class="panel" *ngIf="errorMessage()">
      <p class="error">{{ errorMessage() }}</p>
    </article>

    <article class="panel table-panel">
      <app-loading-spinner *ngIf="isLoading()" label="Loading accounts..."></app-loading-spinner>
      <table *ngIf="!isLoading()">
        <thead>
          <tr><th>Account</th><th>Type</th><th>Customer</th><th>Available Balance</th><th>Status</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let account of filteredAccounts()">
            <td>{{ account.accountNumber | maskSensitive:4 }}</td>
            <td>{{ account.accountTypeCode }}</td>
            <td>{{ account.customerId }}</td>
            <td>{{ account.availableBalance | currency: account.currencyCode }}</td>
            <td><span class="chip" [class.success]="account.status === 'ACTIVE'">{{ account.status }}</span></td>
          </tr>
          <tr *ngIf="!filteredAccounts().length">
            <td colspan="5" class="muted">No accounts found for customer {{ selectedCustomerId }}.</td>
          </tr>
        </tbody>
      </table>
    </article>
  `
})
export class AccountsComponent implements OnInit {
  private readonly accountsService = inject(AccountsService);
  private readonly destroyRef = inject(DestroyRef);

  selectedCustomerId: number | null = 1;
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly accounts = signal<AccountResponse[]>([]);
  searchTerm = '';

  readonly filteredAccounts = computed(() => {
    const query = this.searchTerm.trim().toLowerCase();
    if (!query) {
      return this.accounts();
    }

    return this.accounts().filter((account) =>
      account.accountNumber.toLowerCase().includes(query) || account.status.toLowerCase().includes(query)
    );
  });

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    if (!this.selectedCustomerId) {
      this.errorMessage.set('Customer ID is required to load accounts.');
      return;
    }
    this.isLoading.set(true);
    this.accountsService
      .getAccountsByCustomer(this.selectedCustomerId)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        next: (accounts) => {
          this.accounts.set(accounts);
          this.errorMessage.set(null);
        },
        error: (error: unknown) => {
          const fallback = FRONTEND_ERROR_MESSAGES.accountsLoadFailure;
          this.errorMessage.set(error instanceof ApplicationError ? error.message : fallback);
        }
      });
  }
}
