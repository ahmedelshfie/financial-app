import { CommonModule } from '@angular/common';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { DashboardService } from '../services/dashboard.service';
import { DashboardData } from '../models/dashboard.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, LoadingSpinnerComponent, FormsModule],
  template: `
    <section class="page-header">
      <h2>Dashboard</h2>
      <p class="muted">Operational insights, alerts, and quick actions.</p>
    </section>


    <article class="panel customer-filters">
      <div>
        <label for="dashboard-customer-id">Customer ID</label>
        <input id="dashboard-customer-id" type="number" min="1" [(ngModel)]="customerId" />
      </div>
      <button class="btn ghost" type="button" (click)="loadDashboard()" [disabled]="!customerId">Load</button>
    </article>

    <article class="panel" *ngIf="errorMessage()">
      <p class="error">{{ errorMessage() }}</p>
      <button class="btn ghost" type="button" (click)="loadDashboard()">Retry</button>
    </article>

    <app-loading-spinner *ngIf="isLoading()" label="Loading dashboard..."></app-loading-spinner>

    <ng-container *ngIf="!isLoading() && data() as view">
      <section class="stat-grid" *ngIf="view.metrics.length">
        <article class="panel stat-card" *ngFor="let stat of view.metrics">
          <p class="muted">{{ stat.label }}</p>
          <h3>{{ stat.value }}</h3>
          <span class="trend" [class.negative]="stat.trend.startsWith('-')">{{ stat.trend }}</span>
        </article>
      </section>

      <section class="two-column">
        <article class="panel">
          <h3>Recent Activity</h3>
          <p *ngIf="!view.activities.length" class="muted empty-state">No recent activity found.</p>
          <div class="list-row" *ngFor="let item of view.activities">
            <div>
              <strong>{{ item.title }}</strong>
              <p class="muted">{{ item.subtitle }}</p>
            </div>
            <span class="chip">{{ item.status }}</span>
          </div>
        </article>

        <article class="panel">
          <h3>Alerts</h3>
          <p *ngIf="!view.alerts.length" class="muted empty-state">No active alerts.</p>
          <div class="list-row" *ngFor="let alert of view.alerts">
            <span>{{ alert.message }}</span>
            <span class="chip" [class.warn]="alert.severity === 'warning'" [class.danger]="alert.severity === 'critical'">{{ alert.severity }}</span>
          </div>
        </article>
      </section>

      <article class="panel">
        <h3>Shortcuts</h3>
        <div class="shortcut-grid">
          <a class="shortcut-card" *ngFor="let shortcut of view.shortcuts" [routerLink]="shortcut.route">
            <strong>{{ shortcut.label }}</strong>
            <span class="muted">{{ shortcut.description }}</span>
          </a>
        </div>
      </article>
    </ng-container>
  `
})
export class DashboardComponent {
  private readonly dashboardService = inject(DashboardService);
  private readonly destroyRef = inject(DestroyRef);

  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly data = signal<DashboardData | null>(null);
  readonly hasData = computed(() => !!this.data());
  customerId: number | null = 1;

  constructor() {
    this.loadDashboard();
  }

  loadDashboard(): void {
    if (!this.customerId) {
      this.errorMessage.set('Customer ID is required to load dashboard data.');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.dashboardService
      .getDashboardData(this.customerId)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        next: (data) => this.data.set(data),
        error: () => this.errorMessage.set('Unable to load dashboard data at the moment.')
      });
  }
}
