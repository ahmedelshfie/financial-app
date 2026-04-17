import { CommonModule, DatePipe } from '@angular/common';
import { Component, DestroyRef, computed, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, forkJoin, switchMap } from 'rxjs';
import { ConfirmModalComponent } from '../../../shared/components/confirm-modal.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner.component';
import { AccountsService } from '../../accounts/services/accounts.service';
import { Beneficiary, PaymentRecord, PaymentRequest } from '../models/payment.model';
import { PaymentsService } from '../services/payments.service';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, ConfirmModalComponent, LoadingSpinnerComponent],
  template: `
    <section class="page-header">
      <h2>Payments</h2>
      <p class="muted">Initiate payouts, manage beneficiaries, and monitor payment status.</p>
    </section>

    <app-loading-spinner *ngIf="isLoading()" label="Loading payments..."></app-loading-spinner>

    <section class="two-column" *ngIf="!isLoading()">
      <article class="panel">
        <h3>Initiate Payment</h3>
        <form (ngSubmit)="reviewPayment()" #paymentForm="ngForm" class="stacked-form">
          <div>
            <label for="beneficiaryId">Beneficiary</label>
            <select id="beneficiaryId" name="beneficiaryId" [(ngModel)]="paymentRequest.beneficiaryId" required>
              <option [ngValue]="0" disabled>Select beneficiary</option>
              <option *ngFor="let beneficiary of beneficiaries()" [ngValue]="beneficiary.id">{{ beneficiary.name }} ({{ beneficiary.bankCode }})</option>
            </select>
          </div>
          <div>
            <label for="sourceAccountId">Source account ID</label>
            <input id="sourceAccountId" name="sourceAccountId" type="number" min="1" [(ngModel)]="paymentRequest.sourceAccountId" (ngModelChange)="onAccountIdChange()" required />
          </div>
          <div>
            <label for="amount">Amount</label>
            <input id="amount" name="amount" type="number" min="1" [(ngModel)]="paymentRequest.amount" required />
          </div>
          
          <button type="submit" class="btn primary" [disabled]="paymentForm.invalid">Review & Submit</button>
        </form>

        <p class="success-text" *ngIf="successMessage()">{{ successMessage() }}</p>
        <p class="error" *ngIf="errorMessage()">{{ errorMessage() }}</p>
      </article>

      <article class="panel">
        <h3>Beneficiaries</h3>
        <div class="list-row" *ngFor="let beneficiary of beneficiaries()">
          <div>
            <strong>{{ beneficiary.name }}</strong>
            <p class="muted">{{ beneficiary.bankCode }} • {{ beneficiary.accountNumber }}</p>
          </div>
        </div>
        <p class="muted" *ngIf="!beneficiaries().length">No beneficiaries available.</p>
      </article>
    </section>

    <article class="panel table-panel" *ngIf="!isLoading()">
      <h3>Payment History</h3>
      <table>
        <thead>
          <tr><th>ID</th><th>Beneficiary</th><th>Amount</th><th>Status</th><th>Created</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let payment of paymentHistory()">
            <td>{{ payment.reference }}</td>
            <td>{{ payment.beneficiaryName }}</td>
            <td>{{ payment.amount | currency: payment.currencyCode }}</td>
            <td><span class="chip" [class.success]="payment.status === 'COMPLETED'">{{ payment.status }}</span></td>
            <td>{{ payment.createdAt | date: 'short' }}</td>
          </tr>
        </tbody>
      </table>
    </article>

    <app-confirm-modal
      [open]="showConfirm()"
      title="Confirm payment"
      [message]="confirmationMessage()"
      (confirm)="submitPayment()"
      (cancel)="showConfirm.set(false)">
    </app-confirm-modal>
  `
})
export class PaymentsComponent implements OnInit {
  private readonly paymentsService = inject(PaymentsService);
  private readonly accountsService = inject(AccountsService);
  private readonly destroyRef = inject(DestroyRef);

  readonly isLoading = signal(false);
  readonly showConfirm = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);
  readonly beneficiaries = signal<Beneficiary[]>([]);
  readonly paymentHistory = signal<PaymentRecord[]>([]);

  paymentRequest: PaymentRequest = { sourceAccountId: 0, beneficiaryId: 0, amount: 0 };

  readonly confirmationMessage = computed(() => {
    const beneficiaryName = this.beneficiaries().find((item) => item.id === this.paymentRequest.beneficiaryId)?.name ?? 'beneficiary';
    return `Submit payment of ${this.paymentRequest.amount || 0} from account ${this.paymentRequest.sourceAccountId || '-'} to ${beneficiaryName}?`;
  });

  constructor() {
    // Data will be loaded when user enters account ID
  }

  ngOnInit(): void {
    // Load initial data if default account ID is available
    if (this.paymentRequest.sourceAccountId > 0) {
      this.loadData(this.paymentRequest.sourceAccountId);
    }
  }

  onAccountIdChange(): void {
    if (this.paymentRequest.sourceAccountId > 0) {
      this.loadData(this.paymentRequest.sourceAccountId);
    }
  }

  reviewPayment(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);
    
    // Load data if not already loaded
    if (this.beneficiaries().length === 0 && this.paymentRequest.sourceAccountId > 0) {
      this.loadData(this.paymentRequest.sourceAccountId);
      return;
    }
    
    this.showConfirm.set(true);
  }

  submitPayment(): void {
    this.showConfirm.set(false);
    this.paymentsService
      .initiatePayment(this.paymentRequest)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          this.successMessage.set(`Payment ${response.paymentReference} submitted with status ${response.status}.`);
          this.paymentRequest = { sourceAccountId: 0, beneficiaryId: 0, amount: 0 };
          // Data will be reloaded after user enters account ID
        },
        error: () => this.errorMessage.set('Payment could not be submitted.')
      });
  }

  private loadData(accountId: number): void {
    this.isLoading.set(true);
    this.accountsService
      .getAccountById(accountId)
      .pipe(
        switchMap((account) =>
          forkJoin({
            beneficiaries: this.paymentsService.getBeneficiaries(account.customerId),
            history: this.paymentsService.getPaymentHistory(accountId)
          })
        ),
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        next: ({ beneficiaries, history }) => {
          this.beneficiaries.set(beneficiaries);
          this.paymentHistory.set(history);
        },
        error: () => this.errorMessage.set('Unable to load payment data for this account.')
      });
  }
}
