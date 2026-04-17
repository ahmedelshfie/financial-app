import { CommonModule, DatePipe } from '@angular/common';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { ConfirmModalComponent } from '../../../shared/components/confirm-modal.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner.component';
import { TransferRecord, TransferRequest } from '../models/transfer.model';
import { TransfersService } from '../services/transfers.service';

@Component({
  selector: 'app-transfers',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, ConfirmModalComponent, LoadingSpinnerComponent],
  template: `
    <section class="page-header">
      <h2>Transfers</h2>
      <p class="muted">Initiate account-to-account transfers with confirmation workflow.</p>
    </section>

    <section class="two-column">
      <article class="panel">
        <h3>New Transfer</h3>
        <form (ngSubmit)="reviewTransfer()" #transferForm="ngForm" class="stacked-form">
          <div>
            <label for="sourceAccountId">From account ID</label>
            <input id="sourceAccountId" name="sourceAccountId" type="number" min="1" [(ngModel)]="request.sourceAccountId" required />
          </div>
          <div>
            <label for="destinationAccountId">To account ID</label>
            <input id="destinationAccountId" name="destinationAccountId" type="number" min="1" [(ngModel)]="request.destinationAccountId" required />
          </div>
          <div>
            <label for="transferAmount">Amount</label>
            <input id="transferAmount" name="amount" type="number" min="1" [(ngModel)]="request.amount" required />
          </div>
          <button type="submit" class="btn primary" [disabled]="transferForm.invalid">Continue</button>
        </form>

        <p class="success-text" *ngIf="successMessage()">{{ successMessage() }}</p>
        <p class="error" *ngIf="errorMessage()">{{ errorMessage() }}</p>
      </article>

      <article class="panel table-panel">
        <h3>Transfer History</h3>
        <app-loading-spinner *ngIf="isLoading()" label="Loading transfer history..."></app-loading-spinner>
        <table *ngIf="!isLoading()">
          <thead><tr><th>Reference</th><th>From</th><th>To</th><th>Amount</th><th>Status</th><th>Time</th></tr></thead>
          <tbody>
            <tr *ngFor="let transfer of transfers()">
              <td>{{ transfer.reference }}</td>
              <td>{{ transfer.fromAccountId }}</td>
              <td>{{ transfer.toAccountId }}</td>
              <td>{{ transfer.amount | currency: transfer.currencyCode }}</td>
              <td><span class="chip" [class.success]="transfer.status === 'COMPLETED'">{{ transfer.status }}</span></td>
              <td>{{ transfer.createdAt | date: 'short' }}</td>
            </tr>
            <tr *ngIf="!transfers().length"><td colspan="6" class="muted">No transfer history found.</td></tr>
          </tbody>
        </table>
      </article>
    </section>

    <app-confirm-modal
      [open]="showConfirm()"
      title="Confirm transfer"
      [message]="'Transfer ' + request.amount + ' from account ' + request.sourceAccountId + ' to account ' + request.destinationAccountId + '?'"
      (confirm)="submitTransfer()"
      (cancel)="showConfirm.set(false)">
    </app-confirm-modal>
  `
})
export class TransfersComponent {
  private readonly transfersService = inject(TransfersService);
  private readonly destroyRef = inject(DestroyRef);

  readonly isLoading = signal(false);
  readonly showConfirm = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);
  readonly transfers = signal<TransferRecord[]>([]);

  request: TransferRequest = {
    sourceAccountId: 0,
    destinationAccountId: 0,
    amount: 0
  };

  constructor() {
    this.loadTransfers();
  }

  reviewTransfer(): void {
    this.showConfirm.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
  }

  submitTransfer(): void {
    this.showConfirm.set(false);
    this.transfersService
      .createTransfer(this.request)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          this.successMessage.set(`Transfer ${response.transactionReference} submitted with status ${response.status}.`);
          this.request = { sourceAccountId: 0, destinationAccountId: 0, amount: 0 };
          this.loadTransfers();
        },
        error: () => this.errorMessage.set('Transfer submission failed. Please try again.')
      });
  }

  private loadTransfers(): void {
    this.isLoading.set(true);
    this.transfersService
      .listTransfers()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        next: (records) => this.transfers.set(records),
        error: () => this.errorMessage.set('Unable to load transfer history.')
      });
  }
}
