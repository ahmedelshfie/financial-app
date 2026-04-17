import { CommonModule, DatePipe } from '@angular/common';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { CustomersService } from '../services/customers.service';
import { Customer } from '../models/customer.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner.component';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, LoadingSpinnerComponent],
  template: `
    <section class="page-header">
      <h2>Customers</h2>
      <p class="muted">Directory, KYC progress, and customer profile actions.</p>
    </section>

    <article class="panel customer-filters">
      <div>
        <label for="customer-search">Search customers</label>
        <input id="customer-search" [(ngModel)]="searchTerm" placeholder="Search by name, status, or email" />
      </div>
      <div>
        <label for="kyc-filter">KYC status</label>
        <select id="kyc-filter" [(ngModel)]="kycFilter">
          <option value="ALL">All</option>
          <option value="VERIFIED">Verified</option>
          <option value="PENDING">Pending</option>
          <option value="REJECTED">Rejected</option>
        </select>
      </div>
    </article>

    <section class="stat-grid" *ngIf="summary() as overview">
      <article class="panel stat-card"><p class="muted">Total Customers</p><h3>{{ overview.totalCustomers }}</h3></article>
      <article class="panel stat-card"><p class="muted">Pending KYC</p><h3>{{ overview.pendingKyc }}</h3></article>
      <article class="panel stat-card"><p class="muted">Verified Customers</p><h3>{{ overview.verifiedCustomers }}</h3></article>
    </section>

    <app-loading-spinner *ngIf="isLoading()" label="Loading customers..."></app-loading-spinner>
    <article class="panel" *ngIf="errorMessage()"><p class="error">{{ errorMessage() }}</p></article>

    <article class="panel table-panel" *ngIf="!isLoading()">
      <table>
        <thead>
          <tr><th>Customer</th><th>Code</th><th>Status</th><th>KYC</th><th>Joined</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let customer of filteredCustomers()">
            <td>
              <strong>{{ customer.fullName }}</strong>
              <p class="muted">{{ customer.email }}</p>
            </td>
            <td>{{ customer.customerCode }}</td>
            <td>{{ customer.status }}</td>
            <td><span class="chip" [class.success]="customer.kycStatus === 'VERIFIED'">{{ customer.kycStatus }}</span></td>
            <td>{{ customer.joinedOn | date: 'mediumDate' }}</td>
          </tr>
          <tr *ngIf="!filteredCustomers().length">
            <td colspan="5" class="muted">No customers match your selected filters.</td>
          </tr>
        </tbody>
      </table>
    </article>
  `
})
export class CustomersComponent {
  private readonly customersService = inject(CustomersService);
  private readonly destroyRef = inject(DestroyRef);

  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly customers = signal<Customer[]>([]);
  readonly summary = computed(() => this.customersService.getSummary(this.customers()));

  searchTerm = '';
  kycFilter: 'ALL' | Customer['kycStatus'] = 'ALL';

  readonly filteredCustomers = computed(() => {
    const query = this.searchTerm.trim().toLowerCase();

    return this.customers().filter((customer) => {
      const matchesText = !query || [customer.fullName, customer.status, customer.email, customer.customerCode].some((value) => value.toLowerCase().includes(query));
      const matchesKyc = this.kycFilter === 'ALL' || customer.kycStatus === this.kycFilter;

      return matchesText && matchesKyc;
    });
  });

  constructor() {
    this.loadCustomers();
  }

  private loadCustomers(): void {
    this.isLoading.set(true);
    this.customersService
      .getCustomers()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        next: (customers) => this.customers.set(customers),
        error: () => this.errorMessage.set('Unable to load customers. Please retry later.')
      });
  }
}
